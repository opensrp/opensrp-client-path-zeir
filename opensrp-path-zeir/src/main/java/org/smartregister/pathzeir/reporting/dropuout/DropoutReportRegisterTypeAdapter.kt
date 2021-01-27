package org.smartregister.pathzeir.reporting.dropuout

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.dropout_reports_item.view.*
import org.smartregister.pathzeir.R
import org.smartregister.pathzeir.reporting.DropoutReportGroup
import org.smartregister.pathzeir.reporting.DropoutReportGroupingModel

class DropoutReportRegisterTypeAdapter(
        context: Context,
        val layout: Int,
        private val reportTypes: List<DropoutReportGroupingModel.DropoutReportGrouping>,
        private val listener: OnDropoutItemClick?) :
        ArrayAdapter<DropoutReportGroupingModel.DropoutReportGrouping>(context, layout) {

    override fun getCount() = reportTypes.size

    override fun getItem(position: Int) = reportTypes[position]

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return convertView
                ?: LayoutInflater.from(context).inflate(
                        R.layout.dropout_reports_item,
                        parent, false)
                        .apply {
                            val model = getItem(position)
                            // set views
                            rev1.tv.text = model.displayNameCumulative
                            rev1.setOnClickListener {
                                listener?.onItemClick(model.reportGroupCumulative)
                            }

                            model.displayNameCohort?.let {
                                rev2.visibility = View.VISIBLE
                                adapter_divider_bottom.visibility = View.VISIBLE

                                rev2.tv2.text = it

                                rev2.setOnClickListener {
                                    listener?.onItemClick(model.reportGroupCohort!!)
                                }
                            } ?: run{
                                rev2.visibility = View.GONE
                                adapter_divider_bottom.visibility = View.GONE
                            }
                        }
    }

    interface OnDropoutItemClick {
        fun onItemClick(group: DropoutReportGroup)
    }
}