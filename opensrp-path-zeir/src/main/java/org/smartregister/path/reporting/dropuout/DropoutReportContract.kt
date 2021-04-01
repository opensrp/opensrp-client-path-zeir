package org.smartregister.path.reporting.dropuout

import org.smartregister.path.reporting.DropoutReportGroup

/**
 * Contract for reporting
 */
interface DropoutReportContract {

    interface Presenter {
        /**
         * Start report for the provided [dropoutReportGroup] which can be a dropout report of
         * cumulative or cohort vaccine. The default being
         *
         */
        fun startReport(dropoutReportGroup: DropoutReportGroup)

        val dropoutReportRegisterView: View
    }

    interface View {
        /**
         * Initialize dropout reporting [Presenter]
         */
        fun initializePresenter()
    }
}