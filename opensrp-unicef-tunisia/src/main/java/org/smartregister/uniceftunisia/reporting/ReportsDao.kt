package org.smartregister.uniceftunisia.reporting

import android.database.Cursor
import org.smartregister.dao.AbstractDao
import org.smartregister.dao.AbstractDao.DataMap
import org.smartregister.uniceftunisia.reporting.annual.coverage.domain.CoverageTarget
import org.smartregister.uniceftunisia.reporting.annual.coverage.domain.CoverageTargetType
import org.smartregister.uniceftunisia.reporting.annual.coverage.domain.VaccineCount
import org.smartregister.uniceftunisia.reporting.monthly.domain.MonthlyTally
import java.text.SimpleDateFormat
import java.util.*
import org.smartregister.uniceftunisia.reporting.annual.coverage.repository.VaccineCoverageTargetRepository.ColumnNames as CoverageTableColumns
import org.smartregister.uniceftunisia.reporting.indicatorposition.IndicatorPositionRepository.ColumnNames as IndicatorPositionTableColumns
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository.ColumnNames as MonthlyRepositoryTableColumns

object ReportsDao : AbstractDao() {

    fun dateFormatter(pattern: String = "yyyy-MM") = SimpleDateFormat(pattern, Locale.ENGLISH)

    /**
     * This method return a list of distinct months from the daily indicators
     */
    fun getDistinctReportMonths(): List<String> {
        val sql = """
            SELECT CASE m
                       WHEN '01' THEN 'January'
                       WHEN '02' THEN 'February'
                       WHEN '03' THEN 'March'
                       WHEN '04' THEN 'April'
                       WHEN '05' THEN 'May'
                       WHEN '06' THEN 'June'
                       WHEN '07' THEN 'July'
                       WHEN '08' THEN 'August'
                       WHEN '09' THEN 'September'
                       WHEN '10' THEN 'October'
                       WHEN '11' THEN 'November'
                       WHEN '12' THEN 'December'
                       END || ' ' || y AS dates
            FROM (
                     SELECT DISTINCT strftime('%m', day) m,
                                     strftime('%Y', day) y
                     FROM indicator_daily_tally
                 )
        """.trimIndent()

        val dataMap = DataMap { cursor: Cursor? -> getCursorValue(cursor, "dates") }

        return readData(sql, dataMap).toList().filterNotNull()
    }

    /**
     * This method returns a list of [MonthlyTally] drafted for the [yearMonth]. The [yearMonth] must
     * be in the format YYYY-MM.
     */
    fun getReportsByMonth(yearMonth: String, drafted: Boolean = true): List<MonthlyTally> {
        val sql = """
            SELECT _id,
                   indicator_code,
                   provider_id,
                   value,
                   month,
                   entered_manually,
                   date_sent,
                   indicator_grouping,
                   created_at,
                   updated_at
            FROM monthly_tallies
            WHERE month = '$yearMonth'
              AND date_sent IS ${if (drafted) "" else "NOT"} NULL
        """.trimIndent()

        return readData(sql, extractMonthlyTally()).toList().filterNotNull()
    }

    private fun extractMonthlyTally(): DataMap<MonthlyTally?> = DataMap { cursor: Cursor? ->
        if (cursor == null) null
        else MonthlyTally(
                indicator = getCursorValue(cursor, MonthlyRepositoryTableColumns.INDICATOR_CODE)!!,
                id = getCursorLongValue(cursor, MonthlyRepositoryTableColumns.ID)!!,
                value = getCursorValue(cursor, MonthlyRepositoryTableColumns.VALUE)!!,
                dateSent = if (getCursorValue(cursor, MonthlyRepositoryTableColumns.DATE_SENT) == null)
                    null else Date(getCursorLongValue(cursor, MonthlyRepositoryTableColumns.DATE_SENT)!!),
                month = dateFormatter().parse(getCursorValue(cursor, MonthlyRepositoryTableColumns.MONTH)!!)!!,
                providerId = getCursorValue(cursor, MonthlyRepositoryTableColumns.PROVIDER_ID)!!,
                updatedAt = Date(getCursorLongValue(cursor, MonthlyRepositoryTableColumns.UPDATED_AT)!!),
                grouping = getCursorValue(cursor, MonthlyRepositoryTableColumns.INDICATOR_GROUPING)!!,
                enteredManually = getCursorIntValue(cursor, MonthlyRepositoryTableColumns.ENTERED_MANUALLY) == 1,
                createdAt = getCursorValueAsDate(cursor, MonthlyRepositoryTableColumns.CREATED_AT)!!
        )
    }

    /**
     * Get the months that have been drafted
     */
    fun getDraftedMonths(): List<Pair<String, Date>> {
        val sql = """
            SELECT month, created_at
            FROM monthly_tallies
            WHERE date_sent IS NULL
            GROUP BY month;

        """.trimIndent()
        val dataMap = DataMap { cursor: Cursor? ->
            if (cursor != null && cursor.count > 0)
                Pair(
                        getCursorValue(cursor, MonthlyRepositoryTableColumns.MONTH)!!,
                        getCursorValueAsDate(cursor, MonthlyRepositoryTableColumns.CREATED_AT)!!
                )
            else null
        }
        return readData(sql, dataMap).toList().filterNotNull()
    }

    /**
     * Return all the months that have reports that have been sent.
     */
    fun getSentReportMonths(): List<Pair<String, Date>> {
        val sql = """
            SELECT month, created_at
            FROM monthly_tallies
            WHERE date_sent IS NOT NULL
            GROUP BY month;

        """.trimIndent()
        val dataMap = DataMap { cursor: Cursor? ->
            if (cursor != null && cursor.count > 0)
                Pair(
                        getCursorValue(cursor, MonthlyRepositoryTableColumns.MONTH)!!,
                        getCursorValueAsDate(cursor, MonthlyRepositoryTableColumns.CREATED_AT)!!
                )
            else null
        }
        return readData(sql, dataMap).toList().filterNotNull()
    }

    /**
     * Return all the sent monthly report tallies
     */
    fun getAllSentReportMonths(): List<MonthlyTally> {
        val sql = """
            SELECT _id,
                   indicator_code,
                   provider_id,
                   value,
                   month,
                   date_sent,
                   indicator_grouping,
                   created_at,
                   updated_at
            FROM monthly_tallies
            WHERE date_sent IS NOT NULL
            GROUP BY month
            ORDER BY month DESC;
        """.trimIndent()

        return readData(sql, extractMonthlyTally()).toList().filterNotNull()
    }

    fun getIndicatorPosition(indicator: String): Double {
        val sql = """
            SELECT position
            FROM indicator_position
            WHERE indicator = '$indicator'
        """.trimIndent()
        val result = readData(sql) { cursor: Cursor? ->
            if (cursor != null) getCursorValue(cursor, IndicatorPositionTableColumns.POSITION)!!.toDouble()
            else -1.0
        }
        return if (result.isEmpty()) -1.0 else result.first()
    }

    fun getCoverageTarget(year: Int): List<CoverageTarget> {
        val sql = """
            SELECT * 
            FROM annual_coverage_target
            WHERE year = '$year'
        """.trimIndent()
        val result = readData(sql) { cursor: Cursor? ->
            if (cursor != null && cursor.count > 0) {
                CoverageTarget(
                        targetType = CoverageTargetType.valueOf(getCursorValue(cursor, CoverageTableColumns.TARGET_TYPE)!!),
                        year = getCursorIntValue(cursor, CoverageTableColumns.YEAR)!!,
                        target = getCursorIntValue(cursor, CoverageTableColumns.TARGET) ?: 0,
                )
            } else null

        }
        return result.toList().filterNotNull()
    }

    /**
     * Returns computed vaccine coverage for the [year]
     */
    fun getTargetVaccineCounts(year: Int): List<VaccineCount> {
        val sql = """
            SELECT vaccines.name,
                   count(*) as vaccine_count,
                   strftime('%Y', date(vaccines.date / 1000, 'unixepoch', 'localtime')) as year
            FROM vaccines
                     INNER JOIN ec_client ON vaccines.base_entity_id = ec_client.base_entity_id
            WHERE strftime('%Y', date(vaccines.date / 1000, 'unixepoch', 'localtime')) = '$year'
            group by vaccines.name;
        """.trimIndent()
        val result = readData(sql) { cursor: Cursor? ->
            if (cursor != null && cursor.count > 0) {
                VaccineCount(
                        name = getCursorValue(cursor, "name")!!,
                        count = getCursorIntValue(cursor, "vaccine_count") ?: 0,
                )
            } else null

        }
        return result.toList().filterNotNull()
    }
}