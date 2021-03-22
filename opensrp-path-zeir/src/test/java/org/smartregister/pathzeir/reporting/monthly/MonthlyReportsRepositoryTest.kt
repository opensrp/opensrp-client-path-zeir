package org.smartregister.pathzeir.reporting.monthly

import io.mockk.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.smartregister.pathzeir.reporting.ReportingTestDataProvider.getSentMonthlyTallies
import org.smartregister.pathzeir.reporting.ReportsDao
import org.smartregister.pathzeir.reporting.common.ReportingUtils
import org.smartregister.reporting.domain.IndicatorTally
import org.smartregister.reporting.repository.DailyIndicatorCountRepository
import java.util.*

class MonthlyReportsRepositoryTest {

    private val monthlyReportsRepository = spyk(objToCopy = MonthlyReportsRepository.getInstance(), recordPrivateCalls = true)
    private val dailyIndicatorCountRepository = mockk<DailyIndicatorCountRepository>(relaxUnitFun = true, relaxed = true)

    @Before
    fun `Before every test`() {
        mockkObject(ReportsDao)
        monthlyReportsRepository.updateSupportedReportGroups(
                setOf("report_group_header_vaccination_activity", "report_group_header_vaccine_utilization"))
        every { monthlyReportsRepository.getProviderId() } returns "demo"
        every { monthlyReportsRepository.getDailyIndicatorCountRepository() } returns dailyIndicatorCountRepository
    }

    @After
    fun `After every test`() {
        unmockkAll()
        unmockkObject(ReportsDao)
    }

    @Test
    fun `Should fetch UnDrafted months sorted in reversed order`() {
        every { ReportsDao.getDistinctReportMonths() } returns listOf("January 2020", "October 2019", "September 2019", "December 2019", "November 2019")
        every { ReportsDao.getDraftedMonths() } returns listOf(Pair("2020-01", Date(1607077960529L)))
        every { ReportsDao.getSentReportMonths() } returns listOf(Pair("2019-09", Date(1607077960529L)))
        val unDraftedMonths = monthlyReportsRepository.fetchUnDraftedMonths()
        assertEquals(3, unDraftedMonths.size)
        assertEquals(unDraftedMonths[0], "December 2019")
        assertEquals(unDraftedMonths[1], "November 2019")
        assertEquals(unDraftedMonths[2], "October 2019")
    }

    @Test
    fun `Should fetch Drafted months sorted in reversed order`() {
        every { ReportsDao.getDraftedMonths() } returns
                listOf(Pair("2019-12", Date(1607077960529L)), Pair("2018-12", Date(1607077940529L)), Pair("2020-01", Date(1607037960529L)))
        val draftedMonths = monthlyReportsRepository.fetchDraftedMonths()
        assertEquals(3, draftedMonths.size)
    }

    @Test
    fun `Should fetch Drafted report tallies for the provided month`() {
        val yearMonth = "2020-01"
        every { ReportsDao.getReportsByMonth(yearMonth) } returns listOf()
        val januaryTallies = mapOf("report_group_header_vaccination_activity" to listOf(
                IndicatorTally(1, 2, "indicator_code", Date(1607077960529L))
                        .apply { grouping = "report_group_header_vaccination_activity" },
                IndicatorTally(2, 1, "indicator_code", Date(1607077960529L))
                        .apply { grouping = "report_group_header_vaccination_activity" }
        ))
        every {
            dailyIndicatorCountRepository.findTalliesInMonth(ReportingUtils.dateFormatter().parse(yearMonth)!!, "report_group_header_vaccination_activity")
        } returns januaryTallies

        val reportTallies = monthlyReportsRepository.fetchDraftedReportTalliesByMonth(yearMonth)
        assertEquals(1, reportTallies.size)
        val monthlyTally = reportTallies[0]
        assertEquals("indicator_code", monthlyTally.indicator)
        assertEquals("report_group_header_vaccination_activity", monthlyTally.grouping)
        assertEquals("3", monthlyTally.value)
        assertEquals("demo", monthlyTally.providerId)
        assertNull(monthlyTally.dateSent)
    }

    @Test
    fun `Should fetch all months with sent reports and group them by year`() {
        every { ReportsDao.getAllSentReportMonths() } returns getSentMonthlyTallies()
        val sentReportMonths = monthlyReportsRepository.fetchSentReportMonths()
        assertEquals(2, sentReportMonths.size)
        assertTrue(sentReportMonths.containsKey("2020"))
        val twentyTwentyTallies = sentReportMonths.getValue("2020")
        assertEquals(2, twentyTwentyTallies.size)
        assertEquals("2020-01", ReportingUtils.dateFormatter().format(twentyTwentyTallies[0].month))
        assertEquals("2020-02", ReportingUtils.dateFormatter().format(twentyTwentyTallies[1].month))
        assertTrue(sentReportMonths.containsKey("2019"))
        val twentyNineteenTallies = sentReportMonths.getValue("2019")
        assertEquals(1, twentyNineteenTallies.size)
        assertEquals("2019-12", ReportingUtils.dateFormatter().format(twentyNineteenTallies[0].month))
    }


    @Test
    fun `Should fetch all months for provided single month`() {
        val yearMonth = "2020-01"
        every { ReportsDao.getReportsByMonth(yearMonth, false) } returns
                getSentMonthlyTallies().filter { it.indicator == "indicator_code" }
        val sentReportForMonth = monthlyReportsRepository.fetchSentReportTalliesByMonth(yearMonth)
        assertEquals(1, sentReportForMonth.size)
        val monthlyTally = sentReportForMonth.first()
        assertEquals("indicator_code", monthlyTally.indicator)
        assertEquals("report_group_header_vaccination_activity", monthlyTally.grouping)
        assertEquals("3", monthlyTally.value)
        assertEquals("demo", monthlyTally.providerId)
        assertEquals(Date(1607077960529L), monthlyTally.dateSent)
    }
}