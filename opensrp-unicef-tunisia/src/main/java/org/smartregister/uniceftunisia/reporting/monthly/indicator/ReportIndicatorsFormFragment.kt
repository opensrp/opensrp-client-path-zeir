package org.smartregister.uniceftunisia.reporting.monthly.indicator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_report_indicators_form.*
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.domain.MonthlyTally
import org.smartregister.uniceftunisia.reporting.ViewModelUtil
import org.smartregister.uniceftunisia.reporting.getResourceId
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository

/**
 * A [Fragment] subclass used to launch form allowing editing of report indicators
 */
class ReportIndicatorsFormFragment : Fragment() {

    private val reportIndicatorsViewModel by activityViewModels<ReportIndicatorsViewModel>
    { ViewModelUtil.createFor(ReportIndicatorsViewModel(MonthlyReportsRepository())) }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_report_indicators_form, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        reportIndicatorsViewModel.monthlyTalliesMap.value?.let { loadIndicatorsForm(it) }
    }

    private fun loadIndicatorsForm(monthlyTallies: Map<String, MonthlyTally>) {
        monthlyTallies.forEach { entry ->
            val textInputEditText = TextInputEditText(requireContext()).apply {
                tag = entry.key
                hint = getString(entry.key.getResourceId(requireContext()))
                setText(entry.value.value.toString())
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) =
                            Unit

                    override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                        reportIndicatorsViewModel.monthlyTalliesMap.value
                                ?.set(entry.key, entry.value.apply
                                { value = charSequence?.toString() })
                    }

                    override fun afterTextChanged(editable: Editable?) = Unit
                })

            }
            reportIndicatorsLayout.addView(TextInputLayout(requireContext()).apply
            { addView(textInputEditText) })
        }
    }
}