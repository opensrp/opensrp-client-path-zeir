package org.smartregister.pathzeir.reporting.monthly.indicator

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.vijay.jsonwizard.activities.MultiLanguageActivity
import kotlinx.android.synthetic.main.activity_report_indicators.*
import kotlinx.coroutines.launch
import org.smartregister.pathzeir.R
import org.smartregister.pathzeir.reporting.common.*
import org.smartregister.pathzeir.reporting.monthly.MonthlyReportsActivity
import org.smartregister.pathzeir.reporting.monthly.domain.MonthlyTally

class ReportIndicatorsActivity : MultiLanguageActivity() {

    val reportIndicatorsViewModel by viewModels<ReportIndicatorsViewModel>
    { ReportingUtils.createFor(ReportIndicatorsViewModel()) }

    lateinit var navController: NavController

    private var translatedYearMonth: String? = null

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
                saveFormButton.visibility = View.GONE
                verticalDivider.visibility = View.GONE
            }
        }

        yearMonthTextView.text = if (intent.getBooleanExtra(SHOW_DATA, false))
            getString(R.string.monthly_sent_reports_with_year, translatedYearMonth) else
            getString(R.string.month_year_draft, translatedYearMonth)

        backButton.setOnClickListener { navigateToMonthlyReports(1) }

        saveFormButton.setOnClickListener { submitMonthlyDraft() }
    }

    private fun submitMonthlyDraft() {
        lifecycleScope.launch {
            when (reportIndicatorsViewModel.saveMonthlyDraft()) {
                true -> {
                    reportIndicatorsRootLayout.showSnackBar(R.string.monthly_draft_saved)
                    navigateToMonthlyReports()
                }
                else -> reportIndicatorsRootLayout.showSnackBar(R.string.error_saving_draft_reports)
            }
        }
    }

    private fun navigateToMonthlyReports(selectTab: Int = 0) {
        startActivity(Intent(this@ReportIndicatorsActivity, MonthlyReportsActivity::class.java).apply {
            if (intent.getBooleanExtra(SHOW_DATA, false))
                putExtra(MonthlyReportsActivity.Constants.SELECT_TAB, selectTab)
        })
        finish()
    }
}