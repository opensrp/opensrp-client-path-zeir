package org.smartregister.pathzeir.reporting.monthly.indicator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.smartregister.pathzeir.R

/**
 * Main [Fragment] subclass for ReportIndicator.
 * create an instance of this fragment.
 */
class ReportIndicatorFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.fragment_report_indicator, container, false)
}