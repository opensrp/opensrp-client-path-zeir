package org.smartregister.uniceftunisia.presenter;

import android.database.Cursor;
import android.database.CursorJoiner;

import org.jetbrains.annotations.NotNull;
import org.smartregister.child.contract.ChildAdvancedSearchContract;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.presenter.BaseChildAdvancedSearchPresenter;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBConstants;
import org.smartregister.child.util.Utils;
import org.smartregister.uniceftunisia.cursor.RemoteLocalCursor;
import org.smartregister.uniceftunisia.model.AdvancedSearchModel;
import org.smartregister.uniceftunisia.util.AppConstants;

/**
 * Created by ndegwamartin on 11/04/2019.
 */
public class AdvancedSearchPresenter extends BaseChildAdvancedSearchPresenter {

    public AdvancedSearchPresenter(ChildAdvancedSearchContract.View view, String viewConfigurationIdentifier) {
        super(view, viewConfigurationIdentifier, new AdvancedSearchModel());
    }

    @Override
    protected AdvancedMatrixCursor getRemoteLocalMatrixCursor(AdvancedMatrixCursor remoteCursor) {
        String query = getView().filterAndSortQuery();
        Cursor localCursor = getView().getRawCustomQueryForAdapter(query);
        if (localCursor != null && localCursor.getCount() > 0) {
            AdvancedMatrixCursor remoteLocalCursor = new AdvancedMatrixCursor(
                    new String[]{
                            AppConstants.KEY.ID_LOWER_CASE,
                            AppConstants.KEY.RELATIONALID,
                            AppConstants.KEY.RELATIONAL_ID,
                            AppConstants.KEY.FIRST_NAME,
                            AppConstants.KEY.LAST_NAME,
                            AppConstants.KEY.GENDER,
                            AppConstants.KEY.DOB,
                            AppConstants.KEY.ZEIR_ID,
                            AppConstants.KEY.MOTHER_FIRST_NAME,
                            AppConstants.KEY.MOTHER_LAST_NAME,
                            AppConstants.KEY.INACTIVE,
                            AppConstants.KEY.LOST_TO_FOLLOW_UP
                    });
            CursorJoiner joiner = new CursorJoiner(remoteCursor, new String[]{DBConstants.KEY.ZEIR_ID}, localCursor, new String[]{DBConstants.KEY.ZEIR_ID});
            for (CursorJoiner.Result joinerResult : joiner) {
                switch (joinerResult) {
                    case BOTH:
                        remoteLocalCursor.addRow(getColumnValues(new RemoteLocalCursor(localCursor)));
                        break;
                    case LEFT:
                        remoteLocalCursor.addRow(getColumnValues(new RemoteLocalCursor(remoteCursor)));
                        break;
                    default:
                        break;
                }
            }

            localCursor.close();
            remoteCursor.close();
            return remoteLocalCursor;
        } else {
            return remoteCursor;
        }
    }

    @NotNull
    private Object[] getColumnValues(RemoteLocalCursor remoteLocalCursor) {
        return new Object[]{
                remoteLocalCursor.getId(), remoteLocalCursor.getRelationalId(),
                remoteLocalCursor.getMotherBaseEntityId(),
                remoteLocalCursor.getFirstName(), remoteLocalCursor.getLastName(),
                remoteLocalCursor.getGender(), remoteLocalCursor.getDob(),
                remoteLocalCursor.getOpenSrpId(), remoteLocalCursor.getMotherFirstName(),
                remoteLocalCursor.getMotherLastName(), remoteLocalCursor.getInactive(),
                remoteLocalCursor.getLostToFollowUp()};
    }

    @Override
    public String getMainCondition() {
        return String.format("(%s is null AND %s == '0') OR %s == '0'",
                Utils.metadata().getRegisterQueryProvider().getDemographicTable() + "." + Constants.KEY.DATE_REMOVED,
                Utils.metadata().getRegisterQueryProvider().getDemographicTable() + "." + Constants.KEY.IS_CLOSED,
                Utils.metadata().getRegisterQueryProvider().getChildDetailsTable() + "." + Constants.KEY.IS_CLOSED);
    }
}
