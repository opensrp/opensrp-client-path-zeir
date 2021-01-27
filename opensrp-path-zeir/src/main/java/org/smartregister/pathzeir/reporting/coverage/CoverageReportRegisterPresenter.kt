package org.smartregister.pathzeir.reporting.coverage

import android.app.Activity
import android.content.Intent
import org.smartregister.pathzeir.reporting.CoverageReportGroup
import org.smartregister.pathzeir.reporting.coverage.cohort.CohortCoverageReportActivity
import org.smartregister.pathzeir.reporting.coverage.cso.AnnualCoverageReportCsoActivity
import org.smartregister.pathzeir.reporting.coverage.zeir.AnnualCoverageReportZeirActivity
import java.lang.ref.WeakReference

class CoverageReportRegisterPresenter(override val coverageReportRegisterView: CoverageReportContract.View) : CoverageReportContract.Presenter {

    private val reportingActivity: WeakReference<Activity> = WeakReference<Activity>(coverageReportRegisterView as Activity)

    override fun startReport(reportGroup: CoverageReportGroup) {
        reportingActivity.get()?.let {
            when (reportGroup) {
                CoverageReportGroup.COHORT_COVERAGE_REPORT -> it.startActivity(Intent(it, CohortCoverageReportActivity::class.java))
                CoverageReportGroup.ANNUAL_REPORT_CSO -> it.startActivity(Intent(it, AnnualCoverageReportCsoActivity::class.java))
                CoverageReportGroup.ANNUAL_REPORT_ZEIR -> it.startActivity(Intent(it, AnnualCoverageReportZeirActivity::class.java))
            }
        }
    }
}