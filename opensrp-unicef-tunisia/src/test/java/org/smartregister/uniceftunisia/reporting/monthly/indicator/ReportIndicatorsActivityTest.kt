package org.smartregister.uniceftunisia.reporting.monthly.indicator

import android.content.Intent
import android.widget.ImageButton
import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.TestUnicefTunisiaApplication
import org.smartregister.uniceftunisia.reporting.ReportingTestDataProvider.getSentMonthlyTallies
import org.smartregister.uniceftunisia.reporting.common.MONTHLY_TALLIES
import org.smartregister.uniceftunisia.reporting.common.SHOW_DATA
import org.smartregister.uniceftunisia.reporting.common.YEAR_MONTH
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsActivity
import java.io.Serializable

@RunWith(RobolectricTestRunner::class)
@Config(application = TestUnicefTunisiaApplication::class)
class ReportIndicatorsActivityTest {

    private lateinit var activityController: ActivityController<ReportIndicatorsActivity>
    private lateinit var reportIndicatorsActivity: ReportIndicatorsActivity

    @Before
    fun `Before every test`() {
        val intent = Intent().apply {
            putExtra(YEAR_MONTH, "2020-12")
            putExtra(SHOW_DATA, false)
            putExtra(MONTHLY_TALLIES, getSentMonthlyTallies().associateBy { it.indicator } as Serializable)
        }
        activityController = Robolectric.buildActivity(ReportIndicatorsActivity::class.java, intent)
        reportIndicatorsActivity = activityController.get()
    }

    @Test
    fun `Should start activity successfully`() {
        activityController.create()
        assertEquals("December 2020 Draft", reportIndicatorsActivity.findViewById<TextView>(R.id.yearMonthTextView).text)
        assertEquals(3, reportIndicatorsActivity.reportIndicatorsViewModel.monthlyTalliesMap.value!!.size)
    }

    @Test
    fun `Should finish activity when back button is clicked`() {
        activityController.create()
        reportIndicatorsActivity.findViewById<ImageButton>(R.id.backButton).performClick()
        val startedIntent: Intent = shadowOf(reportIndicatorsActivity).nextStartedActivity
        val shadowIntent = shadowOf(startedIntent)
        assertEquals(MonthlyReportsActivity::class.java, shadowIntent.intentClass)
        assertTrue(reportIndicatorsActivity.isFinishing)
    }

    @Test
    fun `Should navigate to form fragment`() {
        activityController.create().resume()
        val label = reportIndicatorsActivity.navController.currentBackStackEntry?.destination?.label
        assertEquals("Report Indicators Form", label)
        assertEquals(reportIndicatorsActivity.navController.currentDestination?.id, R.id.reportIndicatorsFormFragment)
    }
}