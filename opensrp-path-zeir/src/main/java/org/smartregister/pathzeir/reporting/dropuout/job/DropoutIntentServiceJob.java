package org.smartregister.pathzeir.reporting.dropuout.job;

import android.content.Intent;

import androidx.annotation.NonNull;

import org.smartregister.job.BaseJob;
import org.smartregister.pathzeir.reporting.dropuout.intent.CoverageDropoutIntentService;

public class DropoutIntentServiceJob extends BaseJob {

    public static final String TAG = "DropoutIntentServiceJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getContext(), CoverageDropoutIntentService.class);
        getContext().startService(intent);
        return Result.SUCCESS;
    }
}
