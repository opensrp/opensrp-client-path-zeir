package org.smartregister.pathzeir.presenter;

import org.smartregister.child.contract.ChildRegisterFragmentContract;
import org.smartregister.child.presenter.BaseChildRegisterFragmentPresenter;
import org.smartregister.pathzeir.util.DBQueryHelper;

public class ChildRegisterFragmentPresenter extends BaseChildRegisterFragmentPresenter {

    public ChildRegisterFragmentPresenter(ChildRegisterFragmentContract.View view, ChildRegisterFragmentContract.Model model,
                                          String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getMainCondition() {
        return "(ec_client.dod IS NULL AND ec_client.date_removed is null AND ec_client.is_closed IS NOT '1' AND ec_child_details.is_closed IS NOT '1')";
    }

    @Override
    public String getDefaultSortQuery() {
        return DBQueryHelper.getSortQuery();
    }
}
