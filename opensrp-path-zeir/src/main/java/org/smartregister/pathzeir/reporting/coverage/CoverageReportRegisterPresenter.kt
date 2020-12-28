package org.smartregister.pathzeir.reporting.coverage

import android.app.Activity
import android.content.Intent
import org.smartregister.pathzeir.reporting.CoverageReportGroup
import java.lang.ref.WeakReference

class CoverageReportRegisterPresenter(override val coverageReportRegisterView: CoverageReportContract.View) : CoverageReportContract.Presenter {

    private val reportingActivity: WeakReference<Activity> = WeakReference<Activity>(coverageReportRegisterView as Activity)

    override fun startReport(reportGroup: CoverageReportGroup) {
        reportingActivity.get()?.let {
            when (reportGroup) {
                CoverageReportGroup.COHORT_COVERAGE_REPORT -> it.startActivity(Intent(it, CohortCoverageReportActivity::class.java))
                CoverageReportGroup.ANNUAL_REPORT_CSO -> TODO()
                CoverageReportGroup.ANNUAL_REPORT_ZEIR -> TODO()
            }
        }
    }
}