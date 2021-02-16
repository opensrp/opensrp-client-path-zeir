package org.smartregister.pathzeir.reporting

import org.smartregister.pathzeir.reporting.common.ReportingUtils
import org.smartregister.pathzeir.reporting.monthly.domain.MonthlyTally
import java.util.*

object ReportingTestDataProvider {
    fun getSentMonthlyTallies() = listOf(
            MonthlyTally(
                    indicator = "indicator_code",
                    value = "3",
                    providerId = "demo",
                    updatedAt = Date(1607077960529L),
                    createdAt = Date(1607077960529L),
                    dateSent = Date(1607077960529L),
                    grouping = "report_group_header_vaccination_activity",
                    month = ReportingUtils.dateFormatter().parse("2020-01")!!
            ),
            MonthlyTally(
                    indicator = "indicator_code_2",
                    value = "2",
                    providerId = "demo",
                    updatedAt = Date(1607077960529L),
                    createdAt = Date(1607077960529L),
                    grouping = "report_group_header_vaccination_activity",
                    month = ReportingUtils.dateFormatter().parse("2020-02")!!
            ),
            MonthlyTally(
                    indicator = "indicator_code_3",
                    value = "2",
                    providerId = "demo",
                    updatedAt = Date(1607077960529L),
                    createdAt = Date(1607077960529L),
                    grouping = "report_group_header_vaccination_activity",
                    month = ReportingUtils.dateFormatter().parse("2019-12")!!
            )
    )
}