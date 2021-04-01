package org.smartregister.path.reporting.dropuout

import android.app.Activity
import android.content.Intent
import org.smartregister.path.reporting.DropoutReportGroup
import java.lang.ref.WeakReference

class DropoutReportRegisterPresenter(override val dropoutReportRegisterView: DropoutReportContract.View) : DropoutReportContract.Presenter {

    private val reportingActivity: WeakReference<Activity> = WeakReference<Activity>(dropoutReportRegisterView as Activity)

    override fun startReport(dropooutReportGroup: DropoutReportGroup) {
        reportingActivity.get()?.let {
            when (dropooutReportGroup) {
                DropoutReportGroup.BCG_MEASLES_CUMULATIVE -> it.startActivity(Intent(it, BcgMeaslesCumulativeDropoutReportActivity::class.java))
                DropoutReportGroup.BCG_MEASLES_COHORT -> it.startActivity(Intent(it, BcgMeaslesCohortDropoutReportActivity::class.java))
                DropoutReportGroup.PENTA_CUMULATIVE -> it.startActivity(Intent(it, PentaCumulativeDropoutReportActivity::class.java))
                DropoutReportGroup.PENTA_COHORT -> it.startActivity(Intent(it, PentaCohortDropoutReportActivity::class.java))
                DropoutReportGroup.MEASLES_CUMULATIVE -> it.startActivity(Intent(it, MeaslesCumulativeDropoutReportActivity::class.java))
            }
        }
    }
}