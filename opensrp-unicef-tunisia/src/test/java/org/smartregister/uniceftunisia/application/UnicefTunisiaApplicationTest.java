package org.smartregister.uniceftunisia.application;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.uniceftunisia.BaseUnitTest;
import org.smartregister.uniceftunisia.TestUnicefTunisiaApplication;

/**
 * Created by ndegwamartin on 2019-12-13.
 */
public class UnicefTunisiaApplicationTest extends BaseUnitTest {
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateCommonFtsObjectFunctionsCorrectly() {

        UnicefTunisiaApplication unicefTunisiaApplication = new TestUnicefTunisiaApplication();
        Assert.assertNotNull(unicefTunisiaApplication);

        CommonFtsObject commonFtsObject = UnicefTunisiaApplication.createCommonFtsObject(ApplicationProvider.getApplicationContext());
        Assert.assertNotNull(commonFtsObject);

        String[] ftsObjectTables = commonFtsObject.getTables();
        Assert.assertNotNull(ftsObjectTables);
        Assert.assertEquals(4, ftsObjectTables.length);

        String scheduleName = commonFtsObject.getAlertScheduleName("bcg");
        Assert.assertNotNull(scheduleName);

        scheduleName = commonFtsObject.getAlertScheduleName("penta1");
        Assert.assertNotNull(scheduleName);

        scheduleName = commonFtsObject.getAlertScheduleName("mr1");
        Assert.assertNotNull(scheduleName);

        scheduleName = commonFtsObject.getAlertScheduleName("SomeNonExistentVaccine");
        Assert.assertNull(scheduleName);
    }
}
