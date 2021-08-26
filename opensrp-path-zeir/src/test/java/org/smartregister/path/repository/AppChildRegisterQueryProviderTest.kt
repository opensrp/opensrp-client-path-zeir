package org.smartregister.path.repository

import org.junit.Assert.assertEquals
import org.junit.Test

class AppChildRegisterQueryProviderTest {

    @Test
    fun `Should return main query`() {
        val  appChildRegisterQueryProvider = AppChildRegisterQueryProvider()
        assertEquals("select ec_client.id as _id,ec_client.relationalid ,ec_client.zeir_id ,ec_client.gender ,ec_client.base_entity_id ,ec_client.first_name ,ec_client.last_name ,ec_client.dob ,ec_client.registration_date ,ec_client.last_interacted_with ,ec_mother_details.mother_guardian_number ,ec_mother_details.mother_guardian_nrc ,ec_mother_details.sms_reminder ,ec_mother_details.sms_reminder_phone ,ec_mother_details.sms_reminder_phone_formatted ,ec_child_details.chw_name ,ec_child_details.chw_phone_number ,ec_child_details.relational_id ,ec_child_details.child_birth_certificate ,ec_child_details.child_register_card_number ,ec_child_details.first_health_facility_contract ,ec_child_details.place_of_birth ,ec_child_details.system_of_registration ,ec_child_details.home_facility ,ec_child_details.residential_address ,ec_child_details.residential_address_other ,ec_child_details.father_guardian_name ,ec_child_details.father_nrc_number ,ec_child_details.birth_facility_name ,ec_child_details.birth_facility_name_other ,ec_child_details.pmtct_status ,ec_child_details.inactive ,ec_child_details.lost_to_follow_up ,ec_child_details.physical_landmark ,ec_child_details.residential_area ,ec_child_details.show_bcg_scar ,ec_child_details.show_bcg2_reminder ,ec_child_details.child_zone ,ec_child_details.mother_guardian_number  as mother_phone,mother.first_name                     as mother_first_name,mother.last_name                      as mother_last_name,mother.dob                            as mother_dob from ec_child_details\n" +
                "         join ec_mother_details on ec_child_details.relational_id = ec_mother_details.base_entity_id\n" +
                "         join ec_client on ec_client.base_entity_id = ec_child_details.base_entity_id\n" +
                "         join ec_client mother on mother.base_entity_id = ec_mother_details.base_entity_id", appChildRegisterQueryProvider.mainRegisterQuery())
    }
}