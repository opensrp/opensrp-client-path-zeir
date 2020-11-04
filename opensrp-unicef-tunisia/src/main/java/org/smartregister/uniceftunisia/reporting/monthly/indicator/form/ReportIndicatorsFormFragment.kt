package org.smartregister.uniceftunisia.reporting.monthly.indicator.form

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_report_indicators_form.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication
import org.smartregister.uniceftunisia.reporting.*
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository
import org.smartregister.uniceftunisia.reporting.monthly.domain.MonthlyTally
import org.smartregister.uniceftunisia.reporting.monthly.draft.ConfirmSendDraftDialog
import org.smartregister.uniceftunisia.reporting.monthly.indicator.ReportIndicatorsViewModel
import org.smartregister.uniceftunisia.util.AppJsonFormUtils
import org.smartregister.uniceftunisia.util.AppUtils
import org.smartregister.util.JsonFormUtils
import timber.log.Timber
import java.util.*

/**
 * A [Fragment] subclass used to launch form allowing editing of report indicators
 */
class ReportIndicatorsFormFragment : Fragment(), View.OnClickListener {

    private lateinit var confirmSendDraftDialog: ConfirmSendDraftDialog

    private val reportIndicatorsViewModel by activityViewModels<ReportIndicatorsViewModel>
    { ReportingUtils.createFor(ReportIndicatorsViewModel(MonthlyReportsRepository.getInstance())) }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_report_indicators_form, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        reportIndicatorsViewModel.monthlyTalliesMap.value?.let { loadIndicatorsForm(it) }
        //Setup UI
        confirmSendDraftDialog = ConfirmSendDraftDialog().apply {
            onClickListener = this@ReportIndicatorsFormFragment
            arguments = bundleOf(Pair(ConfirmSendDraftDialog.Constants.MONTH,
                    reportIndicatorsViewModel.yearMonth.value
                            ?.convertToNamedMonth(hasHyphen = true)
                            ?.translateString(view.context)))
        }
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
                setPadding(0, 20, 0, 28)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
            }

            //Create report group header
            reportIndicatorsLayout.addView(reportHeaderTextView)

            //Create text inputs for each indicator
            entry.value.forEach {
                val textInputEditText = TextInputEditText(requireContext()).apply {
                    tag = it.indicator
                    hint = getString(it.indicator.getResourceId(requireContext()))
                    isFocusable = false
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

        //Add confirm Button At the end of the text input fields
        reportIndicatorsLayout.addView(
                Button(requireContext()).apply {
                    val linearLayoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    linearLayoutParams.bottomMargin = 16
                    linearLayoutParams.topMargin = 24

                    layoutParams = linearLayoutParams
                    background = ContextCompat.getDrawable(requireContext(), R.drawable.report_btn_bg)
                    text = getString(R.string.confirm_button_label)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    setOnClickListener {
                        confirmSendDraftDialog.show(requireActivity().supportFragmentManager,
                                ConfirmSendDraftDialog::class.simpleName)
                    }
                })
    }

    private fun syncMonthlyReportsToServer() {
        lifecycleScope.launch {
            when (createAndProcessMonthlyReportEvent()) {
                true -> {
                    reportIndicatorsScrollView.showSnackBar(R.string.monthly_draft_submitted)
                    requireActivity().onBackPressed()
                }
                else -> reportIndicatorsScrollView.showSnackBar(R.string.error_sending_draft_reports)
            }
        }
    }

    private suspend fun createAndProcessMonthlyReportEvent(): Boolean {
        val allSharedPreferences = AppUtils.getAllSharedPreferences()
        val appInstance = UnicefTunisiaApplication.getInstance()
        return try {
            withContext(lifecycleScope.coroutineContext + Dispatchers.IO) {

                val baseEvent = AppJsonFormUtils.createEvent(JSONArray(), JSONObject().put(JsonFormUtils.ENCOUNTER_LOCATION, ""),
                        AppJsonFormUtils.formTag(allSharedPreferences), "", MONTHLY_REPORT, MONTHLY_REPORT)

                with(baseEvent) {
                    val monthlyTalliesMap = reportIndicatorsViewModel.monthlyTalliesMap.value
                    val yearMonth = reportIndicatorsViewModel.yearMonth.value

                    addDetails(MONTHLY_REPORT, JSONObject().apply {
                        put(YEAR_MONTH, yearMonth)
                        put(MONTHLY_TALLIES, AppJsonFormUtils.gson.toJson(monthlyTalliesMap?.values))
                    }.toString())

                    formSubmissionId = UUID.randomUUID().toString()
                    AppJsonFormUtils.tagEventMetadata(this)
                    appInstance.ecSyncHelper.addEvent(this.baseEntityId, JSONObject(AppJsonFormUtils.gson.toJson(this)))
                    appInstance.clientProcessor.processClient(appInstance.ecSyncHelper.getEvents(listOf(this.formSubmissionId)))
                    val lastSyncDate = Date(allSharedPreferences.fetchLastUpdatedAtDate(0))
                    allSharedPreferences.saveLastUpdatedAtDate(lastSyncDate.time)
                }
                true
            }
        } catch (throwable: Throwable) {
            Timber.e(throwable)
            false
        }
    }

    override fun onClick(view: View) {
        if (view.id == R.id.sendDraftReportsButton) syncMonthlyReportsToServer()
    }
}