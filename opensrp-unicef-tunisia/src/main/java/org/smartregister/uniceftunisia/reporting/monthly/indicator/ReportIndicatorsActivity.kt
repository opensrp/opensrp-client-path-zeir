package org.smartregister.uniceftunisia.reporting.monthly.indicator

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.vijay.jsonwizard.activities.MultiLanguageActivity
import kotlinx.android.synthetic.main.activity_report_indicators.*
import kotlinx.coroutines.launch
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.reporting.*
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository
import org.smartregister.uniceftunisia.reporting.monthly.domain.MonthlyTally

class ReportIndicatorsActivity : MultiLanguageActivity() {

    private val reportIndicatorsViewModel by viewModels<ReportIndicatorsViewModel>
    { ReportingUtils.createFor(ReportIndicatorsViewModel(MonthlyReportsRepository.getInstance())) }

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

        backButton.setOnClickListener { finish() }

        saveFormButton.setOnClickListener { submitMonthlyDraft() }
    }

    private fun submitMonthlyDraft() {
        lifecycleScope.launch {
            when (reportIndicatorsViewModel.saveMonthlyDraft()) {
                true -> {
                    reportIndicatorsRootLayout.showSnackBar(R.string.monthly_draft_saved)
                    finish()
                }
                else -> reportIndicatorsRootLayout.showSnackBar(R.string.error_saving_draft_reports)
            }
        }
    }
}