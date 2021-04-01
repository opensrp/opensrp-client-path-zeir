package org.smartregister.path.util;

import android.content.ContentValues;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.child.presenter.BaseChildDetailsPresenter.CardStatus;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.domain.Client;
import org.smartregister.domain.db.EventClient;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.path.BuildConfig;
import org.smartregister.path.application.ZeirApplication;
import org.smartregister.path.dao.AppChildDao;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.JsonFormUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP1;
import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUE;
import static org.smartregister.path.util.AppConstants.KeyConstants.CHILD_ZONE;
import static org.smartregister.path.util.AppConstants.KeyConstants.KEY;

public class AppUtils extends Utils {

    public static final ArrayList<String> ALLOWED_LEVELS;
    public static final String FACILITY = "Facility";
    public static final String DEFAULT_LOCATION_LEVEL = "Health Facility";

    static {
        ALLOWED_LEVELS = new ArrayList<>();
        ALLOWED_LEVELS.add(DEFAULT_LOCATION_LEVEL);
        ALLOWED_LEVELS.add(FACILITY);
    }

    public static void updateChildDeath(@NonNull EventClient eventClient) {
        Client client = eventClient.getClient();
        ContentValues values = new ContentValues();

        if (client.getDeathdate() == null) {
            Timber.e(new Exception(), "Death event for %s cannot be processed because deathdate is NULL : %s"
                    , client.getFirstName() + " " + client.getLastName(), new Gson().toJson(eventClient));
            return;
        }

        values.put(Constants.KEY.IS_CLOSED, 1);
        values.put(Constants.KEY.DATE_REMOVED, Utils.convertDateFormat(client.getDeathdate().toDate(), Utils.DB_DF));
        updateChildTables(client, values, AppConstants.TableNameConstants.CHILD_DETAILS);
        updateChildTables(client, values, AppConstants.TableNameConstants.ALL_CLIENTS);
    }

    private static void updateChildTables(Client client, ContentValues values, String tableName) {
        AllCommonsRepository allCommonsRepository = ZeirApplication.getInstance().context().allCommonsRepositoryobjects(tableName);
        if (allCommonsRepository != null) {
            allCommonsRepository.update(tableName, values, client.getBaseEntityId());
            allCommonsRepository.updateSearch(client.getBaseEntityId());
        }
    }

    @NonNull
    public static ArrayList<String> getLocationLevels() {
        return new ArrayList<>(Arrays.asList(BuildConfig.LOCATION_LEVELS));
    }

    @NonNull
    public static ArrayList<String> getHealthFacilityLevels() {
        return new ArrayList<>(Arrays.asList(BuildConfig.HEALTH_FACILITY_LEVELS));
    }

    @NonNull
    public static String getCurrentLocality() {
        String selectedLocation = ZeirApplication.getInstance().context().allSharedPreferences().fetchCurrentLocality();
        if (StringUtils.isBlank(selectedLocation)) {
            selectedLocation = LocationHelper.getInstance().getDefaultLocation();
            ZeirApplication.getInstance().context().allSharedPreferences().saveCurrentLocality(selectedLocation);
        }
        return selectedLocation;
    }


    public static boolean timeBetweenLastExecutionAndNow(int i, String reportJobExecutionTime) {
        try {
            long executionTime = Long.parseLong(reportJobExecutionTime);
            long now = System.currentTimeMillis();
            long diffNowExecutionTime = now - executionTime;
            return TimeUnit.MILLISECONDS.toMinutes(diffNowExecutionTime) > i;
        } catch (NumberFormatException e) {
            Timber.e(e);
            return false;
        }
    }

    public static void updateSyncStatus(boolean isComplete) {
        ZeirApplication.getInstance().context().allSharedPreferences().savePreference("syncComplete", String.valueOf(isComplete));
    }

    public static void createClientCardReceivedEvent(String baseEntityId, CardStatus cardStatus, String cardStatusDate) {
        //We do not want to unnecessary events when card is not needed
        if (cardStatus == CardStatus.does_not_need_card && !AppChildDao.clientNeedsCard(baseEntityId)) {
            return;
        }
        try {
            Event baseEvent = AppJsonFormUtils.createEvent(new JSONArray(), new JSONObject().put(JsonFormUtils.ENCOUNTER_LOCATION, ""),
                    AppJsonFormUtils.formTag(getAllSharedPreferences()), "", AppConstants.EventTypeConstants.CARD_STATUS_UPDATE, AppConstants.EventTypeConstants.CARD_STATUS_UPDATE);

            baseEvent.setFormSubmissionId(UUID.randomUUID().toString());
            baseEvent.addDetails(AppConstants.KeyConstants.CARD_STATUS, cardStatus.name());
            baseEvent.addDetails(AppConstants.KeyConstants.CARD_STATUS_DATE, cardStatusDate);
            baseEvent.setBaseEntityId(baseEntityId);
            AppJsonFormUtils.tagEventMetadata(baseEvent);

            ZeirApplication appInstance = ZeirApplication.getInstance();
            ECSyncHelper ecSyncHelper = appInstance.getEcSyncHelper();

            ecSyncHelper.addEvent(baseEntityId, new JSONObject(AppJsonFormUtils.gson.toJson(baseEvent)));
            appInstance.getClientProcessor().processClient(ecSyncHelper.getEvents(Collections.singletonList(baseEvent.getFormSubmissionId())));

            Date lastSyncDate = new Date(getAllSharedPreferences().fetchLastUpdatedAtDate(0));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static boolean isSameMonthAndYear(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            DateTime dateTime1 = new DateTime(date1);
            DateTime dateTime2 = new DateTime(date2);

            return dateTime1.getMonthOfYear() == dateTime2.getMonthOfYear() && dateTime1.getYear() == dateTime2.getYear();
        }
        return false;
    }

    public static Date getCohortEndDate(String vaccine, Date startDate) {

        if (StringUtils.isBlank(vaccine) || startDate == null) {
            return null;
        }

        Calendar endDateCalendar = Calendar.getInstance();
        endDateCalendar.setTime(startDate);

        final String opv0 = VaccineRepository.addHyphen(VaccineRepo.Vaccine.opv0.display().toLowerCase());

        final String rota1 = VaccineRepository.addHyphen(VaccineRepo.Vaccine.rota1.display().toLowerCase());
        final String rota2 = VaccineRepository.addHyphen(VaccineRepo.Vaccine.rota2.display().toLowerCase());

        final String measles2 = VaccineRepository.addHyphen(VaccineRepo.Vaccine.measles2.display().toLowerCase());
        final String mr2 = VaccineRepository.addHyphen(VaccineRepo.Vaccine.mr2.display().toLowerCase());

        if (vaccine.equals(opv0)) {
            endDateCalendar.add(Calendar.DATE, 13);
        } else if (vaccine.equals(rota1) || vaccine.equals(rota2)) {
            endDateCalendar.add(Calendar.MONTH, 8);
        } else if (vaccine.equals(measles2) || vaccine.equals(mr2)) {
            endDateCalendar.add(Calendar.YEAR, 2);
        } else {
            endDateCalendar.add(Calendar.YEAR, 1);
        }

        return endDateCalendar.getTime();
    }

    public static Date getLastDayOfMonth(Date month) {
        if (month == null) {
            return null;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(month);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return c.getTime();
    }

    public static boolean isSameYear(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            DateTime dateTime1 = new DateTime(date1);
            DateTime dateTime2 = new DateTime(date2);

            return dateTime1.getYear() == dateTime2.getYear();
        }
        return false;
    }

    public static Date getCohortEndDate(VaccineRepo.Vaccine vaccine, Date startDate) {
        if (vaccine == null || startDate == null) {
            return null;
        }
        String vaccineName = VaccineRepository.addHyphen(vaccine.display().toLowerCase());
        return getCohortEndDate(vaccineName, startDate);

    }

    public static int yearFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static String validateChildZone(String jsonString) {
        try {
            JSONObject jsonForm = JsonFormUtils.toJSONObject(jsonString);
            JSONArray fields = JsonFormUtils.fields(jsonForm);
            for (int fieldIndex = 0; fieldIndex < fields.length(); fieldIndex++) {
                JSONObject field = fields.getJSONObject(fieldIndex);
                if (field.getString(KEY).equalsIgnoreCase(CHILD_ZONE) &&
                        field.has(VALUE)) {
                    String value = field.getString(VALUE);
                    if (StringUtils.isNotBlank(value) && value.equalsIgnoreCase(field.getString(JsonFormConstants.HINT))) {
                        field.remove(VALUE);
                        fields.put(fieldIndex, field);
                        break;
                    }
                }

            }
            jsonForm.getJSONObject(STEP1).put(FIELDS, fields);
            return jsonForm.toString();
        } catch (JSONException e) {
            Timber.e(e);
        }
        return jsonString;
    }
}
