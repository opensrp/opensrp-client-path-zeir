package org.smartregister.uniceftunisia.reporting.monthly.draft

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_monthly_draft_item.*
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.reporting.ReportsDao.dateFormatter
import org.smartregister.uniceftunisia.reporting.convertToNamedMonth
import org.smartregister.uniceftunisia.reporting.translateString
import java.util.*

class DraftedReportsRecyclerAdapter(val onClickListener: View.OnClickListener) :
        RecyclerView.Adapter<DraftedReportsRecyclerAdapter.DraftedMonthsViewHolder>() {

    var draftedMonths: List<Pair<String, Date>> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DraftedMonthsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_monthly_draft_item, parent, false)
        return DraftedMonthsViewHolder(view)
    }

    override fun onBindViewHolder(holder: DraftedMonthsViewHolder, position: Int) = holder.setupView(draftedMonths[position])

    override fun getItemCount() = draftedMonths.size

    inner class DraftedMonthsViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun setupView(monthDraft: Pair<String, Date>) {
            containerView.setOnClickListener(onClickListener)
            with(monthDraft) {
                containerView.tag = this
                monthTextView.text = first.convertToNamedMonth(hasHyphen = true)
                        .translateString(containerView.context)
                startedAtTextView.text = containerView.context.getString(R.string.started_on,
                        dateFormatter("dd/MM/yy").format(second))
            }
        }
    }
}