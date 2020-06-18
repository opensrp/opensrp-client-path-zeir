package org.smartregister.uniceftunisia.presenter;

import org.smartregister.child.contract.ChildRegisterContract;
import org.smartregister.child.presenter.BaseChildRegisterPresenter;

public class AppChildRegisterPresenter extends BaseChildRegisterPresenter {
    public AppChildRegisterPresenter(ChildRegisterContract.View view, ChildRegisterContract.Model model) {
        super(view, model);
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        super.onRegistrationSaved(isEdit);
    }
}
