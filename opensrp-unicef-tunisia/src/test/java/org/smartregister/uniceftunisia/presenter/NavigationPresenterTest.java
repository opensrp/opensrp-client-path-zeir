package org.smartregister.uniceftunisia.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.smartregister.uniceftunisia.contract.NavigationContract;
import org.smartregister.uniceftunisia.util.AppConstants;

import java.util.HashMap;

@RunWith(PowerMockRunner.class)
public class NavigationPresenterTest {

    private NavigationPresenter navigationPresenter;

    @Mock
    private NavigationContract.View view;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        navigationPresenter = new NavigationPresenter(view);
    }

    @Test
    public void initialize() throws Exception {
        HashMap<String, String> tableMap = new HashMap<>();
        Whitebox.setInternalState(navigationPresenter, "tableMap", tableMap);
        Whitebox.invokeMethod(navigationPresenter, "initialize");
        Assert.assertEquals(4, tableMap.size());
        Assert.assertTrue(tableMap.containsKey(AppConstants.DrawerMenu.ALL_CLIENTS));
        Assert.assertTrue(tableMap.containsKey(AppConstants.DrawerMenu.CHILD_CLIENTS));
        Assert.assertTrue(tableMap.containsKey(AppConstants.DrawerMenu.ANC));
        Assert.assertTrue(tableMap.containsKey(AppConstants.DrawerMenu.ANC_CLIENTS));
    }

    @Test
    public void getNavigationView() {
        Assert.assertNotNull(navigationPresenter.getNavigationView());
    }
}