package org.smartregister.uniceftunisia.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import org.smartregister.uniceftunisia.activity.ChildRegisterActivity;
import org.smartregister.uniceftunisia.adapter.NavigationAdapter;
import org.smartregister.uniceftunisia.util.AppConstants;
import org.smartregister.uniceftunisia.view.NavDrawerActivity;
import org.smartregister.uniceftunisia.view.NavigationMenu;

public class NavigationListener implements View.OnClickListener {

    private final Activity activity;
    private final NavigationAdapter navigationAdapter;

    public NavigationListener(Activity activity, NavigationAdapter adapter) {
        this.activity = activity;
        this.navigationAdapter = adapter;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null && v.getTag() instanceof String) {
            String tag = (String) v.getTag();

            if (AppConstants.DrawerMenu.CHILD_CLIENTS.equals(tag)) {
                navigateToActivity();
            }
            navigationAdapter.setSelectedView(tag);
        }
    }

    private void navigateToActivity() {
        NavigationMenu.closeDrawer();

        if (activity instanceof NavDrawerActivity) {
            ((NavDrawerActivity) activity).finishActivity();
        } else {
            activity.finish();
        }

        activity.startActivity(new Intent(activity, ChildRegisterActivity.class));
    }
}
