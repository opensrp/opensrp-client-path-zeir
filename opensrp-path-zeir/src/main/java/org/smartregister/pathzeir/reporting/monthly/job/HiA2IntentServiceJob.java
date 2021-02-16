package org.smartregister.pathzeir.reporting.monthly.job;

import android.content.Intent;

import androidx.annotation.NonNull;

import org.smartregister.job.BaseJob;
import org.smartregister.pathzeir.reporting.monthly.intent.HIA2IntentService;

public class HiA2IntentServiceJob extends BaseJob {

    public static final String TAG = "HiA2IntentServiceJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getContext(), HIA2IntentService.class);
        getContext().startService(intent);
        return Result.SUCCESS;
    }
}
