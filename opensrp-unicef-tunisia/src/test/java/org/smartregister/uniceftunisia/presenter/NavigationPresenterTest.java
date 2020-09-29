package org.smartregister.uniceftunisia.presenter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.uniceftunisia.BaseUnitTest;
import org.smartregister.uniceftunisia.contract.NavigationContract;
import org.smartregister.uniceftunisia.util.AppConstants;

import java.util.HashMap;

public class NavigationPresenterTest extends BaseUnitTest {

    private NavigationPresenter navigationPresenter;

    @Mock
    private NavigationContract.View view;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        navigationPresenter = new NavigationPresenter(view);
    }

    @Test
    public void initialize() {
        HashMap<String, String> tableMap = new HashMap<>();
        ReflectionHelpers.setField(navigationPresenter, "tableMap", tableMap);
        ReflectionHelpers.callInstanceMethod(navigationPresenter, "initialize");
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

    @After
    public void tearDown(){
        navigationPresenter = null;
    }
}