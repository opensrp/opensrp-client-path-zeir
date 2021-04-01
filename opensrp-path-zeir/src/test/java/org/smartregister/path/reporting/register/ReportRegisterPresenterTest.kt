package org.smartregister.path.reporting.register

import android.app.Activity
import io.mockk.justRun
import io.mockk.mockkClass
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.smartregister.path.reporting.ReportGroup

class ReportRegisterPresenterTest {

    private val view: ReportRegisterContract.View = mockkClass(ReportRegisterActivity::class, relaxed = true)
    private lateinit var reportRegisterPresenter: ReportRegisterPresenter

    @Before
    fun `Before every test`() {
        reportRegisterPresenter = ReportRegisterPresenter(view)
    }

    @After
    fun `After every test`() {
        unmockkAll()
    }

    @Test
    fun `Should start monthly report`() {
        justRun { (view as Activity).startActivity(any()) }
        reportRegisterPresenter.startReport(ReportGroup.MONTHLY_REPORTS)
    }

    @Test
    fun `Should start annual coverage report`() {
        justRun { (view as Activity).startActivity(any()) }
        reportRegisterPresenter.startReport(ReportGroup.ANNUAL_COVERAGE_REPORTS)
    }

    @Test
    fun `Should return non null view`() {
        assertNotNull(reportRegisterPresenter.reportRegisterView)
    }
}