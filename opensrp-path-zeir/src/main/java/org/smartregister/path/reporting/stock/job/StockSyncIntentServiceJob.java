package org.smartregister.path.reporting.stock.job;

import android.content.Intent;

import androidx.annotation.NonNull;

import org.smartregister.job.BaseJob;
import org.smartregister.stock.sync.StockSyncIntentService;

public class StockSyncIntentServiceJob extends BaseJob {

    public static final String TAG = "StockSyncIntentServiceJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getContext(), StockSyncIntentService.class);
        getContext().startService(intent);
        return Result.SUCCESS;
    }
}
