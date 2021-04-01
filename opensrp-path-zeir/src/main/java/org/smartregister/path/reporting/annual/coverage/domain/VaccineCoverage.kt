package org.smartregister.path.reporting.annual.coverage.domain

import org.smartregister.path.R
import org.smartregister.path.reporting.common.ReportingUtils.dateFormatter
import java.util.*

data class VaccineCoverage(
        val vaccine: String,
        val vaccinated: String,
        val coverage: String,
        val year: String = dateFormatter("yyyy").format(Date()),
        var coverageColorResource: Int = R.color.black_text_color,
        var name: String = "",
        var target: Int = 0
)