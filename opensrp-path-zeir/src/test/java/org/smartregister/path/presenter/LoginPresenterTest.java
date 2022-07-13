package org.smartregister.path.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.Context;
import org.smartregister.domain.Setting;
import org.smartregister.path.BaseUnitTest;
import org.smartregister.path.application.ZeirApplication;
import org.smartregister.path.util.AppConstants;
import org.smartregister.repository.AllSettings;
import org.smartregister.view.contract.BaseLoginContract;
@Ignore("Fix memory leak error: #250")
public class LoginPresenterTest extends BaseUnitTest {

    @Mock
    private ZeirApplication zeirApplication;

    @Mock
    private Context context;

    @Mock
    private AllSettings allSettings;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void isServerSettingsSetShouldReturnTrue() {
        String value = "{\"identifier\":" +
                "\"site_characteristics\"," +
                "\"settings\":[" +
                "{\"description\":\"\\\"Are all of the following in place at your facility: \\r\\n" +
                "1. A protocol or standard operating procedure for Intimate Partner Violence (IPV); \\r\\n" +
                "2. A health worker trained on how to ask about IPV and how to provide the minimum response or beyond;\\r\\n" +
                "3. A private setting; \\r\\n4. A way to ensure confidentiality; \\r\\n" +
                "5. Time to allow for appropriate disclosure; and\\r\\n" +
                "6. A system for referral in place. \\\"\",\"label\":\"Minimum requirements for IPV assessment\",\"value\":\"true\",\"key\":\"site_ipv_assess\"}," +
                "{\"description\":\"Is the HIV prevalence consistently > 1% in pregnant women attending antenatal clinics at your facility?\",\"label\":\"Generalized HIV epidemic\",\"value\":\"true\",\"key\":\"site_anc_hiv\"}," +
                "{\"description\":\"Is an ultrasound machine available and functional at your facility and a trained health worker available to use it?\",\"label\":\"Ultrasound available\",\"value\":\"true\",\"key\":\"site_ultrasound\"}," +
                "{\"description\":\"Does your facility use an automated blood pressure (BP) measurement tool?\",\"label\":\"Automated BP measurement tool\",\"value\":\"true\",\"key\":\"site_bp_tool\"}],\"serverVersion\":1571141226133," +
                "\"providerId\":\"rwong\",\"locationId\":\"99d01128-51cd-42de-84c4-432b3ac56532\",\"teamId\":\"095f9471-27af-4958-9b98-e129dd92d843\",\"_rev\":\"v4\",\"team\":\"Lizulu Health Centre\",\"_id\":\"f6a0a0c0-9f3e-4161-a52f-0e3da84475fe\",\"type\":\"SettingConfiguration\"}";

        Setting setting = new Setting();
        setting.setValue(value);
        Mockito.when(allSettings.getSetting(AppConstants.KeyConstants.SITE_CHARACTERISTICS)).thenReturn(setting);
        Mockito.when(context.allSettings()).thenReturn(allSettings);
        LoginPresenter loginPresenter = new LoginPresenter(Mockito.mock(BaseLoginContract.View.class));
        Assert.assertTrue(loginPresenter.isServerSettingsSet());
    }

    @Test
    public void isServerSettingsSetShouldReturnFalse() {
        String value = "{\"identifier\":" +
                "\"site_characteristics\"," +
                "\"settings\":[" +
                "{\"description\":\"\\\"Are all of the following in place at your facility: \\r\\n" +
                "1. A protocol or standard operating procedure for Intimate Partner Violence (IPV); \\r\\n" +
                "2. A health worker trained on how to ask about IPV and how to provide the minimum response or beyond;\\r\\n" +
                "3. A private setting; \\r\\n4. A way to ensure confidentiality; \\r\\n" +
                "5. Time to allow for appropriate disclosure; and\\r\\n" +
                "6. A system for referral in place. \\\"\",\"label\":\"Minimum requirements for IPV assessment\",\"key\":\"site_ipv_assess\"}," +
                "{\"description\":\"Is the HIV prevalence consistently > 1% in pregnant women attending antenatal clinics at your facility?\",\"label\":\"Generalized HIV epidemic\",\"value\":\"true\",\"key\":\"site_anc_hiv\"}," +
                "{\"description\":\"Is an ultrasound machine available and functional at your facility and a trained health worker available to use it?\",\"label\":\"Ultrasound available\",\"value\":\"true\",\"key\":\"site_ultrasound\"}," +
                "{\"description\":\"Does your facility use an automated blood pressure (BP) measurement tool?\",\"label\":\"Automated BP measurement tool\",\"value\":\"true\",\"key\":\"site_bp_tool\"}],\"serverVersion\":1571141226133," +
                "\"providerId\":\"rwong\",\"locationId\":\"99d01128-51cd-42de-84c4-432b3ac56532\",\"teamId\":\"095f9471-27af-4958-9b98-e129dd92d843\",\"_rev\":\"v4\",\"team\":\"Lizulu Health Centre\",\"_id\":\"f6a0a0c0-9f3e-4161-a52f-0e3da84475fe\",\"type\":\"SettingConfiguration\"}";

        Setting setting = new Setting();
        setting.setValue(value);
        Mockito.when(allSettings.getSetting(AppConstants.KeyConstants.SITE_CHARACTERISTICS)).thenReturn(setting);
        Mockito.when(context.allSettings()).thenReturn(allSettings);
        LoginPresenter loginPresenter = new LoginPresenter(Mockito.mock(BaseLoginContract.View.class));
        Assert.assertTrue(loginPresenter.isServerSettingsSet());
    }
}