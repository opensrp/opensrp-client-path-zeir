package org.smartregister.uniceftunisia.service.intent;

import android.content.Intent;

import androidx.annotation.Nullable;

import org.smartregister.immunization.domain.VaccinationClient;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.service.intent.VaccineSchedulesUpdateIntentService;
import org.smartregister.immunization.util.IMConstants;
import org.smartregister.uniceftunisia.util.AppConstants;
import org.smartregister.uniceftunisia.util.VaccineUtils;

import java.util.ArrayList;

public class AppVaccineScheduleUpdateIntentService extends VaccineSchedulesUpdateIntentService {

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String tableName = intent != null ? intent.getStringExtra(IMConstants.IntentKey.VaccineScheduleUpdateIntentService.CLIENT_TABLE_NAME)
                : AppConstants.TABLE_NAME.ALL_CLIENTS;

        sendLocalBroadcast(IMConstants.BroadcastAction.VaccineScheduleUpdate.SERVICE_STARTED);

        int page = 0;
        ArrayList<VaccinationClient> vaccinationClients;
        do {
            vaccinationClients = getClients(tableName != null ? tableName :  AppConstants.TABLE_NAME.ALL_CLIENTS, page);

            for (VaccinationClient vaccinationClient : vaccinationClients) {
                VaccineUtils.refreshImmunizationSchedules(vaccinationClient.getBaseEntityId());
                VaccineSchedule.updateOfflineAlerts(vaccinationClient.getBaseEntityId(), vaccinationClient.getBirthDateTime(), AppConstants.KEY.CHILD);
            }

            page++;
        } while (!vaccinationClients.isEmpty());

        sendLocalBroadcast(IMConstants.BroadcastAction.VaccineScheduleUpdate.SERVICE_FINISHED);
    }
}
