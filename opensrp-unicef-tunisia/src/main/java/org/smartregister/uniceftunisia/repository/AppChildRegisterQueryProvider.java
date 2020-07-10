package org.smartregister.uniceftunisia.repository;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.provider.RegisterQueryProvider;
import org.smartregister.child.util.Constants;
import org.smartregister.uniceftunisia.util.AppConstants;

import static org.smartregister.uniceftunisia.util.AppConstants.KEY.BASE_ENTITY_ID;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.BIRTH_REGISTRATION_NUMBER;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.CHILD_REG;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.DOB;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.FATHER_DOB;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.FATHER_NATIONALITY;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.FATHER_NATIONALITY_OTHER;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.FATHER_PHONE;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.FATHER_PHONE_NUMBER;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.FATHER_RELATIONAL_ID;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.FIRST_NAME;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.GA_AT_BIRTH;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.GENDER;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.HOME_ADDRESS;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.ID;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.INACTIVE;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.LAST_INTERACTED_WITH;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.LAST_NAME;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.LOST_TO_FOLLOW_UP;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.MOTHER_DOB;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.MOTHER_GUARDIAN_NUMBER;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.MOTHER_NATIONALITY;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.MOTHER_NATIONALITY_OTHER;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.MOTHER_PHONE_NUMBER;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.MOTHER_RUBELLA;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.MOTHER_SECOND_PHONE_NUMBER;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.MOTHER_TDV_DOSES;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.PLACE_OF_BIRTH;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.PROTECTED_AT_BIRTH;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.REGISTRATION_DATE;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.RELATIONALID;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.RELATIONAL_ID;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.SECOND_PHONE_NUMBER;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.SHOW_BCG2_REMINDER;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.SHOW_BCG_SCAR;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.VILLAGE;
import static org.smartregister.uniceftunisia.util.AppConstants.KEY.ZEIR_ID;

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
                getMotherDetailsColumn(MOTHER_GUARDIAN_NUMBER) + "as " + MOTHER_PHONE_NUMBER,
                getMotherDetailsColumn(SECOND_PHONE_NUMBER) + "as " + MOTHER_SECOND_PHONE_NUMBER,
                getFatherDetailsColumn(FATHER_NATIONALITY),
                getFatherDetailsColumn(FATHER_NATIONALITY_OTHER),
                getFatherDetailsColumn(FATHER_PHONE) + "as " + FATHER_PHONE_NUMBER,
                getChildDetailsColumn(INACTIVE),
                getChildDetailsColumn(LOST_TO_FOLLOW_UP),
                getChildDetailsColumn(RELATIONAL_ID),
                getChildDetailsColumn(SHOW_BCG_SCAR),
                getChildDetailsColumn(SHOW_BCG2_REMINDER),
                getChildDetailsColumn(BIRTH_REGISTRATION_NUMBER),
                getChildDetailsColumn(CHILD_REG),
                getChildDetailsColumn(PLACE_OF_BIRTH),
                getChildDetailsColumn(GA_AT_BIRTH),
                getChildDetailsColumn(FATHER_RELATIONAL_ID),
                "mother.first_name                     as " + AppConstants.KEY.MOTHER_FIRST_NAME,
                "mother.last_name                      as " + AppConstants.KEY.MOTHER_LAST_NAME,
                "mother.dob                            as " + MOTHER_DOB,
                "father.first_name                     as " + AppConstants.KEY.FATHER_FIRST_NAME,
                "father.last_name                      as " + AppConstants.KEY.FATHER_LAST_NAME,
                "father.dob                            as " + FATHER_DOB
        };
    }

    private static String getAllClientColumn(String column) {
        return getColumn(AppConstants.TABLE_NAME.ALL_CLIENTS, column);
    }

    public static String getMotherDetailsColumn(String column) {
        return getColumn(AppConstants.TABLE_NAME.MOTHER_DETAILS, column);
    }

    private static String getFatherDetailsColumn(String column) {
        return getColumn(AppConstants.TABLE_NAME.FATHER_DETAILS, column);
    }

    private static String getChildDetailsColumn(String column) {
        return getColumn(AppConstants.TABLE_NAME.CHILD_DETAILS, column);
    }

    private static String getColumn(String tableName, String column) {
        return String.format("%s.%s ", tableName, column);
    }
}
