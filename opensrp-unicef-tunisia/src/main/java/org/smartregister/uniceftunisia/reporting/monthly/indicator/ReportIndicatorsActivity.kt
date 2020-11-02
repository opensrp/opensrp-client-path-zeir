package org.smartregister.uniceftunisia.reporting.monthly.indicator

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.os.bundleOf
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
import org.smartregister.uniceftunisia.reporting.*
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository
import org.smartregister.uniceftunisia.reporting.monthly.domain.MonthlyTally
import org.smartregister.uniceftunisia.reporting.monthly.draft.ConfirmSendDraftDialog
import org.smartregister.uniceftunisia.util.AppJsonFormUtils
import org.smartregister.uniceftunisia.util.AppUtils
import org.smartregister.util.JsonFormUtils.ENCOUNTER_LOCATION
import timber.log.Timber
import java.util.*

class ReportIndicatorsActivity : MultiLanguageActivity(), View.OnClickListener {

    private val reportIndicatorsViewModel by viewModels<ReportIndicatorsViewModel>
    { ReportingUtils.createFor(ReportIndicatorsViewModel(MonthlyReportsRepository.getInstance())) }

    lateinit var navController: NavController

    private var translatedYearMonth: String? = null

    private lateinit var confirmSendDraftDialog: ConfirmSendDraftDialog

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_indicators)
        setSupportActionBar(reportIndicatorsToolbar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.reportIndicatorsNavController) as NavHostFragment
        navController = navHostFragment.navController

        with(intent) {
            val serializableExtra = getSerializableExtra(MONTHLY_TALLIES)

            getStringExtra(YEAR_MONTH)?.let {
                reportIndicatorsViewModel.yearMonth.value = it
                translatedYearMonth = it.convertToNamedMonth(hasHyphen = true).translateString(this@ReportIndicatorsActivity)
            }

            if (serializableExtra is Map<*, *>)
                reportIndicatorsViewModel.monthlyTalliesMap.value = (serializableExtra as Map<String, MonthlyTally>).toMutableMap()

            //Navigate to ReportIndicatorsSummaryFragment when show data is true
            if (getBooleanExtra(SHOW_DATA, false)) {
                navController.navigate(R.id.reportIndicatorsSummaryFragment)
                confirmButton.visibility = View.GONE
                saveFormButton.visibility = View.GONE
                verticalDivider.visibility = View.GONE
            }
        }

        //Setup UI
        confirmSendDraftDialog = ConfirmSendDraftDialog().apply {
            onClickListener = this@ReportIndicatorsActivity
            arguments = bundleOf(Pair(ConfirmSendDraftDialog.Constants.MONTH, translatedYearMonth))
        }

        yearMonthTextView.text = if (intent.getBooleanExtra(SHOW_DATA, false))
            getString(R.string.monthly_sent_reports_with_year, translatedYearMonth) else
            getString(R.string.month_year_draft, translatedYearMonth)

        backButton.setOnClickListener { finish() }

        saveFormButton.setOnClickListener { submitMonthlyDraft(sync = false) }

        confirmButton.setOnClickListener(this)
    }

    private fun submitMonthlyDraft(sync: Boolean = false) {
        lifecycleScope.launch {
            if (sync) {
                when (createAndProcessMonthlyReportEvent()) {
                    true -> {
                        reportIndicatorsScrollView.showSnackBar(R.string.monthly_draft_submitted)
                        finish()
                    }
                    else -> reportIndicatorsScrollView.showSnackBar(R.string.error_sending_draft_reports)
                }
            } else {
                when (reportIndicatorsViewModel.saveMonthlyDraft()) {
                    true -> {
                        reportIndicatorsScrollView.showSnackBar(R.string.monthly_draft_saved)
                        finish()
                    }
                    else -> reportIndicatorsScrollView.showSnackBar(R.string.error_saving_draft_reports)
                }
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

    override fun onClick(view: View) {
        when (view.id) {
            R.id.sendDraftReportsButton -> submitMonthlyDraft(sync = true)
            R.id.confirmButton -> confirmSendDraftDialog.show(supportFragmentManager, ConfirmSendDraftDialog::class.simpleName)
        }
    }
}