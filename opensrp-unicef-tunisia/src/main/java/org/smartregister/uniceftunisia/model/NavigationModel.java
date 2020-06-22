package org.smartregister.uniceftunisia.model;

import org.smartregister.uniceftunisia.contract.NavigationContract;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class NavigationModel implements NavigationContract.Model {
    private static NavigationModel instance;
    private static List<NavigationOption> navigationOptions = new ArrayList<>();

    public static NavigationModel getInstance() {
        if (instance == null)
            instance = new NavigationModel();

        return instance;
    }

    @Override
    public String getCurrentUser() {
        String currentUser = "";
        try {
            currentUser = Utils.getPrefferedName();
        } catch (Exception e) {
            Timber.e(e);
        }

        return currentUser;
    }
}
