package org.smartregister.pathzeir.presenter;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.core.util.Pair;

import com.google.gson.Gson;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.presenter.ChildFormFragmentPresenter;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationTag;
import org.smartregister.pathzeir.R;
import org.smartregister.pathzeir.activity.ChildFormActivity;
import org.smartregister.pathzeir.fragment.AppChildFormFragment;
import org.smartregister.pathzeir.util.AppConstants;
import org.smartregister.repository.AllSharedPreferences;

import java.util.List;

import timber.log.Timber;

import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP1;
import static org.smartregister.pathzeir.util.AppConstants.KEY.BIRTH_FACILITY_NAME;
import static org.smartregister.pathzeir.util.AppConstants.KEY.HOME_FACILITY;

public class AppChildFormFragmentPresenter extends ChildFormFragmentPresenter {

    private final AppChildFormFragment formFragment;
    private final ChildFormActivity jsonFormView;

    public AppChildFormFragmentPresenter(JsonFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
        this.formFragment = (AppChildFormFragment) formFragment;
        jsonFormView = (ChildFormActivity) formFragment.getActivity();
    }

    @Override
    public void addFormElements() {
        super.addFormElements();
        String encounterType = null;
        try {
            encounterType = formFragment.getJsonApi().getmJSONObject().getString(JsonFormConstants.ENCOUNTER_TYPE);
        } catch (JSONException e) {
            Timber.e(e, "Encounter type missing");
        }
        if (AppConstants.EventType.CHILD_REGISTRATION.equalsIgnoreCase(encounterType)) {
            List<LocationTag> tags = getDistrictTags();
            String districtId = (tags != null && tags.size() > 0) ? tags.get(0).getLocationId() : "";
            populateLocationSpinner(districtId, HOME_FACILITY);
            populateLocationSpinner(districtId, BIRTH_FACILITY_NAME);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        super.onItemSelected(parent, view, position, id);
        String key = (String) parent.getTag(R.id.key);
        try {

            if (key.equals(AppConstants.KEY.REACTION_VACCINE)) {
                MaterialSpinner spinnerReactionVaccine = (MaterialSpinner) ((ChildFormActivity)
                        formFragment.requireActivity()).getFormDataView(
                        JsonFormConstants.STEP1 + ":" + AppConstants.KEY.REACTION_VACCINE);
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

    private List<LocationTag> getDistrictTags() {
        return ChildLibrary.getInstance().context().getLocationTagRepository().getLocationTagsByTagName(AppConstants.KEY.DISTRICT);
    }

    private void populateLocationSpinner(String parentLocationId, String spinnerKey) {
        List<Location> locations = getLocationsByParentId(parentLocationId);
        String selectedLocation = getCurrentLocation(spinnerKey);

        MaterialSpinner spinner = (MaterialSpinner) jsonFormView.getFormDataView(JsonFormConstants.STEP1 + ":" + spinnerKey);
        if (locations != null && !locations.isEmpty() && StringUtils.isNotBlank(selectedLocation)) {
            Pair<JSONArray, JSONArray> options = populateLocationOptions(locations);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getView().getContext(), R.layout.native_form_simple_list_item_1,
                    new Gson().fromJson(options.second != null ? options.second.toString() : null, String[].class));
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(formFragment.getCommonListener());
            spinner.setTag(R.id.keys, options.first);
            spinner.setVisibility(View.VISIBLE);
            spinner.setSelection(adapter.getPosition(selectedLocation) + 1);
        } else {
            spinner.setVisibility(View.GONE);
        }
    }

    private List<Location> getLocationsByParentId(String parentId) {
        return ChildLibrary.getInstance().getLocationRepository().getLocationsByParentId(parentId);
    }

    private String getCurrentLocation(String level) {
        String facilityId = getAllSharedPreferences().fetchUserLocalityId(getAllSharedPreferences().fetchRegisteredANM());

        try {
            JSONObject form = jsonFormView.getmJSONObject();
            if (form.getString(ChildJsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.UPDATE_BITRH_REGISTRATION)) {
                String fieldValue = ChildJsonFormUtils.getFieldValue(form.getJSONObject(STEP1).getJSONArray(FIELDS), level);
                facilityId = StringUtils.isBlank(fieldValue) ? facilityId : fieldValue;
            }
        } catch (JSONException e) {
            Timber.e(e, "Error loading current location");
        }

        Location facility = getLocationById(facilityId);

        return facility != null ? facility.getProperties().getName() : null;
    }

    private AllSharedPreferences getAllSharedPreferences() {
        return CoreLibrary.getInstance().context().allSharedPreferences();
    }

    private Location getLocationById(String locationId) {
        return ChildLibrary.getInstance().getLocationRepository().getLocationById(locationId);
    }

    public Pair<JSONArray, JSONArray> populateLocationOptions(List<Location> locations) {
        if (locations == null)
            return null;
        JSONArray codes = new JSONArray();
        JSONArray values = new JSONArray();

        for (Location location : locations) {
            codes.put(location.getId());
            values.put(location.getProperties().getName());
        }

        return new Pair<>(codes, values);
    }

}
