package org.smartregister.path.job;

import android.content.Intent;

import androidx.annotation.NonNull;

import org.smartregister.immunization.job.VaccineSchedulesUpdateJob;
import org.smartregister.immunization.util.IMConstants;
import org.smartregister.path.service.intent.AppVaccineScheduleUpdateIntentService;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2020-02-13
 */

public class AppVaccineUpdateJob extends VaccineSchedulesUpdateJob {

    @NonNull
    @Override
    protected String getClientTableName() {
        return "ec_client";
    }

    @NonNull
    @Override
    protected DailyJobResult onRunDailyJob(@NonNull Params params) {
        Intent intent = new Intent(getContext(), AppVaccineScheduleUpdateIntentService.class);
        intent.putExtra(IMConstants.IntentKey.VaccineScheduleUpdateIntentService.CLIENT_TABLE_NAME, getClientTableName());
        getContext().startService(intent);
        updateLastTimeRun();
        return DailyJobResult.SUCCESS;
    }
}
