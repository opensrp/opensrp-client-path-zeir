package org.smartregister.uniceftunisia.fragment;

import android.text.TextUtils;

import org.smartregister.child.adapter.ChildRegistrationDataAdapter;
import org.smartregister.child.domain.KeyValueItem;
import org.smartregister.child.fragment.BaseChildRegistrationDataFragment;
import org.smartregister.uniceftunisia.R;
import org.smartregister.uniceftunisia.util.AppConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 2019-05-30.
 */
public class ChildRegistrationDataFragment extends BaseChildRegistrationDataFragment {

    @Override
    protected Map<String, Integer> getDataRowLabelResourceIds() {
        Map<String, Integer> resourceIds = new HashMap<>();

        resourceIds.put("birth_registration_number", R.string.birth_registration_number);
        resourceIds.put("first_name", R.string.first_name);
        resourceIds.put("last_name", R.string.last_name);
        resourceIds.put("middle_name", R.string.middle_name);
        resourceIds.put("Sex", R.string.sex);
        resourceIds.put("Date_Birth", R.string.child_dob);
        resourceIds.put("Birth_Weight", R.string.birth_weight);
        resourceIds.put("Birth_Height", R.string.birth_height);
        resourceIds.put("home_addresss", R.string.home_address);
        resourceIds.put("village", R.string.village);
        resourceIds.put("traditional_authority", R.string.traditional_authority);
        resourceIds.put("mother_guardian_first_name", R.string.mother_guardian_name);
        resourceIds.put("mother_guardian_last_name", R.string.mother_second_name);
        resourceIds.put("mother_dob", R.string.mother_guardian_dob);
        resourceIds.put("nrc_number", R.string.mother_guardian_nid);
        resourceIds.put("mother_guardian_number", R.string.mother_guardian_phone_number);
        resourceIds.put("second_phone_number", R.string.father_guardian_phone_number);
        resourceIds.put("protected_at_birth", R.string.protected_at_birth);
        resourceIds.put("mother_hiv_status", R.string.mother_hiv_status);
        resourceIds.put("child_hiv_status", R.string.child_hiv_status);
        resourceIds.put("child_treatment", R.string.child_treatment);

        return resourceIds;
    }

    @Override
    public String getRegistrationForm() {
        return AppConstants.JSON_FORM.CHILD_ENROLLMENT;
    }

    @Override
    public void resetAdapterData(Map<String, String> detailsMap) {
        List<KeyValueItem> mArrayList = new ArrayList<>();
        String key;
        String value;

        for (int i = 0; i < getFields().size(); i++) {
            key = getFields().get(i).getKey();
            value = detailsMap.get(key);
            value = !TextUtils.isEmpty(value) ? value : detailsMap.get(getPrefix(getFields().get(i).getEntityId()) +
                    cleanOpenMRSEntityId(getFields().get(i).getOpenmrsEntityId().toLowerCase()));
            String label = cleanLabel(key);

            if (!TextUtils.isEmpty(value) && !TextUtils.isEmpty(label)) {
                mArrayList.add(new KeyValueItem(label, cleanValue(getFields().get(i), value)));
            }

        }

        setmAdapter(new ChildRegistrationDataAdapter(mArrayList));
    }

    @Override
    protected List<String> addUnFormattedNumberFields(String... key) {
        return Arrays.asList("mother_guardian_number");
    }
}
