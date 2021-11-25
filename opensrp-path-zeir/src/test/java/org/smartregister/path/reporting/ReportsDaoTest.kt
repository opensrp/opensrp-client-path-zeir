package org.smartregister.path.reporting

import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import net.sqlcipher.MatrixCursor
import net.sqlcipher.database.SQLiteDatabase
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.smartregister.path.reporting.ReportsDao.SqlQueries
import org.smartregister.path.reporting.annual.coverage.domain.CoverageTargetType
import org.smartregister.repository.Repository
import java.util.*

class ReportsDaoTest {

    private val repository: Repository = mockk(relaxed = true)
    private val database: SQLiteDatabase = mockk(relaxed = true)
    private val monthlyTalliesColumns = arrayOf("_id", "indicator_code", "provider_id", "value", "month", "date_sent", "indicator_grouping", "created_at", "updated_at")
    private val reportMonthsColumns = arrayOf("month", "created_at")
    private val daily = arrayOf("_id", "indicator_code","indicator_is_value_set", "indicator_value", "day", "indicator_grouping", "created_at", "updated_at")


    @Before
    fun `Before every test`() {
        ReportsDao.updateRepository(repository)
        every { repository.readableDatabase } returns database
    }

    private fun mockMatrixCursor(columnArray: Array<String>, vararg rowData: Array<Any?>): MatrixCursor {
        val matrixCursor = MatrixCursor(columnArray)
        rowData.forEach {
            matrixCursor.addRow(it)
        }
        return matrixCursor
    }

    @After
    fun `After every test`() {
        unmockkAll()
    }

    @Test
    fun `Should return distinct report months`() {
        val matrixCursor = mockMatrixCursor(
                columnArray = arrayOf("dates"),
                rowData = arrayOf(arrayOf("January 2020"), arrayOf("February 2019"))
        )
        every { database.rawQuery(SqlQueries.DISTINCT_REPORT_MONTHS_SQL, any()) } returns matrixCursor
        val distinctReportMonths = ReportsDao.getDistinctReportMonths()
        assertEquals(2, distinctReportMonths.size)
    }

    @Test
    fun `Should return a list of monthly tallies for the given month`() {
        val yearMonth = "2020-01"
        val matrixCursor = mockMatrixCursor(
                columnArray = monthlyTalliesColumns,
                rowData = arrayOf(arrayOf("1", "indicator_code", "demo", "3", yearMonth, null, "report_group_header_vaccination_activity", 1607077960529L, 1607077960529L))
        )
        every { database.rawQuery(SqlQueries.reportsByMonthSql(yearMonth, true), any()) } returns matrixCursor
        val monthlyTallies = ReportsDao.getReportsByMonth(yearMonth)

        assertEquals(1, monthlyTallies.size)
        assertNull(monthlyTallies.first().dateSent)
        assertEquals("indicator_code", monthlyTallies.first().indicator)
    }

    @Test
    fun `Should return a list of daily tallies for the given month`() {
        val day = "2020-01-01"
        val matrixCursor = mockMatrixCursor(
            columnArray = daily,
            rowData = arrayOf(arrayOf("1", "indicator_code", "1", "3", day, "report_group_header_vaccination_activity", 1607077960529L, 1607077960529L))
        )
        every { database.rawQuery(SqlQueries.reportsByDaySql(day), any()) } returns matrixCursor
        val dailyTallies = ReportsDao.getReportsByDay(day)

        assertEquals(1, dailyTallies.size)
        assertNotNull(dailyTallies.first().day)
        assertEquals("indicator_code", dailyTallies.first().indicator)
    }

    @Test
    fun `Should report months that have been drafted`() {
        val matrixCursor = mockMatrixCursor(
                columnArray = reportMonthsColumns,
                rowData = arrayOf(arrayOf("January 2020", "1607077960439"))
        )
        every { database.rawQuery(SqlQueries.DRAFTED_MONTHS_SQL, any()) } returns matrixCursor
        val draftedMonths = ReportsDao.getDraftedMonths()

        assertEquals(1, draftedMonths.size)
        assertEquals("January 2020", draftedMonths.first().first)
        assertEquals(Date(1607077960439L), draftedMonths.first().second)
    }

    @Test
    fun `Should return report months that have been sent`() {
        val matrixCursor = mockMatrixCursor(
                columnArray = reportMonthsColumns,
                rowData = arrayOf(arrayOf("July 2020", "1607077860439"))
        )
        every { database.rawQuery(SqlQueries.SENT_REPORT_MONTHS_SQL, any()) } returns matrixCursor
        val sentMonths = ReportsDao.getSentReportMonths()

        assertEquals(1, sentMonths.size)
        assertEquals("July 2020", sentMonths.first().first)
        assertEquals(Date(1607077860439L), sentMonths.first().second)
    }

    @Test
    fun `Should return list of monthly tallies for all sent reports`() {
        val matrixCursor = mockMatrixCursor(
                columnArray = monthlyTalliesColumns,
                rowData = arrayOf(
                        arrayOf("1", "indicator_code", "demo", "3", "2020-09", 1607077960603L, "report_group_header_vaccination_activity", 1607077960529L, 1607677960529L),
                        arrayOf("2", "indicator_code2", "demo", "9", "2020-07", 1607077960603L, "report_group_header_vaccination_activity", 1609077960529L, 1607377960529L)
                )
        )
        every { database.rawQuery(SqlQueries.ALL_SENT_REPORT_MONTHS_SQL, any()) } returns matrixCursor
        val allSentMonthlyTallies = ReportsDao.getAllSentReportMonths()

        assertEquals(2, allSentMonthlyTallies.size)

        assertEquals("indicator_code", allSentMonthlyTallies.first().indicator)
        assertEquals("3", allSentMonthlyTallies.first().value)

        assertEquals("indicator_code2", allSentMonthlyTallies.last().indicator)
        assertEquals("9", allSentMonthlyTallies.last().value)
    }

    @Test
    fun `Should return position for the indicator`() {
        val indicator = "indicator_code"
        val matrixCursor = mockMatrixCursor(
                columnArray = arrayOf("position"),
                rowData = arrayOf(arrayOf(2))
        )
        every { database.rawQuery(SqlQueries.indicatorPositionSql(indicator), any()) } returns matrixCursor
        val indicatorPosition = ReportsDao.getIndicatorPosition(indicator)
        assertEquals(2, indicatorPosition.toInt())
    }

    @Test
    fun `Should return coverage for the given year`() {
        val year = 2020
        val matrixCursor = mockMatrixCursor(
                columnArray = arrayOf("target_type", "year", "target"),
                rowData = arrayOf(
                        arrayOf("UNDER_ONE_TARGET", "2020", "190"),
                        arrayOf("ONE_TWO_YEAR_TARGET", "2020", "100")
                )
        )
        every { database.rawQuery(SqlQueries.coverageTargetSql(year), any()) } returns matrixCursor
        val coverageTargets = ReportsDao.getCoverageTarget(year)

        assertEquals(2, coverageTargets.size)

        assertEquals(CoverageTargetType.UNDER_ONE_TARGET, coverageTargets.first().targetType)
        assertEquals(190, coverageTargets.first().target)

        assertEquals(CoverageTargetType.ONE_TWO_YEAR_TARGET, coverageTargets.last().targetType)
        assertEquals(100, coverageTargets.last().target)
    }

    @Test
    fun `Should return correct vaccine counts for the given year`() {
        val bcg = "BCG"
        val ipv1 = "IPV 1"
        val matrixCursor = mockMatrixCursor(
                columnArray = arrayOf("name", "vaccine_count"),
                rowData = arrayOf(arrayOf(bcg, 30), arrayOf(ipv1, 20))
        )
        val year = 2020
        every { database.rawQuery(SqlQueries.targetVaccineCountsSql(year), any()) } returns matrixCursor
        val vaccineCounts = ReportsDao.getTargetVaccineCounts(year)

        assertEquals(2, vaccineCounts.size)

        assertEquals(bcg, vaccineCounts.first().name)
        assertEquals(30, vaccineCounts.first().count)

        assertEquals(ipv1, vaccineCounts.last().name)
        assertEquals(20, vaccineCounts.last().count)

    }
}