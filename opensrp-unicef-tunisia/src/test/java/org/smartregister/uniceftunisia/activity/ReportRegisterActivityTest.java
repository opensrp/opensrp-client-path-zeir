package org.smartregister.uniceftunisia.activity;

import android.widget.ListView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.smartregister.uniceftunisia.BaseRobolectricTest;
import org.smartregister.uniceftunisia.R;
import org.smartregister.uniceftunisia.reporting.register.ReportRegisterActivity;

public class ReportRegisterActivityTest extends BaseRobolectricTest {

    private ReportRegisterActivity reportRegisterActivity;

    @Before
    public void setUp() {
        reportRegisterActivity = Robolectric.setupActivity(ReportRegisterActivity.class);
    }

    @Test
    public void testThatActivityStartedCorrectly() {
        Assert.assertEquals(R.layout.activity_report_register, reportRegisterActivity.getContentView());
        ListView listView = reportRegisterActivity.findViewById(R.id.reportRegisterListView);
        listView.performClick();
        Assert.assertNotNull(listView);

    }
}