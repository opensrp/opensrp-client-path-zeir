package org.smartregister.giz.presenter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.UniqueId;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.interactor.BaseOpdRegisterActivityInteractor;
import org.smartregister.opd.pojos.OpdEventClient;
import org.smartregister.opd.pojos.UpdateRegisterParams;
import org.smartregister.opd.presenter.BaseOpdRegisterActivityPresenter;
import org.smartregister.opd.utils.AppExecutors;
import org.smartregister.opd.utils.Constants;
import org.smartregister.opd.utils.JsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class OpdRegisterActivityInteractor extends BaseOpdRegisterActivityInteractor {
    private AppExecutors appExecutors;


    public OpdRegisterActivityInteractor() {
        this(new AppExecutors());
    }

    OpdRegisterActivityInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    @Override
    public void getNextUniqueId(final Triple<String, String, String> triple, final OpdRegisterActivityContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                UniqueId uniqueId = getUniqueIdRepository().getNextUniqueId();
                final String entityId = uniqueId != null ? uniqueId.getOpenmrsId() : "";
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (StringUtils.isBlank(entityId)) {
                            callBack.onNoUniqueId();
                        } else {
                            callBack.onUniqueIdFetched(triple, entityId);
                        }
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        //TODO set presenter or model to null
    }

    @Override
    public void saveRegistration(final List<OpdEventClient> opdEventClientList, final String jsonString, final UpdateRegisterParams updateRegisterParams, BaseOpdRegisterActivityPresenter opdRegisterActivityPresenter) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                saveRegistration(opdEventClientList, jsonString, updateRegisterParams);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
//                        callBack.onRegistrationSaved(updateRegisterParams.isEditMode());
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }


    private void saveRegistration(List<OpdEventClient> opdEventClients, String jsonString,
                                  UpdateRegisterParams params) {
        try {
            List<String> currentFormSubmissionIds = new ArrayList<>();

            for (int i = 0; i < opdEventClients.size(); i++) {
                try {

                    OpdEventClient opdEventClient = opdEventClients.get(i);
                    Client baseClient = opdEventClient.getClient();
                    Event baseEvent = opdEventClient.getEvent();

                    if (baseClient != null) {
                        JSONObject clientJson = new JSONObject(JsonFormUtils.gson.toJson(baseClient));
                        if (params.isEditMode()) {
                            try {
                                JsonFormUtils.mergeAndSaveClient(baseClient);
                            } catch (Exception e) {
                                Timber.e(e, "OpdRegisterInteractor --> mergeAndSaveClient");
                            }
                        } else {
                            getSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);
                        }
                    }

                    addEvent(params, currentFormSubmissionIds, baseEvent);
                    updateOpenSRPId(jsonString, params, baseClient);
                    addImageLocation(jsonString, i, baseClient, baseEvent);
                } catch (Exception e) {
                    Timber.e(e, "OpdRegisterInteractor --> saveRegistration loop");
                }
            }

            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(currentFormSubmissionIds));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e, "OpdRegisterInteractor --> saveRegistration");
        }
    }
    private void addImageLocation(String jsonString, int i, Client baseClient, Event baseEvent) {
        if (baseClient != null || baseEvent != null) {
            String imageLocation = null;
            if (i == 0) {
                imageLocation = JsonFormUtils.getFieldValue(jsonString, Constants.KEY.PHOTO);
            } else if (i == 1) {
                imageLocation =
                        JsonFormUtils.getFieldValue(jsonString, JsonFormUtils.STEP2, Constants.KEY.PHOTO);
            }

            if (StringUtils.isNotBlank(imageLocation)) {
                JsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);
            }
        }
    }

    private void updateOpenSRPId(String jsonString, UpdateRegisterParams params, Client baseClient) {
        if (params.isEditMode()) {
            // Unassign current OPENSRP ID
            if (baseClient != null) {
                try {
                    String newOpenSRPId = baseClient.getIdentifier(JsonFormUtils.MER_ID).replace("-", "");
                    String currentOpenSRPId = JsonFormUtils.getString(jsonString, JsonFormUtils.CURRENT_MER_ID).replace("-", "");
                    if (!newOpenSRPId.equals(currentOpenSRPId)) {
                        //OPENSRP ID was changed
                        getUniqueIdRepository().open(currentOpenSRPId);
                    }
                } catch (Exception e) {//might crash if M_ZEIR
                    Timber.d(e, "RegisterInteractor --> unassign opensrp id");
                }
            }

        } else {
            if (baseClient != null) {
                String opensrpId = baseClient.getIdentifier(JsonFormUtils.MER_ID);

                //mark OPENSRP ID as used
                getUniqueIdRepository().close(opensrpId);
            }
        }
    }

    private void addEvent(UpdateRegisterParams params, List<String> currentFormSubmissionIds, Event baseEvent) throws JSONException {
        if (baseEvent != null) {
            JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(baseEvent));
            getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson, params.getStatus());
            currentFormSubmissionIds
                    .add(eventJson.getString(EventClientRepository.event_column.formSubmissionId.toString()));
        }
    }

    public ECSyncHelper getSyncHelper() {
        return OpdLibrary.getInstance().getEcSyncHelper();
    }

    public AllSharedPreferences getAllSharedPreferences() {
        return OpdLibrary.getInstance().context().allSharedPreferences();
    }
}
