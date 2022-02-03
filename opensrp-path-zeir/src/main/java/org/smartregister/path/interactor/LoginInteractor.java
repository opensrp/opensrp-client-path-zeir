package org.smartregister.path.interactor;

import org.smartregister.child.job.ArchiveClientsJob;
import org.smartregister.growthmonitoring.job.HeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.WeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.ZScoreRefreshIntentServiceJob;
import org.smartregister.immunization.job.RecurringServiceJob;
import org.smartregister.immunization.job.VaccineServiceJob;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncAllLocationsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.login.interactor.BaseLoginInteractor;
import org.smartregister.path.BuildConfig;
import org.smartregister.path.job.AppVaccineUpdateJob;
import org.smartregister.path.job.ZeirHIA2IntentServiceJob;
import org.smartregister.path.reporting.annual.coverage.job.SyncAnnualReportWorker;
import org.smartregister.path.reporting.dropuout.job.DropoutIntentServiceJob;
import org.smartregister.path.reporting.stock.job.StockSyncIntentServiceJob;
import org.smartregister.reporting.job.RecurringIndicatorGeneratingJob;
import org.smartregister.view.contract.BaseLoginContract;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class LoginInteractor extends BaseLoginInteractor implements BaseLoginContract.Interactor {

    @Override
    public void login(WeakReference<BaseLoginContract.View> view, String userName, char[] password) {
        //change case to lowercase before login attempt
        super.login(view, userName.toLowerCase(), password);
    }

    public LoginInteractor(BaseLoginContract.Presenter loginPresenter) {
        super(loginPresenter);
    }

    @Override
    protected void scheduleJobsPeriodically() {

        VaccineServiceJob.scheduleJob(VaccineServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.DATA_SYNC_DURATION_MINUTES),
                        getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES));

        WeightIntentServiceJob.scheduleJob(WeightIntentServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.DATA_SYNC_DURATION_MINUTES),
                        getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES));

        HeightIntentServiceJob.scheduleJob(HeightIntentServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.DATA_SYNC_DURATION_MINUTES),
                        getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES));

        ZScoreRefreshIntentServiceJob.scheduleJob(ZScoreRefreshIntentServiceJob.TAG,
                TimeUnit.MINUTES.toMinutes(BuildConfig.DATA_SYNC_DURATION_MINUTES),
                getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES));

        //This will also take care of SyncServiceJob when done
        SyncServiceJob.scheduleJob(SyncServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.DATA_SYNC_DURATION_MINUTES),
                getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES));

        PullUniqueIdsServiceJob.scheduleJob(PullUniqueIdsServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.PULL_UNIQUE_IDS_MINUTES),
                        getFlexValue(BuildConfig.PULL_UNIQUE_IDS_MINUTES));

        ImageUploadServiceJob.scheduleJob(ImageUploadServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.IMAGE_UPLOAD_MINUTES),
                        getFlexValue(BuildConfig.IMAGE_UPLOAD_MINUTES));

        RecurringIndicatorGeneratingJob.scheduleJob(RecurringIndicatorGeneratingJob.TAG,
                TimeUnit.HOURS.toMinutes(6), getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES));

        //Schedule job to run at least once daily
        ArchiveClientsJob.scheduleJob(ArchiveClientsJob.TAG, TimeUnit.MINUTES.toMinutes(1440),
                getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES));

        AppVaccineUpdateJob.scheduleEverydayAt(AppVaccineUpdateJob.TAG, 1, 20);
        DropoutIntentServiceJob.scheduleJob(DropoutIntentServiceJob.TAG, TimeUnit.MINUTES.toMinutes(1440),
                getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES));
        StockSyncIntentServiceJob.scheduleJob(StockSyncIntentServiceJob .TAG, TimeUnit.MINUTES.toMinutes(1440),
                getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES));

        SyncAnnualReportWorker.scheduleMonthly();

        RecurringServiceJob.scheduleJob(RecurringServiceJob.TAG,
                TimeUnit.MINUTES.toMinutes(BuildConfig.VACCINE_SYNC_PROCESSING_MINUTES),
                getFlexValue(BuildConfig.VACCINE_SYNC_PROCESSING_MINUTES));

        ZeirHIA2IntentServiceJob.scheduleJob(ZeirHIA2IntentServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.DATA_SYNC_DURATION_MINUTES),
                getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES));
    }

    @Override
    protected void scheduleJobsImmediately() {
        SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
        SyncAllLocationsServiceJob.scheduleJobImmediately(SyncAllLocationsServiceJob.TAG);
        PullUniqueIdsServiceJob.scheduleJobImmediately(PullUniqueIdsServiceJob.TAG);
        ZScoreRefreshIntentServiceJob.scheduleJobImmediately(ZScoreRefreshIntentServiceJob.TAG);
        ImageUploadServiceJob.scheduleJobImmediately(ImageUploadServiceJob.TAG);
        ArchiveClientsJob.scheduleJobImmediately(ArchiveClientsJob.TAG);
        RecurringServiceJob.scheduleJobImmediately(RecurringServiceJob.TAG);
        DropoutIntentServiceJob.scheduleJobImmediately(DropoutIntentServiceJob.TAG);
        StockSyncIntentServiceJob.scheduleJobImmediately(StockSyncIntentServiceJob.TAG);
        ZeirHIA2IntentServiceJob.scheduleJobImmediately(ZeirHIA2IntentServiceJob.TAG);
    }
}
