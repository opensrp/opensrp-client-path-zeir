package org.smartregister.uniceftunisia.reporting.annual

import org.smartregister.repository.BaseRepository
import org.smartregister.uniceftunisia.reporting.ReportsDao

class AnnualReportRepository : BaseRepository() {
    object VaccineByTarget {
        val UNDER_ONE = setOf("bcg", "hep_b_0", "penta_1", "penta_2", "penta_3",
                "ipv_1", "ipv_2", "opv_1", "pcv_1", "pcv_2", "pcv_3", "mr_1")
        val YEAR_ONE_AND_TWO = setOf("mr_2", "dtp_4", "opv_2")
    }

    fun getVaccineCoverage(year: Int) = ReportsDao.getTargetVaccineCounts(year)

    companion object {
        @Volatile
        private var instance: AnnualReportRepository? = null

        @JvmStatic
        fun getInstance(): AnnualReportRepository = instance ?: synchronized(this) {
            AnnualReportRepository().also { instance = it }
        }
    }
}