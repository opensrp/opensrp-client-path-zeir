package org.smartregister.uniceftunisia.repository;

import org.smartregister.child.provider.RegisterQueryProvider;
import org.smartregister.child.util.Constants;
import org.smartregister.uniceftunisia.util.AppConstants;

public class AppChildRegisterQueryProvider extends RegisterQueryProvider {

    @Override
    public String[] mainColumns() {
        return new String[]{
                getDemographicTable() + "." + Constants.KEY.ID + " as _id",
                getDemographicTable() + "." + Constants.KEY.RELATIONALID,
                getDemographicTable() + "." + Constants.KEY.ZEIR_ID,
                getChildDetailsTable() + "." + Constants.KEY.RELATIONAL_ID,
                getDemographicTable() + "." + Constants.KEY.GENDER,
                getDemographicTable() + "." + Constants.KEY.BASE_ENTITY_ID,
                getDemographicTable() + "." + Constants.KEY.FIRST_NAME,
                getDemographicTable() + "." + Constants.KEY.LAST_NAME,
                "mother" + "." + Constants.KEY.FIRST_NAME + " as mother_first_name",
                "mother" + "." + Constants.KEY.LAST_NAME + " as mother_last_name",
                getDemographicTable() + "." + Constants.KEY.DOB,
                "mother" + "." + Constants.KEY.DOB + " as mother_dob",
                getDemographicTable() + "." + Constants.KEY.CLIENT_REG_DATE,
                getDemographicTable() + "." + Constants.KEY.LAST_INTERACTED_WITH,
                getChildDetailsTable() + "." + AppConstants.KEY.INACTIVE,
                getChildDetailsTable() + "." + Constants.KEY.LOST_TO_FOLLOW_UP,
                getDemographicTable() + "." + AppConstants.KEY.VILLAGE,
                getDemographicTable() + "." + AppConstants.KEY.HOME_ADDRESS,
                getChildDetailsTable() + "." + Constants.SHOW_BCG_SCAR,
                getChildDetailsTable() + "." + Constants.SHOW_BCG2_REMINDER,
                getMotherDetailsTable() + "." + AppConstants.PROTECTED_AT_BIRTH,
                getMotherDetailsTable() + "." + AppConstants.MOTHER_TDV_DOSES,
                getChildDetailsTable() + "." + AppConstants.BIRTH_REGISTRATION_NUMBER,
        };
    }

}
