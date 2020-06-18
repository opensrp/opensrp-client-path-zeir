package org.smartregister.uniceftunisia.contract;

import android.app.Activity;

import org.smartregister.uniceftunisia.model.NavigationOption;

import java.util.Date;
import java.util.List;

public interface NavigationContract {

    interface Presenter {

        NavigationContract.View getNavigationView();

        void refreshLastSync();

        void displayCurrentUser();

        void sync(Activity activity);

        List<NavigationOption> getOptions();
    }

    interface View {

        void prepareViews(Activity activity);

        void refreshLastSync(Date lastSync);

        void refreshCurrentUser(String name);

        void logout(Activity activity);

    }

    interface Model {

        List<NavigationOption> getNavigationItems();

        String getCurrentUser();
    }

    interface Interactor {

        void getRegisterCount(String registerType, InteractorCallback<Integer> callback);

        Date sync();

    }

    interface InteractorCallback<T> {
        void onResult(T result);

        void onError(Exception e);
    }
}
