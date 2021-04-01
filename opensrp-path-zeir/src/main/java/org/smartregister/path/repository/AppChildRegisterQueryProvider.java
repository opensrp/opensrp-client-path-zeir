package org.smartregister.path.repository;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.provider.RegisterQueryProvider;

import static org.smartregister.child.util.Constants.SHOW_BCG2_REMINDER;
import static org.smartregister.child.util.Constants.SHOW_BCG_SCAR;
import static org.smartregister.path.util.AppConstants.KeyConstants.BASE_ENTITY_ID;
import static org.smartregister.path.util.AppConstants.KeyConstants.BIRTH_FACILITY_NAME;
import static org.smartregister.path.util.AppConstants.KeyConstants.BIRTH_FACILITY_NAME_OTHER;
import static org.smartregister.path.util.AppConstants.KeyConstants.CHILD_BIRTH_CERTIFICATE;
import static org.smartregister.path.util.AppConstants.KeyConstants.CHILD_REGISTER_CARD_NUMBER;
import static org.smartregister.path.util.AppConstants.KeyConstants.CHILD_ZONE;
import static org.smartregister.path.util.AppConstants.KeyConstants.CHW_NAME;
import static org.smartregister.path.util.AppConstants.KeyConstants.CHW_PHONE_NUMBER;
import static org.smartregister.path.util.AppConstants.KeyConstants.DOB;
import static org.smartregister.path.util.AppConstants.KeyConstants.FATHER_GUARDIAN_NAME;
import static org.smartregister.path.util.AppConstants.KeyConstants.FATHER_NRC_NUMBER;
import static org.smartregister.path.util.AppConstants.KeyConstants.FIRST_HEALTH_FACILITY_CONTRACT;
import static org.smartregister.path.util.AppConstants.KeyConstants.FIRST_NAME;
import static org.smartregister.path.util.AppConstants.KeyConstants.GENDER;
import static org.smartregister.path.util.AppConstants.KeyConstants.HOME_FACILITY;
import static org.smartregister.path.util.AppConstants.KeyConstants.ID;
import static org.smartregister.path.util.AppConstants.KeyConstants.INACTIVE;
import static org.smartregister.path.util.AppConstants.KeyConstants.LAST_INTERACTED_WITH;
import static org.smartregister.path.util.AppConstants.KeyConstants.LAST_NAME;
import static org.smartregister.path.util.AppConstants.KeyConstants.LOST_TO_FOLLOW_UP;
import static org.smartregister.path.util.AppConstants.KeyConstants.MOTHER_DOB;
import static org.smartregister.path.util.AppConstants.KeyConstants.MOTHER_FIRST_NAME;
import static org.smartregister.path.util.AppConstants.KeyConstants.MOTHER_GUARDIAN_NRC;
import static org.smartregister.path.util.AppConstants.KeyConstants.MOTHER_GUARDIAN_NUMBER;
import static org.smartregister.path.util.AppConstants.KeyConstants.MOTHER_LAST_NAME;
import static org.smartregister.path.util.AppConstants.KeyConstants.MOTHER_PHONE;
import static org.smartregister.path.util.AppConstants.KeyConstants.PHYSICAL_LANDMARK;
import static org.smartregister.path.util.AppConstants.KeyConstants.PLACE_OF_BIRTH;
import static org.smartregister.path.util.AppConstants.KeyConstants.PMTCT_STATUS;
import static org.smartregister.path.util.AppConstants.KeyConstants.REGISTRATION_DATE;
import static org.smartregister.path.util.AppConstants.KeyConstants.RELATIONALID;
import static org.smartregister.path.util.AppConstants.KeyConstants.RELATIONAL_ID;
import static org.smartregister.path.util.AppConstants.KeyConstants.RESIDENTIAL_ADDRESS;
import static org.smartregister.path.util.AppConstants.KeyConstants.RESIDENTIAL_ADDRESS_OTHER;
import static org.smartregister.path.util.AppConstants.KeyConstants.RESIDENTIAL_AREA;
import static org.smartregister.path.util.AppConstants.KeyConstants.ZEIR_ID;
import static org.smartregister.path.util.TableUtil.getAllClientColumn;
import static org.smartregister.path.util.TableUtil.getChildDetailsColumn;
import static org.smartregister.path.util.TableUtil.getMotherDetailsColumn;

public class AppChildRegisterQueryProvider extends RegisterQueryProvider {

    @Override
    public String mainRegisterQuery() {
        return "select " + StringUtils.join(mainColumns(), ",") +
                " from ec_child_details\n" +
                "         join ec_mother_details on ec_child_details.relational_id = ec_mother_details.base_entity_id\n" +
                "         join ec_client on ec_client.base_entity_id = ec_child_details.base_entity_id\n" +
                "         join ec_client mother on mother.base_entity_id = ec_mother_details.base_entity_id";
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
                getAllClientColumn(DOB),
                getAllClientColumn(REGISTRATION_DATE),
                getAllClientColumn(LAST_INTERACTED_WITH),
                getMotherDetailsColumn(MOTHER_GUARDIAN_NUMBER),
                getMotherDetailsColumn(MOTHER_GUARDIAN_NRC),
                getChildDetailsColumn(CHW_NAME),
                getChildDetailsColumn(CHW_PHONE_NUMBER),
                getChildDetailsColumn(RELATIONAL_ID),
                getChildDetailsColumn(CHILD_BIRTH_CERTIFICATE),
                getChildDetailsColumn(CHILD_REGISTER_CARD_NUMBER),
                getChildDetailsColumn(FIRST_HEALTH_FACILITY_CONTRACT),
                getChildDetailsColumn(PLACE_OF_BIRTH),
                getChildDetailsColumn(HOME_FACILITY),
                getChildDetailsColumn(RESIDENTIAL_ADDRESS),
                getChildDetailsColumn(RESIDENTIAL_ADDRESS_OTHER),
                getChildDetailsColumn(FATHER_GUARDIAN_NAME),
                getChildDetailsColumn(FATHER_NRC_NUMBER),
                getChildDetailsColumn(BIRTH_FACILITY_NAME),
                getChildDetailsColumn(BIRTH_FACILITY_NAME_OTHER),
                getChildDetailsColumn(PMTCT_STATUS),
                getChildDetailsColumn(INACTIVE),
                getChildDetailsColumn(LOST_TO_FOLLOW_UP),
                getChildDetailsColumn(PHYSICAL_LANDMARK),
                getChildDetailsColumn(RESIDENTIAL_AREA),
                getChildDetailsColumn(SHOW_BCG_SCAR),
                getChildDetailsColumn(SHOW_BCG2_REMINDER),
                getChildDetailsColumn(CHILD_ZONE),
                getChildDetailsColumn(MOTHER_GUARDIAN_NUMBER) + " as " + MOTHER_PHONE,
                "mother.first_name                     as " + MOTHER_FIRST_NAME,
                "mother.last_name                      as " + MOTHER_LAST_NAME,
                "mother.dob                            as " + MOTHER_DOB,
        };
    }
}
