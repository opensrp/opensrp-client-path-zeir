package org.smartregister.uniceftunisia.reporting.monthly.sent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_monthly_sent_reports.*
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.reporting.ViewModelUtil
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsViewModel

class SentReportsFragment : Fragment() {

    private val sentReportsExpandableListAdapter = SentReportsExpandableListAdapter()

    private val monthlyReportsViewModel by activityViewModels<MonthlyReportsViewModel>
    { ViewModelUtil.createFor(MonthlyReportsViewModel(MonthlyReportsRepository())) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_monthly_sent_reports, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        monthlyReportsViewModel.sentReportMonths.observe(viewLifecycleOwner, {
            sentReportsExpandableListAdapter.apply {
                if(it.isNotEmpty()) {
                    sentReportYearHeaders = it.keys.toList()
                    sentReports = it
                    sentReportsExpandableListView.expandGroup(0)
                }
            }
        })

        sentReportsExpandableListView.apply {
            setAdapter(sentReportsExpandableListAdapter)
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
    }
}