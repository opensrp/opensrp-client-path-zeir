package org.smartregister.uniceftunisia.reporting.monthly.indicator.form

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_report_indicators_form.*
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.reporting.ReportingUtils
import org.smartregister.uniceftunisia.reporting.getResourceId
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository
import org.smartregister.uniceftunisia.reporting.monthly.domain.MonthlyTally
import org.smartregister.uniceftunisia.reporting.monthly.indicator.ReportIndicatorsViewModel

/**
 * A [Fragment] subclass used to launch form allowing editing of report indicators
 */
class ReportIndicatorsFormFragment : Fragment() {

    private val reportIndicatorsViewModel by activityViewModels<ReportIndicatorsViewModel>
    { ReportingUtils.createFor(ReportIndicatorsViewModel(MonthlyReportsRepository.getInstance())) }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_report_indicators_form, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        reportIndicatorsViewModel.monthlyTalliesMap.value?.let { loadIndicatorsForm(it) }
    }

    /**
     * Group indicators and create form
     */
    private fun loadIndicatorsForm(monthlyTallies: Map<String, MonthlyTally>) {

        val groupedTallies: Map<String, List<MonthlyTally>> = monthlyTallies.values.groupBy { it.grouping }

        groupedTallies.forEach { entry ->
            val reportHeaderTextView = TextView(requireContext()).apply {
                text = requireContext().getString(entry.key.getResourceId(requireContext()))
                setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                setPadding(0, 20,0,28)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
            }
            reportIndicatorsLayout.addView(reportHeaderTextView)
            entry.value.forEach {
                val textInputEditText = TextInputEditText(requireContext()).apply {
                    tag = it.indicator
                    hint = getString(it.indicator.getResourceId(requireContext()))
                    setText(it.value.toString())
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) =
                                Unit

                        override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                            reportIndicatorsViewModel.monthlyTalliesMap.value?.set(it.indicator,
                                    it.apply { value = charSequence.toString() })
                        }

                        override fun afterTextChanged(editable: Editable?) = Unit
                    })
                }
                reportIndicatorsLayout.addView(
                        TextInputLayout(requireContext()).apply { addView(textInputEditText) })
            }
        }
    }
}