package org.smartregister.uniceftunisia.reporting.monthly.indicator

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.vijay.jsonwizard.activities.MultiLanguageActivity
import kotlinx.android.synthetic.main.activity_report_indicators.*
import kotlinx.android.synthetic.main.fragment_report_indicators_form.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication
import org.smartregister.uniceftunisia.domain.MonthlyTally
import org.smartregister.uniceftunisia.reporting.*
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository
import org.smartregister.uniceftunisia.util.AppJsonFormUtils
import org.smartregister.uniceftunisia.util.AppUtils
import org.smartregister.util.JsonFormUtils.ENCOUNTER_LOCATION
import timber.log.Timber
import java.util.*

class ReportIndicatorsActivity : MultiLanguageActivity() {

    private val reportIndicatorsViewModel by viewModels<ReportIndicatorsViewModel>
    { ViewModelUtil.createFor(ReportIndicatorsViewModel(MonthlyReportsRepository())) }

    lateinit var navController: NavController

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_indicators)
        setSupportActionBar(findViewById(R.id.reportIndicatorsToolbar))
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.reportIndicatorsNavController) as NavHostFragment
        navController = navHostFragment.navController

        var yearMonth: String? = null

        with(intent) {
            val serializableExtra = getSerializableExtra(MONTHLY_TALLIES)
            if (serializableExtra is Map<*, *>)
                reportIndicatorsViewModel.monthlyTalliesMap.value = (serializableExtra as Map<String, MonthlyTally>).toMutableMap()

            //Navigate to ReportIndicatorsSummaryFragment when show data is true
            if (getBooleanExtra(SHOW_DATA, false)) {
                navController.navigate(R.id.reportIndicatorsSummaryFragment)
                confirmButton.visibility = View.GONE
                saveFormButton.visibility = View.GONE
                verticalDivider.visibility = View.GONE
            }
            getStringExtra(YEAR_MONTH)?.let {
                yearMonth = it
                reportIndicatorsViewModel.yearMonth.value = it
            }
        }

        intent.getStringExtra(YEAR_MONTH)

        //Setup UI
        yearMonthTextView.text = getString(R.string.month_year_draft, yearMonth?.convertToNamedMonth(hasHyphen = true)?.translateString(this))

        backButton.setOnClickListener { finish() }

        saveFormButton.setOnClickListener {
            submitMonthlyDraft(sync = false)
        }

        confirmButton.setOnClickListener {
            submitMonthlyDraft(sync = true)
        }
    }

    private fun submitMonthlyDraft(sync: Boolean = false) {
        lifecycleScope.launch {
            if (sync) {
                val syncedEvent = createAndProcessMonthlyReportEvent()
                if (syncedEvent) {
                    this@ReportIndicatorsActivity.showToast(R.string.monthly_draft_submitted)
                    finish()
                } else reportIndicatorsScrollView.showSnackBar(R.string.error_sending_draft_reports)
            } else {
                val saveMonthlyDraft = reportIndicatorsViewModel.saveMonthlyDraft()
                if (saveMonthlyDraft) {
                    this@ReportIndicatorsActivity.showToast(R.string.monthly_draft_saved)
                    finish()
                } else reportIndicatorsScrollView.showSnackBar(R.string.error_saving_draft_reports)
            }
        }
    }

    private suspend fun createAndProcessMonthlyReportEvent(): Boolean {
        val allSharedPreferences = AppUtils.getAllSharedPreferences()
        val appInstance = UnicefTunisiaApplication.getInstance()
        return try {
            withContext(lifecycleScope.coroutineContext + Dispatchers.IO) {

                val baseEvent = AppJsonFormUtils.createEvent(JSONArray(), JSONObject().put(ENCOUNTER_LOCATION, ""),
                        AppJsonFormUtils.formTag(allSharedPreferences), "", MONTHLY_REPORT, MONTHLY_REPORT)

                with(baseEvent) {
                    val monthlyTalliesMap = reportIndicatorsViewModel.monthlyTalliesMap.value
                    val yearMonth = reportIndicatorsViewModel.yearMonth.value

                    addDetails(MONTHLY_REPORT, JSONObject().apply {
                        put("year_month", yearMonth)
                        put("monthly_tallies", AppJsonFormUtils.gson.toJson(monthlyTalliesMap?.values))
                    }.toString())

                    formSubmissionId = UUID.randomUUID().toString()
                    AppJsonFormUtils.tagEventMetadata(this)
                    appInstance.ecSyncHelper.addEvent(this.baseEntityId, JSONObject(AppJsonFormUtils.gson.toJson(this)))
                    appInstance.clientProcessor.processClient(appInstance.ecSyncHelper.getEvents(listOf(this.formSubmissionId)))
                    val lastSyncDate = Date(allSharedPreferences.fetchLastUpdatedAtDate(0))
                    allSharedPreferences.saveLastUpdatedAtDate(lastSyncDate.time)
                }
                true
            }
        } catch (throwable: Throwable) {
            Timber.e(throwable)
            false
        }
    }
}