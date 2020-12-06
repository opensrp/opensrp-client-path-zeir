package org.smartregister.uniceftunisia.reporting.monthly

import android.widget.TextView
import com.google.android.material.tabs.TabLayout
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import org.smartregister.Context
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.TestUnicefTunisiaApplication
import org.smartregister.uniceftunisia.reporting.ReportsDao
import org.smartregister.uniceftunisia.reporting.common.ReportingUtils.dateFormatter

@RunWith(RobolectricTestRunner::class)
@Config(application = TestUnicefTunisiaApplication::class)
class MonthlyReportsActivityTest {

    private lateinit var tabLayout: TabLayout
    private val monthlyReportsActivityController: ActivityController<MonthlyReportsActivity> =
            Robolectric.buildActivity(MonthlyReportsActivity::class.java)

    private lateinit var monthlyReportsActivity: MonthlyReportsActivity

    @Before
    fun `Before every test`() {
        //Set current user
        mockkObject(ReportsDao)
        Context.getInstance().allSharedPreferences().updateANMUserName("demo")
        Context.getInstance().allSharedPreferences().updateANMPreferredName("demo", "Health Worker")
        monthlyReportsActivity = monthlyReportsActivityController.get()
        monthlyReportsActivityController.create()
        tabLayout = monthlyReportsActivity.findViewById(R.id.reportFragmentTabLayout)
    }

    @After
    fun `After every test`() {
        unmockkAll()
        monthlyReportsActivityController.pause().stop().destroy();
    }

    @Test
    fun `Should set user initials`() {
        val nameInitialTextView = monthlyReportsActivity.findViewById<TextView>(R.id.nameInitialsTextView)
        assertEquals("HW", nameInitialTextView.text.toString())
    }

    @Test
    fun `Should set the right title for the activity`() {
        val titleTextView = monthlyReportsActivity.findViewById<TextView>(R.id.titleTextView)
        assertEquals(monthlyReportsActivity.getString(R.string.monthly_immunization_growth_monitoring_reports), titleTextView.text.toString())
    }

    @Test
    fun `Should contain two tabs with distinct titles`() {
        assertEquals(2, tabLayout.tabCount)
        val tabOne = tabLayout.getTabAt(0)
        assertNotNull(tabOne)
        assertEquals(tabOne?.text, monthlyReportsActivity.getString(R.string.monthly_draft_reports, 0))
        val tabTwo = tabLayout.getTabAt(1)
        assertNotNull(tabTwo)
        assertEquals(tabTwo?.text, monthlyReportsActivity.getString(R.string.monthly_sent_reports))
    }

    @Test
    fun `Should update tab title when there are drafts`() {
        monthlyReportsActivityController.resume()
        monthlyReportsActivity.monthlyReportsViewModel.draftedMonths.value =
                listOf(Pair("January 2020", dateFormatter("yyyy-MM-dd").parse("2020-01-01")))
        val tabOne = tabLayout.getTabAt(0)
        assertNotNull(tabOne)
        assertEquals(tabOne?.text, monthlyReportsActivity.getString(R.string.monthly_draft_reports, 1))
    }
}