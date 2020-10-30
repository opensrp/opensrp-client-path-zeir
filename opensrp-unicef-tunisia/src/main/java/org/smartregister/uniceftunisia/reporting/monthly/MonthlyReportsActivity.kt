package org.smartregister.uniceftunisia.reporting.monthly

import android.os.Bundle
import androidx.activity.viewModels
import kotlinx.android.synthetic.main.activity_monthly_reports.*
import org.smartregister.Context
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.reporting.ReportGroup
import org.smartregister.uniceftunisia.reporting.ReportGroupingModel
import org.smartregister.uniceftunisia.reporting.ViewModelUtil
import org.smartregister.uniceftunisia.util.AppConstants
import org.smartregister.view.activity.MultiLanguageActivity

class MonthlyReportsActivity : MultiLanguageActivity() {
    private val monthlyReportsViewModel by viewModels<MonthlyReportsViewModel>
    { ViewModelUtil.createFor(MonthlyReportsViewModel(MonthlyReportsRepository())) }
    private var mPagerAdapterMonthly: MonthlyReportsPagerAdapter? = null
    lateinit var reportGrouping: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_reports)
        reportGrouping = intent.getStringExtra(AppConstants.IntentKey.REPORT_GROUPING)
                ?: ReportGroup.MONTHLY_REPORTS.name
        mPagerAdapterMonthly = MonthlyReportsPagerAdapter(this, supportFragmentManager)

        monthlyReportsViewModel.apply {
            draftedMonths.observe(this@MonthlyReportsActivity, {
                reportFragmentTabLayout.getTabAt(0)?.text =
                        getString(R.string.hia2_draft_monthly_with_count, it.size)
            })
        }

        //Setup UI
        nameInitialsTextView.apply {
            setOnClickListener { onBackPressed() }
            text = getLoggedInUserInitials()
        }
        containerViewPager.apply { adapter = mPagerAdapterMonthly }
        reportFragmentTabLayout.apply { setupWithViewPager(containerViewPager) }
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
        }
    }
}