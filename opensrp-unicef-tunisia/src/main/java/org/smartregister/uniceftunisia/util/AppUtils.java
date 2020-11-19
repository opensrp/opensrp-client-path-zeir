package org.smartregister.uniceftunisia.util;

import android.content.ContentValues;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.domain.Client;
import org.smartregister.domain.db.EventClient;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.uniceftunisia.BuildConfig;
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class AppUtils extends Utils {

    public static final ArrayList<String> ALLOWED_LEVELS;
    public static final String FACILITY = "Facility";
    public static final String DEFAULT_LOCATION_LEVEL = "Health Facility";

    static {
        ALLOWED_LEVELS = new ArrayList<>();
        ALLOWED_LEVELS.add(DEFAULT_LOCATION_LEVEL);
        ALLOWED_LEVELS.add(FACILITY);
    }

    public static String getLanguage() {
        return AppUtils.getAllSharedPreferences().fetchLanguagePreference();
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
        updateChildTables(client, values, AppConstants.TABLE_NAME.CHILD_DETAILS);
        updateChildTables(client, values, AppConstants.TABLE_NAME.ALL_CLIENTS);
    }

    private static void updateChildTables(Client client, ContentValues values, String tableName) {
        AllCommonsRepository allCommonsRepository = UnicefTunisiaApplication.getInstance().context().allCommonsRepositoryobjects(tableName);
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
        String selectedLocation = UnicefTunisiaApplication.getInstance().context().allSharedPreferences().fetchCurrentLocality();
        if (StringUtils.isBlank(selectedLocation)) {
            selectedLocation = LocationHelper.getInstance().getDefaultLocation();
            UnicefTunisiaApplication.getInstance().context().allSharedPreferences().saveCurrentLocality(selectedLocation);
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
        UnicefTunisiaApplication.getInstance().context().allSharedPreferences().savePreference("syncComplete", String.valueOf(isComplete));
    }
}
