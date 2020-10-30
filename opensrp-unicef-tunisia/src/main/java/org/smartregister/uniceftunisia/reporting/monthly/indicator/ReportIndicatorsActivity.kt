package org.smartregister.uniceftunisia.reporting.monthly.indicator

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.vijay.jsonwizard.activities.MultiLanguageActivity
import kotlinx.android.synthetic.main.activity_report_indicators.*
import kotlinx.android.synthetic.main.fragment_report_indicators_form.*
import kotlinx.coroutines.launch
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.domain.MonthlyTally
import org.smartregister.uniceftunisia.reporting.*
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository

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
            lifecycleScope.launch {
                if (reportIndicatorsViewModel.saveMonthlyDraft())
                    finish()
                else
                    Snackbar.make(reportIndicatorsScrollView, R.string.error_saving_draft_reports, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}