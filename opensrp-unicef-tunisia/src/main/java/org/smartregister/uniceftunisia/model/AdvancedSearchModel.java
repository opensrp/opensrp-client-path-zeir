package org.smartregister.uniceftunisia.model;

import org.json.JSONArray;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.model.BaseChildAdvancedSearchModel;
import org.smartregister.child.model.ChildMotherDetailsModel;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.domain.Response;
import org.smartregister.uniceftunisia.util.AppConstants;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
                AppConstants.KEY.FIRST_NAME,
                AppConstants.KEY.LAST_NAME,
                AppConstants.KEY.GENDER,
                AppConstants.KEY.DOB,
                AppConstants.KEY.ZEIR_ID,
                AppConstants.KEY.MOTHER_BASE_ENTITY_ID,
                AppConstants.KEY.FATHER_BASE_ENTITY_ID,
                AppConstants.KEY.MOTHER_FIRST_NAME,
                AppConstants.KEY.MOTHER_LAST_NAME,
                AppConstants.KEY.INACTIVE,
                AppConstants.KEY.LOST_TO_FOLLOW_UP
        };

        AdvancedMatrixCursor matrixCursor = new AdvancedMatrixCursor(columns);

        JSONArray jsonArray = getJsonArray(response);
        if (jsonArray != null) {
            List<ChildMotherDetailsModel> childMotherDetailsModels = JsonFormUtils.processReturnedAdvanceSearchResults(response);
            for (ChildMotherDetailsModel childMotherDetailsModel : childMotherDetailsModels) {
                matrixCursor.addRow(childMotherDetailsModel.getColumnValuesFromJson());
            }
        }

        return matrixCursor;
    }
}
