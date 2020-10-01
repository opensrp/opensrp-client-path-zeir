package org.smartregister.uniceftunisia;

import android.app.Activity;

import org.robolectric.android.controller.ActivityController;

import timber.log.Timber;

public abstract class BaseActivityUnitTest extends BaseUnitTest {

    protected void destroyController() {
        try {
            getActivity().finish();
            getActivityController().pause().stop().destroy(); //destroy controller if we can

        } catch (Exception e) {
            Timber.e(e);
        }
        System.gc();
    }

    protected abstract Activity getActivity();

    protected abstract ActivityController getActivityController();
}
