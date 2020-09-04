package org.smartregister.uniceftunisia.model;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.model.BaseChildAdvancedSearchModel;
import org.smartregister.child.model.ChildMotherDetailModel;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBConstants;
import org.smartregister.domain.Response;
import org.smartregister.uniceftunisia.util.AppConstants;

import java.util.List;
import java.util.Map;

import static org.smartregister.child.util.Constants.KEY.FATHER_RELATIONAL_ID;
import static org.smartregister.child.util.Constants.KEY.RELATIONALID;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.DOB;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.FATHER_BASE_ENTITY_ID;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.FIRST_NAME;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.GENDER;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.ID;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.INACTIVE;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.LAST_NAME;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.LOST_TO_FOLLOW_UP;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.RELATIONAL_ID;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.ZEIR_ID;
import static org.smartregister.uniceftunisia.util.TableUtil.getAllClientColumn;
import static org.smartregister.uniceftunisia.util.TableUtil.getChildDetailsColumn;

/**
 * Created by ndegwamartin on 2019-05-27.
 */
public class AdvancedSearchModel extends BaseChildAdvancedSearchModel {

    @Override
    public Map<String, String> createEditMap(Map<String, String> editMap) {
        return editMap;
    }

    @Override
    public AdvancedMatrixCursor createMatrixCursor(Response<String> response) {

        String[] columns = new String[]{
                AppConstants.KEY.ID_LOWER_CASE,
                AppConstants.KEY.RELATIONALID,
                AppConstants.KEY.RELATIONAL_ID,
                AppConstants.KEY.FATHER_BASE_ENTITY_ID,
                AppConstants.KEY.FIRST_NAME,
                AppConstants.KEY.LAST_NAME,
                AppConstants.KEY.GENDER,
                AppConstants.KEY.DOB,
                AppConstants.KEY.ZEIR_ID,
                AppConstants.KEY.MOTHER_FIRST_NAME,
                AppConstants.KEY.MOTHER_LAST_NAME,
                AppConstants.KEY.INACTIVE,
                AppConstants.KEY.LOST_TO_FOLLOW_UP
        };

        AdvancedMatrixCursor matrixCursor = new AdvancedMatrixCursor(columns);

        JSONArray jsonArray = getJsonArray(response);
        if (jsonArray != null) {
            List<ChildMotherDetailModel> ChildMotherDetailModels = getChildMotherDetailModels(response);
            for (ChildMotherDetailModel ChildMotherDetailModel : ChildMotherDetailModels) {
                matrixCursor.addRow(ChildMotherDetailModel.getColumnValuesFromJson());
            }
        }

        return matrixCursor;
    }

    @Override
    public String mainSelect(String mainCondition) {
        return "select " + StringUtils.join(getColumns(), ",") + " from " + DBConstants.RegisterTable.CHILD_DETAILS + " " +
                "join " + DBConstants.RegisterTable.MOTHER_DETAILS + " on " + DBConstants.RegisterTable.CHILD_DETAILS + "." + Constants.KEY.RELATIONAL_ID + " = " + DBConstants.RegisterTable.MOTHER_DETAILS + "." + Constants.KEY.BASE_ENTITY_ID + " " +
                "join " + DBConstants.RegisterTable.CLIENT + " on " + DBConstants.RegisterTable.CLIENT + "." + Constants.KEY.BASE_ENTITY_ID + " = " + DBConstants.RegisterTable.CHILD_DETAILS + "." + Constants.KEY.BASE_ENTITY_ID + " " +
                "join " + DBConstants.RegisterTable.CLIENT + " mother on mother." + Constants.KEY.BASE_ENTITY_ID + " = " + DBConstants.RegisterTable.MOTHER_DETAILS + "." + Constants.KEY.BASE_ENTITY_ID + " where " + mainCondition;
    }

    @NotNull
    @Override
    public String[] getColumns() {
        return new String[]{
                getAllClientColumn(ID) + "as _id",
                getChildDetailsColumn(RELATIONALID),
                getChildDetailsColumn(RELATIONAL_ID),
                getChildDetailsColumn(FATHER_RELATIONAL_ID),
                getAllClientColumn(FIRST_NAME),
                getAllClientColumn(LAST_NAME),
                getAllClientColumn(GENDER),
                getAllClientColumn(DOB),
                getAllClientColumn(ZEIR_ID),
                "mother.first_name                     as " + AppConstants.KEY.MOTHER_FIRST_NAME,
                "mother.last_name                      as " + AppConstants.KEY.MOTHER_LAST_NAME,
                getChildDetailsColumn(INACTIVE),
                getChildDetailsColumn(LOST_TO_FOLLOW_UP)
        };
    }
}
