package org.smartregister.path.presenter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.path.BaseUnitTest;
import org.smartregister.path.contract.NavigationContract;
import org.smartregister.repository.AllSharedPreferences;

import java.util.Date;
@Ignore("Fix memory leak error: #250")
public class NavigationPresenterTest extends BaseUnitTest {

    private NavigationPresenter navigationPresenter;

    @Mock
    private NavigationContract.View view;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        navigationPresenter = Mockito.spy(new NavigationPresenter(view));
        Mockito.doReturn("demo").when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn("Demo Health Worker").when(allSharedPreferences).getANMPreferredName("demo");
        Mockito.doReturn(allSharedPreferences).when(navigationPresenter).getAllSharedPreferences();
    }

    @Test
    public void testDisplayUser() {
        navigationPresenter.displayCurrentUser();
        Mockito.verify(view, Mockito.atLeastOnce()).refreshCurrentUser(Mockito.anyString());
    }

    @Test
    public void testGetUserInitials() {
        String userInitials = navigationPresenter.getLoggedInUserInitials();
        Assert.assertEquals("DH", userInitials);
    }

    @Test
    public void testRefreshSync() {
        navigationPresenter.refreshLastSync();
        Mockito.verify(view, Mockito.atLeastOnce()).refreshLastSync(Mockito.any(Date.class));
    }

    @Test
    public void testGetNavigationView() {
        Assert.assertNotNull(navigationPresenter.getNavigationView());
    }

    @After
    public void tearDown() {
        navigationPresenter = null;
    }
}