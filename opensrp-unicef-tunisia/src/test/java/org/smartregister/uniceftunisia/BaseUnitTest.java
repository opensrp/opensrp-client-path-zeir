package org.smartregister.uniceftunisia;

import android.os.Build;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.uniceftunisia.shadow.ShadowAssetHandler;

@RunWith (RobolectricTestRunner.class)
@Config (application = TestUnicefTunisiaApplication.class, sdk = Build.VERSION_CODES.P, shadows = {ShadowAssetHandler.class})
public abstract class BaseUnitTest {
    protected static final String DUMMY_USERNAME = "myusername";
    protected static final String DUMMY_PASSWORD = "mypassword";
}
