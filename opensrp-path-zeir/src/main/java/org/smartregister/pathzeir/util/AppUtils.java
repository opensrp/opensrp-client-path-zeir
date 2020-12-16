package org.smartregister.pathzeir.util;

import android.content.ContentValues;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.child.presenter.BaseChildDetailsPresenter.CardStatus;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.domain.Client;
import org.smartregister.domain.db.EventClient;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.pathzeir.BuildConfig;
import org.smartregister.pathzeir.application.ZeirApplication;
import org.smartregister.pathzeir.dao.AppChildDao;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.JsonFormUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class AppUtils extends Utils {

    public static final ArrayList<String> ALLOWED_LEVELS;
    public static final String FACILITY = "Facility";
    public static final String DEFAULT_LOCATION_LEVEL = "Health Facility";

    static {
        ALLOWED_LEVELS = new ArrayList<>();
        ALLOWED_LEVELS.add(DEFAULT_LOCATION_LEVEL);
        ALLOWED_LEVELS.add(FACILITY);
    }

    public static String getLanguage() {
        return AppUtils.getAllSharedPreferences().fetchLanguagePreference();
    }

    public static void updateChildDeath(@NonNull EventClient eventClient) {
        Client client = eventClient.getClient();
        ContentValues values = new ContentValues();

        if (client.getDeathdate() == null) {
            Timber.e(new Exception(), "Death event for %s cannot be processed because deathdate is NULL : %s"
                    , client.getFirstName() + " " + client.getLastName(), new Gson().toJson(eventClient));
            return;
        }

        values.put(Constants.KEY.IS_CLOSED, 1);
        values.put(Constants.KEY.DATE_REMOVED, Utils.convertDateFormat(client.getDeathdate().toDate(), Utils.DB_DF));
        updateChildTables(client, values, AppConstants.TABLE_NAME.CHILD_DETAILS);
        updateChildTables(client, values, AppConstants.TABLE_NAME.ALL_CLIENTS);
    }

    private static void updateChildTables(Client client, ContentValues values, String tableName) {
        AllCommonsRepository allCommonsRepository = ZeirApplication.getInstance().context().allCommonsRepositoryobjects(tableName);
        if (allCommonsRepository != null) {
            allCommonsRepository.update(tableName, values, client.getBaseEntityId());
            allCommonsRepository.updateSearch(client.getBaseEntityId());
        }
    }

    @NonNull
    public static ArrayList<String> getLocationLevels() {
        return new ArrayList<>(Arrays.asList(BuildConfig.LOCATION_LEVELS));
    }

    @NonNull
    public static ArrayList<String> getHealthFacilityLevels() {
        return new ArrayList<>(Arrays.asList(BuildConfig.HEALTH_FACILITY_LEVELS));
    }

    @NonNull
    public static String getCurrentLocality() {
        String selectedLocation = ZeirApplication.getInstance().context().allSharedPreferences().fetchCurrentLocality();
        if (StringUtils.isBlank(selectedLocation)) {
            selectedLocation = LocationHelper.getInstance().getDefaultLocation();
            ZeirApplication.getInstance().context().allSharedPreferences().saveCurrentLocality(selectedLocation);
        }
        return selectedLocation;
    }


    public static boolean timeBetweenLastExecutionAndNow(int i, String reportJobExecutionTime) {
        try {
            long executionTime = Long.parseLong(reportJobExecutionTime);
            long now = System.currentTimeMillis();
            long diffNowExecutionTime = now - executionTime;
            return TimeUnit.MILLISECONDS.toMinutes(diffNowExecutionTime) > i;
        } catch (NumberFormatException e) {
            Timber.e(e);
            return false;
        }
    }

    public static void updateSyncStatus(boolean isComplete) {
        ZeirApplication.getInstance().context().allSharedPreferences().savePreference("syncComplete", String.valueOf(isComplete));
    }

    public static void createClientCardReceivedEvent(String baseEntityId, CardStatus cardStatus, String cardStatusDate) {
        //We do not want to unnecessary events when card is not needed
        if (cardStatus == CardStatus.does_not_need_card && !AppChildDao.clientNeedsCard(baseEntityId)) {
            return;
        }
        try {
            Event baseEvent = AppJsonFormUtils.createEvent(new JSONArray(), new JSONObject().put(JsonFormUtils.ENCOUNTER_LOCATION, ""),
                    AppJsonFormUtils.formTag(getAllSharedPreferences()), "", AppConstants.EventType.CARD_STATUS_UPDATE, AppConstants.EventType.CARD_STATUS_UPDATE);

            baseEvent.setFormSubmissionId(UUID.randomUUID().toString());
            baseEvent.addDetails(AppConstants.KEY.CARD_STATUS, cardStatus.name());
            baseEvent.addDetails(AppConstants.KEY.CARD_STATUS_DATE, cardStatusDate);
            baseEvent.setBaseEntityId(baseEntityId);
            AppJsonFormUtils.tagEventMetadata(baseEvent);

            ZeirApplication appInstance = ZeirApplication.getInstance();
            ECSyncHelper ecSyncHelper = appInstance.getEcSyncHelper();

            ecSyncHelper.addEvent(baseEntityId, new JSONObject(AppJsonFormUtils.gson.toJson(baseEvent)));
            appInstance.getClientProcessor().processClient(ecSyncHelper.getEvents(Collections.singletonList(baseEvent.getFormSubmissionId())));

            Date lastSyncDate = new Date(getAllSharedPreferences().fetchLastUpdatedAtDate(0));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
