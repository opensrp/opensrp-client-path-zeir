package org.smartregister.uniceftunisia.reporting

import android.database.Cursor
import org.smartregister.dao.AbstractDao
import org.smartregister.dao.AbstractDao.DataMap
import org.smartregister.uniceftunisia.domain.MonthlyTally
import org.smartregister.uniceftunisia.domain.Tally
import java.text.SimpleDateFormat
import java.util.*

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
            ORDER BY y DESC
        """.trimIndent()

        val dataMap = DataMap { cursor: Cursor? -> getCursorValue(cursor, "dates") }

        return readData(sql, dataMap).toList().filterNotNull()
    }

    /**
     * This method returns a list of [MonthlyTally] drafted for the [yearMonth]. The [yearMonth] must
     * be in the format YYYY-MM. [grouping] is optional and defaults to 'child'
     */
    fun getReportsByMonth(yearMonth: String, grouping: String = "child", drafted: Boolean = true): List<MonthlyTally> {
        val sql = """
            SELECT _id,
                   indicator_code,
                   provider_id,
                   value,
                   month,
                   edited,
                   date_sent,
                   indicator_grouping,
                   created_at,
                   updated_at
            FROM monthly_tallies
            WHERE month = '$yearMonth'
              AND date_sent IS ${if (drafted) "" else "NOT"} NULL
              AND indicator_grouping = '$grouping';
        """.trimIndent()

        return readData(sql, extractMonthlyTally()).toList().filterNotNull()
    }

    private fun extractMonthlyTally(): DataMap<MonthlyTally?> = DataMap { cursor: Cursor? ->
        cursor?.run {
            return@DataMap if (cursor.count > 0) {
                MonthlyTally().apply {
                    id = getCursorLongValue(cursor, "_id")!!
                    indicator = getCursorValue(cursor, "indicator_code")!!
                    providerId = getCursorValue(cursor, "provider_id")!!
                    value = getCursorValue(cursor, "value")!!
                    month = dateFormatter().parse(getCursorValue(cursor, "month")!!)
                    isEdited = getCursorIntValue(cursor, "edited")!! != 0
                    dateSent = if (getCursorValue(cursor, "date_sent") == null) null else Date(getCursorLongValue(cursor, "date_sent")!!)
                    grouping = getCursorValue(cursor, "indicator_grouping")!!
                    createdAt = getCursorValueAsDate(cursor, "created_at")!!
                    updatedAt = Date(getCursorLongValue(cursor, "updated_at")!!)
                    indicatorTally = Tally().also {
                        id = id
                        value = value
                        indicator = indicator
                    }
                }
            } else null
        }
    }

    fun getDraftedMonths(grouping: String = "child"): List<Pair<String, Date>> {
        val sql = """
            SELECT month, created_at
            FROM monthly_tallies
            WHERE date_sent IS NULL
              AND edited = 1
              AND indicator_grouping = '$grouping'
            GROUP BY month;

        """.trimIndent()
        val dataMap = DataMap { cursor: Cursor? ->
            return@DataMap if (cursor != null && cursor.count > 0)
                Pair(
                        getCursorValue(cursor, "month")!!,
                        getCursorValueAsDate(cursor, "created_at")!!
                )
            else null
        }
        return readData(sql, dataMap).toList().filterNotNull()
    }

    /**
     * Return all the months that have reports that have been sent for the given [grouping]
     */
    fun getSentReportMonths(grouping: String = "child"): List<Pair<String, Date>> {
        val sql = """
            SELECT month, created_at
            FROM monthly_tallies
            WHERE date_sent IS NOT NULL
              AND edited = 1
              AND indicator_grouping = '$grouping'
            GROUP BY month;

        """.trimIndent()
        val dataMap = DataMap { cursor: Cursor? ->
            return@DataMap if (cursor != null && cursor.count > 0)
                Pair(
                        getCursorValue(cursor, "month")!!,
                        getCursorValueAsDate(cursor, "created_at")!!
                )
            else null
        }
        return readData(sql, dataMap).toList().filterNotNull()
    }

    /**
     * Return all the sent monthly report tallies for the provided [grouping]
     */
    fun getAllSentReportMonths(grouping: String = "child"): List<MonthlyTally> {
        val sql = """
            SELECT _id,
                   indicator_code,
                   provider_id,
                   value,
                   month,
                   edited,
                   date_sent,
                   indicator_grouping,
                   created_at,
                   updated_at
            FROM monthly_tallies
            WHERE date_sent IS NOT NULL
              AND indicator_grouping = '$grouping'
            GROUP BY month
            ORDER BY month DESC;
        """.trimIndent()

        return readData(sql, extractMonthlyTally()).toList().filterNotNull()
    }
}