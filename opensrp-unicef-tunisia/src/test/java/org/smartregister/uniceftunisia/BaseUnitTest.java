package org.smartregister.uniceftunisia;

import android.os.Build;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.uniceftunisia.shadow.ShadowAssetHandler;


@RunWith (PowerMockRunner.class)
@PowerMockRunnerDelegate (RobolectricTestRunner.class)
@Config (application = TestUnicefTunisiaApplication.class, sdk = Build.VERSION_CODES.O_MR1, constants = BuildConfig.class, shadows = {ShadowAssetHandler.class})
@PowerMockIgnore ({"org.mockito.*", "org.robolectric.*", "android.*"})
public abstract class BaseUnitTest {

    protected static final String DUMMY_USERNAME = "myusername";
    protected static final String DUMMY_PASSWORD = "mypassword";
}
