package org.smartregister.uniceftunisia.reporting.annual.coverage.job

import android.content.Context
import androidx.work.*
import org.json.JSONArray
import org.json.JSONObject
import org.smartregister.repository.AllSharedPreferences
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication
import org.smartregister.uniceftunisia.reporting.annual.coverage.domain.AnnualVaccineReport
import org.smartregister.uniceftunisia.reporting.annual.coverage.repository.AnnualReportRepository
import org.smartregister.uniceftunisia.reporting.common.ANNUAL_VACCINE_REPORT
import org.smartregister.uniceftunisia.reporting.common.VACCINE_COVERAGES
import org.smartregister.uniceftunisia.util.AppJsonFormUtils
import org.smartregister.uniceftunisia.util.AppUtils
import org.smartregister.util.JsonFormUtils
import java.util.*
import java.util.concurrent.TimeUnit

class SyncAnnualReportWorker(context: Context, workerParams: WorkerParameters) :
        Worker(context, workerParams) {

    private val annualReportRepository = AnnualReportRepository.getInstance()
    private val appInstance: UnicefTunisiaApplication = UnicefTunisiaApplication.getInstance()
    private val allSharedPreferences: AllSharedPreferences = AppUtils.getAllSharedPreferences()

    override fun doWork(): Result {
        val annualVaccineReports = mutableListOf<AnnualVaccineReport>()
        val reportYears = annualReportRepository.getReportYears()
        reportYears.forEach { reportYear ->
            val currentAnnualReports = annualReportRepository.getVaccineCoverage(reportYear.toInt()).map {
                AnnualVaccineReport(
                        vaccine = it.name,
                        year = it.year.toInt(),
                        target = it.target,
                        coverage = it.coverage.toInt()
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
            WorkManager.getInstance(UnicefTunisiaApplication.getInstance().applicationContext)
                    .enqueueUniquePeriodicWork(tag, ExistingPeriodicWorkPolicy.REPLACE, periodicWork)
        }
    }
}