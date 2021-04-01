package org.smartregister.path.util;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.path.R;
import org.smartregister.util.FormUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.path.util.AppConstants.KeyConstants.BIRTH_FACILITY_NAME;
import static org.smartregister.path.util.AppConstants.KeyConstants.CHOOSE_IMAGE;
import static org.smartregister.path.util.AppConstants.KeyConstants.DOB;
import static org.smartregister.path.util.AppConstants.KeyConstants.MOTHER_DOB;
import static org.smartregister.path.util.AppConstants.KeyConstants.MOTHER_FIRST_NAME;
import static org.smartregister.path.util.AppConstants.KeyConstants.MOTHER_LAST_NAME;
import static org.smartregister.path.util.AppConstants.KeyConstants.PLACE_OF_BIRTH;
import static org.smartregister.path.util.AppConstants.KeyConstants.PMTCT_STATUS;

public class AppJsonFormUtils extends ChildJsonFormUtils {

    private static final HashMap<String, String> alternativeKeys = new HashMap<String, String>() {
        {
            put("mother_guardian_first_name", MOTHER_FIRST_NAME);
            put("mother_guardian_last_name", MOTHER_LAST_NAME);
            put("mother_guardian_date_birth", MOTHER_DOB);
            put("Sex", "gender");
            put("Date_Birth", "dob");
            put("Birth_Weight", "birth_weight");
        }
    };

    public static String populateFormValues(Context context, Map<String, String> childDetails, List<String> nonEditableFields) {
        JSONObject form = null;
        try {
            form = new FormUtils(context).getFormJson(Utils.metadata().childRegister.formName);
            form.put(ChildJsonFormUtils.ENTITY_ID, childDetails.get(Constants.KEY.BASE_ENTITY_ID));
            form.put(ChildJsonFormUtils.ENCOUNTER_TYPE, Utils.metadata().childRegister.updateEventType);
            form.put(ChildJsonFormUtils.RELATIONAL_ID, childDetails.get(RELATIONAL_ID));
            form.put(ChildJsonFormUtils.CURRENT_ZEIR_ID, Utils.getValue(childDetails, Constants.KEY.ZEIR_ID, true).replace("-", ""));
            form.put(ChildJsonFormUtils.CURRENT_OPENSRP_ID, Utils.getValue(childDetails, Constants.JSON_FORM_KEY.UNIQUE_ID, false));
            JSONObject metadata = form.getJSONObject(ChildJsonFormUtils.METADATA);
            metadata.put(ChildJsonFormUtils.ENCOUNTER_LOCATION, ChildJsonFormUtils.getProviderLocationId(context));

            JSONObject stepOne = form.getJSONObject(ChildJsonFormUtils.STEP1);
            stepOne.put(JsonFormConstants.STEP_TITLE, context.getString(R.string.update_birth_registration));
            JSONArray fields = stepOne.getJSONArray(ChildJsonFormUtils.FIELDS);


            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.getJSONObject(i);
                field.put(ChildJsonFormUtils.READ_ONLY, nonEditableFields.contains(field.getString(ChildJsonFormUtils.KEY)));
                setFieldValue(field, childDetails);
            }

        } catch (Exception e) {
            Timber.e(e);
        }
        return form != null ? form.toString() : null;
    }

    protected static void setFieldValue(JSONObject field, Map<String, String> childDetails) throws JSONException {
        String key = field.getString(JsonFormConstants.KEY);
        //Key on child details  maybe different
        if (alternativeKeys.containsKey(key)) {
            key = alternativeKeys.get(key);
        }
        switch (field.getString(JsonFormConstants.TYPE)) {
            case CHOOSE_IMAGE:
                processPhoto(childDetails.get(Constants.KEY.BASE_ENTITY_ID), field);
                break;
            case JsonFormConstants.DATE_PICKER:
                if (DOB.equalsIgnoreCase(key) || MOTHER_DOB.equalsIgnoreCase(key)) {
                    if (childDetails.get(key) != null) {
                        String prefix = getEntityPrefix(field);
                        processDate(childDetails, prefix, field);
                    }
                } else {
                    processDateField(field, childDetails, key);
                }
                break;
            case JsonFormConstants.SPINNER:
                //backward compatibility for spinner previously saving values instead of option keys
                // (conventionally key is value in lowercase with space replaced with underscore)
                if (childDetails.get(key) != null && (BIRTH_FACILITY_NAME.equalsIgnoreCase(key) || GENDER.equalsIgnoreCase(key) ||
                        PLACE_OF_BIRTH.equalsIgnoreCase(key) || PMTCT_STATUS.equalsIgnoreCase(key))) {
                    field.put(JsonFormConstants.VALUE, childDetails.get(key).toLowerCase().replace(" ", "_"));
                } else {
                    field.put(JsonFormConstants.VALUE, childDetails.get(key));
                }
                break;
            case JsonFormConstants.EDIT_TEXT:
            case JsonFormConstants.HIDDEN:
            case JsonFormConstants.BARCODE:
                field.put(JsonFormConstants.VALUE, childDetails.get(key));
                break;
            default:
                break;
        }
    }

    private static void processDateField(JSONObject field, Map<String, String> childDetails, String key) throws JSONException {
        if (childDetails.get(key) != null) {
            Date date = Utils.dobStringToDate(childDetails.get(key));
            if (date != null) {
                field.put(JsonFormConstants.VALUE, DATE_FORMAT.format(date));
            } else {
                field.put(JsonFormConstants.VALUE, childDetails.get(key));
            }
        }
    }

    public static void tagEventMetadata(Event event) {
        tagSyncMetadata(event);
    }
}