package org.smartregister.uniceftunisia.util;

public class AppConstants {

    public static final String REACTION_VACCINE = "Reaction_Vaccine";

    public interface LOCALE {
        String ARABIC_LOCALE = "ar";
    }

    public static final class KEY {
        public static final String CHILD = "child";
        public static final String MOTHER_FIRST_NAME = "mother_first_name";
        public static final String FATHER_FIRST_NAME = "father_first_name";
        public static final String FATHER_LAST_NAME = "father_last_name";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String MOTHER_LAST_NAME = "mother_last_name";
        public static final String ZEIR_ID = "zeir_id";
        public static final String LOST_TO_FOLLOW_UP = "lost_to_follow_up";
        public static final String GENDER = "gender";
        public static final String INACTIVE = "inactive";
        public static final String LAST_INTERACTED_WITH = "last_interacted_with";
        public static final String VALUE = "value";
        public static final String STEPNAME = "stepName";
        public static final String TITLE = "title";
        public static final String HIA_2_INDICATOR = "hia2_indicator";
        public static final String RELATIONALID = "relationalid";
        public static final String RELATIONAL_ID = "relational_id";
        public static final String ID_LOWER_CASE = "_id";
        public static final String BASE_ENTITY_ID = "base_entity_id";
        public static final String DOB = "dob";//Date Of Birth
        public static final String DOD = "dod";//Date Of Death
        public static final String DATE_REMOVED = "date_removed";
        public static final String MOTHER_NRC_NUMBER = "nrc_number";
        public static final String SECOND_PHONE_NUMBER = "second_phone_number";
        public static final String VIEW_CONFIGURATION_PREFIX = "ViewConfiguration_";
        public static final String HOME_FACILITY = "home_address";
        public static final String APP_ID = "mer_id";
        public static final String MIDDLE_NAME = "middle_name";
        public static final String ENCOUNTER_TYPE = "encounter_type";
        public static final String BIRTH_REGISTRATION = "Birth Registration";
        public static final String VILLAGE = "village";
        public static final String HOME_ADDRESS = "home_address";
        public static final String DOB_UNKNOWN = "dob_unknown";
        public static final String DATE_BIRTH = "Date_Birth";
        public static final String BIRTH_WEIGHT = "Birth_Weight";
        public static final String CARD_ID = "card_id";
        public static final String MOTHER_PHONE_NUMBER = "mother_phone_number";
        public static final String MOTHER_SECOND_PHONE_NUMBER = "mother_second_phone_number";
        public static final String FATHER_PHONE_NUMBER = "father_phone_number";
        public static final String MOTHER_DOB = "mother_dob";
        public static final String FATHER_DOB = "father_dob";
        public static final String FATHER_BASE_ENTITY_ID = "father_base_entity_id";
        public static final String TODAY = "today";
        public static final String BUTTON_TEXT = "buttonText";
        public static final String DIALOG_TITLE = "dialogTitle";
        public static final String SEARCH_HINT = "searchHint";
        public static final String OPTIONS_TEXT = "options.text";
        public static final String SITE_CHARACTERISTICS = "site_characteristics";
        public static final String REGISTRATION_DATE = "client_reg_date";
        public static final String FIELDS = "fields";
        public static final String KEY = "key";
        public static final String IS_VACCINE_GROUP = "is_vaccine_group";
        public static final String OPTIONS = "options";
        public static final String MOTHER_NATIONALITY = "mother_nationality";
        public static final String FIRST_BIRTH = "first_birth";
        public static final String RUBELLA_SEROLOGY = "rubella_serology";
        public static final String SEROLOGY_RESULTS = "serology_results";
        public static final String MOTHER_RUBELLA = "mother_rubella";
        public static final String FATHER_NATIONALITY = "father_nationality";
        public static final String FATHER_RELATIONAL_ID = "father_relational_id";
        public static final String MOTHER_NATIONALITY_OTHER = "mother_nationality_other";
        public static final String FATHER_NATIONALITY_OTHER = "father_nationality_other";
        public static final String MOTHER_GUARDIAN_NUMBER = "mother_guardian_number";
        public static final String FATHER_PHONE = "father_phone";
        public static final String MOTHER_TDV_DOSES = "mother_tdv_doses";
        public static final String PROTECTED_AT_BIRTH = "protected_at_birth";
        public static final String SHOW_BCG_SCAR = "show_bcg_scar";
        public static final String SHOW_BCG2_REMINDER = "show_bcg2_reminder";
        public static final String BIRTH_REGISTRATION_NUMBER = "birth_registration_number";
        public static final String ID = "id";
        public static final String CHILD_REG = "child_reg";
        public static final String GA_AT_BIRTH = "ga_at_birth";
        public static final String PLACE_OF_BIRTH = "place_of_birth";
        public static final String SMS_RECIPIENT = "sms_recipient";
        public static final String YEAR_MONTH = "year_month";
        public static final String MONTHLY_TALLIES = "monthly_tallies";
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

    public static class CONFIGURATION {
        public static final String LOGIN = "login";
        public static final String CHILD_REGISTER = "child_register";

    }

    public static final class EventType {
        public static final String CHILD_REGISTRATION = "Birth Registration";
        public static final String UPDATE_CHILD_REGISTRATION = "Update Birth Registration";
        public static final String OUT_OF_CATCHMENT = "Out of Area Service";
        public static final String MONTHLY_REPORT = "monthly_report";
    }

    public interface JsonForm {
        String DYNAMIC_VACCINES = "dynamic_vaccines";
        String CHILD_ENROLLMENT = "child_enrollment";
        String OUT_OF_CATCHMENT_SERVICE = "out_of_catchment_service";
    }

    public static class RELATIONSHIP {
        public static final String MOTHER = "mother";
        public static final String FATHER = "father";
    }

    public static class TABLE_NAME {
        public static final String ALL_CLIENTS = "ec_client";
        public static final String REGISTER_TYPE = "client_register_type";
        public static final String CHILD_UPDATED_ALERTS = "child_updated_alerts";
        public static final String FATHER_DETAILS = "ec_father_details";
        public static final String MOTHER_DETAILS = "ec_mother_details";
        public static final String CHILD_DETAILS = "ec_child_details";
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

    public interface IntentKeyUtil {
        String IS_REMOTE_LOGIN = "is_remote_login";
    }

    public interface RegisterType {
        String ANC = "anc";
        String CHILD = "child";
        String OPD = "opd";
    }

    public interface IntentKey {
        String REPORT_GROUPING = "report-grouping";
    }

    public interface Pref {
        String APP_VERSION_CODE = "APP_VERSION_CODE";
        String INDICATOR_DATA_INITIALISED = "INDICATOR_DATA_INITIALISED";
    }

    public interface File {
        String INDICATOR_CONFIG_FILE = "configs/reporting/indicator-definitions.yml";
    }

    public interface ConditionalVaccines {
        String PRETERM_VACCINES = "preterm_vaccines";
    }
}
