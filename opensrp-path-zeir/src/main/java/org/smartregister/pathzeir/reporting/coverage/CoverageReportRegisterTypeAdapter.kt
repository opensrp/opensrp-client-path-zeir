package org.smartregister.pathzeir.reporting.coverage

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.coverage_reports_item.view.*
import org.smartregister.pathzeir.R
import org.smartregister.pathzeir.reporting.CoverageReportGroup
import org.smartregister.pathzeir.reporting.CoverageReportGroupingModel

class CoverageReportRegisterTypeAdapter(
        context: Context,
        val layout: Int,
        private val reportTypes: List<CoverageReportGroupingModel.CoverageReportGrouping>,
        private val listener: OnDropoutItemClick?) :
        ArrayAdapter<CoverageReportGroupingModel.CoverageReportGrouping>(context, layout) {

    override fun getCount() = reportTypes.size

    override fun getItem(position: Int) = reportTypes[position]

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return convertView
                ?: LayoutInflater.from(context).inflate(
                        R.layout.coverage_reports_item,
                        parent, false)
                        .apply {
                            val model = getItem(position)
                            // set views
                            tv.text = model.displayName
                            coverage_rev.setOnClickListener {
                                listener?.onItemClick(model.reportGroup)
                            }
                        }
    }

    interface OnDropoutItemClick {
        fun onItemClick(group: CoverageReportGroup)
    }
}