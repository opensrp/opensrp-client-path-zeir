package org.smartregister.path.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.smartregister.AllConstants;
import org.smartregister.child.activity.BaseChildImmunizationActivity;
import org.smartregister.child.domain.RegisterClickables;
import org.smartregister.child.toolbar.LocationSwitcherToolbar;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.immunization.domain.ServiceSchedule;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.job.VaccineSchedulesUpdateJob;
import org.smartregister.path.application.ZeirApplication;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class ChildImmunizationActivity extends BaseChildImmunizationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationSwitcherToolbar myToolbar = (LocationSwitcherToolbar) this.getToolbar();
        if (myToolbar != null) {
            myToolbar.setOnLocationChangeListener(v -> finish());
        }
    }

    @Override
    protected void goToRegisterPage() {
        Intent intent = new Intent(this, ChildRegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected int getToolbarId() {
        return LocationSwitcherToolbar.TOOLBAR_ID;
    }

    @Override
    protected int getDrawerLayoutId() {
        return 0;
    }

    @Override
    public void launchDetailActivity(Context fromContext, CommonPersonObjectClient childDetails,
                                     RegisterClickables registerClickables) {

        Intent intent = new Intent(fromContext, ChildDetailTabbedActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.INTENT_KEY.LOCATION_ID,
                Utils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID));
        bundle.putSerializable(Constants.INTENT_KEY.EXTRA_CHILD_DETAILS, childDetails);
        bundle.putSerializable(Constants.INTENT_KEY.BASE_ENTITY_ID, childDetails.getCaseId());
        bundle.putSerializable(Constants.INTENT_KEY.EXTRA_REGISTER_CLICKABLES, registerClickables);
        intent.putExtras(bundle);

        fromContext.startActivity(intent);
    }

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    public boolean isLastModified() {
        ZeirApplication application = (ZeirApplication) getApplication();
        return application.isLastModified();
    }

    @Override
    public void setLastModified(boolean lastModified) {
        ZeirApplication application = (ZeirApplication) getApplication();
        if (lastModified != application.isLastModified()) {
            application.setLastModified(lastModified);
        }
    }

    @Override
    public void onClick(View view) {
        // Overridden
    }

    @Override
    public void onUniqueIdFetched(Triple<String, Map<String, String>, String> triple, String s) {
        // Overridden
    }

    @Override
    public void onNoUniqueId() {
        // Overridden
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        hideProgressDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void updateScheduleDate() {
        try {
            Calendar calendar = Calendar.getInstance();
            if (calendar.get(Calendar.HOUR_OF_DAY) != 0 && calendar.get(Calendar.HOUR_OF_DAY) != 1) {
                calendar.set(Calendar.HOUR_OF_DAY, 1);
                long hoursSince1AM = (System.currentTimeMillis() - calendar.getTimeInMillis()) / TimeUnit.HOURS.toMillis(1);
                if (VaccineSchedulesUpdateJob.isLastTimeRunLongerThan(hoursSince1AM) && !ZeirApplication.getInstance().alertUpdatedRepository().findOne(childDetails.entityId())) {
                    String dobString = Utils.getValue(childDetails.getColumnmaps(), "dob", false);
                    DateTime dateTime = Utils.dobStringToDateTime(dobString);
                    if (dateTime != null) {
                        VaccineSchedule.updateOfflineAlerts(childDetails.entityId(), dateTime, "child");
                        ServiceSchedule.updateOfflineAlerts(childDetails.entityId(), dateTime);
                    }
                    ZeirApplication.getInstance().alertUpdatedRepository().saveOrUpdate(childDetails.entityId());
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
