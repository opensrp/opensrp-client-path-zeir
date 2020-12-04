package org.smartregister.uniceftunisia.reporting.monthly.sent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.sent_monthly_report_list_item.view.*
import kotlinx.android.synthetic.main.sent_reports_expansion_panel_item.*
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.reporting.common.ReportingUtils.dateFormatter
import org.smartregister.uniceftunisia.reporting.common.convertToNamedMonth
import org.smartregister.uniceftunisia.reporting.common.translateString
import org.smartregister.uniceftunisia.reporting.monthly.domain.MonthlyTally

class SentReportsRecyclerAdapter(val onClickListener: View.OnClickListener) :
        RecyclerView.Adapter<SentReportsRecyclerAdapter.SentReportsRecyclerHolder>() {

    var sentReports: List<Pair<String, List<MonthlyTally>>> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val expansionsCollection = ExpansionLayoutCollection()

    init {
        expansionsCollection.openOnlyOne(false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SentReportsRecyclerHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sent_reports_expansion_panel_item, parent, false)
        return SentReportsRecyclerHolder(view)
    }

    override fun onBindViewHolder(holderSentReports: SentReportsRecyclerHolder, position: Int) {
        holderSentReports.bindViews(sentReports[position])
        expansionsCollection.add(holderSentReports.sentReportsExpansionLayout.apply { collapse(false) })
    }

    override fun getItemCount() = sentReports.size

    inner class SentReportsRecyclerHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindViews(yearlyTallies: Pair<String, List<MonthlyTally>>) {
            val (year, tallies) = yearlyTallies

            //Set year header
            yearHeaderTextView.text = year

            //Set tallies

            tallies.forEach {
                val view = LayoutInflater.from(containerView.context).inflate(R.layout.sent_monthly_report_list_item,
                        sentReportContainer, false).apply {
                    tag = it
                    dateReportSentTextView.text = dateFormatter("yyyy-MM").format(it.month)
                            .convertToNamedMonth(hasHyphen = true).translateString(context)
                    sentReportDetailsTextView.text = context.getString(R.string.sent_report_details,
                            dateFormatter("dd/MM/YYYY").format(it.dateSent!!), it.providerId)
                    setOnClickListener(onClickListener)
                }
                sentReportContainer.addView(view)
            }
        }
    }
}