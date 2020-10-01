package org.smartregister.uniceftunisia.job;

import androidx.annotation.NonNull;

import org.smartregister.immunization.job.VaccineSchedulesUpdateJob;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2020-02-13
 */

public class AppVaccineUpdateJob extends VaccineSchedulesUpdateJob {

    @NonNull
    @Override
    protected String getClientTableName() {
        return "ec_client";
    }
}
