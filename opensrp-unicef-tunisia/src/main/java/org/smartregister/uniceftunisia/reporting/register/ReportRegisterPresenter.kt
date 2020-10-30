package org.smartregister.uniceftunisia.reporting.register

import android.app.Activity
import android.content.Intent
import org.smartregister.uniceftunisia.reporting.ReportGroup
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsActivity
import org.smartregister.uniceftunisia.util.AppConstants
import timber.log.Timber
import java.lang.ref.WeakReference

class ReportRegisterPresenter(override val reportRegisterView: ReportRegisterContract.View) : ReportRegisterContract.Presenter {

    private val reportingActivity: WeakReference<Activity>? = WeakReference<Activity>(reportRegisterView as Activity)

    override fun startReport(reportGroup: ReportGroup) {
        reportingActivity?.get()?.let {
            when (reportGroup) {
                ReportGroup.MONTHLY_REPORTS -> {
                    val intent = Intent(it, MonthlyReportsActivity::class.java)
                    intent.putExtra(AppConstants.IntentKey.REPORT_GROUPING, reportGroup.name)
                    it.startActivity(intent)
                }
                ReportGroup.ANNUAL_COVERAGE_REPORTS -> {
                    Timber.i("Start Annual Report")
                }
            }
        }
    }
}