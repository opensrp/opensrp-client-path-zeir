package org.smartregister.pathzeir.interactor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.smartregister.growthmonitoring.job.ZScoreRefreshIntentServiceJob;
import org.smartregister.immunization.job.VaccineServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncAllLocationsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.pathzeir.BaseRobolectricTest;
import org.smartregister.pathzeir.shadow.ShadowBaseJob;
import org.smartregister.view.contract.BaseLoginContract;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 05-03-2020.
 */
public class LoginInteractorTest extends BaseRobolectricTest {

    private LoginInteractor loginInteractor;

    @Before
    public void setUp() {
        loginInteractor = new LoginInteractor(Mockito.mock(BaseLoginContract.Presenter.class));
    }

    @Test
    public void scheduleJobsPeriodically() {
        loginInteractor.scheduleJobsPeriodically();
        Assert.assertTrue(ShadowBaseJob.getShadowHelper().isCalled(ShadowBaseJob.scheduleJobMN));
        HashMap<Integer, ArrayList<Object>> methodCalls = ShadowBaseJob.getShadowHelper().getMethodCalls(ShadowBaseJob.scheduleJobMN);
        assertEquals(10, methodCalls.size());
        assertEquals(VaccineServiceJob.TAG, methodCalls.get(0).get(0));
        for (int i = 0; i < methodCalls.size(); i++) {
            ArrayList<Object> items = methodCalls.get(i);
            Assert.assertEquals(3, items.size());
        }
    }

    @Test
    public void scheduleJobsImmediatelyShouldCallEachJobToScheduleImmediateExecution() {
        loginInteractor.scheduleJobsImmediately();

        Assert.assertTrue(ShadowBaseJob.getShadowHelper().isCalled(ShadowBaseJob.scheduleJobImmediatelyMN));
        HashMap<Integer, ArrayList<Object>> methodCalls = ShadowBaseJob.getShadowHelper().getMethodCalls(ShadowBaseJob.scheduleJobImmediatelyMN);
        assertEquals(6, methodCalls.size());
        assertEquals(SyncServiceJob.TAG, methodCalls.get(0).get(0));
        assertEquals(SyncAllLocationsServiceJob.TAG, methodCalls.get(1).get(0));
        assertEquals(PullUniqueIdsServiceJob.TAG, methodCalls.get(2).get(0));
        assertEquals(ZScoreRefreshIntentServiceJob.TAG, methodCalls.get(3).get(0));
    }
}