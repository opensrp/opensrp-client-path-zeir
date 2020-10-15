package org.smartregister.uniceftunisia.fragment;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import org.smartregister.uniceftunisia.activity.HIA2ReportsActivity;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2020-01-07
 */

public abstract class ReportFragment extends Fragment implements HIA2ReportsActivity.FragmentRefreshListener {

    public String reportGrouping;

    @Override
    public void onResume() {
        super.onResume();
        Activity activity = getActivity();

        if (activity instanceof HIA2ReportsActivity) {
            ((HIA2ReportsActivity) activity).addFragmentRefreshListener(this);
        }

        refreshData();
    }

    @Override
    public void onPause() {
        super.onPause();
        Activity activity = getActivity();

        if (activity instanceof HIA2ReportsActivity) {
            ((HIA2ReportsActivity) activity).removeFragmentRefreshListener(this);
        }
    }

    @Override
    public void onRefresh() {
        refreshData();
    }

    public abstract void refreshData();
}
