package org.smartregister.pathzeir.reporting

import android.content.Context
import org.smartregister.pathzeir.R

enum class ReportGroup {
    MONTHLY_REPORTS,
    ANNUAL_COVERAGE_REPORTS
}

class ReportGroupingModel(private val context: Context) {
    val reportGroupings: List<ReportGrouping>
        get() = listOf(
                ReportGrouping(context.getString(R.string.monthly_immunization_growth_monitoring_reports), ReportGroup.MONTHLY_REPORTS),
                ReportGrouping(context.getString(R.string.coverage_reports), ReportGroup.ANNUAL_COVERAGE_REPORTS)
        )

    data class ReportGrouping(val displayName: String, val reportGroup: ReportGroup)
}