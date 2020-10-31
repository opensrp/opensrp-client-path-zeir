package org.smartregister.uniceftunisia.reporting.monthly.sent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_monthly_sent_reports.*
import org.smartregister.uniceftunisia.R

class SentReportsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_monthly_sent_reports, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       sentReportsExpandableListView.apply {
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
    }
}