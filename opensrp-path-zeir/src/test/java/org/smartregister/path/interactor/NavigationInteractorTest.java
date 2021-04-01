package org.smartregister.path.interactor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.smartregister.path.BaseUnitTest;

import java.util.Date;

public class NavigationInteractorTest extends BaseUnitTest {

    private NavigationInteractor navigationInteractor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        navigationInteractor = new NavigationInteractor();
    }

    @Test
    public void testSync(){
        Date syncDate = navigationInteractor.sync();
        Assert.assertNotNull(syncDate);
    }
}