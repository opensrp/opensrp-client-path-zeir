package org.smartregister.pathzeir.application;

import android.content.Intent;
import android.util.Pair;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatDelegate;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.evernote.android.job.JobManager;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.util.ChildAppProperties;
import org.smartregister.child.util.DBConstants;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.growthmonitoring.GrowthMonitoringConfig;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.HeightZScoreRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.growthmonitoring.repository.WeightZScoreRepository;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.jsonmapping.Vaccine;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.pathzeir.BuildConfig;
import org.smartregister.pathzeir.activity.ChildFormActivity;
import org.smartregister.pathzeir.activity.ChildImmunizationActivity;
import org.smartregister.pathzeir.activity.ChildProfileActivity;
import org.smartregister.pathzeir.activity.ChildRegisterActivity;
import org.smartregister.pathzeir.activity.LoginActivity;
import org.smartregister.pathzeir.job.AppJobCreator;
import org.smartregister.pathzeir.processor.AppClientProcessorForJava;
import org.smartregister.pathzeir.reporting.common.ReportIndicatorsProcessor;
import org.smartregister.pathzeir.reporting.dropuout.receiver.CoverageDropoutBroadcastReceiver;
import org.smartregister.pathzeir.reporting.dropuout.repository.CohortIndicatorRepository;
import org.smartregister.pathzeir.reporting.dropuout.repository.CohortPatientRepository;
import org.smartregister.pathzeir.reporting.dropuout.repository.CohortRepository;
import org.smartregister.pathzeir.reporting.dropuout.repository.CumulativeIndicatorRepository;
import org.smartregister.pathzeir.reporting.dropuout.repository.CumulativePatientRepository;
import org.smartregister.pathzeir.reporting.dropuout.repository.CumulativeRepository;
import org.smartregister.pathzeir.reporting.stock.repository.ZeirStockHelperRepository;
import org.smartregister.pathzeir.repository.AppChildRegisterQueryProvider;
import org.smartregister.pathzeir.repository.ChildAlertUpdatedRepository;
import org.smartregister.pathzeir.repository.ClientRegisterTypeRepository;
import org.smartregister.pathzeir.repository.ZeirRepository;
import org.smartregister.pathzeir.util.AppConstants;
import org.smartregister.pathzeir.util.AppUtils;
import org.smartregister.pathzeir.util.VaccineDuplicate;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.reporting.ReportingLibrary;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;
import org.smartregister.stock.StockLibrary;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.receiver.TimeChangedBroadcastReceiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class ZeirApplication extends DrishtiApplication implements TimeChangedBroadcastReceiver.OnTimeChangedListener {

    private static List<VaccineGroup> vaccineGroups;
    private static CommonFtsObject commonFtsObject;
    private static JsonSpecHelper jsonSpecHelper;
    private boolean lastModified;
    private ClientProcessorForJava clientProcessorForJava;
    private EventClientRepository eventClientRepository;
    private ECSyncHelper ecSyncHelper;
    private ClientRegisterTypeRepository registerTypeRepository;
    private ChildAlertUpdatedRepository childAlertUpdatedRepository;
    private CohortRepository cohortRepository;
    private CohortPatientRepository cohortPatientRepository;
    private CohortIndicatorRepository cohortIndicatorRepository;
    private CumulativeRepository cumulativeRepository;
    private CumulativePatientRepository cumulativePatientRepository;
    private CumulativeIndicatorRepository cumulativeIndicatorRepository;

    public static JsonSpecHelper getJsonSpecHelper() {
        return jsonSpecHelper;
    }

    public static CommonFtsObject createCommonFtsObject(android.content.Context context) {
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            for (String ftsTable : commonFtsObject.getTables()) {
                commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
                commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable, context));
            }
        }
        commonFtsObject.updateAlertScheduleMap(getAlertScheduleMap(context));

        return commonFtsObject;
    }

    private static String[] getFtsTables() {
        return new String[]{DBConstants.RegisterTable.CLIENT, DBConstants.RegisterTable.MOTHER_DETAILS, DBConstants.RegisterTable.CHILD_DETAILS};
    }

    private static String[] getFtsSearchFields(String tableName) {
        switch (tableName) {
            case DBConstants.RegisterTable.CLIENT:
                return new String[]{
                        DBConstants.KEY.ZEIR_ID,
                        DBConstants.KEY.FIRST_NAME,
                        DBConstants.KEY.LAST_NAME
                };
            case DBConstants.RegisterTable.CHILD_DETAILS:
                return new String[]{DBConstants.KEY.LOST_TO_FOLLOW_UP, DBConstants.KEY.INACTIVE};
            case DBConstants.RegisterTable.MOTHER_DETAILS:
                return new String[]{AppConstants.KeyConstants.MOTHER_GUARDIAN_NUMBER,};
            default:
                return null;
        }
    }

    private static String[] getFtsSortFields(String tableName, android.content.Context context) {
        switch (tableName) {
            case AppConstants.TableNameConstants.ALL_CLIENTS:
                return Arrays.asList(AppConstants.KeyConstants.FIRST_NAME, AppConstants.KeyConstants.LAST_NAME,
                        AppConstants.KeyConstants.DOB, AppConstants.KeyConstants.ZEIR_ID, AppConstants.KeyConstants.LAST_INTERACTED_WITH,
                        AppConstants.KeyConstants.DOD, AppConstants.KeyConstants.DATE_REMOVED).toArray(new String[0]);
            case DBConstants.RegisterTable.CHILD_DETAILS:
                List<VaccineGroup> vaccineList = VaccinatorUtils.getVaccineGroupsFromVaccineConfigFile(context, VaccinatorUtils.vaccines_file);
                List<String> names = new ArrayList<>();
                names.add(DBConstants.KEY.INACTIVE);
                names.add(AppConstants.KeyConstants.RELATIONAL_ID);
                names.add(DBConstants.KEY.LOST_TO_FOLLOW_UP);

                for (VaccineGroup vaccineGroup : vaccineList) {
                    populateAlertColumnNames(vaccineGroup.vaccines, names);
                }

                return names.toArray(new String[0]);

            default:
                return null;
        }
    }

    private static void populateAlertColumnNames(List<Vaccine> vaccines, List<String> names) {

        for (Vaccine vaccine : vaccines)
            if (vaccine.getVaccineSeparator() != null && vaccine.getName().contains(vaccine.getVaccineSeparator().trim())) {
                String[] individualVaccines = vaccine.getName().split(vaccine.getVaccineSeparator().trim());

                List<Vaccine> vaccineList = new ArrayList<>();
                for (String individualVaccine : individualVaccines) {
                    Vaccine vaccineClone = new Vaccine();
                    vaccineClone.setName(individualVaccine.trim());
                    vaccineList.add(vaccineClone);

                }
                populateAlertColumnNames(vaccineList, names);
            } else {
                names.add("alerts." + VaccinateActionUtils.addHyphen(vaccine.getName()));
            }
    }


    private static void populateAlertScheduleMap(List<Vaccine> vaccines, Map<String, Pair<String, Boolean>> map) {

        for (Vaccine vaccine : vaccines)
            if (vaccine.getVaccineSeparator() != null && vaccine.getName().contains(vaccine.getVaccineSeparator().trim())) {
                String[] individualVaccines = vaccine.getName().split(vaccine.getVaccineSeparator().trim());

                List<Vaccine> vaccineList = new ArrayList<>();
                for (String individualVaccine : individualVaccines) {
                    Vaccine vaccineClone = new Vaccine();
                    vaccineClone.setName(individualVaccine.trim());
                    vaccineList.add(vaccineClone);
                }
                populateAlertScheduleMap(vaccineList, map);

            } else {
                // TODO: This needs to be fixed because it is a configuration & not a hardcoded string
                map.put(vaccine.name, Pair.create(DBConstants.RegisterTable.CHILD_DETAILS, false));
            }
    }

    private static Map<String, Pair<String, Boolean>> getAlertScheduleMap(android.content.Context context) {
        List<VaccineGroup> vaccines = getVaccineGroups(context);

        Map<String, Pair<String, Boolean>> map = new HashMap<>();

        for (VaccineGroup vaccineGroup : vaccines) {
            populateAlertScheduleMap(vaccineGroup.vaccines, map);
        }
        return map;
    }

    public static synchronized ZeirApplication getInstance() {
        return (ZeirApplication) mInstance;
    }

    public static List<VaccineGroup> getVaccineGroups(android.content.Context context) {
        if (vaccineGroups == null) {
            vaccineGroups = VaccinatorUtils.getVaccineGroupsFromVaccineConfigFile(context, VaccinatorUtils.vaccines_file);
        }
        return vaccineGroups;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = Context.getInstance();

        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject(context.applicationContext()));

        //Initialize Modules
        CoreLibrary.init(context, new AppSyncConfiguration(), BuildConfig.BUILD_TIMESTAMP);

        GrowthMonitoringConfig growthMonitoringConfig = new GrowthMonitoringConfig();
        growthMonitoringConfig.setWeightForHeightZScoreFile("weight_for_height.csv");
        GrowthMonitoringLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION, growthMonitoringConfig);
        GrowthMonitoringLibrary.getInstance().setGrowthMonitoringSyncTime(3, TimeUnit.MINUTES);

        ImmunizationLibrary.init(context, getRepository(), createCommonFtsObject(context.applicationContext()),
                BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        ImmunizationLibrary.getInstance().setVaccineSyncTime(3, TimeUnit.MINUTES);
        fixHardcodedVaccineConfiguration();

        ConfigurableViewsLibrary.init(context);
        CoverageDropoutBroadcastReceiver.init(this);

        ChildLibrary.init(context, getRepository(), getMetadata(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        ChildLibrary childLibrary = ChildLibrary.getInstance();
        childLibrary.setApplicationVersionName(BuildConfig.VERSION_NAME);
        childLibrary.setClientProcessorForJava(getClientProcessor());
        childLibrary.getProperties().setProperty(ChildAppProperties.KEY.FEATURE_SCAN_QR_ENABLED, "true");
        childLibrary.setEventBus(EventBus.getDefault());

        ReportingLibrary.init(context, getRepository(), null, BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        ReportingLibrary.getInstance().addMultiResultProcessor(new ReportIndicatorsProcessor());

        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());

        initRepositories();
        initOfflineSchedules();

        SyncStatusBroadcastReceiver.init(this);
        LocationHelper.init(new ArrayList<>(Arrays.asList(BuildConfig.ALLOWED_LEVELS)), BuildConfig.DEFAULT_LOCATION);
        jsonSpecHelper = new JsonSpecHelper(this);

        //init Job Manager
        JobManager.create(this).addJobCreator(new AppJobCreator());
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        StockLibrary.init(context, getRepository(), new ZeirStockHelperRepository(getRepository()));
    }

    private ChildMetadata getMetadata() {
        ChildMetadata metadata = new ChildMetadata(ChildFormActivity.class, ChildProfileActivity.class,
                ChildImmunizationActivity.class, ChildRegisterActivity.class, true, new AppChildRegisterQueryProvider());
        metadata.updateChildRegister(AppConstants.JsonForm.CHILD_ENROLLMENT, AppConstants.TableNameConstants.ALL_CLIENTS,
                AppConstants.TableNameConstants.ALL_CLIENTS, AppConstants.EventTypeConstants.CHILD_REGISTRATION,
                AppConstants.EventTypeConstants.UPDATE_CHILD_REGISTRATION, AppConstants.EventTypeConstants.OUT_OF_CATCHMENT, AppConstants.ConfigurationConstants.CHILD_REGISTER,
                AppConstants.RelationshipConstants.MOTHER, AppConstants.JsonForm.OUT_OF_CATCHMENT_SERVICE);
        metadata.setFieldsWithLocationHierarchy(new HashSet<>(Arrays.asList("home_facility", "birth_facility_name")));
        metadata.setLocationLevels(AppUtils.getLocationLevels());
        metadata.setHealthFacilityLevels(AppUtils.getHealthFacilityLevels());
        return metadata;
    }

    private void initRepositories() {
        weightRepository();
        heightRepository();
        vaccineRepository();
        weightZScoreRepository();
        heightZScoreRepository();
    }

    private void initOfflineSchedules() {
        try {
            List<VaccineGroup> childVaccines = VaccinatorUtils.getSupportedVaccines(this);
            List<Vaccine> specialVaccines = VaccinatorUtils.getSpecialVaccines(this);
            VaccineSchedule.init(childVaccines, specialVaccines, AppConstants.KeyConstants.CHILD);
        } catch (Exception e) {
            Timber.e(e, "ZeirApplication --> initOfflineSchedules");
        }
    }

    public WeightRepository weightRepository() {
        return GrowthMonitoringLibrary.getInstance().weightRepository();
    }

    public HeightRepository heightRepository() {
        return GrowthMonitoringLibrary.getInstance().heightRepository();
    }

    public VaccineRepository vaccineRepository() {
        return ImmunizationLibrary.getInstance().vaccineRepository();
    }

    public WeightZScoreRepository weightZScoreRepository() {
        return GrowthMonitoringLibrary.getInstance().weightZScoreRepository();
    }

    public HeightZScoreRepository heightZScoreRepository() {
        return GrowthMonitoringLibrary.getInstance().heightZScoreRepository();
    }

    @Override
    public void logoutCurrentUser() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new ZeirRepository(getApplicationContext(), context);
            }
        } catch (UnsatisfiedLinkError e) {
            Timber.e(e, "ZeirApplication --> getRepository");
        }
        return repository;
    }

    public Context getContext() {
        return context;
    }

    @NotNull
    @Override
    public ClientProcessorForJava getClientProcessor() {
        if (clientProcessorForJava == null) {
            clientProcessorForJava = new AppClientProcessorForJava(getApplicationContext());
        }
        return clientProcessorForJava;
    }

    @Override
    public void onTerminate() {
        Timber.i("Application is terminating. Stopping sync scheduler and resetting isSyncInProgress setting.");
        cleanUpSyncState();
        TimeChangedBroadcastReceiver.destroy(this);
        SyncStatusBroadcastReceiver.destroy(this);
        super.onTerminate();
    }

    protected void cleanUpSyncState() {
        try {
            DrishtiSyncScheduler.stop(getApplicationContext());
            context.allSharedPreferences().saveIsSyncInProgress(false);
        } catch (Exception e) {
            Timber.e(e, "ZeirApplication --> cleanUpSyncState");
        }
    }

    @Override
    public void onTimeChanged() {
        context.userService().forceRemoteLogin(context().allSharedPreferences().fetchRegisteredANM());
        logoutCurrentUser();
    }

    @Override
    public void onTimeZoneChanged() {
        context.userService().forceRemoteLogin(context().allSharedPreferences().fetchRegisteredANM());
        logoutCurrentUser();
    }

    public Context context() {
        return context;
    }

    public EventClientRepository eventClientRepository() {
        if (eventClientRepository == null) {
            eventClientRepository = new EventClientRepository();
        }
        return eventClientRepository;
    }

    public boolean isLastModified() {
        return lastModified;
    }

    public void setLastModified(boolean lastModified) {
        this.lastModified = lastModified;
    }

    public ECSyncHelper getEcSyncHelper() {
        if (ecSyncHelper == null) {
            ecSyncHelper = ECSyncHelper.getInstance(getApplicationContext());
        }
        return ecSyncHelper;
    }

    @VisibleForTesting
    protected void fixHardcodedVaccineConfiguration() {
        VaccineRepo.Vaccine[] vaccines = ImmunizationLibrary.getInstance().getVaccines(AppConstants.KeyConstants.CHILD);

        HashMap<String, VaccineDuplicate> replacementVaccines = new HashMap<>();
        replacementVaccines.put("BCG 2", new VaccineDuplicate("BCG 2", VaccineRepo.Vaccine.bcg, 1825, 0, 15, AppConstants.KeyConstants.CHILD));

        for (VaccineRepo.Vaccine vaccine : vaccines) {
            if (replacementVaccines.containsKey(vaccine.display())) {
                VaccineDuplicate vaccineDuplicate = replacementVaccines.get(vaccine.display());
                vaccine.setCategory(vaccineDuplicate.category());
                vaccine.setExpiryDays(vaccineDuplicate.expiryDays());
                vaccine.setMilestoneGapDays(vaccineDuplicate.milestoneGapDays());
                vaccine.setPrerequisite(vaccineDuplicate.prerequisite());
                vaccine.setPrerequisiteGapDays(vaccineDuplicate.prerequisiteGapDays());
            }
        }

        ImmunizationLibrary.getInstance().setVaccines(vaccines, AppConstants.KeyConstants.CHILD);
    }


    @VisibleForTesting
    public void setVaccineGroups(List<VaccineGroup> vaccines) {
        vaccineGroups = vaccines;
    }

    public ClientRegisterTypeRepository registerTypeRepository() {
        if (registerTypeRepository == null) {
            this.registerTypeRepository = new ClientRegisterTypeRepository();
        }
        return this.registerTypeRepository;
    }

    public ChildAlertUpdatedRepository alertUpdatedRepository() {
        if (childAlertUpdatedRepository == null) {
            this.childAlertUpdatedRepository = new ChildAlertUpdatedRepository();
        }
        return this.childAlertUpdatedRepository;
    }

    public RecurringServiceTypeRepository getRecurringServiceTypeRepository() {
        return ImmunizationLibrary.getInstance().recurringServiceTypeRepository();
    }

    public RecurringServiceRecordRepository getRecurringServiceRecordRepository() {
        return ImmunizationLibrary.getInstance().recurringServiceRecordRepository();
    }

    public CohortRepository cohortRepository() {
        if (cohortRepository == null) {
            cohortRepository = new CohortRepository();
        }
        return cohortRepository;
    }

    public CohortPatientRepository cohortPatientRepository() {
        if (cohortPatientRepository == null) {
            cohortPatientRepository = new CohortPatientRepository();
        }
        return cohortPatientRepository;
    }

    public CohortIndicatorRepository cohortIndicatorRepository() {
        if (cohortIndicatorRepository == null) {
            cohortIndicatorRepository = new CohortIndicatorRepository();
        }
        return cohortIndicatorRepository;
    }

    public CumulativeRepository cumulativeRepository() {
        if (cumulativeRepository == null) {
            cumulativeRepository = new CumulativeRepository();
        }
        return cumulativeRepository;
    }

    public CumulativePatientRepository cumulativePatientRepository() {
        if (cumulativePatientRepository == null) {
            cumulativePatientRepository = new CumulativePatientRepository();
        }
        return cumulativePatientRepository;
    }

    public CumulativeIndicatorRepository cumulativeIndicatorRepository() {
        if (cumulativeIndicatorRepository == null) {
            cumulativeIndicatorRepository = new CumulativeIndicatorRepository();
        }
        return cumulativeIndicatorRepository;
    }

}

