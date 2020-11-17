package org.smartregister.uniceftunisia.reporting.annual

import android.content.Context
import org.smartregister.immunization.util.VaccinatorUtils
import org.smartregister.repository.BaseRepository
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication
import org.smartregister.uniceftunisia.reporting.ReportsDao
import org.smartregister.uniceftunisia.reporting.annual.coverage.domain.CoverageTargetType
import org.smartregister.uniceftunisia.reporting.annual.coverage.domain.VaccineCount
import org.smartregister.uniceftunisia.reporting.annual.coverage.domain.VaccineCoverage
import org.smartregister.uniceftunisia.reporting.common.findTarget
import org.smartregister.uniceftunisia.reporting.common.getResourceId
import org.smartregister.uniceftunisia.reporting.common.toWholeNumber
import java.util.*

class AnnualReportRepository : BaseRepository() {

    val context: Context = UnicefTunisiaApplication.getInstance().applicationContext

    object VaccineByTarget {
        val underOne = setOf("bcg", "hep_b_0", "penta_1", "penta_2", "penta_3",
                "ipv_1", "ipv_2", "opv_1", "pcv_1", "pcv_2", "pcv_3", "mr_1")
        val yearOneAndTwo = setOf("mr_2", "dtp_4", "opv_2")
    }

    fun getVaccineCoverage(year: Int): List<VaccineCoverage> =
            computeVaccineCoverages(CoverageTargetType.UNDER_ONE_TARGET, year)
                    .plus(computeVaccineCoverages(CoverageTargetType.ONE_TWO_YEAR_TARGET, year))

    private fun computeVaccineCoverages(coverageTargetType: CoverageTargetType, year: Int): MutableList<VaccineCoverage> {
        val vaccineCountsMap: Map<String, VaccineCount> = ReportsDao.getTargetVaccineCounts(year).associateBy { it.name }
        val vaccineCoverages = mutableListOf<VaccineCoverage>()

        val vaccineSet = when (coverageTargetType) {
            CoverageTargetType.UNDER_ONE_TARGET -> VaccineByTarget.underOne
            CoverageTargetType.ONE_TWO_YEAR_TARGET -> VaccineByTarget.yearOneAndTwo
        }

        val target = when (coverageTargetType) {
            CoverageTargetType.UNDER_ONE_TARGET -> ReportsDao.getCoverageTarget(year).findTarget(CoverageTargetType.UNDER_ONE_TARGET)
            CoverageTargetType.ONE_TWO_YEAR_TARGET -> ReportsDao.getCoverageTarget(year).findTarget(CoverageTargetType.ONE_TWO_YEAR_TARGET)
        }

        vaccineSet.forEach {
            val vaccineCoverage: VaccineCoverage
            val vaccine = VaccinatorUtils.getTranslatedVaccineName(context, it)
            val translatedVaccineName =
                    if (vaccine == it) context.getString(it.getResourceId(context)) else vaccine
            val errorNoTarget = context.getString(R.string.error_no_target)

            if (vaccineCountsMap.containsKey(it)) {
                val vaccineCount = vaccineCountsMap.getValue(it)

                val underOneCoverage = if (target.isEmpty()) 0 else ((vaccineCount.count / target.toDouble()) * 100).toWholeNumber()
                        .toString().plus("%")
                vaccineCoverage = VaccineCoverage(
                        vaccine = translatedVaccineName,
                        vaccinated = vaccineCount.count.toString(),
                        coverage = "${if (target.isEmpty()) errorNoTarget else underOneCoverage}",
                        year = year.toString()
                )
            } else {
                vaccineCoverage = VaccineCoverage(
                        vaccine = translatedVaccineName,
                        vaccinated = 0.toString(),
                        coverage = if (target.isEmpty()) errorNoTarget else 0.toString().plus("%"),
                        year = year.toString()
                )
            }

            if (year.toString() != ReportsDao.dateFormatter("yyyy").format(Date()))
                vaccineCoverage.coverageColorResource = R.color.primary
            if (vaccineCoverage.coverage == context.getString(R.string.error_no_target))
                vaccineCoverage.coverageColorResource = R.color.cso_error_red

            vaccineCoverages.add(vaccineCoverage)
        }
        return vaccineCoverages
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