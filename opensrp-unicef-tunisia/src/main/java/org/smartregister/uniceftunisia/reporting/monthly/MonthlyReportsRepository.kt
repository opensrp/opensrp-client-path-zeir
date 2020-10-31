package org.smartregister.uniceftunisia.reporting.monthly

import android.database.sqlite.SQLiteDatabase
import androidx.core.content.contentValuesOf
import androidx.sqlite.db.transaction
import org.smartregister.reporting.ReportingLibrary
import org.smartregister.reporting.domain.IndicatorTally
import org.smartregister.repository.BaseRepository
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication
import org.smartregister.uniceftunisia.domain.MonthlyTally
import org.smartregister.uniceftunisia.reporting.ReportsDao
import org.smartregister.uniceftunisia.reporting.ReportsDao.dateFormatter
import org.smartregister.uniceftunisia.reporting.convertToNamedMonth
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository.ColumnNames.CREATED_AT
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository.ColumnNames.DATE_SENT
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository.ColumnNames.EDITED
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository.ColumnNames.INDICATOR_CODE
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository.ColumnNames.INDICATOR_GROUPING
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository.ColumnNames.MONTH
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository.ColumnNames.PROVIDER_ID
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository.ColumnNames.UPDATED_AT
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository.ColumnNames.VALUE
import java.util.*

class MonthlyReportsRepository : BaseRepository() {

    private val username = UnicefTunisiaApplication.getInstance().context().allSharedPreferences().fetchRegisteredANM()

    private object Constants {
        const val TABLE_NAME = "monthly_tallies"
    }

    private object ColumnNames {
        const val PROVIDER_ID = "provider_id"
        const val INDICATOR_CODE = "indicator_code"
        const val VALUE = "value"
        const val MONTH = "month"
        const val EDITED = "edited"
        const val DATE_SENT = "date_sent"
        const val INDICATOR_GROUPING = "indicator_grouping"
        const val CREATED_AT = "created_at"
        const val UPDATED_AT = "updated_at"
    }

    fun fetchUnDraftedMonths() = ReportsDao.getDistinctReportMonths()
            .subtract(fetchDraftedMonths().map { it.first.convertToNamedMonth(true) })
            .subtract(ReportsDao.getSentReportMonths().map { it.first.convertToNamedMonth(true) })
            .toList()

    fun fetchDraftedMonths() = ReportsDao.getDraftedMonths()

    fun fetchMonthlyDraftedReportTallies(yearMonth: String, grouping: String = "child"): List<MonthlyTally> {
        val draftReports = ReportsDao.getMonthlyDraftedReportTallies(yearMonth = yearMonth)
        if (draftReports.isNotEmpty()) return draftReports

        val monthlyTallies = arrayListOf<MonthlyTally>()
        ReportingLibrary.getInstance().dailyIndicatorCountRepository()
                .findTalliesInMonth(dateFormatter().parse(yearMonth)!!, grouping).run {
                    values.forEach { currentList ->
                        computeDailyTallies(currentList)?.let { monthlyTallies.add(it) }
                    }
                }
        return monthlyTallies
    }

    private fun computeDailyTallies(dailyTallies: List<IndicatorTally>) =
            if (dailyTallies.isNotEmpty()) {
                MonthlyTally().apply {
                    indicator = dailyTallies[0].indicatorCode
                    grouping = dailyTallies[0].grouping
                    updatedAt = Calendar.getInstance().time
                    providerId = username
                    value = dailyTallies.map { it.floatCount.toInt() }.reduce { sum, element -> sum + element }.toString()
                }
            } else null

    fun saveMonthlyDraft(monthlyTallies: Map<String, MonthlyTally>?, yearMonth: String?, sync: Boolean): Boolean {
        if (!monthlyTallies.isNullOrEmpty() && !yearMonth.isNullOrBlank()) {
            monthlyTallies.values.forEach { tally ->
                writableDatabase.transaction(exclusive = true) {
                    val currentTime = Calendar.getInstance().timeInMillis
                    val contentValues = contentValuesOf(
                            Pair(INDICATOR_CODE, tally.indicator),
                            Pair(INDICATOR_GROUPING, tally.grouping),
                            Pair(VALUE, tally.value),
                            Pair(CREATED_AT, if (sync && tally.createdAt != null) tally.createdAt.time else currentTime),
                            Pair(UPDATED_AT, if (sync && tally.updatedAt != null) tally.updatedAt.time else currentTime),
                            Pair(PROVIDER_ID, tally.providerId),
                            Pair(EDITED, 1),
                            Pair(MONTH, yearMonth),
                            Pair(DATE_SENT, if (sync) currentTime else null)
                    )
                    writableDatabase.insertWithOnConflict(Constants.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
                }
            }
            return true
        }
        return false
    }
}