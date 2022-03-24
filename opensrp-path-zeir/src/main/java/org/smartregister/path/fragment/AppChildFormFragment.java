package org.smartregister.path.fragment;

import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP1;
import static org.smartregister.path.util.AppConstants.KeyConstants.OA_SERVICE_DATE;
import static org.smartregister.path.util.AppConstants.KeyConstants.OPENSRP_ID;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.smartregister.child.fragment.ChildFormFragment;
import org.smartregister.child.presenter.ChildFormFragmentPresenter;
import org.smartregister.path.R;
import org.smartregister.path.activity.ChildFormActivity;
import org.smartregister.path.contract.ChildFormContract;
import org.smartregister.path.interactor.ChildFormInteractor;
import org.smartregister.path.presenter.AppChildFormFragmentPresenter;
import org.smartregister.path.util.AppConstants;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import timber.log.Timber;

public class AppChildFormFragment extends ChildFormFragment {

    private OnReactionVaccineSelected OnReactionVaccineSelected;
    private ChildFormActivity childFormActivity;

    public static AppChildFormFragment getFormFragment(String stepName) {
        AppChildFormFragment jsonFormFragment = new AppChildFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    public OnReactionVaccineSelected getOnReactionVaccineSelected() {
        return OnReactionVaccineSelected;
    }

    public void setOnReactionVaccineSelected(OnReactionVaccineSelected onReactionVaccineSelected) {
        this.OnReactionVaccineSelected = onReactionVaccineSelected;
    }

    @Override
    protected ChildFormFragmentPresenter createPresenter() {
        WeakReference<JsonFormFragment> weakReference = new WeakReference<>(this);
        return new AppChildFormFragmentPresenter(weakReference.get(), ChildFormInteractor.getInstance());
    }

    public interface OnReactionVaccineSelected {
        void updateDatePicker(String date);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        childFormActivity = (ChildFormActivity) requireActivity();
    }

    @Override
    public void onDestroy() {
        setOnReactionVaccineSelected(null);
        super.onDestroy();
        ChildFormContract.Presenter childFormFragmentPresenter = (ChildFormContract.Presenter) this.presenter;
        childFormFragmentPresenter.tearDown();

    }

    @Override
    protected @NotNull HashMap<String, String> getKeyAliasMap() {
        return new HashMap<String, String>() {
            {
                put("mother_guardian_last_name", AppConstants.KeyConstants.LAST_NAME);
                put("mother_guardian_first_name", AppConstants.KeyConstants.FIRST_NAME);
                put("mother_guardian_date_birth", AppConstants.KeyConstants.DOB);
            }
        };
    }

    @Override
    protected @NotNull HashSet<String> getNonHumanizedFields() {
        return new HashSet<String>() {
            {
                add("mother_nationality_other");
            }
        };
    }

    @Override
    public void addFormElements(List<View> views) {
        super.addFormElements(views);
        JSONObject jsonObject = getJsonApi().getmJSONObject();
        String encounterType = jsonObject.optString(JsonFormConstants.ENCOUNTER_TYPE, "");
        if (AppConstants.EventTypeConstants.OUT_OF_CATCHMENT.equalsIgnoreCase(encounterType)) {
            disableViews(Arrays.asList(STEP1 + ":" + OA_SERVICE_DATE, STEP1 + ":" + OPENSRP_ID));
        }
    }

    public void disableViews(List<String> skippedViews) {
        toggleReadOnly(false, skippedViews);
        MaterialEditText serviceDateEditText = (MaterialEditText) childFormActivity.getFormDataView(
                STEP1 + ":" + OA_SERVICE_DATE);
        serviceDateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                //Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                //Do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                toggleReadOnly(StringUtils.isNotBlank(editable.toString()), skippedViews);
            }
        });
    }

    private void toggleReadOnly(boolean enabled, List<String> skippedViews) {
        Collection<View> formDataViews = childFormActivity.getFormDataViews();
        for (View formDataView : formDataViews) {
            String address = (String) formDataView.getTag(R.id.address);
            if (address != null && skippedViews.contains(address))
                continue;
            setViewAndChildrenEnabled(formDataView, enabled);
        }
    }

    private static void setViewAndChildrenEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }
        }
    }
}
