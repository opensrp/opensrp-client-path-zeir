package org.smartregister.uniceftunisia.interactor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.smartregister.uniceftunisia.BaseRobolectricTest;
import org.smartregister.uniceftunisia.shadow.ShadowBaseJob;
import org.smartregister.growthmonitoring.job.HeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.WeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.ZScoreRefreshIntentServiceJob;
import org.smartregister.immunization.job.RecurringServiceJob;
import org.smartregister.immunization.job.VaccineServiceJob;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.reporting.job.RecurringIndicatorGeneratingJob;
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
    public void setUp() throws Exception {
        loginInteractor = new LoginInteractor(Mockito.mock(BaseLoginContract.Presenter.class));
    }

    @Test
    public void scheduleJobsPeriodically() {
        loginInteractor.scheduleJobsPeriodically();

        Assert.assertTrue(ShadowBaseJob.getShadowHelper().isCalled(ShadowBaseJob.scheduleJobMN));
        HashMap<Integer, ArrayList<Object>> methodCalls = ShadowBaseJob.getShadowHelper().getMethodCalls(ShadowBaseJob.scheduleJobMN);
        assertEquals(9, methodCalls.size());
        assertEquals(VaccineServiceJob.TAG, methodCalls.get(0).get(0));
        assertEquals(RecurringServiceJob.TAG, methodCalls.get(1).get(0));
        assertEquals(WeightIntentServiceJob.TAG, methodCalls.get(2).get(0));
        assertEquals(HeightIntentServiceJob.TAG, methodCalls.get(3).get(0));
        assertEquals(ZScoreRefreshIntentServiceJob.TAG, methodCalls.get(4).get(0));
        assertEquals(SyncServiceJob.TAG, methodCalls.get(5).get(0));
        assertEquals(PullUniqueIdsServiceJob.TAG, methodCalls.get(6).get(0));
        assertEquals(ImageUploadServiceJob.TAG, methodCalls.get(7).get(0));
        assertEquals(15L, methodCalls.get(7).get(1));
        assertEquals(RecurringIndicatorGeneratingJob.TAG, methodCalls.get(8).get(0));
    }

    @Test
    public void scheduleJobsImmediatelyShouldCallEachJobToScheduleImmediateExecution() {
        loginInteractor.scheduleJobsImmediately();

        Assert.assertTrue(ShadowBaseJob.getShadowHelper().isCalled(ShadowBaseJob.scheduleJobImmediatelyMN));
        HashMap<Integer, ArrayList<Object>> methodCalls = ShadowBaseJob.getShadowHelper().getMethodCalls(ShadowBaseJob.scheduleJobImmediatelyMN);
        assertEquals(4, methodCalls.size());
        assertEquals(SyncServiceJob.TAG, methodCalls.get(0).get(0));
        assertEquals(PullUniqueIdsServiceJob.TAG, methodCalls.get(1).get(0));
        assertEquals(ZScoreRefreshIntentServiceJob.TAG, methodCalls.get(2).get(0));
        assertEquals(ImageUploadServiceJob.TAG, methodCalls.get(3).get(0));
    }
}