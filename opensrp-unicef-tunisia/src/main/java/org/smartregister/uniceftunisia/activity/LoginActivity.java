package org.smartregister.uniceftunisia.activity;

import android.content.Intent;
import android.os.Bundle;

import org.smartregister.growthmonitoring.service.intent.WeightForHeightIntentService;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.uniceftunisia.R;
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication;
import org.smartregister.uniceftunisia.presenter.LoginPresenter;
import org.smartregister.uniceftunisia.util.AppConstants;
import org.smartregister.view.activity.BaseLoginActivity;
import org.smartregister.view.contract.BaseLoginContract;

public class LoginActivity extends BaseLoginActivity implements BaseLoginContract.View {

    private static final String WFH_CSV_PARSED = "WEIGHT_FOR_HEIGHT_CSV_PARSED";

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginPresenter.processViewCustomizations();
        if (!mLoginPresenter.isUserLoggedOut()) {
            goToHome(false);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initializePresenter() {
        mLoginPresenter = new LoginPresenter(this);
    }

    @Override
    public void goToHome(boolean remote) {
        if (remote) {
            LocationHelper.getInstance().locationIdsFromHierarchy();
            processWeightForHeightZScoreCSV();
        }

        if (mLoginPresenter.isServerSettingsSet()) {
            Intent intent = new Intent(this, ChildRegisterActivity.class);
            intent.putExtra(AppConstants.IntentKeyUtil.IS_REMOTE_LOGIN, remote);
            startActivity(intent);
        }

        finish();
    }

    private void processWeightForHeightZScoreCSV() {
        AllSharedPreferences allSharedPreferences = UnicefTunisiaApplication.getInstance().getContext().allSharedPreferences();
        if (!allSharedPreferences.getPreference(WFH_CSV_PARSED).equals("true")) {
            WeightForHeightIntentService.startParseWFHZScores(this);
            allSharedPreferences.savePreference(WFH_CSV_PARSED, "true");
        }
    }
}
