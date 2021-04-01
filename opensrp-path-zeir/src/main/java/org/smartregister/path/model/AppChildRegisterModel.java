package org.smartregister.path.model;

import androidx.annotation.NonNull;

import org.smartregister.child.domain.ChildEventClient;
import org.smartregister.child.model.BaseChildRegisterModel;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.path.application.ZeirApplication;
import org.smartregister.repository.AllSharedPreferences;

import java.util.List;

import static org.smartregister.path.util.AppConstants.KeyConstants.REGISTRATION_LOCATION_ID;
import static org.smartregister.path.util.AppConstants.KeyConstants.REGISTRATION_LOCATION_NAME;

public class AppChildRegisterModel extends BaseChildRegisterModel {

    @Override
    public List<ChildEventClient> processRegistration(@NonNull String jsonString, FormTag formTag) {
        List<ChildEventClient> childEventClients = super.processRegistration(jsonString, formTag);
        //Add location name as part of child attributes to avoid fetching name from events
        for (ChildEventClient childEventClient : childEventClients) {
            Client client = childEventClient.getClient();
            AllSharedPreferences sharedPreferences = getSharedPrefs();
            String currentUser = sharedPreferences.fetchRegisteredANM();
            client.getAttributes().put(REGISTRATION_LOCATION_ID, sharedPreferences.fetchDefaultLocalityId(currentUser));
            client.getAttributes().put(REGISTRATION_LOCATION_NAME, sharedPreferences.fetchCurrentLocality());
        }
        return childEventClients;
    }

    private AllSharedPreferences getSharedPrefs() {
        return ZeirApplication.getInstance().context().allSharedPreferences();
    }
}
