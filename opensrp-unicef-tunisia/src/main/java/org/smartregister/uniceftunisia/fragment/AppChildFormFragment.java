package org.smartregister.uniceftunisia.fragment;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.fragment.ChildFormFragment;
import org.smartregister.child.presenter.ChildFormFragmentPresenter;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.uniceftunisia.R;
import org.smartregister.uniceftunisia.interactor.ChildFormInteractor;
import org.smartregister.uniceftunisia.presenter.AppChildFormFragmentPresenter;
import org.smartregister.uniceftunisia.util.AppConstants;
import org.smartregister.util.Utils;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.util.Utils.getValue;

public class AppChildFormFragment extends ChildFormFragment {

    private OnReactionVaccineSelected OnReactionVaccineSelected;

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
        WeakReference weakReference = new WeakReference<>(this);
        return new AppChildFormFragmentPresenter((JsonFormFragment) weakReference.get(),
                ChildFormInteractor.getChildInteractorInstance());
    }

    public interface OnReactionVaccineSelected {
        void updateDatePicker(String date);
    }

    @Override
    public void onDestroy() {
        setOnReactionVaccineSelected(null);
        super.onDestroy();
    }

    @Override
    protected void lookupDialogDismissed(CommonPersonObjectClient client) {
        final Map<String, String> keyAliasMap = new HashMap<String, String>() {
            {
                put("mother_guardian_last_name", AppConstants.KEY.LAST_NAME);
                put("mother_guardian_first_name", AppConstants.KEY.FIRST_NAME);
                put("mother_guardian_date_birth", AppConstants.KEY.DOB);
            }
        };
        if (client != null && getActivity() != null) {
            Map<String, List<View>> lookupMap = getLookUpMap();
            if (lookupMap.containsKey(Constants.KEY.MOTHER)) {
                List<View> lookUpViews = lookupMap.get(Constants.KEY.MOTHER);
                if (lookUpViews != null && !lookUpViews.isEmpty()) {
                    for (View view : lookUpViews) {
                        String key = (String) view.getTag(com.vijay.jsonwizard.R.id.key);
                        String fieldName = keyAliasMap.get(key) != null ? keyAliasMap.get(key) : key;
                        String value = getCurrentFieldValue(client.getColumnmaps(), fieldName);
                        if (StringUtils.isNotBlank(value))
                            setValueOnView(value, view);
                    }
                    updateFormLookupField(client);
                }
            }
        }
    }

    private String getCurrentFieldValue(Map<String, String> columnMaps, String fieldName) {
        String value = getValue(columnMaps, fieldName, true);
        if (getActivity() != null) {
            Locale locale = getActivity().getResources().getConfiguration().locale;
            SimpleDateFormat mlsLookupDateFormatter = new SimpleDateFormat(FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN,
                    locale.getLanguage().equals("ar") ? Locale.ENGLISH : locale);
            if (fieldName.equalsIgnoreCase(AppConstants.KEY.DOB)) {
                String dobString = getValue(columnMaps, AppConstants.KEY.DOB, false);
                Date motherDob = Utils.dobStringToDate(dobString);
                if (motherDob != null) {
                    try {
                        value = mlsLookupDateFormatter.format(motherDob);
                    } catch (Exception e) {
                        Timber.e(e, e.toString());
                    }
                }
            }
        }
        return value;
    }

    private void setValueOnView(String value, View view) {
        if (view instanceof MaterialEditText) {
            MaterialEditText materialEditText = (MaterialEditText) view;
            materialEditText.setEnabled(false);
            materialEditText.setTag(R.id.after_look_up, true);
            materialEditText.setText(value);
            materialEditText.setInputType(InputType.TYPE_NULL);

        } else if (view instanceof RelativeLayout) {
            ViewGroup spinnerViewGroup = (ViewGroup) view;
            if (spinnerViewGroup.getChildAt(0) instanceof MaterialSpinner) {
                MaterialSpinner spinner = (MaterialSpinner) spinnerViewGroup.getChildAt(0);
                for (int index = 0; index < spinner.getAdapter().getCount(); index++) {
                    if (String.valueOf(spinner.getAdapter().getItem(index)).equalsIgnoreCase(value)) {
                        spinner.setSelection(index + 1);
                        break;
                    }
                }
                spinner.setEnabled(false);
            }
        }
    }
}
