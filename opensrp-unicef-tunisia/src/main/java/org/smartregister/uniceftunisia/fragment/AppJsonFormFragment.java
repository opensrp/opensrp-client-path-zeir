package org.smartregister.uniceftunisia.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.child.interactor.ChildFormInteractor;
import org.smartregister.uniceftunisia.R;
import org.smartregister.uniceftunisia.activity.AppStockJsonFormActivity;
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication;
import org.smartregister.uniceftunisia.util.AppConstants;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

public class AppJsonFormFragment extends JsonFormFragment {

    public static AppJsonFormFragment getFormFragment(String stepName) {
        AppJsonFormFragment jsonFormFragment = new AppJsonFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected JsonFormFragmentViewState createViewState() {
        return new JsonFormFragmentViewState();
    }

    @Override
    protected JsonFormFragmentPresenter createPresenter() {
        return new JsonFormFragmentPresenter(this, ChildFormInteractor.getChildInteractorInstance());
    }

    public Context context() {
        return UnicefTunisiaApplication.getInstance().context();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean fillFormCheck = true;

        if (item.getItemId() == com.vijay.jsonwizard.R.id.action_save && getActivity() != null) {
            JSONObject object = getStep(JsonFormConstants.STEP1);
            try {
                if (object.getString(AppConstants.KEY.TITLE).contains("Record out of catchment area service")) {
                    fillFormCheck = ((AppStockJsonFormActivity) getActivity()).checkIfAtLeastOneServiceGiven();
                }
            } catch (Exception e) {
                Timber.e(e);
            }
        }

        if (fillFormCheck) {
            return super.onOptionsItemSelected(item);
        } else {
            String errorMessage = getString(R.string.fill_form_error_msg);

            final Snackbar snackbar = Snackbar
                    .make(getMainView(), errorMessage, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.close, view -> snackbar.dismiss());

            // Changing message text color
            snackbar.setActionTextColor(Color.WHITE);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
            return true;
        }
    }
}