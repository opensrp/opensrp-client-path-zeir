package org.smartregister.uniceftunisia.task;

import android.content.Intent;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.uniceftunisia.activity.AppStockJsonFormActivity;
import org.smartregister.uniceftunisia.activity.HIA2ReportsActivity;
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication;
import org.smartregister.uniceftunisia.domain.MonthlyTally;
import org.smartregister.uniceftunisia.repository.MonthlyTalliesRepository;
import org.smartregister.uniceftunisia.util.AppConstants;
import org.smartregister.uniceftunisia.util.AppReportUtils;
import org.smartregister.util.FormUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-12-02
 */
public class StartDraftMonthlyFormTask extends AsyncTask<Void, Void, Intent> {

    private final HIA2ReportsActivity baseActivity;
    private final Date date;
    private final String formName;
    private String reportGrouping;

    public StartDraftMonthlyFormTask(@NonNull HIA2ReportsActivity baseActivity,
                                     @Nullable String reportGrouping, @NonNull Date date, @NonNull String formName) {
        this.baseActivity = baseActivity;
        this.reportGrouping = reportGrouping;
        this.date = date;
        this.formName = formName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        baseActivity.showProgressDialog();
    }

    @Override
    protected Intent doInBackground(Void... params) {
        try {
            MonthlyTalliesRepository monthlyTalliesRepository = UnicefTunisiaApplication.getInstance().monthlyTalliesRepository();
            List<MonthlyTally> monthlyTallies = monthlyTalliesRepository.findDrafts(MonthlyTalliesRepository.DF_YYYYMM.format(date), reportGrouping);

            if (monthlyTallies.size() < 1) {
                return null;
            }

            JSONObject form;
            try {
                form = FormUtils.getInstance(baseActivity).getFormJson(formName);
            } catch (Exception e) {
                Timber.e(e);
                return null;
            }

            JSONObject step1 = form.getJSONObject("step1");
            String title = MonthlyTalliesRepository.DF_YYYYMM.format(date).concat(" Draft");
            step1.put(AppConstants.KEY.TITLE, title);

            JSONArray fieldsArray = step1.getJSONArray(AppConstants.KEY.FIELDS);

            // Sort the indicators for better readability from the JSON FORM
            Collections.sort(monthlyTallies, new Comparator<MonthlyTally>() {
                @Override
                public int compare(MonthlyTally o1, MonthlyTally o2) {
                    return o1.getIndicator().compareTo(o2.getIndicator());
                }
            });

            // This map holds each category as key and all the fields for that category as the
            // value (jsonarray)
            for (MonthlyTally monthlyTally : monthlyTallies) {
                JSONObject jsonObject = createFormIndicatorField(monthlyTally);

                fieldsArray.put(jsonObject);
            }

            // Add the confirm button
            JSONObject buttonObject = createFormConfirmButton();

            fieldsArray.put(buttonObject);

            form.put(JsonFormConstants.REPORT_MONTH, HIA2ReportsActivity.yyyyMMdd.format(date));
            form.put("identifier", "HIA2ReportForm");

            Intent intent = new Intent(baseActivity, AppStockJsonFormActivity.class);
            intent.putExtra("json", form.toString());
            intent.putExtra(JsonFormConstants.SKIP_VALIDATION, false);

            return intent;
        } catch (JSONException e) {
            Timber.e(e);
        }

        return null;
    }

    @NonNull
    private JSONObject createFormIndicatorField(MonthlyTally monthlyTally) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        int resourceId = baseActivity.getResources().getIdentifier(AppReportUtils.getStringIdentifier(monthlyTally.getIndicator()), "string", baseActivity.getPackageName());
        String label = resourceId != 0 ? baseActivity.getResources().getString(resourceId) : monthlyTally.getIndicator();

        JSONObject vRequired = new JSONObject();
        vRequired.put(JsonFormConstants.VALUE, "true");
        vRequired.put(JsonFormConstants.ERR, "Specify: " + label);
        JSONObject vNumeric = new JSONObject();
        vNumeric.put(JsonFormConstants.VALUE, "true");
        vNumeric.put(JsonFormConstants.ERR, "Value should be numeric");

        String key = monthlyTally.getIndicator();
        if (monthlyTally.getGrouping() != null) {
            key += ">" + monthlyTally.getGrouping();
        }

        jsonObject.put(JsonFormConstants.KEY, key);
        jsonObject.put(JsonFormConstants.TYPE, "edit_text");
        jsonObject.put(JsonFormConstants.READ_ONLY, false);
        jsonObject.put(JsonFormConstants.HINT, label);
        jsonObject.put(JsonFormConstants.VALUE, monthlyTally.getValue());

        //jsonObject.put(JsonFormConstants.READ_ONLY, readOnlyList.contains(monthlyTally.getIndicatorCode()));

        // This will be used for the target indicators
                    /*if (DailyTalliesRepository.IGNORED_INDICATOR_CODES
                            .contains(monthlyTally.getIndicatorCode()) && firstTimeEdit) {
                        jsonObject.put(JsonFormConstants.VALUE, "");
                    }*/
        jsonObject.put(JsonFormConstants.V_REQUIRED, vRequired);
        jsonObject.put(JsonFormConstants.V_NUMERIC, vNumeric);
        jsonObject.put(JsonFormConstants.OPENMRS_ENTITY_PARENT, "");
        jsonObject.put(JsonFormConstants.OPENMRS_ENTITY, "");
        jsonObject.put(JsonFormConstants.OPENMRS_ENTITY_ID, "");
        jsonObject.put(AppConstants.KEY.HIA_2_INDICATOR, monthlyTally.getIndicator());
        return jsonObject;
    }

    @NonNull
    private JSONObject createFormConfirmButton() throws JSONException {
        JSONObject buttonObject = new JSONObject();
        buttonObject.put(JsonFormConstants.KEY, HIA2ReportsActivity.FORM_KEY_CONFIRM);
        buttonObject.put(JsonFormConstants.VALUE, "false");
        buttonObject.put(JsonFormConstants.TYPE, "button");
        buttonObject.put(JsonFormConstants.HINT, "Confirm");
        buttonObject.put(JsonFormConstants.OPENMRS_ENTITY_PARENT, "");
        buttonObject.put(JsonFormConstants.OPENMRS_ENTITY, "");
        buttonObject.put(JsonFormConstants.OPENMRS_ENTITY_ID, "");
        JSONObject action = new JSONObject();
        action.put(JsonFormConstants.BEHAVIOUR, "finish_form");
        buttonObject.put(JsonFormConstants.ACTION, action);
        return buttonObject;
    }

    @Override
    protected void onPostExecute(Intent intent) {
        super.onPostExecute(intent);
        baseActivity.hideProgressDialog();

        if (intent != null) {
            baseActivity.startActivityForResult(intent, HIA2ReportsActivity.REQUEST_CODE_GET_JSON);
        }
    }
}
