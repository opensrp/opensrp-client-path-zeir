package org.smartregister.uniceftunisia.model;

import androidx.annotation.NonNull;

import org.smartregister.child.domain.ChildEventClient;
import org.smartregister.child.model.BaseChildRegisterModel;
import org.smartregister.child.presenter.BaseChildDetailsPresenter.CardStatus;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication;
import org.smartregister.uniceftunisia.util.AppConstants;

import java.util.Calendar;
import java.util.List;

import static org.smartregister.uniceftunisia.util.AppConstants.KEY.REGISTRATION_LOCATION_ID;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.REGISTRATION_LOCATION_NAME;

public class AppChildRegisterModel extends BaseChildRegisterModel {

    @Override
    public List<ChildEventClient> processRegistration(@NonNull String jsonString, FormTag formTag) {
        List<ChildEventClient> childEventClients = super.processRegistration(jsonString, formTag);
        //Add location name as part of child attributes to avoid fetching name from events
        for (ChildEventClient childEventClient : childEventClients) {
            Client client = childEventClient.getClient();
            if (AppConstants.EventType.CHILD_REGISTRATION.equalsIgnoreCase(childEventClient.getEvent().getEventType())) {
                client.getAttributes().put(AppConstants.KEY.CARD_STATUS, CardStatus.needs_card.name());
                client.getAttributes().put(AppConstants.KEY.CARD_STATUS_DATE, DateUtil.fromDate(Calendar.getInstance().getTime()));
            }
            AllSharedPreferences sharedPreferences = getSharedPrefs();
            String currentUser = sharedPreferences.fetchRegisteredANM();
            client.getAttributes().put(REGISTRATION_LOCATION_ID, sharedPreferences.fetchDefaultLocalityId(currentUser));
            client.getAttributes().put(REGISTRATION_LOCATION_NAME, sharedPreferences.fetchCurrentLocality());
        }
        return childEventClients;
    }

    private AllSharedPreferences getSharedPrefs() {
        return UnicefTunisiaApplication.getInstance().context().allSharedPreferences();
    }
}
