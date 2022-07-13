package org.smartregister.path.service.intent;

import android.content.Intent;

import androidx.annotation.Nullable;

import org.joda.time.DateTime;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Client;
import org.smartregister.immunization.domain.ServiceSchedule;
import org.smartregister.immunization.domain.VaccinationClient;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.service.intent.VaccineSchedulesUpdateIntentService;
import org.smartregister.immunization.util.IMConstants;
import org.smartregister.path.util.AppConstants;

import java.util.ArrayList;

public class AppVaccineScheduleUpdateIntentService extends VaccineSchedulesUpdateIntentService {

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String tableName = intent != null ? intent.getStringExtra(IMConstants.IntentKey.VaccineScheduleUpdateIntentService.CLIENT_TABLE_NAME)
                : AppConstants.TableNameConstants.ALL_CLIENTS;

        sendLocalBroadcast(IMConstants.BroadcastAction.VaccineScheduleUpdate.SERVICE_STARTED);

        int page = 0;
        ArrayList<VaccinationClient> vaccinationClients;
        do {
            vaccinationClients = getClients(tableName != null ? tableName : AppConstants.TableNameConstants.ALL_CLIENTS, page);

            for (VaccinationClient vaccinationClient : vaccinationClients) {
                DateTime birthDateTime = vaccinationClient.getBirthDateTime();
                String baseEntityId = vaccinationClient.getBaseEntityId();

                Client client = CoreLibrary.getInstance().context().getEventClientRepository().fetchClientByBaseEntityId(baseEntityId);
                if (AppConstants.EntityTypeConstants.CHILD.equals(client.getClientType()) || client.getRelationships() != null) {

                    VaccineSchedule.updateOfflineAlertsOnly(baseEntityId, birthDateTime, IMConstants.VACCINE_TYPE.CHILD);
                    ServiceSchedule.updateOfflineAlerts(baseEntityId, birthDateTime);
                }
            }

            page++;
        } while (!vaccinationClients.isEmpty());

        sendLocalBroadcast(IMConstants.BroadcastAction.VaccineScheduleUpdate.SERVICE_FINISHED);
    }
}
