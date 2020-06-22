package org.smartregister.uniceftunisia.util;

import android.content.ContentValues;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.domain.db.Client;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication;

@PrepareForTest({Utils.class, UnicefTunisiaApplication.class, CoreLibrary.class, LocationHelper.class})
@RunWith(PowerMockRunner.class)
public class AppUtilsTest {

    @Mock
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
    private ChildMetadata childMetadata;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private LocationHelper locationHelper;

    @Test
    public void testUpdateChildDeath() {
        PowerMockito.mockStatic(UnicefTunisiaApplication.class);
        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(unicefTunisiaApplication.context()).thenReturn(context);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(context.allCommonsRepositoryobjects(null)).thenReturn(allCommonsRepository);
        PowerMockito.when(UnicefTunisiaApplication.getInstance()).thenReturn(unicefTunisiaApplication);
        childMetadata.childRegister = PowerMockito.mock(ChildMetadata.ChildRegister.class);
        PowerMockito.when(Utils.metadata()).thenReturn(childMetadata);
        Client client = new Client("123");
        client.setDeathdate(new DateTime());
        EventClient eventClient = new EventClient(new Event(), client);

        AppUtils.updateChildDeath(eventClient);

        Mockito.verify(allCommonsRepository).update((String) argumentCaptorUpdateChildTable.capture(), (ContentValues) argumentCaptorUpdateChildTable.capture(), (String) argumentCaptorUpdateChildTable.capture());
        Mockito.verify(allCommonsRepository).updateSearch((String) argumentCaptorUpdateChildFtsTable.capture());
        Assert.assertNull(argumentCaptorUpdateChildTable.getAllValues().get(0));
        Assert.assertNull(argumentCaptorUpdateChildTable.getAllValues().get(1).toString());
        Assert.assertEquals(client.getBaseEntityId(), argumentCaptorUpdateChildTable.getAllValues().get(2));
        Assert.assertEquals(client.getBaseEntityId(), argumentCaptorUpdateChildFtsTable.getValue());
    }

    @Test
    public void testGetCurrentLocalityShouldReturnCorrectValueIfPresent() {
        PowerMockito.mockStatic(UnicefTunisiaApplication.class);
        PowerMockito.when(UnicefTunisiaApplication.getInstance()).thenReturn(unicefTunisiaApplication);
        PowerMockito.when(unicefTunisiaApplication.context()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        PowerMockito.when(allSharedPreferences.fetchCurrentLocality()).thenReturn("child location 1");
        Assert.assertEquals("child location 1", AppUtils.getCurrentLocality());
    }

    @Test
    public void testGetCurrentLocalityShouldReturnCorrectValueIfAbsent() {
        PowerMockito.mockStatic(UnicefTunisiaApplication.class);
        PowerMockito.mockStatic(LocationHelper.class);
        PowerMockito.when(LocationHelper.getInstance()).thenReturn(locationHelper);
        PowerMockito.when(locationHelper.getDefaultLocation()).thenReturn("Default Location");
        PowerMockito.when(UnicefTunisiaApplication.getInstance()).thenReturn(unicefTunisiaApplication);
        PowerMockito.when(unicefTunisiaApplication.context()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        PowerMockito.when(allSharedPreferences.fetchCurrentLocality()).thenReturn(null);
        Assert.assertEquals("Default Location", AppUtils.getCurrentLocality());
        Mockito.verify(allSharedPreferences).saveCurrentLocality(String.valueOf(argumentCaptorSaveCurrentLocality.capture()));
        Assert.assertEquals("Default Location", argumentCaptorSaveCurrentLocality.getValue());
    }
}