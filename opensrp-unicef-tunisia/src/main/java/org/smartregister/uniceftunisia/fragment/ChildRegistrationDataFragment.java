package org.smartregister.uniceftunisia.fragment;

import android.os.Bundle;

import org.smartregister.child.fragment.BaseChildRegistrationDataFragment;
import org.smartregister.uniceftunisia.util.AppConstants;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ndegwamartin on 2019-05-30.
 */
public class ChildRegistrationDataFragment extends BaseChildRegistrationDataFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fieldNameAliasMap.put("mother_guardian_number", "mother_phone_number");
        fieldNameAliasMap.put("second_phone_number", "mother_second_phone_number");
        fieldNameAliasMap.put("father_phone", "father_phone_number");
    }

    @Override
    public String getRegistrationForm() {
        return AppConstants.JSON_FORM.CHILD_ENROLLMENT;
    }


    @Override
    protected List<String> addUnFormattedNumberFields(String... key) {
        return Arrays.asList("mother_guardian_number");
    }
}
