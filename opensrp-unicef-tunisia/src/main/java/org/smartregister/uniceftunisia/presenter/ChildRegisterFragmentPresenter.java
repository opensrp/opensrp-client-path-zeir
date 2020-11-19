package org.smartregister.uniceftunisia.presenter;

import org.smartregister.child.contract.ChildRegisterFragmentContract;
import org.smartregister.child.presenter.BaseChildRegisterFragmentPresenter;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.uniceftunisia.util.DBQueryHelper;

public class ChildRegisterFragmentPresenter extends BaseChildRegisterFragmentPresenter {

    public ChildRegisterFragmentPresenter(ChildRegisterFragmentContract.View view, ChildRegisterFragmentContract.Model model,
                                          String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getMainCondition() {
        return String.format("(%s IS NULL AND %s is null AND %s IS NOT '1')",
                Utils.metadata().getRegisterQueryProvider().getDemographicTable() + "." + Constants.KEY.DOD,
                Utils.metadata().getRegisterQueryProvider().getDemographicTable() + "." + Constants.KEY.DATE_REMOVED,
                Utils.metadata().getRegisterQueryProvider().getDemographicTable() + "." + Constants.KEY.IS_CLOSED);
    }

    @Override
    public String getDefaultSortQuery() {
        return DBQueryHelper.getSortQuery();
    }
}
