package org.smartregister.pathzeir.interactor;

import org.smartregister.pathzeir.application.ZeirApplication;
import org.smartregister.pathzeir.contract.NavigationContract;

import java.util.Date;

import timber.log.Timber;

public class NavigationInteractor implements NavigationContract.Interactor {

    private static NavigationInteractor instance;

    public static NavigationInteractor getInstance() {
        if (instance == null)
            instance = new NavigationInteractor();

        return instance;
    }

    @Override
    public Date sync() {
        Date syncDate = null;
        try {
            syncDate = new Date(getLastCheckTimeStamp());
        } catch (Exception e) {
            Timber.e(e);
        }

        return syncDate;
    }

    private Long getLastCheckTimeStamp() {
        return ZeirApplication.getInstance().getEcSyncHelper().getLastCheckTimeStamp();
    }
}
