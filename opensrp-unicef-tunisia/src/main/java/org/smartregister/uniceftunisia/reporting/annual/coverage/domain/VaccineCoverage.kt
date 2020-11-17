package org.smartregister.uniceftunisia.reporting.annual.coverage.domain

import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.reporting.ReportsDao
import java.util.*

data class VaccineCoverage(
        val vaccine: String,
        val vaccinated: String,
        val coverage: String,
        val year: String = ReportsDao.dateFormatter("yyyy").format(Date()),
        var coverageColorResource: Int = R.color.black_text_color
)