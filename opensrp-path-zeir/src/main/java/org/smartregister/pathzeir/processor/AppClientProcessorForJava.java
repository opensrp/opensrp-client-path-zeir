package org.smartregister.pathzeir.processor;

import android.content.ContentValues;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.smartregister.CoreLibrary;
import org.smartregister.child.util.ChildDbUtils;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.MoveToMyCatchmentUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.domain.jsonmapping.ClientField;
import org.smartregister.domain.jsonmapping.Column;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.growthmonitoring.service.intent.HeightIntentService;
import org.smartregister.growthmonitoring.service.intent.WeightIntentService;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.ServiceSchedule;
import org.smartregister.immunization.domain.ServiceType;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.service.intent.RecurringIntentService;
import org.smartregister.immunization.service.intent.VaccineIntentService;
import org.smartregister.pathzeir.application.ZeirApplication;
import org.smartregister.pathzeir.reporting.common.ReportExtensionsKt;
import org.smartregister.pathzeir.reporting.common.ReportingUtils;
import org.smartregister.pathzeir.util.AppConstants;
import org.smartregister.pathzeir.util.AppExecutors;
import org.smartregister.pathzeir.util.AppUtils;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.MiniClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class AppClientProcessorForJava extends ClientProcessorForJava {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final HashMap<String, MiniClientProcessorForJava> processorMap = new HashMap<>();
    private final HashMap<MiniClientProcessorForJava, List<Event>> unsyncEventsPerProcessor = new HashMap<>();
    private final AppExecutors appExecutors = new AppExecutors();
    private final HashMap<String, DateTime> clientsForAlertUpdates = new HashMap<>();

    public AppClientProcessorForJava(Context context) {
        super(context);
    }

    public void addMiniProcessors(MiniClientProcessorForJava... miniClientProcessorsForJava) {
        for (MiniClientProcessorForJava miniClientProcessorForJava : miniClientProcessorsForJava) {
            unsyncEventsPerProcessor.put(miniClientProcessorForJava, new ArrayList<>());

            HashSet<String> eventTypes = miniClientProcessorForJava.getEventTypes();

            for (String eventType : eventTypes) {
                processorMap.put(eventType, miniClientProcessorForJava);
            }
        }
    }

    @Override
    public void processClient(List<EventClient> eventClients) {
        ClientClassification clientClassification = assetJsonToJava("ec_client_classification.json",
                ClientClassification.class);
        Table vaccineTable = assetJsonToJava("ec_client_vaccine.json", Table.class);
        Table weightTable = assetJsonToJava("ec_client_weight.json", Table.class);
        Table heightTable = assetJsonToJava("ec_client_height.json", Table.class);
        Table serviceTable = assetJsonToJava("ec_client_service.json", Table.class);

        if (!eventClients.isEmpty()) {
            List<Event> eventsToRemove = new ArrayList<>();
            for (EventClient eventClient : eventClients) {
                Event event = eventClient.getEvent();
                if (event == null) {
                    return;
                }

                String eventType = event.getEventType();

                if (eventType == null || clientClassification == null) {
                    continue;
                }

                switch (eventType) {
                    case RecurringIntentService.EVENT_TYPE:
                        processService(eventClient, serviceTable);
                        break;
                    case AppConstants.EventTypeConstants.CARD_STATUS_UPDATE:
                        processCardStatusUpdateEvent(event);
                        break;
                    case ReportExtensionsKt.MONTHLY_REPORT:
                        ReportingUtils.processMonthlyReportEvent(event);
                        CoreLibrary.getInstance().context().getEventClientRepository()
                                .markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
                        break;
                    case ReportExtensionsKt.ANNUAL_VACCINE_REPORT:
                        ReportingUtils.processAnnualVaccineReportEvent(event);
                        CoreLibrary.getInstance().context().getEventClientRepository()
                                .markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
                        break;
                    case ReportExtensionsKt.VACCINE_COVERAGE_TARGET:
                        ReportingUtils.processCoverageTarget(event);
                        CoreLibrary.getInstance().context().getEventClientRepository()
                                .markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
                        break;
                    case VaccineIntentService.EVENT_TYPE:
                    case VaccineIntentService.EVENT_TYPE_OUT_OF_CATCHMENT:
                        processVaccinationEvent(vaccineTable, eventClient, event, eventType);
                        break;
                    case WeightIntentService.EVENT_TYPE:
                    case WeightIntentService.EVENT_TYPE_OUT_OF_CATCHMENT:
                        processWeightAndHeightEvent(weightTable, heightTable, eventClient, eventType);
                        break;
                    case ChildJsonFormUtils.BCG_SCAR_EVENT:
                        processBCGScarEvent(eventClient);
                        break;
                    case MoveToMyCatchmentUtils.MOVE_TO_CATCHMENT_EVENT:
                        eventsToRemove.add(event);
                        break;
                    case Constants.EventType.ARCHIVE_CHILD_RECORD:
                    case Constants.EventType.DEATH:
                        if (eventClient.getClient() != null) {
                            processEventClient(clientClassification, eventClient, event);
                            if (eventType.equalsIgnoreCase(Constants.EventType.DEATH) &&
                                    eventClient.getEvent().getEntityType().equals(AppConstants.EntityTypeConstants.CHILD)) {
                                AppUtils.updateChildDeath(eventClient);
                            }
                        }
                        break;
                    case Constants.EventType.DYNAMIC_VACCINES:
                        processEventClient(clientClassification, eventClient, event);
                        break;
                    case Constants.EventType.BITRH_REGISTRATION:
                    case Constants.EventType.UPDATE_BITRH_REGISTRATION:
                    case Constants.EventType.NEW_WOMAN_REGISTRATION:
                    case Constants.EventType.UPDATE_MOTHER_DETAILS:
                        if (eventClient.getClient() == null) continue;
                        if (eventType.equals(Constants.EventType.BITRH_REGISTRATION))
                            getApplication().registerTypeRepository().add(AppConstants.RegisterType.CHILD, event.getBaseEntityId());
                        processChildRegistrationAndRelatedEvents(clientClassification, eventClient, event);
                        break;
                    default:
                        if (processorMap.containsKey(eventType)) {
                            try {
                                processEventUsingMiniprocessor(clientClassification, eventClient, eventType);
                            } catch (Exception ex) {
                                Timber.e(ex);
                            }
                        }
                }
            }

            // Process alerts for clients
            Runnable runnable = () -> updateClientAlerts(clientsForAlertUpdates);
            appExecutors.diskIO().execute(runnable);
            // Unsync events that are should not be in this device
            unSync(eventsToRemove);
        }
    }

    private void processService(EventClient service, Table serviceTable) {
        try {
            if (service == null || service.getEvent() == null) {
                return;
            }

            if (serviceTable == null) {
                return;
            }

            Timber.d("Starting processService table: %s", serviceTable.name);

            ContentValues contentValues = processCaseModel(service, serviceTable);

            // save the values to db
            if (contentValues != null && contentValues.size() > 0) {
                String name = getServiceTypeName(contentValues);

                String eventDateStr = contentValues.getAsString(RecurringServiceRecordRepository.DATE);
                Date date = getDate(eventDateStr);

                String value = null;

                if (StringUtils.containsIgnoreCase(name, "ITN")) {
                    String itnDateString = contentValues.getAsString("itn_date");
                    if (StringUtils.isNotBlank(itnDateString)) {
                        date = simpleDateFormat.parse(itnDateString);
                    }

                    value = getServiceValue(contentValues);

                }

                List<ServiceType> serviceTypeList = getServiceTypes(name);
                if (serviceTypeList == null || serviceTypeList.isEmpty()) {
                    return;
                }

                if (date == null) {
                    return;
                }

                recordServiceRecord(service, contentValues, name, date, value, serviceTypeList);

                Timber.d("Ending processService table: %s", serviceTable.name);
            }

        } catch (Exception e) {
            Timber.e(e, "Process Service Error");
        }
    }


    @NotNull
    private String getServiceValue(ContentValues contentValues) {
        String value;
        value = RecurringIntentService.ITN_PROVIDED;
        if (contentValues.getAsString("itn_has_net") != null) {
            value = RecurringIntentService.CHILD_HAS_NET;
        }
        return value;
    }

    @Nullable
    private String getServiceTypeName(ContentValues contentValues) {
        String name = contentValues.getAsString(RecurringServiceTypeRepository.NAME);
        if (StringUtils.isNotBlank(name)) {
            name = name.replaceAll("_", " ").replace("dose", "").trim();
        }
        return name;
    }

    private void recordServiceRecord(EventClient service, ContentValues contentValues, String name,
                                     Date date, String value, List<ServiceType> serviceTypeList) {
        RecurringServiceRecordRepository recurringServiceRecordRepository = ZeirApplication.getInstance()
                .getRecurringServiceRecordRepository();
        ServiceRecord serviceObj = getServiceRecord(service, contentValues, name, date, value, serviceTypeList);
        String createdAtString = contentValues.getAsString(RecurringServiceRecordRepository.CREATED_AT);
        Date createdAt = getDate(createdAtString);
        serviceObj.setCreatedAt(createdAt);
        recurringServiceRecordRepository.add(serviceObj);
    }

    @NotNull
    private ServiceRecord getServiceRecord(EventClient service, ContentValues contentValues, String name, Date date,
                                           String value, List<ServiceType> serviceTypeList) {
        ServiceRecord serviceObj = new ServiceRecord();
        serviceObj.setBaseEntityId(contentValues.getAsString(RecurringServiceRecordRepository.BASE_ENTITY_ID));
        serviceObj.setName(name);
        serviceObj.setDate(date);
        serviceObj.setAnmId(contentValues.getAsString(RecurringServiceRecordRepository.ANMID));
        serviceObj.setLocationId(contentValues.getAsString(RecurringServiceRecordRepository.LOCATION_ID));
        serviceObj.setSyncStatus(RecurringServiceRecordRepository.TYPE_Synced);
        serviceObj.setFormSubmissionId(service.getEvent().getFormSubmissionId());
        serviceObj.setEventId(service.getEvent().getEventId()); //FIXME hard coded id
        serviceObj.setValue(value);
        serviceObj.setRecurringServiceId(serviceTypeList.get(0).getId());
        return serviceObj;
    }

    private List<ServiceType> getServiceTypes(String name) {
        RecurringServiceTypeRepository recurringServiceTypeRepository = ZeirApplication.getInstance()
                .getRecurringServiceTypeRepository();
        return recurringServiceTypeRepository.searchByName(name);
    }

    private void processCardStatusUpdateEvent(Event event) {
        if (event != null && StringUtils.isNotBlank(event.getBaseEntityId())) {
            Map<String, String> eventDetails = event.getDetails();
            String cardStatus = eventDetails.get(AppConstants.KeyConstants.CARD_STATUS);
            String cardStatusDate = eventDetails.get(AppConstants.KeyConstants.CARD_STATUS_DATE);
            if (StringUtils.isNotBlank(cardStatus) && StringUtils.isNotBlank(cardStatusDate)) {
                CommonRepository commonrepository = getApplication().context().commonrepository(AppConstants.TableNameConstants.CHILD_DETAILS);
                ContentValues contentValues = new ContentValues();
                contentValues.put(AppConstants.KeyConstants.CARD_STATUS, cardStatus);
                contentValues.put(AppConstants.KeyConstants.CARD_STATUS_DATE, cardStatusDate);
                commonrepository.updateColumn(AppConstants.TableNameConstants.CHILD_DETAILS, contentValues, event.getBaseEntityId());
            }
        }
    }

    private void unSync(List<Event> events) {
        try {
            ClientField clientField = assetJsonToJava("ec_client_fields.json", ClientField.class);
            if (events == null || events.isEmpty() || clientField == null) {
                return;
            }

            for (Event event : events) {
                unSync(clientField.bindobjects, event);
            }

        } catch (Exception e) {
            Timber.e(e, e.toString());
        }
    }

    private void unSync(List<Table> bindObjects, Event event) {
        try {
            String baseEntityId = event.getBaseEntityId();

            if (event.getProviderId().equals(ZeirApplication.getInstance()
                    .context().allSharedPreferences().fetchRegisteredANM())) {

                ECSyncHelper.getInstance(getContext()).deleteClient(baseEntityId);

                for (Table bindObject : bindObjects) {
                    String tableName = bindObject.name;
                    deleteCase(tableName, baseEntityId);
                    removeSearchRecord(tableName, baseEntityId);
                }
            }
        } catch (Exception e) {
            Timber.e(e, e.toString());
        }
    }

    public void removeSearchRecord(String bindTable, String baseEntityId) {
        String ftsSearchTable = bindTable + "_search";
        SQLiteDatabase db = ZeirApplication.getInstance().getRepository().getWritableDatabase();
        if (tableExists(db, ftsSearchTable)) {
            String query = AppConstants.KeyConstants.OBJECT_ID + " = ?";
            db.delete(ftsSearchTable, query, new String[]{baseEntityId});
        }
    }

    private boolean tableExists(SQLiteDatabase db, String tableName) {
        if (tableName == null || db == null || !db.isOpen()) {
            return false;
        }
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?",
                new String[]{"table", tableName}
        );
        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    private void processEventClient(@NonNull ClientClassification clientClassification, @NonNull EventClient eventClient, @NonNull Event event) {
        Client client = eventClient.getClient();
        if (client != null) {
            try {
                processEvent(event, client, clientClassification);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    private void updateClientAlerts(@NonNull HashMap<String, DateTime> clientsForAlertUpdates) {
        HashMap<String, DateTime> stringDateTimeHashMap = new HashMap<>(clientsForAlertUpdates);
        for (String baseEntityId : stringDateTimeHashMap.keySet()) {
            DateTime birthDateTime = clientsForAlertUpdates.get(baseEntityId);
            if (birthDateTime != null) {
                VaccineSchedule.updateOfflineAlerts(baseEntityId, birthDateTime, AppConstants.KeyConstants.CHILD);
                ServiceSchedule.updateOfflineAlerts(baseEntityId, birthDateTime);
            }
        }
        clientsForAlertUpdates.clear();
    }

    private void processChildRegistrationAndRelatedEvents(@NonNull ClientClassification clientClassification, @NonNull EventClient eventClient, @NonNull Event event) {
        Client client = eventClient.getClient();
        if (client != null) {
            try {
                processEvent(event, client, clientClassification);
                scheduleUpdatingClientAlerts(client.getBaseEntityId(), client.getBirthdate());
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    private void processEventUsingMiniprocessor(ClientClassification clientClassification, EventClient eventClient, String eventType) throws Exception {
        MiniClientProcessorForJava miniClientProcessorForJava = processorMap.get(eventType);
        if (miniClientProcessorForJava != null) {
            List<Event> processorUnsyncEvents = unsyncEventsPerProcessor.get(miniClientProcessorForJava);
            if (processorUnsyncEvents == null) {
                processorUnsyncEvents = new ArrayList<>();
                unsyncEventsPerProcessor.put(miniClientProcessorForJava, processorUnsyncEvents);
            }

            miniClientProcessorForJava.processEventClient(eventClient, processorUnsyncEvents, clientClassification);
        }
    }

    private void processWeightAndHeightEvent(Table weightTable, Table heightTable, EventClient eventClient, String eventType) {
        if (weightTable == null || heightTable == null) {
            return;
        }

        processWeight(eventClient, weightTable, eventType.equals(WeightIntentService.EVENT_TYPE_OUT_OF_CATCHMENT));
        processHeight(eventClient, heightTable, eventType.equals(HeightIntentService.EVENT_TYPE_OUT_OF_CATCHMENT));
    }

    private void processVaccinationEvent(Table vaccineTable, EventClient eventClient, Event event, String eventType) {
        Client client = eventClient.getClient();
        if (vaccineTable == null || client == null) {
            return;
        }

        if (!childExists(client.getBaseEntityId())) {
            List<String> createCase = new ArrayList<>();
            createCase.add(Utils.metadata().childRegister.tableName);
            processCaseModel(event, client, createCase);
        }

        processVaccine(eventClient, vaccineTable, eventType.equals(VaccineIntentService.EVENT_TYPE_OUT_OF_CATCHMENT));
        scheduleUpdatingClientAlerts(client.getBaseEntityId(), client.getBirthdate());
    }

    private boolean childExists(String entityId) {
        return getApplication().eventClientRepository().checkIfExists(EventClientRepository.Table.client, entityId);
    }

    private void processVaccine(@Nullable EventClient vaccine, @Nullable Table vaccineTable, boolean outOfCatchment) {
        try {
            if (vaccine == null || vaccine.getEvent() == null || vaccineTable == null) {
                return;
            }

            Timber.i("Starting processVaccine table: %s", vaccineTable.name);

            ContentValues contentValues = processCaseModel(vaccine, vaccineTable);

            // save the values to db
            if (contentValues != null && contentValues.size() > 0) {
                SimpleDateFormat simpleDateFormat = this.simpleDateFormat;
                Date date = simpleDateFormat.parse(contentValues.getAsString(VaccineRepository.DATE));

                VaccineRepository vaccineRepository = getApplication().vaccineRepository();
                Vaccine vaccineObj = new Vaccine();
                vaccineObj.setBaseEntityId(contentValues.getAsString(VaccineRepository.BASE_ENTITY_ID));
                vaccineObj.setName(contentValues.getAsString(VaccineRepository.NAME));
                if (contentValues.containsKey(VaccineRepository.CALCULATION)) {
                    vaccineObj.setCalculation(parseInt(contentValues.getAsString(VaccineRepository.CALCULATION)));
                }
                vaccineObj.setDate(date);
                vaccineObj.setAnmId(contentValues.getAsString(VaccineRepository.ANMID));
                vaccineObj.setLocationId(contentValues.getAsString(VaccineRepository.LOCATION_ID));
                vaccineObj.setSyncStatus(VaccineRepository.TYPE_Synced);
                vaccineObj.setFormSubmissionId(vaccine.getEvent().getFormSubmissionId());
                vaccineObj.setEventId(vaccine.getEvent().getEventId());
                vaccineObj.setOutOfCatchment(outOfCatchment ? 1 : 0);

                String createdAtString = contentValues.getAsString(VaccineRepository.CREATED_AT);
                Date createdAt = getDate(createdAtString);
                vaccineObj.setCreatedAt(createdAt);

                Utils.addVaccine(vaccineRepository, vaccineObj);

                Timber.i("Ending processVaccine table: %s", vaccineTable.name);
            }

        } catch (Exception e) {
            Timber.e(e, "Process Vaccine Error");
        }
    }

    private void processWeight(EventClient weight, Table weightTable, boolean outOfCatchment) {

        try {

            if (weight == null || weight.getEvent() == null || weightTable == null) {
                return;
            }

            Timber.i("Starting processWeight table: %s", weightTable.name);

            ContentValues contentValues = processCaseModel(weight, weightTable);

            // save the values to db
            if (contentValues != null && contentValues.size() > 0) {
                String eventDateStr = contentValues.getAsString(WeightRepository.DATE);
                Date date = getDate(eventDateStr);

                WeightRepository weightRepository = getApplication().weightRepository();
                Weight weightObj = new Weight();
                weightObj.setBaseEntityId(contentValues.getAsString(WeightRepository.BASE_ENTITY_ID));
                if (contentValues.containsKey(WeightRepository.KG)) {
                    weightObj.setKg(parseFloat(contentValues.getAsString(WeightRepository.KG)));
                }
                weightObj.setDate(date);
                weightObj.setAnmId(contentValues.getAsString(WeightRepository.ANMID));
                weightObj.setLocationId(contentValues.getAsString(WeightRepository.LOCATIONID));
                weightObj.setSyncStatus(WeightRepository.TYPE_Synced);
                weightObj.setFormSubmissionId(weight.getEvent().getFormSubmissionId());
                weightObj.setEventId(weight.getEvent().getEventId());
                weightObj.setOutOfCatchment(outOfCatchment ? 1 : 0);

                if (contentValues.containsKey(WeightRepository.Z_SCORE)) {
                    String zscoreString = contentValues.getAsString(WeightRepository.Z_SCORE);
                    if (NumberUtils.isNumber(zscoreString)) {
                        weightObj.setZScore(Double.valueOf(zscoreString));
                    }
                }

                String createdAtString = contentValues.getAsString(WeightRepository.CREATED_AT);
                Date createdAt = getDate(createdAtString);
                weightObj.setCreatedAt(createdAt);

                weightRepository.add(weightObj);

                Timber.i("Ending processWeight table: %s", weightTable.name);
            }

        } catch (Exception e) {
            Timber.e(e, "Process Weight Error");
        }
    }

    public ZeirApplication getApplication() {
        return ZeirApplication.getInstance();
    }

    private void processHeight(@Nullable EventClient height, @Nullable Table heightTable, boolean outOfCatchment) {

        try {

            if (height == null || height.getEvent() == null || heightTable == null) {
                return;
            }

            Timber.i("Starting processWeight table: %s", heightTable.name);

            ContentValues contentValues = processCaseModel(height, heightTable);

            // save the values to db
            if (contentValues != null && contentValues.size() > 0) {
                String eventDateStr = contentValues.getAsString(HeightRepository.DATE);
                Date date = getDate(eventDateStr);

                HeightRepository heightRepository = getApplication().heightRepository();
                Height heightObject = new Height();
                heightObject.setBaseEntityId(contentValues.getAsString(WeightRepository.BASE_ENTITY_ID));
                if (contentValues.containsKey(HeightRepository.CM)) {
                    heightObject.setCm(parseFloat(contentValues.getAsString(HeightRepository.CM)));
                }
                heightObject.setDate(date);
                heightObject.setAnmId(contentValues.getAsString(HeightRepository.ANMID));
                heightObject.setLocationId(contentValues.getAsString(HeightRepository.LOCATIONID));
                heightObject.setSyncStatus(HeightRepository.TYPE_Synced);
                heightObject.setFormSubmissionId(height.getEvent().getFormSubmissionId());
                heightObject.setEventId(height.getEvent().getEventId());
                heightObject.setOutOfCatchment(outOfCatchment ? 1 : 0);

                if (contentValues.containsKey(HeightRepository.Z_SCORE)) {
                    String zScoreString = contentValues.getAsString(HeightRepository.Z_SCORE);
                    if (NumberUtils.isNumber(zScoreString)) {
                        heightObject.setZScore(Double.valueOf(zScoreString));
                    }
                }

                String createdAtString = contentValues.getAsString(HeightRepository.CREATED_AT);
                Date createdAt = getDate(createdAtString);
                heightObject.setCreatedAt(createdAt);

                heightRepository.add(heightObject);

                Timber.i("Ending processWeight table: %s", heightTable.name);
            }

        } catch (Exception e) {
            Timber.e(e, "Process Height Error");
        }
    }

    private void processBCGScarEvent(EventClient bcgScarEventClient) {
        if (bcgScarEventClient == null || bcgScarEventClient.getEvent() == null) {
            return;
        }

        Event event = bcgScarEventClient.getEvent();
        String baseEntityId = event.getBaseEntityId();
        DateTime eventDate = event.getEventDate();
        long date = 0;
        if (eventDate != null) {
            date = eventDate.getMillis();
        }

        ChildDbUtils.updateChildDetailsValue(Constants.SHOW_BCG_SCAR, String.valueOf(date), baseEntityId);
    }

    @VisibleForTesting
    ContentValues processCaseModel(EventClient eventClient, Table table) {
        try {
            List<Column> columns = table.columns;
            ContentValues contentValues = new ContentValues();

            for (Column column : columns) {
                processCaseModel(eventClient.getEvent(), eventClient.getClient(), column, contentValues);
            }

            return contentValues;
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    private Integer parseInt(String string) {
        try {
            return Integer.valueOf(string);
        } catch (NumberFormatException e) {
            Timber.e(e, e.toString());
        }
        return null;
    }

    @Nullable
    private Date getDate(@Nullable String eventDateStr) {
        Date date = null;
        if (StringUtils.isNotBlank(eventDateStr)) {
            try {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.getDefault());
                date = dateFormat.parse(eventDateStr);
            } catch (ParseException e) {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
                    date = dateFormat.parse(eventDateStr);
                } catch (ParseException pe) {
                    try {
                        date = DateUtil.parseDate(eventDateStr);
                    } catch (ParseException pee) {
                        Timber.e(e);
                    }
                }
            }
        }
        return date;
    }

    private Float parseFloat(String string) {
        try {
            return Float.valueOf(string);
        } catch (NumberFormatException e) {
            Timber.e(e);
        }
        return null;
    }

    @Override
    public void updateFTSsearch(String tableName, String entityId, ContentValues contentValues) {

        Timber.i("Starting updateFTSsearch table: %s", tableName);

        AllCommonsRepository allCommonsRepository = getApplication().context().
                allCommonsRepositoryobjects(tableName);

        if (allCommonsRepository != null) {
            allCommonsRepository.updateSearch(entityId);
        }

        // Todo: Disable this in favour of the vaccine post-processing at the end :shrug: Might not be the best for real-time updates to the register
        if (contentValues != null && AppConstants.TableNameConstants.ALL_CLIENTS.equals(tableName)) {
            String dobString = contentValues.getAsString(Constants.KEY.DOB);
            // TODO: Fix this to use the ec_child_details table & fetch the birthDateTime from the ec_client table
            if (StringUtils.isNotBlank(dobString)) {
                DateTime birthDateTime = Utils.dobStringToDateTime(dobString);
                if (birthDateTime != null) {
                    VaccineSchedule.updateOfflineAlerts(entityId, birthDateTime, "child");
                    ServiceSchedule.updateOfflineAlerts(entityId, birthDateTime);
                }
            }
        }

        Timber.i("Finished updateFTSsearch table: %s", tableName);
    }

    @Override
    public String[] getOpenmrsGenIds() {
        return new String[]{AppConstants.KeyConstants.ZEIR_ID};
    }

    private void scheduleUpdatingClientAlerts(@NonNull String baseEntityId, @NonNull DateTime dateTime) {
        if (!clientsForAlertUpdates.containsKey(baseEntityId)) {
            clientsForAlertUpdates.put(baseEntityId, dateTime);
        }
    }
}