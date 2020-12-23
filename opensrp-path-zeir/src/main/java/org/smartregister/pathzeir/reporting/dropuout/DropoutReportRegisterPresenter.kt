package org.smartregister.pathzeir.reporting.dropuout

import android.app.Activity
import android.content.Intent
import org.smartregister.pathzeir.reporting.DropoutReportGroup
import org.smartregister.pathzeir.reporting.ReportGroup
import org.smartregister.pathzeir.reporting.annual.AnnualReportActivity
import org.smartregister.pathzeir.reporting.monthly.MonthlyReportsActivity
import java.lang.ref.WeakReference

class DropoutReportRegisterPresenter(override val dropoutReportRegisterView: DropoutReportContract.View) : DropoutReportContract.Presenter {

    private val reportingActivity: WeakReference<Activity> = WeakReference<Activity>(dropoutReportRegisterView as Activity)

    override fun startReport(dropooutReportGroup: DropoutReportGroup) {
        reportingActivity.get()?.let {
            when (dropooutReportGroup) {
                DropoutReportGroup.BCG_MEASLES_CUMULATIVE -> TODO()
                DropoutReportGroup.BCG_MEASLES_COHORT -> TODO()
                DropoutReportGroup.PENTA_CUMULATIVE -> TODO()
                DropoutReportGroup.PENTA_COHORT -> TODO()
                DropoutReportGroup.MEASLES_CUMULATIVE -> TODO()
            }
        }
    }
}