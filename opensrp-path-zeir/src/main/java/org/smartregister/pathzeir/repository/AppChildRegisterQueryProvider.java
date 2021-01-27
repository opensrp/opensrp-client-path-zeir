package org.smartregister.pathzeir.repository;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.provider.RegisterQueryProvider;

import static org.smartregister.pathzeir.util.AppConstants.KEY.BASE_ENTITY_ID;
import static org.smartregister.pathzeir.util.AppConstants.KEY.BIRTH_FACILITY_NAME;
import static org.smartregister.pathzeir.util.AppConstants.KEY.CHILD_BIRTH_CERTIFICATE;
import static org.smartregister.pathzeir.util.AppConstants.KEY.CHILD_REGISTER_CARD_NUMBER;
import static org.smartregister.pathzeir.util.AppConstants.KEY.CHW_NAME;
import static org.smartregister.pathzeir.util.AppConstants.KEY.CHW_PHONE_NUMBER;
import static org.smartregister.pathzeir.util.AppConstants.KEY.DOB;
import static org.smartregister.pathzeir.util.AppConstants.KEY.FATHER_GUARDIAN_NAME;
import static org.smartregister.pathzeir.util.AppConstants.KEY.FATHER_NRC_NUMBER;
import static org.smartregister.pathzeir.util.AppConstants.KEY.FIRST_HEALTH_FACILITY_CONTRACT;
import static org.smartregister.pathzeir.util.AppConstants.KEY.FIRST_NAME;
import static org.smartregister.pathzeir.util.AppConstants.KEY.GENDER;
import static org.smartregister.pathzeir.util.AppConstants.KEY.HOME_FACILITY;
import static org.smartregister.pathzeir.util.AppConstants.KEY.ID;
import static org.smartregister.pathzeir.util.AppConstants.KEY.INACTIVE;
import static org.smartregister.pathzeir.util.AppConstants.KEY.LAST_INTERACTED_WITH;
import static org.smartregister.pathzeir.util.AppConstants.KEY.LAST_NAME;
import static org.smartregister.pathzeir.util.AppConstants.KEY.LOST_TO_FOLLOW_UP;
import static org.smartregister.pathzeir.util.AppConstants.KEY.MOTHER_DOB;
import static org.smartregister.pathzeir.util.AppConstants.KEY.MOTHER_FIRST_NAME;
import static org.smartregister.pathzeir.util.AppConstants.KEY.MOTHER_GUARDIAN_NRC;
import static org.smartregister.pathzeir.util.AppConstants.KEY.MOTHER_GUARDIAN_NUMBER;
import static org.smartregister.pathzeir.util.AppConstants.KEY.MOTHER_LAST_NAME;
import static org.smartregister.pathzeir.util.AppConstants.KEY.PHYSICAL_LANDMARK;
import static org.smartregister.pathzeir.util.AppConstants.KEY.PLACE_OF_BIRTH;
import static org.smartregister.pathzeir.util.AppConstants.KEY.PMTCT_STATUS;
import static org.smartregister.pathzeir.util.AppConstants.KEY.REGISTRATION_DATE;
import static org.smartregister.pathzeir.util.AppConstants.KEY.RELATIONALID;
import static org.smartregister.pathzeir.util.AppConstants.KEY.RELATIONAL_ID;
import static org.smartregister.pathzeir.util.AppConstants.KEY.RESIDENTIAL_ADDRESS;
import static org.smartregister.pathzeir.util.AppConstants.KEY.RESIDENTIAL_AREA;
import static org.smartregister.pathzeir.util.AppConstants.KEY.ZEIR_ID;
import static org.smartregister.pathzeir.util.TableUtil.getAllClientColumn;
import static org.smartregister.pathzeir.util.TableUtil.getChildDetailsColumn;
import static org.smartregister.pathzeir.util.TableUtil.getMotherDetailsColumn;

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
                getMotherDetailsColumn(CHW_NAME),
                getMotherDetailsColumn(CHW_PHONE_NUMBER),
                getChildDetailsColumn(RELATIONAL_ID),
                getChildDetailsColumn(CHILD_BIRTH_CERTIFICATE),
                getChildDetailsColumn(CHILD_REGISTER_CARD_NUMBER),
                getChildDetailsColumn(FIRST_HEALTH_FACILITY_CONTRACT),
                getChildDetailsColumn(PLACE_OF_BIRTH),
                getChildDetailsColumn(HOME_FACILITY),
                getChildDetailsColumn(RESIDENTIAL_ADDRESS),
                getChildDetailsColumn(FATHER_GUARDIAN_NAME),
                getChildDetailsColumn(FATHER_NRC_NUMBER),
                getChildDetailsColumn(BIRTH_FACILITY_NAME),
                getChildDetailsColumn(PMTCT_STATUS),
                getChildDetailsColumn(INACTIVE),
                getChildDetailsColumn(LOST_TO_FOLLOW_UP),
                getChildDetailsColumn(PHYSICAL_LANDMARK),
                getChildDetailsColumn(RESIDENTIAL_AREA),
                "mother.first_name                     as " + MOTHER_FIRST_NAME,
                "mother.last_name                      as " + MOTHER_LAST_NAME,
                "mother.dob                            as " + MOTHER_DOB,
        };
    }
}
