package org.smartregister.uniceftunisia.interactor;

import org.smartregister.child.job.ArchiveClientsJob;
import org.smartregister.growthmonitoring.job.HeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.WeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.ZScoreRefreshIntentServiceJob;
import org.smartregister.immunization.job.VaccineServiceJob;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncAllLocationsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.login.interactor.BaseLoginInteractor;
import org.smartregister.reporting.job.RecurringIndicatorGeneratingJob;
import org.smartregister.uniceftunisia.BuildConfig;
import org.smartregister.uniceftunisia.job.AppVaccineUpdateJob;
import org.smartregister.uniceftunisia.reporting.annual.coverage.job.SyncAnnualReportWorker;
import org.smartregister.view.contract.BaseLoginContract;

import java.util.concurrent.TimeUnit;

public class LoginInteractor extends BaseLoginInteractor implements BaseLoginContract.Interactor {

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

        SyncServiceJob.scheduleJob(SyncServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.DATA_SYNC_DURATION_MINUTES),
                getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES));

        SyncAllLocationsServiceJob.scheduleJob(SyncAllLocationsServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.DATA_SYNC_DURATION_MINUTES),
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

        SyncAnnualReportWorker.scheduleMonthly();
    }

    @Override
    protected void scheduleJobsImmediately() {
        SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
        SyncAllLocationsServiceJob.scheduleJobImmediately(SyncAllLocationsServiceJob.TAG);
        PullUniqueIdsServiceJob.scheduleJobImmediately(PullUniqueIdsServiceJob.TAG);
        ZScoreRefreshIntentServiceJob.scheduleJobImmediately(ZScoreRefreshIntentServiceJob.TAG);
        ImageUploadServiceJob.scheduleJobImmediately(ImageUploadServiceJob.TAG);
        ArchiveClientsJob.scheduleJobImmediately(ArchiveClientsJob.TAG);
    }
}
