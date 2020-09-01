package org.smartregister.uniceftunisia;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.uniceftunisia.shadow.CustomFontTextViewShadow;
import org.smartregister.uniceftunisia.shadow.ShadowAssetHandler;
import org.smartregister.uniceftunisia.shadow.ShadowBaseJob;
import org.smartregister.view.customcontrols.CustomFontTextView;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 05-03-2020.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {27}, shadows = {ShadowBaseJob.class, ShadowAssetHandler.class, CustomFontTextViewShadow.class}, application = TestUnicefTunisiaApplication.class)
public abstract class BaseRobolectricTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

}
