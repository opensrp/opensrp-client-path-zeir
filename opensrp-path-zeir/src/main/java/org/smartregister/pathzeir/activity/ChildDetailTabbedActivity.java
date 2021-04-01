package org.smartregister.pathzeir.activity;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;

import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.fragment.StatusEditDialogFragment;
import org.smartregister.child.presenter.BaseChildDetailsPresenter.CardStatus;
import org.smartregister.child.task.LoadAsyncTask;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.pathzeir.R;
import org.smartregister.pathzeir.fragment.ChildRegistrationDataFragment;
import org.smartregister.pathzeir.util.AppConstants.KeyConstants;
import org.smartregister.pathzeir.util.AppJsonFormUtils;
import org.smartregister.pathzeir.util.AppUtils;
import org.smartregister.util.DateUtil;
import org.smartregister.util.FormUtils;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.child.util.Utils.metadata;
import static org.smartregister.pathzeir.util.AppConstants.KeyConstants.MOTHER_GUARDIAN_NUMBER;
import static org.smartregister.pathzeir.util.AppConstants.KeyConstants.MOTHER_PHONE;
import static org.smartregister.pathzeir.util.FormUtils.obtainUpdatedForm;

/**
 * Created by ndegwamartin on 06/03/2019.
 */
public class ChildDetailTabbedActivity extends BaseChildDetailTabbedActivity {

    private final static List<String> nonEditableFields = Arrays.asList("Sex", "zeir_id", "Date_Birth");

    @Override
    public void onUniqueIdFetched(Triple<String, Map<String, String>, String> triple, String s) {
        //Overridden - not required
    }

    @Override
    public void onNoUniqueId() {
        //Overridden - not required
    }

    public ChildRegistrationDataFragment getChildRegistrationDataFragment() {
        return new ChildRegistrationDataFragment();
    }

    @Nullable
    @Override
    protected Bundle initLoadChildDetails() {
        Bundle bundle = super.initLoadChildDetails();
        updateChildDetails();
        return bundle;
    }

    public void updateChildDetails() {
        //Backward compatibility of residential area --> previously the location identifier was saved, but not anymore
        if (LocationHelper.getInstance() != null) {
            String location = childDetails.getColumnmaps().get(KeyConstants.RESIDENTIAL_AREA);
            if (KeyConstants.OTHER.equalsIgnoreCase(location)) {
                updateDetailsMap(KeyConstants.RESIDENTIAL_AREA, childDetails.getColumnmaps().get(KeyConstants.RESIDENTIAL_ADDRESS_OTHER));
            } else {
                String openMrsLocationName = LocationHelper.getInstance().getOpenMrsLocationName(location);
                updateDetailsMap(KeyConstants.RESIDENTIAL_AREA, openMrsLocationName != null ? openMrsLocationName : location);
            }
        }

        //Convert date to the right format dd-MM-YYYY
        if (detailsMap.containsKey(KeyConstants.FIRST_HEALTH_FACILITY_CONTRACT)) {
            String value = detailsMap.get(KeyConstants.FIRST_HEALTH_FACILITY_CONTRACT);
            String formatDate = DateUtil.formatDate(value, "dd-MM-YYYY");
            updateDetailsMap(KeyConstants.FIRST_HEALTH_FACILITY_CONTRACT,
                    StringUtils.isBlank(formatDate) ? value : formatDate);
        }

        //Backward compatibility
        if (detailsMap.containsKey(MOTHER_PHONE) && detailsMap.get(MOTHER_PHONE) != null) {
            updateDetailsMap(MOTHER_GUARDIAN_NUMBER, detailsMap.get(MOTHER_PHONE));
        }
    }

    private void updateDetailsMap(String key, String value) {
        childDetails.getDetails().put(key, value);
        childDetails.getColumnmaps().put(key, value);
        detailsMap.put(key, value);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        overflow.findItem(R.id.register_card).setVisible(false);
        overflow.findItem(R.id.write_to_card).setVisible(false);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.registration_data:
                updateChildDetails();
                String populatedForm = AppJsonFormUtils.populateFormValues(this, detailsMap, nonEditableFields);
                startFormActivity(populatedForm);
                // User chose the "Settings" item, show the app settings UI...
                return true;
            case R.id.immunization_data:
                if (viewPager.getCurrentItem() != 1) {
                    viewPager.setCurrentItem(1);
                }
                Utils.startAsyncTask(
                        new LoadAsyncTask(org.smartregister.child.enums.Status.EDIT_VACCINE, detailsMap,
                                getChildDetails(), this, getChildDataFragment(), getChildUnderFiveFragment(), getOverflow()),
                        null);
                saveButton.setVisibility(View.VISIBLE);
                for (int i = 0; i < overflow.size(); i++) {
                    overflow.getItem(i).setVisible(false);
                }
                return true;

            case R.id.recurring_services_data:
                if (viewPager.getCurrentItem() != 1) {
                    viewPager.setCurrentItem(1);
                }
                Utils.startAsyncTask(
                        new LoadAsyncTask(org.smartregister.child.enums.Status.EDIT_SERVICE, detailsMap,
                                getChildDetails(), this, getChildDataFragment(), getChildUnderFiveFragment(), getOverflow()),
                        null);
                saveButton.setVisibility(View.VISIBLE);
                for (int i = 0; i < overflow.size(); i++) {
                    overflow.getItem(i).setVisible(false);
                }
                return true;
            case R.id.weight_data:
                if (viewPager.getCurrentItem() != 1) {
                    viewPager.setCurrentItem(1);
                }
                Utils.startAsyncTask(new LoadAsyncTask(org.smartregister.child.enums.Status.EDIT_GROWTH, detailsMap,
                        getChildDetails(), this, getChildDataFragment(), getChildUnderFiveFragment(), getOverflow()), null);
                saveButton.setVisibility(View.VISIBLE);
                for (int i = 0; i < overflow.size(); i++) {
                    overflow.getItem(i).setVisible(false);
                }
                return true;

            case R.id.report_deceased:
                String reportDeceasedMetadata = getReportDeceasedMetadata();
                startFormActivity(reportDeceasedMetadata);
                return true;
            case R.id.change_status:
                FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                android.app.Fragment prev = this.getFragmentManager().findFragmentByTag(DIALOG_TAG);
                if (prev != null) {
                    ft.remove(prev);
                }
                StatusEditDialogFragment.newInstance(detailsMap).show(ft, DIALOG_TAG);
                return true;
            case R.id.report_adverse_event:
                return launchAdverseEventForm();
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void navigateToRegisterActivity() {
        Intent intent = new Intent(getApplicationContext(), ChildRegisterActivity.class);
        intent.putExtra(AllConstants.INTENT_KEY.IS_REMOTE_LOGIN, false);
        startActivity(intent);
        finish();
    }

    @Override
    public void startFormActivity(String formData) {
        if (StringUtils.isNotBlank(formData)) {
            try {
                Intent intent;
                Form form = new Form();

                JSONObject formJson = new JSONObject(formData);
                if (formJson.has(JsonFormConstants.ENCOUNTER_TYPE) &&
                        formJson.getString(JsonFormConstants.ENCOUNTER_TYPE).equalsIgnoreCase(Constants.EventType.AEFI)) {
                    form.setWizard(true);
                    form.setName(getString(R.string.adverse_effects));
                    form.setHideSaveLabel(true);
                    form.setNextLabel(getString(R.string.next));
                    form.setPreviousLabel(getString(R.string.previous));
                    form.setSaveLabel(getString(R.string.save));
                    form.setActionBarBackground(R.color.actionbar);
                    form.setNavigationBackground(R.color.primary_dark);
                    intent = new Intent(this, JsonWizardFormActivity.class);
                } else {
                    form.setWizard(false);
                    form.setHideSaveLabel(true);
                    form.setNextLabel("");
                    intent = new Intent(this, metadata().childFormActivity);
                }

                String formDataString = obtainUpdatedForm(formJson, childDetails, getContext());
                intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
                intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, formDataString);
                intent.putExtra(JsonFormConstants.PERFORM_FORM_TRANSLATION, true);
                startActivityForResult(intent, REQUEST_CODE_GET_JSON);
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    @Override
    protected String getReportDeceasedMetadata() {
        try {
            JSONObject form = FormUtils.getInstance(getApplicationContext()).getFormJson("report_deceased");
            if (form != null) {
                //inject zeir id into the form
                JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase("Date_of_Death")) {
                        SimpleDateFormat simpleDateFormat =
                                new SimpleDateFormat(com.vijay.jsonwizard.utils.FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN,
                                        Locale.ENGLISH);
                        String dobString = Utils.getValue(childDetails.getColumnmaps(), "dob", true);
                        Date dob = Utils.dobStringToDate(dobString);
                        if (dob != null) {
                            jsonObject.put("min_date", simpleDateFormat.format(dob));
                        }
                        break;
                    }
                }
            }

            return form == null ? null : form.toString();

        } catch (Exception e) {
            Timber.e(e);
        }
        return "";
    }

    @Override
    public void notifyLostCardReported(String orderDate) {
        super.notifyLostCardReported(orderDate);
        AppUtils.createClientCardReceivedEvent(childDetails.getCaseId(), CardStatus.needs_card, orderDate);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (data != null) {
                String jsonString = data.getStringExtra(JsonFormConstants.JSON_FORM_KEY.JSON);
                Timber.d(jsonString);
                JSONObject form = new JSONObject(jsonString);
                String encounterType = form.getString(ChildJsonFormUtils.ENCOUNTER_TYPE);
                if (encounterType.equalsIgnoreCase(Constants.EventType.UPDATE_BITRH_REGISTRATION)) {
                    String jsonForm = AppUtils.validateChildZone(jsonString);
                    data.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonForm);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
