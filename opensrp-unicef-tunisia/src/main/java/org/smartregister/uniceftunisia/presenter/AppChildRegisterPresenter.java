package org.smartregister.uniceftunisia.presenter;

import android.app.Activity;

import org.smartregister.child.contract.ChildRegisterContract;
import org.smartregister.child.presenter.BaseChildRegisterPresenter;
import org.smartregister.uniceftunisia.view.NavigationMenu;

public class AppChildRegisterPresenter extends BaseChildRegisterPresenter {
    public AppChildRegisterPresenter(ChildRegisterContract.View view, ChildRegisterContract.Model model) {
        super(view, model);
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        super.onRegistrationSaved(isEdit);
        NavigationMenu navigationMenu = NavigationMenu.getInstance((Activity) viewReference.get(), null, null);
        if (navigationMenu != null) {
            navigationMenu.runRegisterCount();
        }
    }
}
