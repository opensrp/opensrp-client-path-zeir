package org.smartregister.path.presenter;

import android.database.Cursor;
import android.database.CursorJoiner;

import org.jetbrains.annotations.NotNull;
import org.smartregister.child.contract.ChildAdvancedSearchContract;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.presenter.BaseChildAdvancedSearchPresenter;
import org.smartregister.child.util.DBConstants;
import org.smartregister.path.cursor.RemoteLocalCursor;
import org.smartregister.path.model.AdvancedSearchModel;
import org.smartregister.path.util.AppConstants;

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
                            AppConstants.KeyConstants.ID_LOWER_CASE,
                            AppConstants.KeyConstants.RELATIONALID,
                            AppConstants.KeyConstants.RELATIONAL_ID,
                            AppConstants.KeyConstants.FIRST_NAME,
                            AppConstants.KeyConstants.LAST_NAME,
                            AppConstants.KeyConstants.GENDER,
                            AppConstants.KeyConstants.DOB,
                            AppConstants.KeyConstants.ZEIR_ID,
                            AppConstants.KeyConstants.MOTHER_FIRST_NAME,
                            AppConstants.KeyConstants.MOTHER_LAST_NAME,
                            AppConstants.KeyConstants.INACTIVE,
                            AppConstants.KeyConstants.LOST_TO_FOLLOW_UP
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
                remoteLocalCursor.getId(),
                remoteLocalCursor.getRelationalId(),
                remoteLocalCursor.getMotherBaseEntityId(),
                remoteLocalCursor.getFirstName(),
                remoteLocalCursor.getLastName(),
                remoteLocalCursor.getGender(),
                remoteLocalCursor.getDob(),
                remoteLocalCursor.getOpenSrpId(),
                remoteLocalCursor.getMotherFirstName(),
                remoteLocalCursor.getMotherLastName(),
                remoteLocalCursor.getInactive(),
                remoteLocalCursor.getLostToFollowUp()};
    }

    @Override
    public String getCountQuery() {
        return model.countSelect(getMainCondition());
    }

    @Override
    public String getMainCondition() {
        return "(ec_client.dod IS NULL AND ec_client.date_removed is null AND ec_client.is_closed IS NOT '1' AND ec_child_details.is_closed IS NOT '1')";
    }
}
