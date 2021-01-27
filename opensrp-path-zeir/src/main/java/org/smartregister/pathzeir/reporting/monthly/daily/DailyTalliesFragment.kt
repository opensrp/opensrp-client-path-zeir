package org.smartregister.pathzeir.reporting.monthly.daily

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
import kotlinx.android.synthetic.main.fragment_hia2_daily_tallies_report.*
import org.smartregister.pathzeir.R
import org.smartregister.pathzeir.reporting.common.*
import org.smartregister.pathzeir.reporting.monthly.MonthlyReportsViewModel
import org.smartregister.pathzeir.reporting.monthly.domain.DailyTally
import org.smartregister.pathzeir.reporting.monthly.indicator.ReportIndicatorsActivity
import java.io.Serializable

class DailyTalliesFragment : Fragment(), View.OnClickListener {

    private val dailyTalliesRecyclerAdapter = DailyTalliesRecyclerAdapter(this)

    private val monthlyReportsViewModel by activityViewModels<MonthlyReportsViewModel>
    { ReportingUtils.createFor(MonthlyReportsViewModel()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_hia2_daily_tallies_report, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dailyTalliesRecyclerView.apply {
            adapter = dailyTalliesRecyclerAdapter
            layoutManager = LinearLayoutManager(context)
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        monthlyReportsViewModel.run {
            dailyReportDays.observe(viewLifecycleOwner, {
                dailyTalliesRecyclerAdapter.apply {
                    if (it.isNotEmpty()) dailyTallies = it.toList()
                }
            })
            dailyTallies.observe(viewLifecycleOwner, {
                val (_, dailyTallies) = it
                startActivity(Intent(requireActivity(), ReportIndicatorsActivity::class.java).apply {
                    putExtras(Bundle().apply {
                        putExtra(DAILY_TALLIES, dailyTallies.associateBy { monthlyTally -> monthlyTally.indicator } as Serializable)
                        putExtra(DAY, ReportingUtils.dateFormatter("dd MMMM yy").format(dailyTallies[0].day))
                        putExtra(SHOW_DATA, true)
                    })
                })
                requireActivity().finish()

            })
        }

    }

    override fun onClick(view: View) {
        if (view.tag is DailyTally) {
            val dailyTally = view.tag as DailyTally
            monthlyReportsViewModel.fetchDailyTalliesByDay(ReportingUtils.dateFormatter("yyyy-MM-dd").format(dailyTally.day))
        }
    }

}