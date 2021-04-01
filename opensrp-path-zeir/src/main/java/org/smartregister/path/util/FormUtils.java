package org.smartregister.path.util;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.path.R;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.util.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class FormUtils {

    public static String obtainUpdatedForm(JSONObject form, Context context) throws JSONException {
        return obtainUpdatedForm(form, null, context);
    }

    public static String obtainUpdatedForm(JSONObject form, CommonPersonObjectClient childDetails, Context context) throws JSONException {
        JSONArray fields = JsonFormUtils.fields(form);
        String facilityName = LocationHelper.getInstance().getDefaultLocation(); //Default location always the Health Facility
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);

            if (field != null) {
                if (childDetails != null & field.getString(JsonFormConstants.TYPE).equalsIgnoreCase(JsonFormConstants.DATE_PICKER) &&
                        !childDetails.getDetails().isEmpty() && childDetails.getDetails().containsKey(AppConstants.KeyConstants.DOB)) {
                    Date date = Utils.dobStringToDate(childDetails.getDetails().get(AppConstants.KeyConstants.DOB));
                    field.put(JsonFormConstants.MIN_DATE, new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date));
                    field.put(JsonFormConstants.MAX_DATE, AppConstants.KeyConstants.TODAY);
                }
                createSpinnerOptions(field, AppConstants.KeyConstants.HOME_FACILITY, getSpinnerKeys(facilityName), getSpinnerValues(facilityName));

                String other = context.getString(R.string.other);
                String facilities = facilityName + "," + AppConstants.KeyConstants.OTHER;
                createSpinnerOptions(field, AppConstants.KeyConstants.BIRTH_FACILITY_NAME, getSpinnerKeys(facilities), getSpinnerValues(facilityName + "," + other));

                String operationalAreas = Utils.getAllSharedPreferences().getPreference(AllConstants.OPERATIONAL_AREAS); // Operational areas are at the ZONE level
                createSpinnerOptions(field, AppConstants.KeyConstants.CHILD_ZONE, getSpinnerKeys(operationalAreas), getSpinnerValues(operationalAreas));
            }

        }

        return form.toString();
    }

    private static JSONArray getSpinnerKeys(String locations) {
        JSONArray keys = new JSONArray();
        String[] splitLocations = locations.split(",");

        if (LocationHelper.getInstance() != null) {
            for (String location : splitLocations) {
                String locationId = LocationHelper.getInstance().getOpenMrsLocationId(location);
                if (locationId != null) {
                    keys.put(locationId);
                }
            }
        }
        return keys;
    }

    private static String[] getSpinnerValues(String locations) {
        List<String> values = new ArrayList<>();
        String[] splitLocations = locations.split(",");
        for (String splitLocation : splitLocations) {
            String location = splitLocation.trim();
            values.add(location);
        }
        return values.toArray(new String[]{});
    }

    private static void createSpinnerOptions(JSONObject field, String fieldName, JSONArray spinnerOptionKeys, String[] spinnerOptionValues) {
        try {
            JSONArray options = new JSONArray();
            if (field.has(JsonFormConstants.KEY) && field.getString(JsonFormConstants.KEY).equalsIgnoreCase(fieldName)) {
                for (int index = 0; index < spinnerOptionKeys.length(); index++) {
                    JSONObject option = new JSONObject();
                    option.put(JsonFormConstants.KEY, spinnerOptionKeys.get(index));
                    option.put(JsonFormConstants.TEXT, spinnerOptionValues[index]);
                    options.put(option);
                }
                field.put(JsonFormConstants.OPTIONS_FIELD_NAME, options);
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
    }
}

