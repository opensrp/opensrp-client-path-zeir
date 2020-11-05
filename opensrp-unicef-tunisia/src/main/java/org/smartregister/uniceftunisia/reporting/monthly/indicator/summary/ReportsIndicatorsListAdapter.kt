package org.smartregister.uniceftunisia.reporting.monthly.indicator.summary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.report_indicator_summary_header_item.view.*
import kotlinx.android.synthetic.main.report_indicator_summary_item.view.*
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.reporting.getResourceId
import org.smartregister.uniceftunisia.reporting.monthly.domain.MonthlyTally

class ReportsIndicatorsListAdapter : BaseExpandableListAdapter() {

    var reportGroupHeaders: List<String> = arrayListOf()

    var reportIndicators: Map<String, List<MonthlyTally>> = hashMapOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getGroupCount() = reportGroupHeaders.size

    override fun getChildrenCount(groupPosition: Int) = reportIndicators.getValue(getGroup(groupPosition)).size

    override fun getGroup(groupPosition: Int): String = reportGroupHeaders[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): MonthlyTally =
            reportIndicators.getValue(getGroup(groupPosition))[childPosition]

    override fun getGroupId(groupPosition: Int) = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int) = childPosition.toLong()

    override fun hasStableIds() = false

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View =
            convertView
                    ?: LayoutInflater.from(parent.context).inflate(R.layout.report_indicator_summary_header_item,
                            parent, false).apply {
                        val group = getGroup(groupPosition)
                        reportGroupHeaderTextView.text = context.getString(group.getResourceId(context))
                        when {
                            isExpanded -> collapsibleImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.expand_less))
                            else -> collapsibleImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.expand_more))
                        }
                        tag = group
                    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View =
            convertView
                    ?: LayoutInflater.from(parent.context).inflate(R.layout.report_indicator_summary_item,
                            parent, false).apply {
                        with(getChild(groupPosition, childPosition)) {
                            tag = this
                            indicatorTextView.text = context.getString(indicator.getResourceId(context))
                            valueTextView.text = value
                        }
                    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = false
}