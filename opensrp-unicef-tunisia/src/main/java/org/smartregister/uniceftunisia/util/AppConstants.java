package org.smartregister.uniceftunisia.util;

public class AppConstants {

    public static final String MOTHER_TDV_DOSES = "mother_tdv_doses";
    public static final String PROTECTED_AT_BIRTH = "protected_at_birth";
    public static final String REACTION_VACCINE = "Reaction_Vaccine";
    public static final String FORM_CONFIG_LOCATION = "json.form/json.form.config.json";
    public static final String NATIONAL_ID = "national_id";
    public static final String BHT_MID = "bht_mid";
    public static final String MOTHER_HIV_STATUS = "mother_hiv_status";
    public static final String BIRTH_REGISTRATION_NUMBER = "birth_registration_number";

    public static final class KEY {
        public static final String MOTHER_BASE_ENTITY_ID = "mother_base_entity_id";
        public static final String CHILD = "child";
        public static final String MOTHER_FIRST_NAME = "mother_first_name";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String BIRTHDATE = "birthdate";
        public static final String DEATHDATE = "deathdate";
        public static final String MOTHER_LAST_NAME = "mother_last_name";
        public static final String ZEIR_ID = "zeir_id";
        public static final String LOST_TO_FOLLOW_UP = "lost_to_follow_up";
        public static final String GENDER = "gender";
        public static final String INACTIVE = "inactive";
        public static final String LAST_INTERACTED_WITH = "last_interacted_with";
        public static final String MOTHER = "mother";
        public static final String ENTITY_ID = "entity_id";
        public static final String VALUE = "value";
        public static final String STEPNAME = "stepName";
        public static final String TITLE = "title";
        public static final String HIA_2_INDICATOR = "hia2_indicator";
        public static final String RELATIONALID = "relationalid";
        public static final String ID_LOWER_CASE = "_id";
        public static final String BASE_ENTITY_ID = "base_entity_id";
        public static final String DOB = "dob";//Date Of Birth
        public static final String DOD = "dod";//Date Of Birth
        public static final String DATE_REMOVED = "date_removed";
        public static final String MOTHER_NRC_NUMBER = "nrc_number";
        public static final String MOTHER_GUARDIAN_NUMBER = "mother_guardian_number";
        public static final String MOTHER_SECOND_PHONE_NUMBER = "second_phone_number";
        public static final String VIEW_CONFIGURATION_PREFIX = "ViewConfiguration_";
        public static final String HOME_FACILITY = "home_address";
        public static final String MALAWI_ID = "mer_id";
        public static final String MIDDLE_NAME = "middle_name";
        public static final String OTHER = "other";
        public static final String BIRTH_FACILITY_NAME_OTHER = "Birth_Facility_Name_Other";
        public static final String ADDRESS_3 = "address3";
        public static final String BIRTH_FACILITY_NAME = "Birth_Facility_Name";
        public static final String RESIDENTIAL_AREA = "Residential_Area";
        public static final String MOTHER_ = "mother_";
        public static final String ENCOUNTER_TYPE = "encounter_type";
        public static final String BIRTH_REGISTRATION = "Birth Registration";
        public static final String REGISTRATION_HOME_ADDRESS = "home_address";
        public static final String IDENTIFIERS = "identifiers";
        public static final String FIRSTNAME = "firstName";
        public static final String MIDDLENAME = "middleName";
        public static final String LASTNAME = "lastName";
        public static final String ATTRIBUTES = "attributes";
        public static final int FIVE_YEAR = 5;
        public static final String OPD_REGISTRATION = "Opd Registration" ;
        public static String SITE_CHARACTERISTICS = "site_characteristics";

        public static final String FIELDS = "fields";
        public static final String KEY = "key";
        public static final String IS_VACCINE_GROUP = "is_vaccine_group";
        public static final String OPTIONS = "options";

        public static String HIDDEN_FIELDS = "hidden_fields";
        public static String DISABLED_FIELDS = "disabled_fields";
    }

    public static final class DrawerMenu {
        public static final String ALL_FAMILIES = "All Families";
        public static final String ALL_CLIENTS = "All Clients";
        public static final String ANC_CLIENTS = "ANC Clients";
        public static final String CHILD_CLIENTS = "Child Clients";
        public static final String ANC = "ANC";
    }

    public static final class FormTitleUtil {
        public static final String UPDATE_CHILD_FORM = "Update Child Registration";
    }

    public static final class RQ_CODE {
        public static final int STORAGE_PERMISIONS = 1;
    }

    public static class CONFIGURATION {
        public static final String LOGIN = "login";
        public static final String CHILD_REGISTER = "child_register";

    }

    public static final class EventType {
        public static final String CHILD_REGISTRATION = "Birth Registration";
        public static final String UPDATE_CHILD_REGISTRATION = "Update Birth Registration";
        public static final String OUT_OF_CATCHMENT = "Out of Catchment";

    }

    public static class JSON_FORM {
        public static String CHILD_ENROLLMENT = "child_enrollment";
        public static String OUT_OF_CATCHMENT_SERVICE = "out_of_catchment_service";

    }

    public static class RELATIONSHIP {
        public static final String MOTHER = "mother";

    }

    public static class TABLE_NAME {
        public static final String CHILD = "ec_client";
        public static final String ALL_CLIENTS = "ec_client";
        public static final String REGISTER_TYPE = "client_register_type";
        public static final String CHILD_UPDATED_ALERTS = "child_updated_alerts";
    }

    public interface Columns {
        interface RegisterType {
            String BASE_ENTITY_ID = "base_entity_id";
            String REGISTER_TYPE = "register_type";
            String DATE_REMOVED = "date_removed";
            String DATE_CREATED = "date_created";
        }
    }

    public static final class EntityType {
        public static final String CHILD = "child";
    }

    public class IntentKeyUtil {
        public static final String IS_REMOTE_LOGIN = "is_remote_login";
    }

    public interface DateFormat {
        String HH_MM_AMPM = "h:mm a";
    }

    public interface RegisterType {
        String ANC = "anc";
        String CHILD = "child";
        String OPD = "opd";
    }

    public interface MultiResultProcessor {
        String GROUPING_SEPARATOR = "_";
    }

    public interface IntentKey {
        String REPORT_GROUPING = "report-grouping";
    }

    public interface Pref {
        String APP_VERSION_CODE = "APP_VERSION_CODE";
        String INDICATOR_DATA_INITIALISED = "INDICATOR_DATA_INITIALISED";
    }

    public interface File {
        String INDICATOR_CONFIG_FILE = "config/indicator-definitions.yml";
    }

}
