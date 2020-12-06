package org.smartregister.uniceftunisia.reporting.monthly.indicator.form

import android.content.Intent
import androidx.navigation.fragment.NavHostFragment
import junit.framework.Assert.assertTrue
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.TestUnicefTunisiaApplication
import org.smartregister.uniceftunisia.reporting.ReportingTestDataProvider
import org.smartregister.uniceftunisia.reporting.common.MONTHLY_TALLIES
import org.smartregister.uniceftunisia.reporting.common.SHOW_DATA
import org.smartregister.uniceftunisia.reporting.common.YEAR_MONTH
import org.smartregister.uniceftunisia.reporting.monthly.indicator.ReportIndicatorsActivity
import java.io.Serializable

@RunWith(RobolectricTestRunner::class)
@Config(application = TestUnicefTunisiaApplication::class)
class ReportIndicatorsFormFragmentTest {

    private lateinit var reportIndicatorsActivity: ReportIndicatorsActivity

    @Before
    fun `Before every test`() {
        val intent = Intent().apply {
            putExtra(YEAR_MONTH, "2020-12")
            putExtra(SHOW_DATA, false)
            putExtra(MONTHLY_TALLIES, ReportingTestDataProvider.getSentMonthlyTallies().associateBy { it.indicator } as Serializable)
        }
        reportIndicatorsActivity = Robolectric.buildActivity(ReportIndicatorsActivity::class.java, intent).create().resume().get()
    }

    @Test
    fun `Should launch form fragment with data`() {
        reportIndicatorsActivity.navController.navigate(R.id.reportIndicatorsFormFragment)
        val navHostFragment = reportIndicatorsActivity.supportFragmentManager
                .findFragmentById(R.id.reportIndicatorsNavController) as NavHostFragment
        val fragment = navHostFragment.childFragmentManager.fragments[0]
        assertNotNull(fragment)
        assertTrue(fragment is ReportIndicatorsFormFragment)
    }

    @After
    fun `After every test`() {
        reportIndicatorsActivity.finish()
    }
}