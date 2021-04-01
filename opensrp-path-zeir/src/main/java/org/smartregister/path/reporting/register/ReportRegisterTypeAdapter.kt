package org.smartregister.path.reporting.register

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.report_type_list_item.view.*
import org.smartregister.path.R

class ReportRegisterTypeAdapter(context: Context, val layout: Int, private val reportTypes: List<String>) :
        ArrayAdapter<String>(context, layout) {

    override fun getCount() = reportTypes.size

    override fun getItem(position: Int) = reportTypes[position]

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return convertView
                ?: LayoutInflater.from(context).inflate(R.layout.report_type_list_item,
                        parent, false)
                        .apply {
                            reportTypeTextView.text = getItem(position)
                            tag = getItem(position)
                        }
    }
}