package org.smartregister.uniceftunisia.activity;

import android.os.Bundle;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.stock.activity.StockJsonFormActivity;
import org.smartregister.uniceftunisia.fragment.AppJsonFormFragment;
import org.smartregister.uniceftunisia.util.AppConstants;

import timber.log.Timber;

import static org.smartregister.uniceftunisia.fragment.AppJsonFormFragment.getFormFragment;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

public class AppStockJsonFormActivity extends StockJsonFormActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initializeFormFragment() {
        AppJsonFormFragment appJsonFormFragment = getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction()
                .add(com.vijay.jsonwizard.R.id.container, appJsonFormFragment).commit();
    }

    @Override
    public void writeValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity, String openMrsEntityId) throws JSONException {
        super.writeValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId);
        refreshCalculateLogic(key, value);
    }

    public boolean checkIfAtLeastOneServiceGiven() {
        JSONObject object = getStep("step1");
        try {
            if (object.getString(AppConstants.KEY.TITLE).contains("Record out of catchment area service")) {
                JSONArray fields = object.getJSONArray(AppConstants.KEY.FIELDS);
                for (int i = 0; i < fields.length(); i++) {
                    JSONObject vaccineGroup = fields.getJSONObject(i);
                    if (vaccineGroup.has(AppConstants.KEY.KEY) && vaccineGroup.has(AppConstants.KEY.IS_VACCINE_GROUP)) {
                        if (vaccineGroup.getBoolean(AppConstants.KEY.IS_VACCINE_GROUP) && vaccineGroup.has(AppConstants.KEY.OPTIONS)) {
                            JSONArray vaccineOptions = vaccineGroup.getJSONArray(AppConstants.KEY.OPTIONS);
                            for (int j = 0; j < vaccineOptions.length(); j++) {
                                JSONObject vaccineOption = vaccineOptions.getJSONObject(j);
                                if (vaccineOption.has(AppConstants.KEY.VALUE) && vaccineOption.getBoolean(AppConstants.KEY.VALUE)) {
                                    return true;
                                }
                            }
                        }
                    } else if (vaccineGroup.has(AppConstants.KEY.KEY) && vaccineGroup.getString(AppConstants.KEY.KEY).equals("Weight_Kg")
                            && vaccineGroup.has(AppConstants.KEY.VALUE) && vaccineGroup.getString(AppConstants.KEY.VALUE).length() > 0) {
                        return true;
                    }
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }

        return false;
    }

}
