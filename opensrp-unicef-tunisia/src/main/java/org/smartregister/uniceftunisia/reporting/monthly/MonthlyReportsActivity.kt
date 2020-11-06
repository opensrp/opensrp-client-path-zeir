package org.smartregister.uniceftunisia.reporting.monthly

import android.os.Bundle
import androidx.activity.viewModels
import kotlinx.android.synthetic.main.activity_monthly_reports.*
import org.smartregister.Context
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.reporting.ReportGroup
import org.smartregister.uniceftunisia.reporting.ReportGroupingModel
import org.smartregister.uniceftunisia.reporting.common.ReportingUtils
import org.smartregister.uniceftunisia.util.AppConstants
import org.smartregister.view.activity.MultiLanguageActivity

class MonthlyReportsActivity : MultiLanguageActivity() {

    object Constants {
        const val SELECT_TAB = "select_tab"
    }

    private val monthlyReportsViewModel by viewModels<MonthlyReportsViewModel>
    { ReportingUtils.createFor(MonthlyReportsViewModel(MonthlyReportsRepository.getInstance())) }
    private lateinit var reportsPagerAdapter: MonthlyReportsPagerAdapter
    lateinit var reportGrouping: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_reports)
        reportGrouping = intent.getStringExtra(AppConstants.IntentKey.REPORT_GROUPING)
                ?: ReportGroup.MONTHLY_REPORTS.name
        reportsPagerAdapter = MonthlyReportsPagerAdapter(this, supportFragmentManager)

        monthlyReportsViewModel.apply {
            draftedMonths.observe(this@MonthlyReportsActivity, {
                reportFragmentTabLayout.getTabAt(0)?.text = getString(R.string.monthly_draft_reports, it.size)
            })
        }

        //Setup UI
        nameInitialsTextView.apply {
            setOnClickListener { onBackPressed() }
            text = getLoggedInUserInitials()
        }
        containerViewPager.apply {
            adapter = reportsPagerAdapter
            currentItem = intent.getIntExtra(Constants.SELECT_TAB, 0)
        }


        reportFragmentTabLayout.apply {
            setupWithViewPager(containerViewPager)
            tabRippleColor = null
        }
        titleTextView.apply {
            text = ReportGroupingModel(this@MonthlyReportsActivity).reportGroupings.first().displayName
        }
    }

    private fun getLoggedInUserInitials(): String {
        val allSharedPreferences = Context.getInstance().allSharedPreferences()
        return allSharedPreferences.getANMPreferredName(allSharedPreferences.fetchRegisteredANM())
                .split(" ").take(2).map { it.first() }.joinToString("")
    }

    override fun onResume() {
        super.onResume()
        monthlyReportsViewModel.apply {
            fetchDraftedMonths()
            fetchUnDraftedMonths()
            fetchAllSentReportMonths()
        }
    }
}