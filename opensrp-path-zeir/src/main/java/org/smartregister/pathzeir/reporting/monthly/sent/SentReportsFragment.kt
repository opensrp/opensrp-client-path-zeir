package org.smartregister.pathzeir.reporting.monthly.sent

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_monthly_sent_reports.*
import org.smartregister.pathzeir.R
import org.smartregister.pathzeir.reporting.common.MONTHLY_TALLIES
import org.smartregister.pathzeir.reporting.common.ReportingUtils
import org.smartregister.pathzeir.reporting.common.ReportingUtils.dateFormatter
import org.smartregister.pathzeir.reporting.common.SHOW_DATA
import org.smartregister.pathzeir.reporting.common.YEAR_MONTH
import org.smartregister.pathzeir.reporting.monthly.MonthlyReportsViewModel
import org.smartregister.pathzeir.reporting.monthly.domain.MonthlyTally
import org.smartregister.pathzeir.reporting.monthly.indicator.ReportIndicatorsActivity
import java.io.Serializable

class SentReportsFragment : Fragment(), View.OnClickListener {

    private val sentReportsRecyclerAdapter = SentReportsRecyclerAdapter(this)

    private val monthlyReportsViewModel by activityViewModels<MonthlyReportsViewModel>
    { ReportingUtils.createFor(MonthlyReportsViewModel()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_monthly_sent_reports, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        sentReportsRecyclerView.apply {
            adapter = sentReportsRecyclerAdapter
            layoutManager = LinearLayoutManager(context)
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        monthlyReportsViewModel.run {
            sentReportMonths.observe(viewLifecycleOwner, {
                sentReportsRecyclerAdapter.apply {
                    if (it.isNotEmpty()) sentReports = it.toList()
                }
            })
            sentReportTallies.observe(viewLifecycleOwner, {
                val (yearMonth, monthlyTallies) = it
                startActivity(Intent(requireActivity(), ReportIndicatorsActivity::class.java).apply {
                    putExtras(Bundle().apply {
                        putExtra(MONTHLY_TALLIES, monthlyTallies.associateBy { monthlyTally -> monthlyTally.indicator } as Serializable)
                        putExtra(YEAR_MONTH, yearMonth)
                        putExtra(SHOW_DATA, true)
                    })
                })
                requireActivity().finish()
            })
        }
    }

    override fun onClick(view: View) {
        if (view.tag is MonthlyTally) {
            val monthlyTally = view.tag as MonthlyTally
            monthlyReportsViewModel.fetchSentReportTalliesByMonth(dateFormatter().format(monthlyTally.month))
        }
    }
}