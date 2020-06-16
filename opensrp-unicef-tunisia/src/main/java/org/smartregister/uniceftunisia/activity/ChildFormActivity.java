package org.smartregister.uniceftunisia.activity;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.uniceftunisia.fragment.AppChildFormFragment;

import static org.smartregister.uniceftunisia.fragment.AppChildFormFragment.getFormFragment;

public class ChildFormActivity extends BaseChildFormActivity {
    @Override
    public void initializeFormFragment() {
        initializeFormFragmentCore();
    }

    protected void initializeFormFragmentCore() {
        AppChildFormFragment AppChildFormFragment = getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction().add(com.vijay.jsonwizard.R.id.container, AppChildFormFragment).commit();
    }

}
