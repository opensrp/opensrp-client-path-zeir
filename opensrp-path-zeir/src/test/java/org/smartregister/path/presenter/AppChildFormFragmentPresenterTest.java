package org.smartregister.path.presenter;

import android.widget.CheckBox;

import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.views.JsonFormFragmentView;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.util.Constants;
import org.smartregister.path.BaseUnitTest;
import org.smartregister.path.activity.ChildFormActivity;
import org.smartregister.path.fragment.AppChildFormFragment;

/**
 * Created by ndegwamartin on 14/07/2020.
 */
public class AppChildFormFragmentPresenterTest extends BaseUnitTest {

    @Mock
    private AppChildFormFragment formFragment;

    @Mock
    private JsonFormInteractor jsonFormInteractor;

    @Mock
    private JSONObject mStepDetails;

    @Mock
    private ChildFormActivity childFormActivity;

    @Mock
    private JsonFormFragmentView<JsonFormFragmentViewState> formFragmentView;

    @Mock
    private JsonApi jsonApi;

    private AppChildFormFragmentPresenter presenter;


    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        Mockito.doReturn(jsonApi).when(formFragment).getJsonApi();
        Mockito.doReturn(childFormActivity).when(formFragment).getActivity();
        String outOfCatchmentMockJson = "{\n" +
                "  \"count\": \"1\",\n" +
                "  \"encounter_type\": \"Out of Area Service\",\n" +
                "  \"entity_id\": \"\",\n" +
                "  \"metadata\": {\n" +
                "    \"start\": {\n" +
                "      \"openmrs_entity_parent\": \"\",\n" +
                "      \"openmrs_entity\": \"concept\",\n" +
                "      \"openmrs_data_type\": \"start\",\n" +
                "      \"openmrs_entity_id\": \"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "      \"value\": \"2021-03-09 15:03:17\"\n" +
                "    },\n" +
                "    \"end\": {\n" +
                "      \"openmrs_entity_parent\": \"\",\n" +
                "      \"openmrs_entity\": \"concept\",\n" +
                "      \"openmrs_data_type\": \"end\",\n" +
                "      \"openmrs_entity_id\": \"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "    },\n" +
                "    \"today\": {\n" +
                "      \"openmrs_entity_parent\": \"\",\n" +
                "      \"openmrs_entity\": \"encounter\",\n" +
                "      \"openmrs_entity_id\": \"encounter_date\"\n" +
                "    },\n" +
                "    \"deviceid\": {\n" +
                "      \"openmrs_entity_parent\": \"\",\n" +
                "      \"openmrs_entity\": \"concept\",\n" +
                "      \"openmrs_data_type\": \"deviceid\",\n" +
                "      \"openmrs_entity_id\": \"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "      \"value\": \"358240051111110\"\n" +
                "    },\n" +
                "    \"subscriberid\": {\n" +
                "      \"openmrs_entity_parent\": \"\",\n" +
                "      \"openmrs_entity\": \"concept\",\n" +
                "      \"openmrs_data_type\": \"subscriberid\",\n" +
                "      \"openmrs_entity_id\": \"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "      \"value\": \"310260000000000\"\n" +
                "    },\n" +
                "    \"simserial\": {\n" +
                "      \"openmrs_entity_parent\": \"\",\n" +
                "      \"openmrs_entity\": \"concept\",\n" +
                "      \"openmrs_data_type\": \"simserial\",\n" +
                "      \"openmrs_entity_id\": \"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "      \"value\": \"89014103211118510720\"\n" +
                "    },\n" +
                "    \"phonenumber\": {\n" +
                "      \"openmrs_entity_parent\": \"\",\n" +
                "      \"openmrs_entity\": \"concept\",\n" +
                "      \"openmrs_data_type\": \"phonenumber\",\n" +
                "      \"openmrs_entity_id\": \"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "      \"value\": \"+15555215556\"\n" +
                "    },\n" +
                "    \"encounter_location\": \"\"\n" +
                "  },\n" +
                "  \"step1\": {\n" +
                "    \"title\": \"Out of Area Service\",\n" +
                "    \"fields\": [\n" +
                "      {\n" +
                "        \"key\": \"opensrp_id\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"\",\n" +
                "        \"openmrs_entity_id\": \"\",\n" +
                "        \"type\": \"barcode\",\n" +
                "        \"barcode_type\": \"qrcode\",\n" +
                "        \"hint\": \"ZEIR ID\",\n" +
                "        \"scanButtonText\": \"Scan QR Code\",\n" +
                "        \"value\": \"\",\n" +
                "        \"v_required\": {\n" +
                "          \"value\": \"true\",\n" +
                "          \"err\": \"Enter the card Id\"\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"OA_Service_Date\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"encounter\",\n" +
                "        \"openmrs_entity_id\": \"encounter_date\",\n" +
                "        \"type\": \"date_picker\",\n" +
                "        \"hint\": \"Date of Service\",\n" +
                "        \"expanded\": false,\n" +
                "        \"max_date\": \"today\",\n" +
                "        \"v_required\": {\n" +
                "          \"value\": \"true\",\n" +
                "          \"err\": \"Enter the date of service\"\n" +
                "        },\n" +
                "        \"value\": \"12-03-2021\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"Vaccines_Provided_Label\",\n" +
                "        \"type\": \"label\",\n" +
                "        \"label_text_size\": \"20sp\",\n" +
                "        \"label_text_style\": \"bold\",\n" +
                "        \"text_color\": \"#000000\",\n" +
                "        \"text\": \"Which vaccinations were provided?\",\n" +
                "        \"openmrs_entity_parent\": \"-\",\n" +
                "        \"openmrs_entity\": \"-\",\n" +
                "        \"openmrs_entity_id\": \"-\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"Birth\",\n" +
                "        \"type\": \"check_box\",\n" +
                "        \"is_vaccine_group\": true,\n" +
                "        \"label\": \"Birth\",\n" +
                "        \"openmrs_entity_parent\": \"-\",\n" +
                "        \"openmrs_entity\": \"-\",\n" +
                "        \"openmrs_entity_id\": \"-\",\n" +
                "        \"options\": [\n" +
                "          {\n" +
                "            \"key\": \"OPV 0\",\n" +
                "            \"text\": \"OPV 0\",\n" +
                "            \"value\": true,\n" +
                "            \"constraints\": [\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Six_Wks, \\\"[\\\"OPV 1\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other OPV dose\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Ten_Wks, \\\"[\\\"OPV 2\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other OPV dose\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Fourteen_Weeks, \\\"[\\\"OPV 3\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other OPV dose\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Nine_Months, \\\"[\\\"OPV 4\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other OPV dose\"\n" +
                "              }\n" +
                "            ]\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"BCG\",\n" +
                "            \"text\": \"BCG\",\n" +
                "            \"value\": \"false\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"value\": [\n" +
                "          \"OPV 0\"\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"Six_Wks\",\n" +
                "        \"type\": \"check_box\",\n" +
                "        \"is_vaccine_group\": true,\n" +
                "        \"label\": \"6 Weeks\",\n" +
                "        \"openmrs_entity_parent\": \"-\",\n" +
                "        \"openmrs_entity\": \"-\",\n" +
                "        \"openmrs_entity_id\": \"-\",\n" +
                "        \"options\": [\n" +
                "          {\n" +
                "            \"key\": \"OPV 1\",\n" +
                "            \"text\": \"OPV 1\",\n" +
                "            \"value\": true,\n" +
                "            \"constraints\": [\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Birth, \\\"[\\\"OPV 0\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other OPV dose\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Ten_Wks, \\\"[\\\"OPV 2\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other OPV dose\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Fourteen_Weeks, \\\"[\\\"OPV 3\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other OPV dose\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Nine_Months, \\\"[\\\"OPV 4\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other OPV dose\"\n" +
                "              }\n" +
                "            ]\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"Penta 1\",\n" +
                "            \"text\": \"Penta 1\",\n" +
                "            \"value\": \"false\",\n" +
                "            \"constraints\": [\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Ten_Wks, \\\"[\\\"Penta 2\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other Penta dose\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Fourteen_Weeks, \\\"[\\\"Penta 3\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other Penta dose\"\n" +
                "              }\n" +
                "            ]\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"PCV 1\",\n" +
                "            \"text\": \"PCV 1\",\n" +
                "            \"value\": \"false\",\n" +
                "            \"constraints\": [\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Ten_Wks, \\\"[\\\"PCV 2\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other PCV dose\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Fourteen_Weeks, \\\"[\\\"PCV 3\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other PCV dose\"\n" +
                "              }\n" +
                "            ]\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"Rota 1\",\n" +
                "            \"text\": \"Rota 1\",\n" +
                "            \"value\": \"false\",\n" +
                "            \"constraints\": [\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Ten_Wks, \\\"[\\\"Rota 2\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other Rota dose\"\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        ],\n" +
                "        \"value\": [\n" +
                "          \"OPV 1\"\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"Ten_Wks\",\n" +
                "        \"type\": \"check_box\",\n" +
                "        \"is_vaccine_group\": true,\n" +
                "        \"label\": \"10 Weeks\",\n" +
                "        \"openmrs_entity_parent\": \"-\",\n" +
                "        \"openmrs_entity\": \"-\",\n" +
                "        \"openmrs_entity_id\": \"-\",\n" +
                "        \"options\": [\n" +
                "          {\n" +
                "            \"key\": \"OPV 2\",\n" +
                "            \"text\": \"OPV 2\",\n" +
                "            \"value\": true,\n" +
                "            \"constraints\": [\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Birth, \\\"[\\\"OPV 0\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other OPV dose\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Six_Wks, \\\"[\\\"OPV 1\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other OPV dose\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Fourteen_Weeks, \\\"[\\\"OPV 3\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other OPV dose\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Nine_Months, \\\"[\\\"OPV 4\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other OPV dose\"\n" +
                "              }\n" +
                "            ]\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"Penta 2\",\n" +
                "            \"text\": \"Penta 2\",\n" +
                "            \"value\": true,\n" +
                "            \"constraints\": [\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Six_Wks, \\\"[\\\"Penta 1\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other Penta dose\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Fourteen_Weeks, \\\"[\\\"Penta 3\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other Penta dose\"\n" +
                "              }\n" +
                "            ]\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"PCV 2\",\n" +
                "            \"text\": \"PCV 2\",\n" +
                "            \"value\": \"false\",\n" +
                "            \"constraints\": [\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Six_Wks, \\\"[\\\"PCV 1\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other PCV dose\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Fourteen_Weeks, \\\"[\\\"PCV 3\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other PCV dose\"\n" +
                "              }\n" +
                "            ]\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"Rota 2\",\n" +
                "            \"text\": \"Rota 2\",\n" +
                "            \"value\": \"false\",\n" +
                "            \"constraints\": [\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Six_Wks, \\\"[\\\"Rota 1\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other Rota dose\"\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        ],\n" +
                "        \"value\": [\n" +
                "          \"Penta 2\",\n" +
                "          \"OPV 2\"\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"Fourteen_Weeks\",\n" +
                "        \"type\": \"check_box\",\n" +
                "        \"is_vaccine_group\": true,\n" +
                "        \"label\": \"14 Weeks\",\n" +
                "        \"openmrs_entity_parent\": \"-\",\n" +
                "        \"openmrs_entity\": \"-\",\n" +
                "        \"openmrs_entity_id\": \"-\",\n" +
                "        \"options\": [\n" +
                "          {\n" +
                "            \"key\": \"OPV 3\",\n" +
                "            \"text\": \"OPV 3\",\n" +
                "            \"value\": true,\n" +
                "            \"constraints\": [\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Birth, \\\"[\\\"OPV 0\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other OPV dose\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Six_Wks, \\\"[\\\"OPV 1\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other OPV dose\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Ten_Wks, \\\"[\\\"OPV 2\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other OPV dose\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Nine_Months, \\\"[\\\"OPV 4\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other OPV dose\"\n" +
                "              }\n" +
                "            ]\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"PCV 3\",\n" +
                "            \"text\": \"PCV 3\",\n" +
                "            \"value\": \"false\",\n" +
                "            \"constraints\": [\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Six_Wks, \\\"[\\\"PCV 1\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other PCV dose\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Ten_Wks, \\\"[\\\"PCV 2\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other PCV dose\"\n" +
                "              }\n" +
                "            ]\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"Penta 3\",\n" +
                "            \"text\": \"Penta 3\",\n" +
                "            \"value\": \"false\",\n" +
                "            \"constraints\": [\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Six_Wks, \\\"[\\\"Penta 1\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other Penta dose\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Ten_Wks, \\\"[\\\"Penta 2\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other Penta dose\"\n" +
                "              }\n" +
                "            ]\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"IPV\",\n" +
                "            \"text\": \"IPV\",\n" +
                "            \"value\": \"false\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"value\": [\n" +
                "          \"OPV 3\"\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"Nine_Months\",\n" +
                "        \"type\": \"check_box\",\n" +
                "        \"is_vaccine_group\": true,\n" +
                "        \"label\": \"9 Months\",\n" +
                "        \"openmrs_entity_parent\": \"-\",\n" +
                "        \"openmrs_entity\": \"-\",\n" +
                "        \"openmrs_entity_id\": \"-\",\n" +
                "        \"options\": [\n" +
                "          {\n" +
                "            \"key\": \"MR 1\",\n" +
                "            \"text\": \"MR 1\",\n" +
                "            \"value\": \"false\",\n" +
                "            \"constraints\": [\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Eighteen_Months, \\\"[\\\"MR 2\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other MR dose\"\n" +
                "              }\n" +
                "            ]\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"OPV 4\",\n" +
                "            \"text\": \"OPV 4\",\n" +
                "            \"value\": \"false\",\n" +
                "            \"constraints\": [\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Birth, \\\"[\\\"OPV 0\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other OPV dose\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Six_Wks, \\\"[\\\"OPV 1\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other OPV dose\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Ten_Wks, \\\"[\\\"OPV 2\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other OPV dose\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Fourteen_Weeks, \\\"[\\\"OPV 3\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other OPV dose\"\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"Eighteen_Months\",\n" +
                "        \"type\": \"check_box\",\n" +
                "        \"is_vaccine_group\": true,\n" +
                "        \"label\": \"18 Months\",\n" +
                "        \"openmrs_entity_parent\": \"-\",\n" +
                "        \"openmrs_entity\": \"-\",\n" +
                "        \"openmrs_entity_id\": \"-\",\n" +
                "        \"options\": [\n" +
                "          {\n" +
                "            \"key\": \"MR 2\",\n" +
                "            \"text\": \"MR 2\",\n" +
                "            \"value\": \"false\",\n" +
                "            \"constraints\": [\n" +
                "              {\n" +
                "                \"type\": \"array\",\n" +
                "                \"ex\": \"notEqualTo(step1:Nine_Months, \\\"[\\\"MR 1\\\"]\\\")\",\n" +
                "                \"err\": \"Cannot be given with the other MR dose\"\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"recurring_service_types\",\n" +
                "        \"type\": \"check_box\",\n" +
                "        \"label\": \"Other services provided\",\n" +
                "        \"text_color\": \"#000000\",\n" +
                "        \"label_text_style\": \"bold\",\n" +
                "        \"openmrs_entity_parent\": \"recurring_service_types\",\n" +
                "        \"openmrs_entity\": \"concept\",\n" +
                "        \"openmrs_entity_id\": \"recurring_service_types\",\n" +
                "        \"options\": [\n" +
                "          {\n" +
                "            \"key\": \"vit_a\",\n" +
                "            \"text\": \"Vitamin A dose\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"deworming\",\n" +
                "            \"text\": \"Deworming\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\": \"itn\",\n" +
                "            \"text\": \"ITNs\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"properties_file_name\": \"out_of_catchment_service\",\n" +
                "  \"invisible_required_fields\": \"[]\"\n" +
                "}";
        Mockito.doReturn(new JSONObject(outOfCatchmentMockJson)).when(jsonApi).getmJSONObject();
        presenter = Mockito.spy(new AppChildFormFragmentPresenter(formFragment, jsonFormInteractor));
        presenter.attachView(formFragmentView);

        ReflectionHelpers.setField(presenter, "mStepName", "step1");

        Mockito.doReturn(true).when(mStepDetails).has(Constants.JSON_FORM_EXTRA.NEXT);
        ReflectionHelpers.setField(presenter, "mStepDetails", mStepDetails);

    }

    @Test
    public void testOnCheckedChange() {
        CheckBox compoundButton = Mockito.mock(CheckBox.class);

        Mockito.doReturn(true).when(compoundButton).isChecked();
        Mockito.doReturn("Birth").when(compoundButton).getTag(com.vijay.jsonwizard.R.id.key);
        Mockito.doReturn("OPV 0").when(compoundButton).getTag(com.vijay.jsonwizard.R.id.childKey);
        ReflectionHelpers.setField(presenter,"encounterType",  "Out of Area Service");

        presenter.onCheckedChanged(compoundButton, true);

        Mockito.verify(compoundButton, Mockito.times(1)).setChecked(false);
    }
}