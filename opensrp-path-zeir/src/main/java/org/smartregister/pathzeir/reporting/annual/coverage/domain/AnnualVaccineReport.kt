package org.smartregister.pathzeir.reporting.annual.coverage.domain

import java.util.*

data class AnnualVaccineReport(
        val vaccine: String,
        val year: Int,
        val target: Int,
        val coverage: Int,
        val updatedAt: Long = Calendar.getInstance().timeInMillis
)