package org.smartregister.uniceftunisia.reporting.annual.coverage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_annual_report.*
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.reporting.ReportsDao
import org.smartregister.uniceftunisia.reporting.annual.AnnualReportRepository
import org.smartregister.uniceftunisia.reporting.annual.AnnualReportViewModel
import org.smartregister.uniceftunisia.reporting.common.ReportingUtils
import java.util.*

class AnnualCoverageFragment : Fragment() {

    private val annualReportViewModel by activityViewModels<AnnualReportViewModel>
    { ReportingUtils.createFor(AnnualReportViewModel(AnnualReportRepository.getInstance())) }

    private val annualCoverageRecyclerAdapter = AnnualCoverageRecyclerAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_annual_report, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        annualCoverageRecyclerView.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(context)
            adapter = annualCoverageRecyclerAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        reportYearsSpinner.apply {
            adapter = ReportYearsAdapter(listOf("2020", "2019"))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        annualReportViewModel.run {
            vaccineCoverageReports.observe(viewLifecycleOwner, {
                val labelList = listOf(VaccineCoverage(
                        vaccine = getString(R.string.vaccine),
                        vaccinated = getString(R.string.vaccinated),
                        coverage = getString(R.string.coverage)
                ))
                annualCoverageRecyclerAdapter.vaccineCoverageReports = labelList.plus(it)
            })
        }
    }

    inner class ReportYearsAdapter(private val reportYears: List<String>) : BaseAdapter() {
        override fun getCount() = reportYears.size

        override fun getItem(position: Int) = reportYears[position]

        override fun getItemId(position: Int) = reportYears[position].hashCode().toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return ((convertView
                    ?: LayoutInflater.from(parent.context).inflate(R.layout.app_spinner_item, parent, false)) as TextView)
                    .apply {
                        setTextColor(ContextCompat.getColor(context, R.color.black_text_color))
                        val item = getItem(position)
                        text = when (item) {
                            ReportsDao.dateFormatter("yyyy").format(Date()) ->
                                getString(R.string.current_report_year, item)
                            else -> item
                        }
                    }
        }
    }
}