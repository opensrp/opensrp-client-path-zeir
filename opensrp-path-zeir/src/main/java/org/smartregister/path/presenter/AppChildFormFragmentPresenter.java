package org.smartregister.path.presenter;

import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP1;
import static org.smartregister.path.util.AppConstants.KeyConstants.KEY;

import android.view.View;
import android.widget.AdapterView;
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
import org.smartregister.child.presenter.ChildFormFragmentPresenter;
import org.smartregister.path.R;
import org.smartregister.path.activity.ChildFormActivity;
import org.smartregister.path.fragment.AppChildFormFragment;
import org.smartregister.path.util.AppConstants;

import timber.log.Timber;

public class AppChildFormFragmentPresenter extends ChildFormFragmentPresenter {

    private final AppChildFormFragment formFragment;
    private String encounterType = null;

    public AppChildFormFragmentPresenter(JsonFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
        this.formFragment = (AppChildFormFragment) formFragment;
    }

    @Override
    public void addFormElements() {
        encounterType = formFragment.getJsonApi().getmJSONObject()
                .optString(JsonFormConstants.ENCOUNTER_TYPE, "");
        super.addFormElements();
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
                && AppConstants.EventTypeConstants.OUT_OF_CATCHMENT.equalsIgnoreCase(encounterType)
                && compoundButton.isChecked()) {
            String parentKey = (String) compoundButton.getTag(R.id.key);
            String childKey = (String) compoundButton.getTag(R.id.childKey);

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
                if (field.has(JsonFormConstants.VALUE) && !field.getString(KEY).equalsIgnoreCase(parentKey)) {
                    String values = field.getString(JsonFormConstants.VALUE);
                    if (!values.isEmpty()
                            && valueContainsKey(getArray(values), childKey)) {
                        return false;
                    }
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
        return true;
    }

    private String[] getArray(String values) {
        try {
            return new Gson().fromJson(values, String[].class);
        } catch (Exception e) {
            Timber.e(e);
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
