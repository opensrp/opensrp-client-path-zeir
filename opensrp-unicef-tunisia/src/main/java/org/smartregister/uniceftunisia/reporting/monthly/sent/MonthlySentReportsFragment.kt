package org.smartregister.uniceftunisia.reporting.monthly.sent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.smartregister.uniceftunisia.R

class MonthlySentReportsFragment : Fragment() {

    private lateinit var expandableListView: ExpandableListView
   
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.report_expandable_list_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        expandableListView = view.findViewById<ExpandableListView>(R.id.expandable_list_view).apply {
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
    }
}