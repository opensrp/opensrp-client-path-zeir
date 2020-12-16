package org.smartregister.pathzeir.reporting.register

import org.smartregister.pathzeir.reporting.ReportGroup

/**
 * Contract for reporting
 */
interface ReportRegisterContract {

    interface Presenter {
        /**
         * Start report for the provided [reportGroup] which can be either monthly or annual report. The default being
         *
         */
        fun startReport(reportGroup: ReportGroup)

        val reportRegisterView: View
    }

    interface View {
        /**
         * Initialize reporting [Presenter]
         */
        fun initializePresenter()
    }
}