package org.smartregister.path.reporting.annual.coverage.job

import android.content.Context
import androidx.work.*
import org.json.JSONArray
import org.json.JSONObject
import org.smartregister.path.R
import org.smartregister.path.application.ZeirApplication
import org.smartregister.path.reporting.annual.coverage.domain.AnnualVaccineReport
import org.smartregister.path.reporting.annual.coverage.repository.AnnualReportRepository
import org.smartregister.path.reporting.common.ANNUAL_VACCINE_REPORT
import org.smartregister.path.reporting.common.VACCINE_COVERAGES
import org.smartregister.path.util.AppJsonFormUtils
import org.smartregister.path.util.AppUtils
import org.smartregister.repository.AllSharedPreferences
import org.smartregister.util.JsonFormUtils
import java.util.*
import java.util.concurrent.TimeUnit

class SyncAnnualReportWorker(val context: Context, workerParams: WorkerParameters) :
        Worker(context, workerParams) {

    private val annualReportRepository = AnnualReportRepository.getInstance()
    private val appInstance: ZeirApplication = ZeirApplication.getInstance()
    private val allSharedPreferences: AllSharedPreferences = AppUtils.getAllSharedPreferences()

    override fun doWork(): Result {
        val annualVaccineReports = mutableListOf<AnnualVaccineReport>()
        val reportYears = annualReportRepository.getReportYears()
        reportYears.forEach { reportYear ->
            val currentAnnualReports = annualReportRepository.getVaccineCoverage(reportYear.toInt())
                    .filter { it.coverage != context.getString(R.string.error_no_target) }
                    .map {
                        AnnualVaccineReport(
                                vaccine = it.name,
                                year = it.year.toInt(),
                                target = it.target,
                                coverage = it.coverage.replace("%", "").toInt()
                        )
                    }
            annualVaccineReports.addAll(currentAnnualReports)
        }
        if (annualVaccineReports.isNotEmpty()) createAnnualCoverageReportEvent(annualVaccineReports)

        return Result.success()
    }

    private fun createAnnualCoverageReportEvent(vaccineCoverages: List<AnnualVaccineReport>) {

        val baseEvent = AppJsonFormUtils.createEvent(JSONArray(), JSONObject().put(JsonFormUtils.ENCOUNTER_LOCATION, ""),
                AppJsonFormUtils.formTag(allSharedPreferences), "", ANNUAL_VACCINE_REPORT, ANNUAL_VACCINE_REPORT)

        baseEvent.run {
            addDetails(ANNUAL_VACCINE_REPORT, JSONObject().apply {
                put(VACCINE_COVERAGES, AppJsonFormUtils.gson.toJson(vaccineCoverages))
            }.toString())
            AppJsonFormUtils.tagEventMetadata(this)
            formSubmissionId = UUID.randomUUID().toString()
            appInstance.ecSyncHelper.addEvent(this.baseEntityId, JSONObject(AppJsonFormUtils.gson.toJson(this)))
            appInstance.clientProcessor.processClient(appInstance.ecSyncHelper.getEvents(listOf(this.formSubmissionId)))
            val lastSyncDate = Date(allSharedPreferences.fetchLastUpdatedAtDate(0))
            allSharedPreferences.saveLastUpdatedAtDate(lastSyncDate.time)
        }
    }

    companion object {
        @JvmStatic
        private val tag = SyncAnnualReportWorker::class.java.simpleName

        @JvmStatic
        fun scheduleMonthly() {
            // 30 days = 43200 minutes = 1 month
            val periodicWork = PeriodicWorkRequest.Builder(SyncAnnualReportWorker::class.java, 43200, TimeUnit.MINUTES)
                    .setInitialDelay(15, TimeUnit.MINUTES)
                    .addTag(tag)
                    .build()
            WorkManager.getInstance(ZeirApplication.getInstance().applicationContext)
                    .enqueueUniquePeriodicWork(tag, ExistingPeriodicWorkPolicy.REPLACE, periodicWork)
        }
    }
}