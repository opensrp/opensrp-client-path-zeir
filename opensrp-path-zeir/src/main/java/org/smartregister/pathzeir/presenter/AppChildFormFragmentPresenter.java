package org.smartregister.pathzeir.presenter;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.gson.Gson;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.child.presenter.ChildFormFragmentPresenter;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.pathzeir.R;
import org.smartregister.pathzeir.activity.ChildFormActivity;
import org.smartregister.pathzeir.fragment.AppChildFormFragment;
import org.smartregister.pathzeir.util.AppConstants;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP1;
import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUE;
import static org.smartregister.pathzeir.util.AppConstants.KeyConstants.BIRTH_FACILITY_NAME;
import static org.smartregister.pathzeir.util.AppConstants.KeyConstants.CHILD_ZONE;
import static org.smartregister.pathzeir.util.AppConstants.KeyConstants.HOME_FACILITY;
import static org.smartregister.pathzeir.util.AppConstants.KeyConstants.KEY;

public class AppChildFormFragmentPresenter extends ChildFormFragmentPresenter {

    private final AppChildFormFragment formFragment;
    private final ChildFormActivity jsonFormView;
    private String encounterType = null;

    public AppChildFormFragmentPresenter(JsonFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
        this.formFragment = (AppChildFormFragment) formFragment;
        jsonFormView = (ChildFormActivity) formFragment.getActivity();
    }

    @Override
    public void addFormElements() {
        super.addFormElements();
        //Set Health Facility to default location
        try {
            encounterType = formFragment.getJsonApi().getmJSONObject().getString(JsonFormConstants.ENCOUNTER_TYPE);
        } catch (JSONException e) {
            Timber.e(e, "Encounter type missing");
        }
        if (encounterType != null && (encounterType.equalsIgnoreCase(AppConstants.EventTypeConstants.CHILD_REGISTRATION)
                || encounterType.equalsIgnoreCase(AppConstants.EventTypeConstants.UPDATE_CHILD_REGISTRATION))) {

            String facilityName = LocationHelper.getInstance().getDefaultLocation(); //Default location always the Health Facility
            populateLocationSpinner(HOME_FACILITY, getSpinnerKeys(facilityName), getSpinnerValues(facilityName));

            String other = formFragment.getString(R.string.other);
            String facilities = facilityName + "," + other;
            populateLocationSpinner(BIRTH_FACILITY_NAME, getSpinnerKeys(facilities), getSpinnerValues(facilities));

            String operationalAreas = Utils.getAllSharedPreferences().getPreference(AllConstants.OPERATIONAL_AREAS); // Operational areas are at the ZONE level
            populateLocationSpinner(CHILD_ZONE, getSpinnerKeys(operationalAreas), getSpinnerValues(operationalAreas));

        }
    }

    private JSONArray getSpinnerKeys(String locations) {
        JSONArray keys = new JSONArray();
        String[] splitLocations = locations.split(",");

        for (String location : splitLocations) {
            keys.put(location.trim().toLowerCase().replace(" ", "_"));
        }
        return keys;
    }

    private String[] getSpinnerValues(String locations) {
        List<String> values = new ArrayList<>();
        String[] splitLocations = locations.split(",");
        for (String splitLocation : splitLocations) {
            String location = splitLocation.trim();
            values.add(location);
        }
        return values.toArray(new String[]{});
    }

    private void populateLocationSpinner(String fieldName, JSONArray spinnerOptionKeys, String[] spinnerOptionValues) {

        MaterialSpinner spinner = (MaterialSpinner) jsonFormView.getFormDataView(JsonFormConstants.STEP1 + ":" + fieldName);
        if (spinnerOptionValues != null && spinnerOptionValues.length > 0 && spinnerOptionKeys != null && spinnerOptionKeys.length() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getView().getContext(), R.layout.native_form_simple_list_item_1, spinnerOptionValues);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(formFragment.getCommonListener());
            spinner.setTag(R.id.keys, spinnerOptionKeys);
            spinner.setVisibility(View.VISIBLE);
            if (adapter.getCount() == 1) {
                spinner.setSelection(1);
            }
        } else {
            spinner.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        super.onItemSelected(parent, view, position, id);
        String key = (String) parent.getTag(R.id.key);
        try {
            if (key.equals(AppConstants.KeyConstants.REACTION_VACCINE)) {
                MaterialSpinner spinnerReactionVaccine = (MaterialSpinner) ((ChildFormActivity)
                        formFragment.requireActivity()).getFormDataView(
                        STEP1 + ":" + AppConstants.KeyConstants.REACTION_VACCINE);
                int selectedItemPos = spinnerReactionVaccine.getSelectedItemPosition();
                AppChildFormFragment.OnReactionVaccineSelected onReactionVaccineSelected = formFragment.getOnReactionVaccineSelected();
                if (selectedItemPos > 0) {
                    selectedItemPos = selectedItemPos - 1;
                    String reactionVaccine = (String) spinnerReactionVaccine.getAdapter().getItem(selectedItemPos);
                    if (StringUtils.isNotBlank(reactionVaccine) && (reactionVaccine.length() > 10)) {
                        String reactionVaccineDate = reactionVaccine.substring(reactionVaccine.length() - 11, reactionVaccine.length() - 1);
                        if (onReactionVaccineSelected != null) {
                            onReactionVaccineSelected.updateDatePicker(reactionVaccineDate);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (compoundButton instanceof CheckBox
                && encounterType.equalsIgnoreCase(AppConstants.EventTypeConstants.OUT_OF_CATCHMENT)
                && compoundButton.isChecked()) {
            String parentKey = (String) compoundButton.getTag(com.vijay.jsonwizard.R.id.key);
            String childKey = (String) compoundButton.getTag(com.vijay.jsonwizard.R.id.childKey);

            if (isValidChoice(parentKey, childKey)) {
                super.onCheckedChanged(compoundButton, isChecked);
            } else {
                compoundButton.setChecked(false);
            }
        } else {
            super.onCheckedChanged(compoundButton, isChecked);
        }
    }

    private boolean isValidChoice(String parentKey, String childKey) {
        try {
            JSONObject form = formFragment.getJsonApi().getmJSONObject();
            JSONObject step1 = form.getJSONObject(STEP1);
            JSONArray fields = step1.getJSONArray(FIELDS);
            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.getJSONObject(i);
                if (field.has(VALUE) && !field.getString(KEY).equalsIgnoreCase(parentKey)) {
                    String values = field.getString(VALUE);
                    if (!values.isEmpty()
                            && valueContainsKey(getArray(values), childKey)) {
                        return false;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    private String[] getArray(String values) {
        try {
            return new Gson().fromJson(values, String[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    private boolean valueContainsKey(String[] values, String childKey) {
        for (String value : values) {
            String val = value.split(" ")[0];
            String key = childKey.split(" ")[0];
            if (val.equalsIgnoreCase(key))
                return true;
        }
        return false;
    }

}
