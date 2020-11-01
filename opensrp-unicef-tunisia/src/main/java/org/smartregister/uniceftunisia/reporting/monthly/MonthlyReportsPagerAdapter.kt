package org.smartregister.uniceftunisia.reporting.monthly

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.reporting.monthly.draft.DraftedReportsFragment
import org.smartregister.uniceftunisia.reporting.monthly.sent.SentReportsFragment

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class MonthlyReportsPagerAdapter(private val monthlyReportsActivity: MonthlyReportsActivity, fragmentManager: FragmentManager) :
        FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> DraftedReportsFragment()
        else -> SentReportsFragment()
    }

    override fun getCount() = 2

    override fun getPageTitle(position: Int): CharSequence? = when (position) {
        0 -> monthlyReportsActivity.getString(R.string.monthly_draft_reports, 0)
        else -> monthlyReportsActivity.getString(R.string.monthly_sent_reports)
    }

}