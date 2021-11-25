package org.smartregister.path.reporting.monthly.indicator.summary

import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Typeface
import android.provider.Settings.Global.getString
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.RecyclerView
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.report_indicator_summary_list_item.view.*
import kotlinx.android.synthetic.main.report_indicators_expansion_panel_item.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import org.smartregister.path.R
import org.smartregister.path.reporting.common.getResourceId
import org.smartregister.path.reporting.common.sortIndicators
import org.smartregister.path.reporting.monthly.domain.MonthlyTally
import org.smartregister.path.reporting.monthly.domain.Tally
import org.smartregister.reporting.ReportingLibrary
import timber.log.Timber
import kotlin.concurrent.timer

class ReportIndicatorsRecyclerAdapter : RecyclerView.Adapter<ReportIndicatorsRecyclerAdapter.SentReportsRecyclerHolder>() {

    var reportIndicators: List<Pair<String, List<*>>> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val expansionsCollection = ExpansionLayoutCollection()

    init {
        expansionsCollection.openOnlyOne(false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SentReportsRecyclerHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.report_indicators_expansion_panel_item, parent, false)
        return SentReportsRecyclerHolder(view)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holderSentReports: SentReportsRecyclerHolder, position: Int) {
        holderSentReports.bindViews(reportIndicators[position] as Pair<String, List<Tally>>)
        expansionsCollection.add(holderSentReports.reportIndicatorsExpansionLayout)
    }

    override fun getItemCount() = reportIndicators.size

    inner class SentReportsRecyclerHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindViews(monthlyTallies: Pair<String, List<Tally>>) {
            CoroutineScope(Dispatchers.Main).launch {
                val (reportGroup, tallies) = monthlyTallies

                //Set report group header
                reportIndicatorTextView.apply {
                    typeface = Typeface.DEFAULT_BOLD
                    text = context.getString(reportGroup.getResourceId(containerView.context))
                }

                //Set display tallies
                val topLabel = listOf(
                        MonthlyTally(
                                grouping = reportGroup,
                                indicator = "indicator",
                                value = containerView.context.getString(R.string.value)
                        )
                )
                reportIndicatorsContainer.removeAllViews()

                val sortedIndicators = tallies.sortIndicators()
                topLabel.plus(sortedIndicators).forEach {
                    val view = LayoutInflater.from(containerView.context).inflate(R.layout.report_indicator_summary_list_item,
                            reportIndicatorsContainer, false).apply {
                        tag = it
                        indicatorTextView.text = context.getString(it.indicator.getResourceId(context))
                        if(!it.enteredManually) {
                            valueTextView.visibility = View.VISIBLE
                            valueEditText.visibility = View.GONE
                            valueTextView.text = it.value
                        }
                        else
                        {
                            valueTextView.visibility = View.GONE
                            valueEditText.visibility = View.VISIBLE
                            valueEditText.inputType = InputType.TYPE_CLASS_NUMBER
                            valueEditText.maxLines = 1
                            valueEditText.imeOptions = EditorInfo.IME_ACTION_DONE
                            valueEditText.setText(it.value)
                            valueEditText.tag = it.id.toString()

                            valueEditText.setOnEditorActionListener { _, actionId, _ ->
                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                    // Hide Keyboard
                                    Timber.d("done")
                                    val valueText = valueEditText.text.toString();
                                    if(StringUtils.isNotBlank(valueText))
                                        ReportingLibrary.getInstance().dailyIndicatorCountRepository().updateIndicatorValue(valueEditText.tag.toString(),valueText);


                                }
                                false
                            }

                            valueEditText.addTextChangedListener(object : TextWatcher {
                                override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) =
                                    Unit

                                override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) =
                                    Unit

                                override fun afterTextChanged(editable: Editable?) {
                                    when {
                                        editable.toString().count { it == '.' } > 2 -> {
                                            valueEditText.error = context.getString(R.string.error_enter_valid_number)
                                        }
                                        editable.isNullOrEmpty() -> {
                                            valueEditText.error = context.getString(R.string.error_field_required)
                                        }
                                    }
                                }
                            })
                        }
                    }
                    reportIndicatorsContainer.addView(view)
                }
            }
        }
    }
}