package org.smartregister.uniceftunisia.model;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.model.BaseChildAdvancedSearchModel;
import org.smartregister.domain.Response;
import org.smartregister.uniceftunisia.util.AppConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

        String[] columns = new String[]{AppConstants.KEY.ID_LOWER_CASE, AppConstants.KEY.RELATIONALID,
                AppConstants.KEY.FIRST_NAME, AppConstants.KEY.MIDDLE_NAME, AppConstants.KEY.LAST_NAME,
                AppConstants.KEY.GENDER, AppConstants.KEY.DOB, AppConstants.KEY.ZEIR_ID,
                AppConstants.KEY.MOTHER_BASE_ENTITY_ID, AppConstants.KEY.MOTHER_FIRST_NAME,
                AppConstants.KEY.MOTHER_FIRST_NAME, AppConstants.KEY.INACTIVE,
                AppConstants.KEY.LOST_TO_FOLLOW_UP};

        AdvancedMatrixCursor matrixCursor = new AdvancedMatrixCursor(columns);

        if (response == null || response.isFailure() || StringUtils.isBlank(response.payload())) {
            return matrixCursor;
        }

        JSONArray jsonArray = getJsonArray(response);
        if (jsonArray != null) {

            List<JSONObject> jsonValues = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonValues.add(getJsonObject(jsonArray, i));
            }

            sortValues(jsonValues);

            for (JSONObject client : jsonValues) {

                if (client == null) {
                    continue;
                }

                CheckChildDetailsModel checkChildDetails = new CheckChildDetailsModel(client).invoke();
                if (checkChildDetails.is())
                    continue;
                String entityId = checkChildDetails.getEntityId();
                String firstName = checkChildDetails.getFirstName();
                String middleName = checkChildDetails.getMiddleName();
                String lastName = checkChildDetails.getLastName();
                String gender = checkChildDetails.getGender();
                String dob = checkChildDetails.getDob();
                String zeirId = checkChildDetails.getZeirId();
                String inactive = checkChildDetails.getInactive();
                String lostToFollowUp = checkChildDetails.getLostToFollowUp();


                CheckMotherDetailsModel checkMotherDetails = new CheckMotherDetailsModel(client).invoke();
                String motherBaseEntityId = checkMotherDetails.getMotherBaseEntityId();
                String motherFirstName = checkMotherDetails.getMotherFirstName();
                String motherLastName = checkMotherDetails.getMotherLastName();

                matrixCursor.addRow(new Object[]{entityId, null, firstName, middleName, lastName, gender, dob, zeirId, motherBaseEntityId, motherFirstName, motherLastName, inactive, lostToFollowUp});
            }

            return matrixCursor;
        } else {
            return matrixCursor;
        }
    }

    private void sortValues(List<JSONObject> jsonValues) {
        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {

                if (!lhs.has(AppConstants.KEY.CHILD) || !rhs.has(AppConstants.KEY.CHILD)) {
                    return 0;
                }

                JSONObject lhsChild = getJsonObject(lhs, AppConstants.KEY.CHILD);
                JSONObject rhsChild = getJsonObject(rhs, AppConstants.KEY.CHILD);

                String lhsInactive = getJsonString(getJsonObject(lhsChild, AppConstants.KEY.ATTRIBUTES), AppConstants.KEY.INACTIVE);
                String rhsInactive = getJsonString(getJsonObject(rhsChild, AppConstants.KEY.ATTRIBUTES), AppConstants.KEY.INACTIVE);

                int aComp = 0;
                if (lhsInactive.equalsIgnoreCase(Boolean.TRUE.toString()) && !rhsInactive.equalsIgnoreCase(Boolean.TRUE.toString())) {
                    aComp = 1;
                } else if (!lhsInactive.equalsIgnoreCase(Boolean.TRUE.toString()) && rhsInactive.equalsIgnoreCase(Boolean.TRUE.toString())) {
                    aComp = -1;
                }

                if (aComp != 0) {
                    return aComp;
                } else {
                    Integer lostToFollowUp = getLostToFollowUp(lhsChild, rhsChild);
                    if (lostToFollowUp != null) return lostToFollowUp;
                }

                return 0;

            }
        });
    }

    @Nullable
    private Integer getLostToFollowUp(JSONObject lhsChild, JSONObject rhsChild) {
        String lhsLostToFollowUp = getJsonString(getJsonObject(lhsChild, AppConstants.KEY.ATTRIBUTES), AppConstants.KEY.LOST_TO_FOLLOW_UP);
        String rhsLostToFollowUp = getJsonString(getJsonObject(rhsChild, AppConstants.KEY.ATTRIBUTES), AppConstants.KEY.LOST_TO_FOLLOW_UP);
        if (lhsLostToFollowUp.equalsIgnoreCase(Boolean.TRUE.toString()) && !rhsLostToFollowUp.equalsIgnoreCase(Boolean.TRUE.toString())) {
            return 1;
        } else if (!lhsLostToFollowUp.equalsIgnoreCase(Boolean.TRUE.toString()) && rhsLostToFollowUp.equalsIgnoreCase(Boolean.TRUE.toString())) {
            return -1;
        }
        return null;
    }

}
