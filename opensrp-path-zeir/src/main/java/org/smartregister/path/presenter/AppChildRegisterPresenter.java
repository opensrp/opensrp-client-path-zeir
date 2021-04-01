package org.smartregister.path.presenter;


import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.child.contract.ChildRegisterContract;
import org.smartregister.child.domain.UpdateRegisterParams;
import org.smartregister.child.presenter.BaseChildDetailsPresenter.CardStatus;
import org.smartregister.child.presenter.BaseChildRegisterPresenter;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.path.application.ZeirApplication;
import org.smartregister.path.dao.AppChildDao;
import org.smartregister.path.util.AppConstants;
import org.smartregister.path.util.AppUtils;
import org.smartregister.repository.EventClientRepository;

import java.util.Calendar;
import java.util.Date;

import timber.log.Timber;

import static org.smartregister.path.util.AppConstants.KeyConstants.CARD_STATUS;

public class AppChildRegisterPresenter extends BaseChildRegisterPresenter {
    private final EventClientRepository eventClientRepository = ZeirApplication.getInstance().eventClientRepository();

    public AppChildRegisterPresenter(ChildRegisterContract.View view, ChildRegisterContract.Model model) {
        super(view, model);
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        super.onRegistrationSaved(isEdit);
    }

    public void updateChildCardStatus(String openSRPId) {
        if (StringUtils.isNotBlank(openSRPId)) {
            String baseEntityId = AppChildDao.getBaseEntityIdByOpenSRPId(openSRPId);
            if (StringUtils.isNotBlank(baseEntityId)) {
                Date currentTime = Calendar.getInstance().getTime();
                String cardStatusDate = DateUtil.fromDate(currentTime);
                JSONObject client = eventClientRepository.getClientByBaseEntityId(baseEntityId);
                if (client != null) {
                    try {
                        JSONObject clientAttributes = client.getJSONObject(AllConstants.ATTRIBUTES);
                        clientAttributes.put(CARD_STATUS, CardStatus.does_not_need_card.name());
                        clientAttributes.put(AppConstants.KeyConstants.CARD_STATUS_DATE, cardStatusDate);
                        client.put(AllConstants.ATTRIBUTES, clientAttributes);
                        eventClientRepository.addorUpdateClient(baseEntityId, client);
                    } catch (JSONException e) {
                        Timber.e(e);
                    }
                }
                AppUtils.createClientCardReceivedEvent(baseEntityId, CardStatus.does_not_need_card, cardStatusDate);
            }
        }
    }

    @Override
    public void saveForm(String jsonString, UpdateRegisterParams updateRegisterParams) {
        String jsonForm = AppUtils.validateChildZone(jsonString);
        super.saveForm(jsonForm, updateRegisterParams);
    }

}
