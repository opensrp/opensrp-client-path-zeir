package org.smartregister.uniceftunisia.reporting.monthly.sent

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_monthly_sent_reports.*
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.reporting.*
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsViewModel
import org.smartregister.uniceftunisia.reporting.monthly.domain.MonthlyTally
import org.smartregister.uniceftunisia.reporting.monthly.indicator.ReportIndicatorsActivity
import java.io.Serializable

class SentReportsFragment : Fragment(), ExpandableListView.OnChildClickListener {

    private val sentReportsExpandableListAdapter = SentReportsMonthsListAdapter()

    private val monthlyReportsViewModel by activityViewModels<MonthlyReportsViewModel>
    { ReportingUtils.createFor(MonthlyReportsViewModel(MonthlyReportsRepository.getInstance())) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_monthly_sent_reports, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        monthlyReportsViewModel.run {
            sentReportMonths.observe(viewLifecycleOwner, {
                sentReportsExpandableListAdapter.apply {
                    if (it.isNotEmpty()) {
                        sentReportYearHeaders = it.keys.toList()
                        sentReports = it
                        sentReportsExpandableListView.expandGroup(0, false)
                    }
                }
            })
            sentReportTallies.observe(viewLifecycleOwner, {
                val (yearMonth, monthlyTallies) = it
                startActivity(Intent(activity, ReportIndicatorsActivity::class.java).apply {
                    putExtras(Bundle().apply {
                        putExtra(MONTHLY_TALLIES, monthlyTallies.associateBy { monthlyTally -> monthlyTally.indicator } as Serializable)
                        putExtra(YEAR_MONTH, yearMonth)
                        putExtra(SHOW_DATA, true)
                    })
                })
            })
        }

        sentReportsExpandableListView.apply {
            setAdapter(sentReportsExpandableListAdapter)
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            setOnChildClickListener(this@SentReportsFragment)
        }
    }

    override fun onChildClick(parent: ExpandableListView, view: View, groupPosition: Int, childPosition: Int, id: Long): Boolean {
        if (view.tag is MonthlyTally) {
            val monthlyTally = view.tag as MonthlyTally
            monthlyReportsViewModel.fetchSentReportTalliesByMonth(ReportsDao.dateFormatter().format(monthlyTally.month))
           return true
        }
        return false
    }
}