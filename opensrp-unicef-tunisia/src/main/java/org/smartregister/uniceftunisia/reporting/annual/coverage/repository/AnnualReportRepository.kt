package org.smartregister.uniceftunisia.reporting.annual.coverage.repository

import android.content.Context
import androidx.core.content.contentValuesOf
import androidx.sqlite.db.transaction
import net.sqlcipher.database.SQLiteDatabase
import org.smartregister.immunization.util.VaccinatorUtils
import org.smartregister.repository.BaseRepository
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication
import org.smartregister.uniceftunisia.reporting.ReportsDao
import org.smartregister.uniceftunisia.reporting.annual.coverage.domain.AnnualVaccineReport
import org.smartregister.uniceftunisia.reporting.annual.coverage.domain.CoverageTargetType
import org.smartregister.uniceftunisia.reporting.annual.coverage.domain.VaccineCount
import org.smartregister.uniceftunisia.reporting.annual.coverage.domain.VaccineCoverage
import org.smartregister.uniceftunisia.reporting.common.findTarget
import org.smartregister.uniceftunisia.reporting.common.getResourceId
import org.smartregister.uniceftunisia.reporting.common.toWholeNumber
import java.util.*

class AnnualReportRepository : BaseRepository() {

    val context: Context = UnicefTunisiaApplication.getInstance().applicationContext

    object VaccineTarget {
        val underOne = setOf("bcg", "hep_b_0", "penta_1", "penta_2", "penta_3",
                "ipv_1", "ipv_2", "opv_1", "pcv_1", "pcv_2", "pcv_3", "mr_1")
        val yearOneAndTwo = setOf("mr_2", "dtp_4", "opv_2")
    }

    private object Constants {
        const val TABLE_NAME = "annual_vaccine_coverage_report"
    }

    private object TableQueries {
        const val CREATE_TABLE_SQL = """
        CREATE TABLE annual_vaccine_coverage_report
        (
            _id             INTEGER
                constraint annual_vaccine_coverage_report_pk
                    primary key autoincrement,
            vaccine           TEXT NOT NULL,
            year              INTEGER NOT NULL,
            target            INTEGER NOT NULL,
            coverage          INTEGER NOT NULL,
            updated_at        DATETIME NOT NULL
        );
        """
        const val CREATE_UNIQUE_COVERAGE_INDEX =
                "CREATE UNIQUE INDEX annual_vaccine_coverage_report_unique_year_coverage ON annual_vaccine_coverage_report (vaccine, year);"
    }

    object ColumnNames {
        const val VACCINE = "vaccine"
        const val YEAR = "year"
        const val TARGET = "target"
        const val COVERAGE = "coverage"
        const val UPDATED_AT = "updated_at"
    }

    fun createTable(database: SQLiteDatabase) = database.run {
        execSQL(TableQueries.CREATE_TABLE_SQL)
        execSQL(TableQueries.CREATE_UNIQUE_COVERAGE_INDEX)
    }

    fun saveAnnualVaccineReport(annualVaccineReports: List<AnnualVaccineReport>): Boolean {
        val failedInsertionsList = arrayListOf<Long>()
        writableDatabase.transaction(exclusive = true) {
            annualVaccineReports.forEach {
                val contentValues = contentValuesOf(
                        Pair(ColumnNames.VACCINE, it.vaccine),
                        Pair(ColumnNames.YEAR, it.year),
                        Pair(ColumnNames.TARGET, it.target),
                        Pair(ColumnNames.COVERAGE, it.coverage),
                        Pair(ColumnNames.UPDATED_AT, it.updatedAt)
                )
                val id = writableDatabase.insertWithOnConflict(Constants.TABLE_NAME, null,
                        contentValues, android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE)
                if (id == (-1).toLong()) failedInsertionsList.add(id)
            }
        }
        return failedInsertionsList.isEmpty()
    }

    fun getVaccineCoverage(year: Int): List<VaccineCoverage> =
            computeVaccineCoverages(CoverageTargetType.UNDER_ONE_TARGET, year)
                    .plus(computeVaccineCoverages(CoverageTargetType.ONE_TWO_YEAR_TARGET, year))

    private fun computeVaccineCoverages(coverageTargetType: CoverageTargetType, year: Int): MutableList<VaccineCoverage> {
        val vaccineCountsMap: Map<String, VaccineCount> = ReportsDao.getTargetVaccineCounts(year).associateBy { it.name }

        val vaccineSet = when (coverageTargetType) {
            CoverageTargetType.UNDER_ONE_TARGET -> VaccineTarget.underOne
            CoverageTargetType.ONE_TWO_YEAR_TARGET -> VaccineTarget.yearOneAndTwo
        }

        val target = when (coverageTargetType) {
            CoverageTargetType.UNDER_ONE_TARGET -> ReportsDao.getCoverageTarget(year).findTarget(CoverageTargetType.UNDER_ONE_TARGET)
            CoverageTargetType.ONE_TWO_YEAR_TARGET -> ReportsDao.getCoverageTarget(year).findTarget(CoverageTargetType.ONE_TWO_YEAR_TARGET)
        }
        return getVaccineCoverages(vaccineSet, vaccineCountsMap, target, year)
    }

    private fun getVaccineCoverages(vaccineSet: Set<String>, vaccineCountsMap: Map<String, VaccineCount>,
                                    vaccineTarget: String, year: Int): MutableList<VaccineCoverage> {
        val vaccineCoverages = mutableListOf<VaccineCoverage>()
        vaccineSet.forEach {
            val vaccine = VaccinatorUtils.getTranslatedVaccineName(context, it)
            val translatedVaccineName =
                    if (vaccine == it) context.getString(it.getResourceId(context)) else vaccine
            val errorNoTarget = context.getString(R.string.error_no_target)
            val targetEmpty = vaccineTarget.isEmpty()
            var vaccinated = "0"
            var coverage = if (targetEmpty) errorNoTarget else 0.toString().plus("%")

            if (vaccineCountsMap.containsKey(it)) {
                val vaccineCount = vaccineCountsMap.getValue(it)
                vaccinated = vaccineCount.count.toString()
                coverage = if (targetEmpty) errorNoTarget
                else ((vaccineCount.count / vaccineTarget.toDouble()) * 100).toWholeNumber().toString().plus("%")
            }

            val vaccineCoverage = VaccineCoverage(
                    vaccine = translatedVaccineName.toUpperCase(Locale.getDefault()),
                    vaccinated = vaccinated,
                    coverage = coverage,
                    year = year.toString()
            ).apply {
                name = it
                target = if (targetEmpty) 0 else vaccineTarget.toInt()
                if (year.toString() != ReportsDao.dateFormatter("yyyy").format(Date()))
                    coverageColorResource = R.color.primary
                if (coverage == context.getString(R.string.error_no_target))
                    coverageColorResource = R.color.cso_error_red
            }

            vaccineCoverages.add(vaccineCoverage)
        }
        return vaccineCoverages
    }

    fun getReportYears() = ReportsDao.getDistinctReportMonths()
            .map { ReportsDao.dateFormatter("yyyy").format(ReportsDao.dateFormatter("MMMM yyyy").parse(it)!!) }
            .distinct()
            .asReversed()

    companion object {
        @Volatile
        private var instance: AnnualReportRepository? = null

        @JvmStatic
        fun getInstance(): AnnualReportRepository = instance ?: synchronized(this) {
            AnnualReportRepository().also { instance = it }
        }
    }
}