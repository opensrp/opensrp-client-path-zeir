package org.smartregister.path.reporting.annual.coverage

import android.widget.ImageButton
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.smartregister.path.R
import org.smartregister.path.TestZeirApplication
import org.smartregister.path.reporting.annual.AnnualReportActivity

@RunWith(RobolectricTestRunner::class)
@Config(application = TestZeirApplication::class)
class AnnualCoverageFragmentTest {

    private val annualReportActivityController = Robolectric.buildActivity(AnnualReportActivity::class.java)
    private lateinit var annualReportActivity: AnnualReportActivity

    @Before
    fun `Before every test`() {
        annualReportActivity = annualReportActivityController.get()
    }

    @Test
    fun `Should start annual report activity`() {
        annualReportActivityController.create()
        annualReportActivity.findViewById<ImageButton>(R.id.backButton).performClick()
        assertTrue(annualReportActivity.isFinishing)
    }

    @After
    fun `After every test`() {
        annualReportActivityController.destroy()
    }
}