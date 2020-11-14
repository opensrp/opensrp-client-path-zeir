package org.smartregister.uniceftunisia.reporting.annual

import org.smartregister.repository.BaseRepository
import org.smartregister.uniceftunisia.reporting.annual.coverage.VaccineCoverage

class AnnualReportRepository : BaseRepository() {
    fun getVaccineCoverage(): List<VaccineCoverage>? {
        return listOf(
                VaccineCoverage("BCG", "100", "100%"),
                VaccineCoverage("OPV 0", "20", "100%"),
                VaccineCoverage("OPV 1", "98", "90%"),
                VaccineCoverage("OPV 3", "20", "24%"),
                VaccineCoverage("Penta 1", "34", "Error: No target")
        )
    }

    companion object {
        @Volatile
        private var instance: AnnualReportRepository? = null

        @JvmStatic
        fun getInstance(): AnnualReportRepository = instance ?: synchronized(this) {
            AnnualReportRepository().also { instance = it }
        }
    }
}