package org.smartregister.uniceftunisia.fragment;

import android.os.Bundle;

import org.smartregister.child.fragment.BaseChildRegistrationDataFragment;
import org.smartregister.uniceftunisia.R;
import org.smartregister.uniceftunisia.util.AppConstants;

import java.util.Arrays;
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
                put("second_phone_number", "mother_second_phone_number");
                put("father_phone", "father_phone_number");
            }
        };
    }

    @Override
    public String getRegistrationForm() {
        return AppConstants.JSON_FORM.CHILD_ENROLLMENT;
    }


    @Override
    protected List<String> addUnFormattedNumberFields(String... key) {
        return Arrays.asList("mother_guardian_number", "second_phone_number");
    }

    @Override
    protected Map<String, String> getDataRowLabelResourceIds() {
        fieldNameResourceMap = new HashMap<String, Integer>() {
            {
                put("Date_Birth", R.string.child_DOB);
                put("father_last_name", R.string.father_last_name);
                put("father_first_name", R.string.father_first_name);
                put("father_dob", R.string.father_dob);
                put("father_nationality", R.string.father_nationality);
                put("father_nationality_other", R.string.father_nationality_other);
                put("father_phone", R.string.father_phone);
                put("mother_guardian_last_name", R.string.mother_caregiver_last_name);
                put("mother_guardian_first_name", R.string.mother_caregiver_first_name);
                put("mother_guardian_date_birth", R.string.mother_dob);
                put("mother_nationality", R.string.mother_nationality);
                put("mother_nationality_other", R.string.mother_nationality_other);
                put("mother_guardian_number", R.string.mother_caregiver_phone);
                put("second_phone_number", R.string.mother_caregiver_alt_phone);
                put("mother_tdv_doses", R.string.mother_tdv_doses);
                put("mother_rubella", R.string.mother_rubella);
                put("first_birth", R.string.first_birth);
                put("rubella_serology", R.string.rubella_serology);
                put("serology_results", R.string.serology_results);
            }
        };
        return super.getDataRowLabelResourceIds();
    }
}
