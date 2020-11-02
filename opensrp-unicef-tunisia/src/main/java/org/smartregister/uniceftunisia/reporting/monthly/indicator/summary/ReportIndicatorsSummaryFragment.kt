package org.smartregister.uniceftunisia.reporting.monthly.indicator.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_report_indicators_summary.*
import kotlinx.android.synthetic.main.report_indicator_summary_header_item.*
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.reporting.ReportingUtils
import org.smartregister.uniceftunisia.reporting.ReportsDao
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository
import org.smartregister.uniceftunisia.reporting.monthly.domain.MonthlyTally
import org.smartregister.uniceftunisia.reporting.monthly.indicator.ReportIndicatorsViewModel

/**
 * A [Fragment] subclass used to display list of report indicators
 */
class ReportIndicatorsSummaryFragment : Fragment(), ExpandableListView.OnGroupClickListener {

    private val reportsIndicatorsListAdapter = ReportsIndicatorsListAdapter()

    private val reportIndicatorsViewModel by activityViewModels<ReportIndicatorsViewModel>()
    { ReportingUtils.createFor(ReportIndicatorsViewModel(MonthlyReportsRepository.getInstance())) }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_report_indicators_summary, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        indicatorsExpandableListView.apply {
            setAdapter(reportsIndicatorsListAdapter)
            setOnGroupClickListener(this@ReportIndicatorsSummaryFragment)
        }
        reportIndicatorsViewModel.monthlyTalliesMap.value?.let { displayIndicators(it) }
    }

    private fun displayIndicators(monthlyTallies: Map<String, MonthlyTally>) {
        val groupedTallies: Map<String, List<MonthlyTally>> = monthlyTallies.values.groupBy { it.grouping }
        reportsIndicatorsListAdapter.apply {
            if (groupedTallies.isNotEmpty()) {
                val firstMonthlyTally: MonthlyTally = groupedTallies.values.first()[0]
                val submittedBy = requireContext().getString(R.string.submitted_by_,
                        ReportsDao.dateFormatter("dd/MM/YYYY").format(firstMonthlyTally.dateSent), firstMonthlyTally.providerId)
                submittedByTextView.text = submittedBy
                reportGroupHeaders = groupedTallies.keys.toList()
                reportIndicators = groupedTallies
                indicatorsExpandableListView.expandGroup(0, true)
            }
        }
    }

    override fun onGroupClick(parent: ExpandableListView, view: View, groupPosition: Int, id: Long): Boolean {
        return when (parent.expandGroup(groupPosition)) {
            true -> {
                collapsibleImageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.expand_less))
                true
            }
            else -> {
                collapsibleImageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.expand_more))
                false
            }
        }
    }
}