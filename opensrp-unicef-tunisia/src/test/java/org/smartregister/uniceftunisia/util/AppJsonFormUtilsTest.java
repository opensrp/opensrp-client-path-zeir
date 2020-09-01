package org.smartregister.uniceftunisia.util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.smartregister.CoreLibrary;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.domain.ChildMetadata.ChildRegister;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.domain.Photo;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.FormUtils;
import org.smartregister.util.ImageUtils;
import org.smartregister.view.LocationPickerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PrepareForTest({FormUtils.class, CoreLibrary.class, ChildLibrary.class, ImageUtils.class, LocationHelper.class})
@RunWith(PowerMockRunner.class)
public class AppJsonFormUtilsTest {

    @Mock
    private Context context;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private LocationHelper locationHelper;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private ChildRegister childRegister;

    @Mock
    private FormUtils formUtils;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @Ignore("Will fix this later")
    public void getMetadataForEditForm() throws Exception {
        PowerMockito.mockStatic(FormUtils.class);
        PowerMockito.mockStatic(ChildLibrary.class);
        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.mockStatic(LocationHelper.class);
        PowerMockito.mockStatic(ImageUtils.class);
        PowerMockito.when(LocationHelper.getInstance()).thenReturn(locationHelper);
        PowerMockito.when(locationHelper.getOpenMrsLocationHierarchy(Mockito.<String>any(), Mockito.anyBoolean())).thenReturn(new ArrayList<String>());
        PowerMockito.when(ImageUtils.profilePhotoByClientID(Mockito.<String>any(), Mockito.anyInt())).thenReturn(Mockito.mock(Photo.class));
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        ChildMetadata childMetadata = new ChildMetadata(null, null, null,null, true);
        PowerMockito.when(childLibrary.metadata()).thenReturn(childMetadata);
        LocationPickerView locationPickerView = Mockito.mock(LocationPickerView.class);
        PowerMockito.when(locationPickerView.getSelectedItem()).thenReturn("selected");
        PowerMockito.when(childLibrary.getLocationPickerView(context)).thenReturn(locationPickerView);

        childMetadata.childRegister = childRegister;
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null,null,
                null, true);
        metadata.updateChildRegister("test", "test",
                "test", "ChildRegister",
                "test", "test",
                "test",
                "test", "test");
        String strForm = "{\"count\":\"1\",\"encounter_type\":\"Birth Registration\",\"mother\":{\"encounter_type\":\"New Woman Registration\"},\"entity_id\":\"\",\"relational_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"encounter_location\":\"\",\"look_up\":{\"entity_id\":\"\",\"value\":\"\"}},\"step1\":{\"title\":\"Birth Registration\",\"fields\":[{\"key\":\"photo\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"choose_image\",\"uploadButtonText\":\"Take a photo of the child\"},{\"key\":\"zeir_id\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_identifier\",\"openmrs_entity_id\":\"zeir_id\",\"type\":\"edit_text\",\"hint\":\"Child's MER ID\",\"label_info_text\":\"Write this number down on the child's health passport.\",\"scanButtonText\":\"Scan QR Code\",\"value\":\"0\",\"read_only\":true,\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a valid ID\"},\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the Child's MER ID\"}},{\"key\":\"birth_registration_number\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"Birth_Certificate\",\"type\":\"edit_text\",\"hint\":\"Child's NRB birth registration number\",\"label_info_text\":\"If the child was registered in vital registration, enter the registration number here.\",\"edit_type\":\"name\",\"v_required\":{\"value\":false,\"err\":\"Please enter the Birth Registration Number\"},\"v_regex\":{\"value\":\"([A-Z]{2,3}/[0-9]{8}/[0-9]{4})|\\\\s*\",\"err\":\"Number must take the format of ###/########/####\"}},{\"key\":\"last_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"type\":\"edit_text\",\"hint\":\"Last name\",\"edit_type\":\"name\",\"v_required\":{\"value\":true,\"err\":\"Please enter the last name\"},\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"}},{\"key\":\"first_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"type\":\"edit_text\",\"hint\":\"First name\",\"edit_type\":\"name\",\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"},\"v_required\":{\"value\":true,\"err\":\"Please enter a first name\"}},{\"key\":\"middle_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"middle_name\",\"type\":\"edit_text\",\"hint\":\"Middle name\",\"edit_type\":\"name\",\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"},\"v_required\":{\"value\":false,\"err\":\"Please enter the child's middle name\"}},{\"key\":\"Sex\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"gender\",\"type\":\"spinner\",\"hint\":\"Sex\",\"values\":[\"Male\",\"Female\"],\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the Gender of the child\"}},{\"key\":\"Date_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"type\":\"date_picker\",\"hint\":\"Child's DOB\",\"expanded\":false,\"duration\":{\"label\":\"Age\"},\"min_date\":\"today-5y\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the date of birth\"}},{\"key\":\"age\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"age\",\"type\":\"hidden\",\"value\":\"\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child_register_registration_calculation_rules.yml\"}}}},{\"key\":\"home_address\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"tree\",\"tree\":[],\"value\":\"Lombwa Outreach\",\"hint\":\"Address/Location\",\"v_required\":{\"value\":false,\"err\":\"Please enter the Child's Home Address\"}},{\"key\":\"village\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"address1\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"label_info_text\":\"Indicate the village where the child comes from.\",\"hint\":\"Child's Village\"},{\"key\":\"traditional_authority\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"traditional_authority\",\"hint\":\"Traditional Authority\",\"label_info_text\":\"Indicate the name of the Traditional Authority for the child.\",\"type\":\"edit_text\"},{\"key\":\"Birth_Weight\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"label_info_text\":\"The weight as measured when the child is born\",\"hint\":\"Birth weight (kg)\",\"v_min\":{\"value\":\"0.1\",\"err\":\"Weight must be greater than 0\"},\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid weight\"},\"v_required\":{\"value\":\"true\",\"err\":\"Enter the child's birth weight\"}},{\"key\":\"Birth_Height\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"hint\":\"Birth Height (cm)\",\"v_min\":{\"value\":\"0.1\",\"err\":\"Height must be greater than 0\"},\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid height\"},\"v_required\":{\"value\":false,\"err\":\"Enter the child's birth height\"}},{\"key\":\"mother_guardian_first_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/Guardian first name\",\"edit_type\":\"name\",\"look_up\":\"true\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother/guardian's first name\"},\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"}},{\"key\":\"mother_guardian_last_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian last name\",\"edit_type\":\"name\",\"look_up\":\"true\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother/guardian's last name\"},\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"}},{\"key\":\"mother_guardian_date_birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"entity_id\":\"mother\",\"type\":\"date_picker\",\"hint\":\"Mother/guardian DOB\",\"look_up\":\"true\",\"expanded\":false,\"duration\":{\"label\":\"Age\"},\"default\":\"01-01-1960\",\"min_date\":\"01-01-1960\",\"max_date\":\"today-10y\"},{\"key\":\"nrc_number\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"nrc_number\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian NRB Identification number\",\"look_up\":\"true\",\"v_regex\":{\"value\":\"([A-Za-z0-9]{1,11})|\\\\s*\",\"err\":\"ID should be at-most 11 characters in length\"}},{\"key\":\"mother_guardian_number\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian phone number\",\"v_regex\":{\"value\":\"([0][0-9]{9})|\\\\s*\",\"err\":\"Number must begin with 0 and must be a total of 10 digits in length\"}},{\"key\":\"second_phone_number\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"second_phone_number\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Alternative phone number\",\"v_regex\":{\"value\":\"([0][0-9]{9})|\\\\s*\",\"err\":\"Number must begin with 0 and must be a total of 10 digits in length\"}},{\"key\":\"protected_at_birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"164826AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"spinner\",\"label_info_text\":\"Whether the child's mother received 2+ doses of Td.\",\"hint\":\"Protected at birth (PAB)\",\"entity_id\":\"mother\",\"v_required\":{\"value\":true,\"err\":\"Please choose an option\"},\"values\":[\"Yes\",\"No\",\"Don't Know\"],\"openmrs_choice_ids\":{\"Yes\":\"1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"No\":\"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Don't Know\":\"1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child_register_registration_relevance_rules.yml\"}}}},{\"key\":\"mother_hiv_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1396AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"entity_id\":\"mother\",\"type\":\"spinner\",\"hint\":\"HIV Status of the Child's Mother\",\"values\":[\"Positive\",\"Negative\",\"Unknown\"],\"openmrs_choice_ids\":{\"Positive\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Negative\":\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Unknown\":\"1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}},{\"key\":\"child_hiv_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5303AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"spinner\",\"hint\":\"HIV Status of the Child\",\"values\":[\"Positive\",\"Negative\",\"Unknown\",\"Exposed\"],\"openmrs_choice_ids\":{\"Positive\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Negative\":\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Unknown\":\"1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Exposed\":\"822AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child_register_registration_relevance_rules.yml\"}}}},{\"key\":\"child_treatment\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"162240AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"spinner\",\"label_info_text\":\"Indicate whether the child is on CPT and/or ART.\",\"hint\":\"Child's treatment\",\"values\":[\"CPT\",\"ART\",\"None\"],\"openmrs_choice_ids\":{\"CPT\":\"160434AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"ART\":\"160119AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"None\":\"1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child_register_registration_relevance_rules.yml\"}}}},{\"key\":\"lost_to_follow_up\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"lost_to_follow_up\",\"type\":\"hidden\"},{\"key\":\"inactive\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"inactive\",\"type\":\"hidden\"}]}}";
        JSONObject jsonObject = new JSONObject(strForm);

        PowerMockito.when(formUtils.getFormJson(null)).thenReturn(jsonObject);
        PowerMockito.when(FormUtils.getInstance(context)).thenReturn(formUtils);

        Map<String, String> childDetails = new HashMap<>();
        childDetails.put(Constants.KEY.BASE_ENTITY_ID, "baseEntityId");
        childDetails.put(Constants.KEY.RELATIONAL_ID, "relationalId");
        childDetails.put(AppConstants.KEY.APP_ID, "unicef_tunisia");
        childDetails.put(Constants.JSON_FORM_KEY.UNIQUE_ID, "opensrpId");
        childDetails.put(AppConstants.KEY.DOB, "2010-06-30");
        childDetails.put(AppConstants.KEY.FIRST_NAME, "first");
        childDetails.put(AppConstants.KEY.LAST_NAME, "last");
        childDetails.put(AppConstants.KEY.MIDDLE_NAME, "middle");
        childDetails.put(AppConstants.KEY.MOTHER_NRC_NUMBER, "nrc_number");
        childDetails.put(AppConstants.KEY.SECOND_PHONE_NUMBER, "0232453923");
        List<String> nonEditableFields = new ArrayList<>();
        String result = AppJsonFormUtils.updateJsonFormWithClientDetails(context, childDetails, nonEditableFields);
        JSONObject jsonResultObject = new JSONObject(result);
        JSONObject stepOne = jsonResultObject.getJSONObject(JsonFormUtils.STEP1);
        JSONArray stepOneFields = stepOne.optJSONArray(org.smartregister.util.JsonFormUtils.FIELDS);

        Assert.assertEquals("first", JsonFormUtils.getFieldValue(stepOneFields, AppConstants.KEY.FIRST_NAME));

        Assert.assertEquals("last", JsonFormUtils.getFieldValue(stepOneFields, AppConstants.KEY.LAST_NAME));

        Assert.assertEquals("30-06-2010", JsonFormUtils.getFieldValue(stepOneFields, AppConstants.KEY.DATE_BIRTH));

        Assert.assertEquals("Middle", JsonFormUtils.getFieldValue(stepOneFields, AppConstants.KEY.MIDDLE_NAME));

        Assert.assertEquals("baseEntityId", jsonResultObject.optString(JsonFormUtils.ENTITY_ID));

        Assert.assertEquals("relationalId", jsonResultObject.optString(Constants.KEY.RELATIONAL_ID));

        Assert.assertEquals("Unicef Tunisia", jsonResultObject.optString(JsonFormUtils.CURRENT_ZEIR_ID));

        Assert.assertEquals("Nrc Number", JsonFormUtils.getFieldValue(stepOneFields, AppConstants.KEY.MOTHER_NRC_NUMBER));

        Assert.assertEquals("0232453923", JsonFormUtils.getFieldValue(stepOneFields, AppConstants.KEY.SECOND_PHONE_NUMBER));
    }

    @Test
    @Ignore("Need to investigate whether this method was removed: updateBirthFacilityHierarchy")
    public void updateBirthFacilityHierarchy() throws Exception {
        PowerMockito.mockStatic(LocationHelper.class);
        PowerMockito.when(LocationHelper.getInstance()).thenReturn(locationHelper);

        PowerMockito.when(locationHelper.getOpenMrsLocationHierarchy("facility", true))
                .thenReturn(Collections.singletonList("location"));
        Map<String, String> childDetails = new HashMap<>();
        childDetails.put(AppConstants.KEY.BIRTH_FACILITY_NAME, "facility");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormUtils.KEY, AppConstants.KEY.BIRTH_FACILITY_NAME);
        Whitebox.invokeMethod(AppJsonFormUtils.class, "updateBirthFacilityHierarchy", childDetails, jsonObject);
        Assert.assertEquals("[\"location\"]", jsonObject.getString(JsonFormUtils.VALUE));
    }

    @Test
    @Ignore("Need to investigate whether this method was removed: updateResidentialAreaHierarchy")
    public void updateResidentialAreaHierarchy() throws Exception {
        PowerMockito.mockStatic(LocationHelper.class);
        PowerMockito.when(LocationHelper.getInstance()).thenReturn(locationHelper);

        PowerMockito.when(locationHelper.getOpenMrsLocationHierarchy("address", true))
                .thenReturn(Collections.singletonList("location"));
        Map<String, String> childDetails = new HashMap<>();
        childDetails.put(AppConstants.KEY.ADDRESS_3, "address");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormUtils.KEY, AppConstants.KEY.RESIDENTIAL_AREA);
        Whitebox.invokeMethod(AppJsonFormUtils.class, "updateResidentialAreaHierarchy", childDetails, jsonObject);
        Assert.assertEquals("[\"location\"]", jsonObject.getString(JsonFormUtils.VALUE));
    }
}