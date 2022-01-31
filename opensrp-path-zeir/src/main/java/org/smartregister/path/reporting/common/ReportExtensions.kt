package org.smartregister.path.reporting.common

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.smartregister.AllConstants
import org.smartregister.child.util.ChildJsonFormUtils
import org.smartregister.child.util.Constants
import org.smartregister.domain.Event
import org.smartregister.domain.Obs
import org.smartregister.path.R
import org.smartregister.path.application.ZeirApplication
import org.smartregister.path.job.ZeirHIA2IntentServiceJob
import org.smartregister.path.model.Hia2Indicator
import org.smartregister.path.reporting.ReportsDao
import org.smartregister.path.reporting.annual.coverage.domain.AnnualVaccineReport
import org.smartregister.path.reporting.annual.coverage.domain.CoverageTarget
import org.smartregister.path.reporting.annual.coverage.domain.CoverageTargetType
import org.smartregister.path.reporting.annual.coverage.repository.AnnualReportRepository
import org.smartregister.path.reporting.annual.coverage.repository.VaccineCoverageTargetRepository
import org.smartregister.path.reporting.monthly.MonthlyReportsRepository
import org.smartregister.path.reporting.monthly.domain.MonthlyTally
import org.smartregister.path.reporting.monthly.domain.Report
import org.smartregister.path.reporting.monthly.domain.ReportHia2Indicator
import org.smartregister.path.reporting.monthly.domain.Tally
import org.smartregister.path.util.AppConstants
import org.smartregister.path.util.AppJsonFormUtils
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import org.smartregister.path.reporting.annual.coverage.repository.VaccineCoverageTargetRepository.ColumnNames as VaccineCoverageColumn

/**
 * String constants
 */
const val DAILY_TALLIES = "daily_tallies"
const val MONTHLY_TALLIES = "monthly_tallies"
const val MONTHLY_REPORT = "monthly_report"
const val ANNUAL_VACCINE_REPORT = "annual_vaccine_report"
const val VACCINE_COVERAGES = "vaccine_coverages"
const val YEAR_MONTH = "year_month"
const val DAY = "day"
const val SHOW_DATA = "show_data"
const val VACCINE_COVERAGE_TARGET = "vaccine_coverage_target"

/**
 * Utility method for creating ViewModel Factory
 */
@Suppress("UNCHECKED_CAST")
object ReportingUtils {

    @JvmStatic
    fun createReportAndSaveReport(hia2Indicators: List<ReportHia2Indicator?>, month: Date,
                                  reportType: String, grouping: String) {
        try {
            val providerId: String = ZeirApplication.getInstance().context().allSharedPreferences().fetchRegisteredANM()
            val locationId: String = ZeirApplication.getInstance().context().allSharedPreferences().getPreference(Constants.CURRENT_LOCATION_ID)
            val report = Report()
            report.formSubmissionId = UUID.randomUUID().toString()
            report.hia2Indicators = hia2Indicators
            report.locationId = locationId
            report.providerId = providerId
            report.dateCreated = DateTime.now()
            report.grouping = grouping
            // Get the second last day of the month
            val calendar = Calendar.getInstance()
            calendar.time = month
            calendar[Calendar.DAY_OF_MONTH] = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 2
            report.reportDate = DateTime(calendar.time)
            report.reportType = reportType
            val reportJson = JSONObject(ChildJsonFormUtils.gson.toJson(report))
            ZeirApplication.getInstance().hia2ReportRepository().addReport(reportJson)
        } catch (e: JSONException) {
            Timber.e(e)
        }
    }

    @JvmStatic
    fun <T : ViewModel> createFor(viewModel: T): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(viewModel::class.java)) {
                    return viewModel as T
                }
                throw IllegalArgumentException("unexpected viewModel class $modelClass")
            }
        }
    }

    @JvmStatic
    fun processMonthlyReportEvent(event: Event) {
        event.details[MONTHLY_REPORT]?.let { monthlyReportString ->
            try {
                val monthlyReportJson = JSONObject(monthlyReportString)
                val typeToken = object : TypeToken<Map<String, MonthlyTally>>() {}.type
                val yearMonth = monthlyReportJson.getString(YEAR_MONTH)
                val monthlyTalliesJsonArray = JSONArray(monthlyReportJson.getString(MONTHLY_TALLIES))
                (0 until monthlyTalliesJsonArray.length())
                        .map { monthlyTalliesJsonArray.getJSONObject(it) }
                        .map { JSONObject().put(it.getString(AllConstants.INDICATOR), it) }
                        .map { AppJsonFormUtils.gson.fromJson<Map<String, MonthlyTally>>(it.toString(), typeToken) }
                        .forEach { MonthlyReportsRepository.getInstance().saveMonthlyDraft(it, yearMonth, true) }
            } catch (e: JSONException) {
                Timber.e(e)
            }
        }
    }

    @JvmStatic
    fun processAnnualVaccineReportEvent(event: Event) {
        event.details[ANNUAL_VACCINE_REPORT]?.let { vaccineCoverage ->
            try {
                val typeToken = object : TypeToken<List<AnnualVaccineReport>>() {}.type
                val vaccineReportsJsonArray = JSONArray(JSONObject(vaccineCoverage).getString(VACCINE_COVERAGES))
                val annualVaccineReports = AppJsonFormUtils.gson.fromJson<List<AnnualVaccineReport>>(vaccineReportsJsonArray.toString(), typeToken)
                AnnualReportRepository.getInstance().saveAnnualVaccineReport(annualVaccineReports)
            } catch (e: JSONException) {
                Timber.e(e)
            }
        }
    }

    @JvmStatic
    fun processCoverageTarget(event: Event) {
        val eventObsMap: Map<String, Obs> = event.obs.associateBy { it.formSubmissionField }
        val containsRequiredObs = eventObsMap.keys.containsAll(
                setOf(
                        VaccineCoverageColumn.TARGET,
                        VaccineCoverageColumn.TARGET_TYPE,
                        VaccineCoverageColumn.YEAR
                ))
        if (eventObsMap.size == 3 && containsRequiredObs) {
            with(eventObsMap) {
                val targetType = CoverageTargetType.valueOf(getValue(VaccineCoverageColumn.TARGET_TYPE).value.toString())
                val target = getValue(VaccineCoverageColumn.TARGET).value.toString().toInt()
                val year = getValue(VaccineCoverageColumn.YEAR).value.toString().toInt()
                val coverageTarget = CoverageTarget(
                        targetType = targetType,
                        target = target,
                        year = year
                )
                VaccineCoverageTargetRepository.getInstance().saveCoverageTarget(coverageTarget)
            }
        }
    }

    fun dateFormatter(pattern: String = "yyyy-MM") = SimpleDateFormat(pattern, Locale.ENGLISH)

    @JvmStatic
    fun saveReportAndInitiateSync(month: String, monthlyTallies: MutableCollection<MonthlyTally>) {
        val hia2IndicatorHashMap: HashMap<String, Hia2Indicator> = ZeirApplication.getInstance().hIA2IndicatorsRepository().findAllByGrouping(AppConstants.ReportConstants.CHN_GROUPING);
        val reportHia2Indicators: MutableList<ReportHia2Indicator> = ArrayList<ReportHia2Indicator>()
        for (curTally in monthlyTallies) {
            val reportHia2Indicator = ReportHia2Indicator(curTally.indicator, curTally.indicator, curTally.grouping, curTally.value)
            val hia2Indicator: Hia2Indicator? = hia2IndicatorHashMap[reportHia2Indicator.indicatorCode]
            if (hia2Indicator != null && StringUtils.isNotBlank(hia2Indicator.dhisId) && StringUtils.isNotBlank(hia2Indicator.categoryOptionCombo)) {
                reportHia2Indicator.dhisId = hia2Indicator.dhisId
                reportHia2Indicator.categoryOptionCombo = hia2Indicator.categoryOptionCombo
            } else {
                reportHia2Indicator.dhisId = AppConstants.ReportConstants.UNKNOWN
            }
            reportHia2Indicators.add(reportHia2Indicator)
        }

        createReportAndSaveReport(reportHia2Indicators, dateFormatter().parse(month),
                AppConstants.ReportConstants.REPORT_TYPE, AppConstants.ReportConstants.CHN_GROUPING)
        ZeirHIA2IntentServiceJob.scheduleJobImmediately(ZeirHIA2IntentServiceJob.TAG);
    }

}

/**
 * This extension method applied on [String] class allows you to convert a date string with named months
 * to numbered month and vice versa
 *      Example October 2020 will be converted to 2020-10
 *      and 20119-01 will be converted to January 2019
 */
fun String.convertToNamedMonth(hasHyphen: Boolean = false): String {
    val yearAndMonth = this.split(if (hasHyphen) "-" else " ")
    val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    return if (hasHyphen)
        "${months[yearAndMonth.last().toInt() - 1]} ${(yearAndMonth.first())}"
    else
        "${yearAndMonth.last()}-${(months.indexOf(yearAndMonth.first()) + 1).toString().padStart(2, '0')}"
}

/**
 * Return a formatted string identifier
 */
fun String.getResourceId(context: Context): Int = try {
    context.resources.getIdentifier(this.replace(" ", "_").replace("/", ""), "string", context.packageName)
} catch (exception: Resources.NotFoundException) {
    Timber.e("String Resource for $this is not found. Specify it on strings.xml file.$exception")
    0
}


/**
 * Translate string if string used to get translated value for year month
 */
fun String.translateString(context: Context): String {
    val textSplit = this.split(" ")
    if (textSplit.size == 2) {
        textSplit.run {
            val resourceId = first().getResourceId(context)
            return if (resourceId != 0)
                "${context.resources.getString(resourceId)} ${last()}"
            else this@translateString
        }
    }
    return this
}

/**
 * Activity context extensions to display toast messages
 */
fun Context.showToast(resourceId: Int, duration: Int = Toast.LENGTH_LONG) =
        Toast.makeText(this, getString(resourceId), duration).show()

/**
 * Show progress dialog
 */

fun Context.showProgressDialog(show: Boolean = true, title: String =
        this.getString(R.string.please_wait_title), message: String = this.getString(R.string.loading)):
        AlertDialog {

    val parentLayout = LayoutInflater.from(this).inflate(R.layout.progress_dialog_layout, null, false).apply {
        findViewById<TextView>(R.id.titleTextView).text = title
        findViewById<TextView>(R.id.messageTextView).text = message
    }

    val builder: AlertDialog.Builder = AlertDialog.Builder(this).apply {
        setCancelable(false)
        setView(parentLayout)
    }

    val dialog = builder.create()
    if (show) dialog.show() else dialog.dismiss()
    dialog.window?.run {
        attributes = WindowManager.LayoutParams().apply {
            copyFrom(attributes)
            width = LinearLayout.LayoutParams.WRAP_CONTENT
            height = LinearLayout.LayoutParams.WRAP_CONTENT
        }
    }
    return dialog
}

/**
 * Activity context extensions to display SnackBar messages
 */
fun View.showSnackBar(resourceId: Int, duration: Int = Snackbar.LENGTH_LONG) =
        Snackbar.make(this, this.context.getString(resourceId), duration).show()

/**
 * This method creates pair of indicator against its position then sorts them. The indicator positions
 * are defined in "configs/reporting/indicator-positions.json" file
 */
suspend fun <T : Tally> List<T>.sortIndicators(): List<T> {
    return withContext(Dispatchers.IO) {
        this@sortIndicators.map { Pair(ReportsDao.getIndicatorPosition(it.indicator), it) }
                .filter { talliesPair -> talliesPair.first != -1.0 }
                .sortedBy { talliesPair -> talliesPair.first }
                .map { talliesPair -> talliesPair.second }
    }
}


/**
 * Find saved [CoverageTarget] for the given [targetType]
 */
fun List<CoverageTarget>.findTarget(targetType: CoverageTargetType): String {
    val coverageTarget: CoverageTarget? = this.find { target -> target.targetType == targetType }
    return (if (coverageTarget != null && coverageTarget.target > 0)
        coverageTarget.target else null)?.toString() ?: ""
}

fun Double.toWholeNumber(): BigDecimal = this.toBigDecimal().setScale(0, RoundingMode.UP)