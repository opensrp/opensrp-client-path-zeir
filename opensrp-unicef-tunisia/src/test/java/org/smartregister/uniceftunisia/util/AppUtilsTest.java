package org.smartregister.uniceftunisia.util;

import android.content.ContentValues;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.activity.BaseChildImmunizationActivity;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication;
import org.smartregister.view.activity.BaseProfileActivity;
import org.smartregister.view.activity.DrishtiApplication;

public class AppUtilsTest {

    @Spy
    private UnicefTunisiaApplication unicefTunisiaApplication;

    @Mock
    private Context context;

    @Captor
    private ArgumentCaptor argumentCaptorUpdateChildTable;

    @Captor
    private ArgumentCaptor argumentCaptorSaveCurrentLocality;

    @Captor
    private ArgumentCaptor argumentCaptorUpdateChildFtsTable;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private AllCommonsRepository allCommonsRepository;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private LocationHelper locationHelper;

    @Mock
    private ChildLibrary childLibrary;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", unicefTunisiaApplication);
        ChildMetadata childMetadata = new ChildMetadata(BaseChildFormActivity.class, BaseProfileActivity.class, BaseChildImmunizationActivity.class, null, true);
        childMetadata.updateChildRegister(
                "crazy_form_name",
                "childTable",
                "guardianTable",
                "Birth Registration",
                "Birth Registration",
                "Immunization",
                "none",
                "12345",
                "Out of Catchment");
        Mockito.when(Utils.metadata()).thenReturn(childMetadata);
    }

    @Test
    public void testUpdateChildDeath() {
        Mockito.when(unicefTunisiaApplication.context()).thenReturn(context);
        Mockito.when(context.allCommonsRepositoryobjects("childTable")).thenReturn(allCommonsRepository);
        Client client = new Client("123");
        client.setDeathdate(new DateTime());
        EventClient eventClient = new EventClient(new Event(), client);
        AppUtils.updateChildDeath(eventClient);

        Mockito.verify(allCommonsRepository).update((String) argumentCaptorUpdateChildTable.capture(), (ContentValues) argumentCaptorUpdateChildTable.capture(), (String) argumentCaptorUpdateChildTable.capture());
        Mockito.verify(allCommonsRepository).updateSearch((String) argumentCaptorUpdateChildFtsTable.capture());
        Assert.assertNotNull(argumentCaptorUpdateChildTable.getAllValues().get(0));
        Assert.assertEquals(client.getBaseEntityId(), argumentCaptorUpdateChildTable.getAllValues().get(2));
        Assert.assertEquals(client.getBaseEntityId(), argumentCaptorUpdateChildFtsTable.getValue());
    }

    @Test
    public void testGetCurrentLocalityShouldReturnCorrectValueIfPresent() {
        Mockito.when(unicefTunisiaApplication.context()).thenReturn(context);
        Mockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        Mockito.when(allSharedPreferences.fetchCurrentLocality()).thenReturn("child location 1");
        Assert.assertEquals("child location 1", AppUtils.getCurrentLocality());
    }

    @Test
    public void testGetCurrentLocalityShouldReturnCorrectValueIfAbsent() {
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);
        Mockito.when(locationHelper.getDefaultLocation()).thenReturn("Default Location");
        Mockito.when(unicefTunisiaApplication.context()).thenReturn(context);
        Mockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        Mockito.when(allSharedPreferences.fetchCurrentLocality()).thenReturn(null);
        Assert.assertEquals("Default Location", AppUtils.getCurrentLocality());
        Mockito.verify(allSharedPreferences).saveCurrentLocality(String.valueOf(argumentCaptorSaveCurrentLocality.capture()));
        Assert.assertEquals("Default Location", argumentCaptorSaveCurrentLocality.getValue());
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", null);
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", null);
    }
}