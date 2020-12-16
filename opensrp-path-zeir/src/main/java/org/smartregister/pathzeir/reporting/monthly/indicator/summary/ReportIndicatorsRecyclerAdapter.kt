package org.smartregister.pathzeir.reporting.monthly.indicator.summary

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.report_indicator_summary_list_item.view.*
import kotlinx.android.synthetic.main.report_indicators_expansion_panel_item.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.smartregister.pathzeir.R
import org.smartregister.pathzeir.reporting.common.getResourceId
import org.smartregister.pathzeir.reporting.common.sortIndicators
import org.smartregister.pathzeir.reporting.monthly.domain.MonthlyTally

class ReportIndicatorsRecyclerAdapter : RecyclerView.Adapter<ReportIndicatorsRecyclerAdapter.SentReportsRecyclerHolder>() {

    var reportIndicators: List<Pair<String, List<MonthlyTally>>> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val expansionsCollection = ExpansionLayoutCollection()

    init {
        expansionsCollection.openOnlyOne(false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SentReportsRecyclerHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.report_indicators_expansion_panel_item, parent, false)
        return SentReportsRecyclerHolder(view)
    }

    override fun onBindViewHolder(holderSentReports: SentReportsRecyclerHolder, position: Int) {
        holderSentReports.bindViews(reportIndicators[position])
        expansionsCollection.add(holderSentReports.reportIndicatorsExpansionLayout)
    }

    override fun getItemCount() = reportIndicators.size

    inner class SentReportsRecyclerHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindViews(monthlyTallies: Pair<String, List<MonthlyTally>>) {
            CoroutineScope(Dispatchers.Main).launch {
                val (reportGroup, tallies) = monthlyTallies

                //Set report group header
                reportIndicatorTextView.apply {
                    typeface = Typeface.DEFAULT_BOLD
                    text = context.getString(reportGroup.getResourceId(containerView.context))
                }

                //Set display tallies
                val topLabel = listOf(
                        MonthlyTally(
                                grouping = reportGroup,
                                indicator = "indicator",
                                value = containerView.context.getString(R.string.value)
                        )
                )
                reportIndicatorsContainer.removeAllViews()

                val sortedIndicators = tallies.sortIndicators()
                topLabel.plus(sortedIndicators).forEach {
                    val view = LayoutInflater.from(containerView.context).inflate(R.layout.report_indicator_summary_list_item,
                            reportIndicatorsContainer, false).apply {
                        tag = it
                        indicatorTextView.text = context.getString(it.indicator.getResourceId(context))
                        valueTextView.text = it.value
                    }
                    reportIndicatorsContainer.addView(view)
                }
            }
        }
    }
}