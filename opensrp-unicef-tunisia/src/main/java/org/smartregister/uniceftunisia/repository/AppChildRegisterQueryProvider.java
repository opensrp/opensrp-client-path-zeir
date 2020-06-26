package org.smartregister.uniceftunisia.repository;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.provider.RegisterQueryProvider;
import org.smartregister.child.util.Constants;
import org.smartregister.uniceftunisia.util.AppConstants;

import static org.smartregister.uniceftunisia.util.AppConstants.KEY.*;

public class AppChildRegisterQueryProvider extends RegisterQueryProvider {

    @Override
    public String mainRegisterQuery() {
        return "select " + StringUtils.join(mainColumns(), ",") + " from " + getChildDetailsTable() + " " +
                "join " + getMotherDetailsTable() + " on " + getChildDetailsTable() + "." + Constants.KEY.RELATIONAL_ID + " = " + getMotherDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID + " " +
                "left join " + getFatherDetailsTable() + " on " + getChildDetailsTable() + "." + Constants.KEY.FATHER_RELATIONAL_ID + " = " + getFatherDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID + " " +
                "join " + getDemographicTable() + " on " + getDemographicTable() + "." + Constants.KEY.BASE_ENTITY_ID + " = " + getChildDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID + " " +
                "join " + getDemographicTable() + " mother on mother." + Constants.KEY.BASE_ENTITY_ID + " = " + getMotherDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID + " " +
                "left join " + getDemographicTable() + " father on father." + Constants.KEY.BASE_ENTITY_ID + " = " + getFatherDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID;
    }

    private String getFatherDetailsTable() {
        return AppConstants.TABLE_NAME.FATHER_DETAILS;
    }

    @Override
    public String[] mainColumns() {
        return new String[]{
                getAllClientColumn(ID) + "as _id",
                getAllClientColumn(RELATIONALID),
                getAllClientColumn(ZEIR_ID),
                getAllClientColumn(GENDER),
                getAllClientColumn(BASE_ENTITY_ID),
                getAllClientColumn(FIRST_NAME),
                getAllClientColumn(LAST_NAME),
                getAllClientColumn(VILLAGE),
                getAllClientColumn(HOME_ADDRESS),
                getAllClientColumn(DOB),
                getAllClientColumn(REGISTRATION_DATE),
                getAllClientColumn(LAST_INTERACTED_WITH),
                getMotherDetailsColumn(MOTHER_NATIONALITY),
                getMotherDetailsColumn(MOTHER_NATIONALITY_OTHER),
                getMotherDetailsColumn(PROTECTED_AT_BIRTH),
                getMotherDetailsColumn(MOTHER_TDV_DOSES),
                getMotherDetailsColumn(MOTHER_RUBELLA),
                getMotherDetailsColumn(PHONE_NUMBER) + "as mother_phone_number",
                getMotherDetailsColumn(SECOND_PHONE_NUMBER) + "as mother_second_phone_number",
                getFatherDetailsColumn(FATHER_NATIONALITY),
                getFatherDetailsColumn(FATHER_NATIONALITY_OTHER),
                getFatherDetailsColumn(PHONE_NUMBER) + "as father_phone_number",
                getChildDetailsColumn(INACTIVE),
                getChildDetailsColumn(LOST_TO_FOLLOW_UP),
                getChildDetailsColumn(RELATIONAL_ID),
                getChildDetailsColumn(SHOW_BCG_SCAR),
                getChildDetailsColumn(SHOW_BCG2_REMINDER),
                getChildDetailsColumn(BIRTH_REGISTRATION_NUMBER),
                "mother.first_name                     as mother_first_name",
                "mother.last_name                      as mother_last_name",
                "mother.dob                            as mother_dob",
                "father.first_name                     as father_first_name",
                "father.last_name                      as father_last_name",
                "father.dob                            as father_dob"
        };
    }

    private String getAllClientColumn(String column) {
        return getColumn(AppConstants.TABLE_NAME.ALL_CLIENTS, column);
    }

    private String getMotherDetailsColumn(String column) {
        return getColumn(AppConstants.TABLE_NAME.MOTHER_DETAILS, column);
    }

    private String getFatherDetailsColumn(String column) {
        return getColumn(AppConstants.TABLE_NAME.FATHER_DETAILS, column);
    }

    private String getChildDetailsColumn(String column) {
        return getColumn(AppConstants.TABLE_NAME.CHILD_DETAILS, column);
    }

    private String getColumn(String tableName, String column) {
        return String.format("%s.%s ", tableName, column);
    }
}
