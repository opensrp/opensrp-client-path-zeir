package org.smartregister.pathzeir.contract;

import android.app.Activity;

import java.util.Date;

public interface NavigationContract {

    interface Presenter {

        NavigationContract.View getNavigationView();

        void refreshLastSync();

        void displayCurrentUser();

        void sync(Activity activity);

        String getLoggedInUserInitials();
    }

    interface View {

        void prepareViews(Activity activity);

        void refreshLastSync(Date lastSync);

        void refreshCurrentUser(String name);

        void logout(Activity activity);

    }

    interface Model {

        String getCurrentUser();
    }

    interface Interactor {

        Date sync();

    }

}
