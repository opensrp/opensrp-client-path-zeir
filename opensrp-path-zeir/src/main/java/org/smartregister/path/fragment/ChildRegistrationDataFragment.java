package org.smartregister.path.fragment;

import android.os.Bundle;
import android.text.TextUtils;

import org.smartregister.child.domain.Field;
import org.smartregister.child.domain.KeyValueItem;
import org.smartregister.child.fragment.BaseChildRegistrationDataFragment;
import org.smartregister.path.R;
import org.smartregister.path.activity.ChildDetailTabbedActivity;
import org.smartregister.path.util.AppConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 2019-05-30.
 */
public class ChildRegistrationDataFragment extends BaseChildRegistrationDataFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fieldNameAliasMap = new HashMap<String, String>() {
            {
                put("mother_guardian_number", "mother_phone_number");
            }
        };

    }

    @Override
    public String getRegistrationForm() {
        return AppConstants.JsonForm.CHILD_ENROLLMENT;
    }


    @Override
    protected List<String> addUnFormattedNumberFields(String... key) {
        List<String> unFormattedFields = new ArrayList<>();
        unFormattedFields.add("mother_guardian_number");
        unFormattedFields.add("sms_reminder_phone");
        return unFormattedFields;
    }

    @Override
    protected Map<String, String> getDataRowLabelResourceIds() {
        fieldNameResourceMap = new HashMap<String, Integer>() {
            {
                put("Date_Birth", R.string.child_DOB);
                put("mother_guardian_date_birth", R.string.mother_dob);
                put("birth_facility_name", R.string.birth_health_facility);
            }
        };
        return super.getDataRowLabelResourceIds();
    }

    @Override
    public void resetAdapterData(Map<String, String> detailsMap) {
        if (getActivity() instanceof ChildDetailTabbedActivity) {
            ChildDetailTabbedActivity childDetailTabbedActivity = (ChildDetailTabbedActivity) getActivity();
            childDetailTabbedActivity.updateChildDetails();
            detailsMap.putAll(childDetailTabbedActivity.getChildDetails().getColumnmaps());
        }
        super.resetAdapterData(detailsMap);
    }

    @Override
    protected void addDetail(String key, String value, Field field) {
        String resourceLabel = getResourceLabel(key);
        if (!TextUtils.isEmpty(resourceLabel)) {
            getDetailsList().add(new KeyValueItem(resourceLabel, cleanValue(field, value)));
        }
    }

    @Override
    public String cleanValue(Field field, String raw) {
        if (raw == null) {
            return "";
        }
        return super.cleanValue(field, raw);
    }
}
