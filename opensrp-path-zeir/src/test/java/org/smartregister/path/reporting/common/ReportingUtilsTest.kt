package org.smartregister.path.reporting.common

import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.robolectric.util.ReflectionHelpers
import org.smartregister.path.BaseRobolectricTest
import org.smartregister.path.application.ZeirApplication
import org.smartregister.path.reporting.monthly.domain.ReportHia2Indicator
import org.smartregister.repository.Hia2ReportRepository
import java.util.*

class ReportingUtilsTest : BaseRobolectricTest() {

    @Test
    fun testCreateReportAndSaveReport() {
        val month = Date()
        val reportType = "Immunization Monthly Report"
        val reportHia2Indicators: ArrayList<ReportHia2Indicator> = ArrayList<ReportHia2Indicator>()
        reportHia2Indicators.add(ReportHia2Indicator())
        reportHia2Indicators.add(ReportHia2Indicator())

        val hia2ReportRepository: Hia2ReportRepository = Mockito.spy(ZeirApplication.getInstance().hia2ReportRepository())
        ReflectionHelpers.setField(ZeirApplication.getInstance(), "hia2ReportRepository", hia2ReportRepository)

        Mockito.doNothing().`when`(hia2ReportRepository).addReport(Mockito.any(JSONObject::class.java))

        ReportingUtils.createReportAndSaveReport(reportHia2Indicators, month, reportType, "child")
        val jsonObjectArgumentCaptor = ArgumentCaptor.forClass(JSONObject::class.java)
        Mockito.verify(hia2ReportRepository).addReport(jsonObjectArgumentCaptor.capture())

        val reportJson = jsonObjectArgumentCaptor.value
        Assert.assertEquals(reportType, reportJson.getString("reportType"))
        Assert.assertEquals(2, reportJson.getJSONArray("hia2Indicators").length().toLong())
    }
}