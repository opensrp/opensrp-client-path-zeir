package org.smartregister.uniceftunisia.fragment;

import org.smartregister.child.fragment.BaseChildRegistrationDataFragment;
import org.smartregister.uniceftunisia.util.AppConstants;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ndegwamartin on 2019-05-30.
 */
public class ChildRegistrationDataFragment extends BaseChildRegistrationDataFragment {

    @Override
    public String getRegistrationForm() {
        return AppConstants.JSON_FORM.CHILD_ENROLLMENT;
    }


    @Override
    protected List<String> addUnFormattedNumberFields(String... key) {
        return Arrays.asList("mother_guardian_number");
    }
}
