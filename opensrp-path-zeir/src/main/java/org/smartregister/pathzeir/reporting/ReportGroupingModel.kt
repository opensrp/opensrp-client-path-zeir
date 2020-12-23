package org.smartregister.pathzeir.reporting

import android.content.Context
import org.smartregister.pathzeir.R

enum class ReportGroup {
    MONTHLY_REPORTS,
    ANNUAL_COVERAGE_REPORTS
}

enum class DropoutReportGroup {
    BCG_MEASLES_CUMULATIVE,
    BCG_MEASLES_COHORT,
    PENTA_CUMULATIVE,
    PENTA_COHORT,
    MEASLES_CUMULATIVE
}

class ReportGroupingModel(private val context: Context) {
    val reportGroupings: List<ReportGrouping>
        get() = listOf(
                ReportGrouping(context .getString(R.string.monthly_immunization_growth_monitoring_reports), ReportGroup.MONTHLY_REPORTS),
                ReportGrouping(context.getString(R.string.coverage_reports), ReportGroup.ANNUAL_COVERAGE_REPORTS)
        )

    data class ReportGrouping(val displayName: String, val reportGroup: ReportGroup)
}

class DropoutReportGroupingModel(private val context: Context) {
    val reportGroupings: List<DropoutReportGrouping>
        get() = listOf(
                DropoutReportGrouping(
                        displayNameCumulative = context.getString(R.string.bcg_measles_cumulative),
                        displayNameCohort = context.getString(R.string.bcg_measles_cohort),
                        reportGroupCumulative = DropoutReportGroup.BCG_MEASLES_CUMULATIVE,
                        reportGroupCohort = DropoutReportGroup.BCG_MEASLES_COHORT,
                ),
                DropoutReportGrouping(
                        displayNameCumulative = context.getString(R.string.penta_cumulative),
                        reportGroupCumulative = DropoutReportGroup.PENTA_CUMULATIVE,
                        displayNameCohort = context.getString(R.string.penta_cohort),
                        reportGroupCohort = DropoutReportGroup.PENTA_COHORT,
                ),
                DropoutReportGrouping(
                        displayNameCumulative = context.getString(R.string.measles_cumulative),
                        reportGroupCumulative = DropoutReportGroup.MEASLES_CUMULATIVE,
                        displayNameCohort = null,
                        reportGroupCohort = null,
                ),
        )

    data class DropoutReportGrouping(
            val displayNameCumulative: String,
            var displayNameCohort : String?,
            val reportGroupCumulative: DropoutReportGroup,
            var reportGroupCohort: DropoutReportGroup?,)
}