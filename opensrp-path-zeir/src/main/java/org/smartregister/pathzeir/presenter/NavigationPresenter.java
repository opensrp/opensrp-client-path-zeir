package org.smartregister.pathzeir.presenter;

import android.app.Activity;
import android.text.TextUtils;

import org.smartregister.Context;
import org.smartregister.growthmonitoring.job.HeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.WeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.ZScoreRefreshIntentServiceJob;
import org.smartregister.immunization.job.VaccineServiceJob;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.pathzeir.contract.NavigationContract;
import org.smartregister.pathzeir.interactor.NavigationInteractor;
import org.smartregister.pathzeir.model.NavigationModel;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.stock.job.SyncStockServiceJob;

import java.lang.ref.WeakReference;

import timber.log.Timber;

public class NavigationPresenter implements NavigationContract.Presenter {

    private final NavigationContract.Model model;
    private final NavigationContract.Interactor interactor;
    private final WeakReference<NavigationContract.View> view;


    public NavigationPresenter(NavigationContract.View view) {
        this.view = new WeakReference<>(view);
        interactor = NavigationInteractor.getInstance();
        model = NavigationModel.getInstance();
    }

    @Override
    public NavigationContract.View getNavigationView() {
        return view.get();
    }

    @Override
    public void refreshLastSync() {
        // get last sync date
        getNavigationView().refreshLastSync(interactor.sync());
    }

    @Override
    public void displayCurrentUser() {
        getNavigationView().refreshCurrentUser(model.getCurrentUser());
    }

    @Override
    public void sync(Activity activity) {
        ImageUploadServiceJob.scheduleJobImmediately(ImageUploadServiceJob.TAG);
        SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
        ZScoreRefreshIntentServiceJob.scheduleJobImmediately(ZScoreRefreshIntentServiceJob.TAG);
        WeightIntentServiceJob.scheduleJobImmediately(WeightIntentServiceJob.TAG);
        HeightIntentServiceJob.scheduleJobImmediately(HeightIntentServiceJob.TAG);
        VaccineServiceJob.scheduleJobImmediately(VaccineServiceJob.TAG);
        SyncStockServiceJob.scheduleJobImmediately(SyncStockServiceJob.TAG);
    }

    @Override
    public String getLoggedInUserInitials() {

        try {
            AllSharedPreferences allSharedPreferences = getAllSharedPreferences();
            String preferredName = allSharedPreferences.getANMPreferredName(allSharedPreferences.fetchRegisteredANM());
            if (!TextUtils.isEmpty(preferredName)) {
                String[] initialsArray = preferredName.split(" ");
                String initials = "";
                if (initialsArray.length > 0) {
                    initials = initialsArray[0].substring(0, 1);
                    if (initialsArray.length > 1) {
                        initials = initials + initialsArray[1].substring(0, 1);
                    }
                }
                return initials.toUpperCase();
            }

        } catch (StringIndexOutOfBoundsException exception) {
            Timber.e(exception, "Error fetching initials");
        }

        return null;
    }


    public AllSharedPreferences getAllSharedPreferences() {
        return Context.getInstance().allSharedPreferences();
    }
}
