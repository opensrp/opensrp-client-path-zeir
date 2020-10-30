package org.smartregister.uniceftunisia.reporting.monthly.indicator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.reporting.ViewModelUtil
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository

/**
 * A [Fragment] subclass used to display list of report indicators
 */
class ReportIndicatorsSummaryFragment : Fragment() {

    private val reportIndicatorsViewModel by activityViewModels<ReportIndicatorsViewModel>()
    { ViewModelUtil.createFor(ReportIndicatorsViewModel(MonthlyReportsRepository())) }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_report_indicators_summary, container, false)

}