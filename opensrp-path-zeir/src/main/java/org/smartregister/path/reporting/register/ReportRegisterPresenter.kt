package org.smartregister.path.reporting.register

import android.app.Activity
import android.content.Intent
import org.smartregister.path.reporting.ReportGroup
import org.smartregister.path.reporting.annual.AnnualReportActivity
import org.smartregister.path.reporting.monthly.MonthlyReportsActivity
import java.lang.ref.WeakReference

class ReportRegisterPresenter(override val reportRegisterView: ReportRegisterContract.View) : ReportRegisterContract.Presenter {

    private val reportingActivity: WeakReference<Activity> = WeakReference<Activity>(reportRegisterView as Activity)

    override fun startReport(reportGroup: ReportGroup) {
        reportingActivity.get()?.let {
            when (reportGroup) {
                ReportGroup.MONTHLY_REPORTS -> it.startActivity(Intent(it, MonthlyReportsActivity::class.java))
                ReportGroup.ANNUAL_COVERAGE_REPORTS -> it.startActivity(Intent(it, AnnualReportActivity::class.java))
            }
        }
    }
}