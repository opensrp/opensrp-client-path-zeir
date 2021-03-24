package org.smartregister.pathzeir.fragment;

import android.os.Bundle;
import android.text.TextUtils;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.adapter.ChildRegistrationDataAdapter;
import org.smartregister.child.domain.Field;
import org.smartregister.child.domain.KeyValueItem;
import org.smartregister.child.fragment.BaseChildRegistrationDataFragment;
import org.smartregister.child.util.ChildDbUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.pathzeir.R;
import org.smartregister.pathzeir.util.AppConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
        return Collections.singletonList("mother_guardian_number");
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
        // Add Birth weight to the details
        if (detailsMap.get(AppConstants.KeyConstants.BIRTH_WEIGHT.toLowerCase()) == null) {
            String caseId = detailsMap.get(AppConstants.KeyConstants.BASE_ENTITY_ID);
            Utils.putAll(detailsMap, ChildDbUtils.fetchChildFirstGrowthAndMonitoring(caseId));
            String weight = detailsMap.get(AppConstants.KeyConstants.BIRTH_WEIGHT.toLowerCase());
            if (weight != null && !TextUtils.isEmpty(weight)) {
                detailsMap.put(AppConstants.KeyConstants.BIRTH_WEIGHT.toLowerCase(), Utils.kgStringSuffix(weight));
            }
        }
        List<KeyValueItem> detailsList = new ArrayList<>();
        String key;
        String value;

        for (int i = 0; i < getFields().size(); i++) {
            Field field = getFields().get(i);
            key = field.getKey();

            //Some fields have alias name on query
            if (fieldNameAliasMap.containsKey(key)) {
                String keyAlias = fieldNameAliasMap.get(key);
                value = getFieldValue(detailsMap, field, keyAlias);
            } else {
                value = getFieldValue(detailsMap, field, key);
            }

            //TODO Temporary fix for spinner setting value as hint when nothing is selected
            if (JsonFormConstants.SPINNER.equalsIgnoreCase(field.getType()) && value != null && value.equalsIgnoreCase(field.getHint())) {
                value = null;
            }

            String label = getResourceLabel(key);

            if (!TextUtils.isEmpty(label)) {
                detailsList.add(new KeyValueItem(label, cleanValue(field, value)));
            }
        }
        setmAdapter(new ChildRegistrationDataAdapter(detailsList));
    }

    protected String getFieldValue(Map<String, String> detailsMap, Field field, String key) {
        String value;
        value = detailsMap.get(field.getKey().toLowerCase(Locale.getDefault()));
        value = !StringUtils.isBlank(value) ? value : detailsMap.get(getPrefix(field.getEntityId()) + key.toLowerCase(Locale.getDefault()));
        value = !StringUtils.isBlank(value) ? value : detailsMap.get(getPrefix(field.getEntityId()) + cleanOpenMRSEntityId(field.getOpenmrsEntityId().toLowerCase(Locale.getDefault())));
        value = !StringUtils.isBlank(value) ? value : detailsMap.get(key.toLowerCase(Locale.getDefault()));
        return value;
    }

    @Override
    public String cleanValue(Field field, String raw) {
        if (raw == null) {
            return "";
        }
        return super.cleanValue(field, raw);
    }
}
