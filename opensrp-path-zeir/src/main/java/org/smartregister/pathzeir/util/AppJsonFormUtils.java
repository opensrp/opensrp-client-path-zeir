package org.smartregister.pathzeir.util;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.pathzeir.R;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class AppJsonFormUtils extends ChildJsonFormUtils {

    public static String populateFormValues(Context context, Map<String, String> detailsMap, List<String> nonEditableFields) {
        JSONObject form = null;
        try {
            String populatedForm = ChildJsonFormUtils.getMetadataForEditForm(context, detailsMap, nonEditableFields);
            form = new JSONObject(populatedForm);
            JSONObject stepOne = form.getJSONObject(ChildJsonFormUtils.STEP1);
            stepOne.put(JsonFormConstants.STEP_TITLE, context.getString(R.string.update_birth_registration));
            JSONArray jsonArray = stepOne.getJSONArray(ChildJsonFormUtils.FIELDS);
            updateFormDetailsForEdi(detailsMap, jsonArray);
        } catch (JSONException e) {
            Timber.e(e);
        }
        return form != null ? form.toString() : null;
    }

    private static void updateFormDetailsForEdi(
            Map<String, String> detailsMap, JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase("Sex")) {
                jsonObject.put(ChildJsonFormUtils.VALUE, detailsMap.get(ChildJsonFormUtils.GENDER).toLowerCase());
            } else if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase(AppConstants.KeyConstants.FIRST_HEALTH_FACILITY_CONTRACT)) {
                jsonObject.put(ChildJsonFormUtils.VALUE, detailsMap.get(AppConstants.KeyConstants.FIRST_HEALTH_FACILITY_CONTRACT));
            } else if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase(AppConstants.KeyConstants.MOTHER_GUARDIAN_NRC)) {
                jsonObject.put(ChildJsonFormUtils.VALUE, detailsMap.get(AppConstants.KeyConstants.MOTHER_GUARDIAN_NRC));
            } else if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase(AppConstants.KeyConstants.MOTHER_GUARDIAN_NUMBER)) {
                jsonObject.put(ChildJsonFormUtils.VALUE, detailsMap.get(AppConstants.KeyConstants.MOTHER_GUARDIAN_NUMBER));
            } else if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase(AppConstants.KeyConstants.PLACE_OF_BIRTH)) {
                jsonObject.put(ChildJsonFormUtils.VALUE, detailsMap.get(AppConstants.KeyConstants.PLACE_OF_BIRTH));
            } else if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase(AppConstants.KeyConstants.RESIDENTIAL_ADDRESS)) {
                jsonObject.put(ChildJsonFormUtils.VALUE, detailsMap.get(AppConstants.KeyConstants.RESIDENTIAL_ADDRESS));
            } else if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase(AppConstants.KeyConstants.PMTCT_STATUS)) {
                jsonObject.put(ChildJsonFormUtils.VALUE, detailsMap.get(AppConstants.KeyConstants.PMTCT_STATUS));
            } else if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase(AppConstants.KeyConstants.RESIDENTIAL_AREA)) {
                jsonObject.put(ChildJsonFormUtils.VALUE, detailsMap.get(AppConstants.KeyConstants.RESIDENTIAL_AREA));
            } else if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase(AppConstants.KeyConstants.HOME_FACILITY)) {
                jsonObject.put(ChildJsonFormUtils.VALUE, detailsMap.get(AppConstants.KeyConstants.HOME_FACILITY));
            } else if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase(AppConstants.KeyConstants.CHILD_ZONE)) {
                jsonObject.put(ChildJsonFormUtils.VALUE, detailsMap.get(AppConstants.KeyConstants.CHILD_ZONE));
            } else if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase(AppConstants.KeyConstants.BIRTH_FACILITY_NAME)) {
                jsonObject.put(ChildJsonFormUtils.VALUE, detailsMap.get(AppConstants.KeyConstants.BIRTH_FACILITY_NAME));
            }
        }
    }

    public static void tagEventMetadata(Event event) {
        tagSyncMetadata(event);
    }
}