package org.smartregister.uniceftunisia.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

import org.smartregister.growthmonitoring.job.HeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.WeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.ZScoreRefreshIntentServiceJob;
import org.smartregister.immunization.job.RecurringServiceJob;
import org.smartregister.immunization.job.VaccineServiceJob;
import org.smartregister.job.ExtendedSyncServiceJob;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.job.SyncSettingsServiceJob;
import org.smartregister.job.ValidateSyncDataServiceJob;
import org.smartregister.reporting.job.RecurringIndicatorGeneratingJob;
import org.smartregister.sync.intent.SyncIntentService;

public class AppJobCreator implements JobCreator {
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case SyncServiceJob.TAG:
                return new SyncServiceJob(SyncIntentService.class);
            case ExtendedSyncServiceJob.TAG:
                return new ExtendedSyncServiceJob();
            case PullUniqueIdsServiceJob.TAG:
                return new PullUniqueIdsServiceJob();
            case ValidateSyncDataServiceJob.TAG:
                return new ValidateSyncDataServiceJob();
            case VaccineServiceJob.TAG:
                return new VaccineServiceJob();
            case RecurringServiceJob.TAG:
                return new RecurringServiceJob();
            case WeightIntentServiceJob.TAG:
                return new WeightIntentServiceJob();
            case HeightIntentServiceJob.TAG:
                return new HeightIntentServiceJob();
            case ZScoreRefreshIntentServiceJob.TAG:
                return new ZScoreRefreshIntentServiceJob();
            case SyncSettingsServiceJob.TAG:
                return new SyncSettingsServiceJob();
            case RecurringIndicatorGeneratingJob.TAG:
                return new RecurringIndicatorGeneratingJob();
            case AppVaccineUpdateJob.TAG:
                return new AppVaccineUpdateJob();
            case AppVaccineUpdateJob.SCHEDULE_ADHOC_TAG:
                return new AppVaccineUpdateJob();

            case ImageUploadServiceJob.TAG:
                return new ImageUploadServiceJob();

            default:
                Log.w(AppJobCreator.class.getCanonicalName(), tag + " is not declared in Job Creator");
                return null;
        }
    }
}