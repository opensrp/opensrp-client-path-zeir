package org.smartregister.path.reporting.monthly.indicator.daily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_report_indicators_summary.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.smartregister.path.R
import org.smartregister.path.reporting.ReportingRulesEngine
import org.smartregister.path.reporting.common.ReportingUtils
import org.smartregister.path.reporting.common.showProgressDialog
import org.smartregister.path.reporting.common.sortIndicators
import org.smartregister.path.reporting.monthly.MonthlyReportsRepository
import org.smartregister.path.reporting.monthly.domain.DailyTally
import org.smartregister.path.reporting.monthly.indicator.ReportIndicatorsViewModel
import org.smartregister.path.reporting.monthly.indicator.summary.ReportIndicatorsRecyclerAdapter
import org.smartregister.util.Utils

/**
 * A [Fragment] subclass used to display list of daily indicators
 */
class ReportDailyIndicatorsSummaryFragment : Fragment() {

    private lateinit var progressDialog: AlertDialog

    private val reportIndicatorsRecyclerAdapter = ReportIndicatorsRecyclerAdapter()

    private val reportIndicatorsViewModel by activityViewModels<ReportIndicatorsViewModel>()
    { ReportingUtils.createFor(ReportIndicatorsViewModel()) }

    private val extendedIndicatorTallies by lazy {
        getExtendedIndicatorTallies().associateBy { it.indicator }.toMutableMap()
    }

    private lateinit var reportingRulesEngine: ReportingRulesEngine<DailyTally>

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_report_indicators_summary, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        progressDialog = requireContext().showProgressDialog(
                show = true,
                title = getString(R.string.loading_monthly_reports_title),
                message = getString(R.string.loading_daily_reports_message))
        indicatorsRecyclerView.apply {
            adapter = reportIndicatorsRecyclerAdapter
            layoutManager = LinearLayoutManager(context)
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        submittedByTextView.visibility = View.GONE
        reportIndicatorsViewModel.dailyTalliesMap.value?.let { dailyTallies ->
            reportingRulesEngine = ReportingRulesEngine(tallies = dailyTallies, context = requireContext())
            extendedIndicatorTallies.forEach { tallyEntry ->
                tallyEntry.apply {
                    value.providerId = MonthlyReportsRepository.getInstance().getProviderId()
                    value.day = ReportingUtils.dateFormatter("dd MMMM yyyy").parse(reportIndicatorsViewModel.day.value!!)!!
                    value.value = "0"
                }
                includeExtendedTallies(dailyTallies, tallyEntry)
            }
            executeRules(dailyTallies)
            displayIndicators(dailyTallies)
        }
    }

    private fun executeRules(dailyTallies: MutableMap<String, DailyTally>) {
        dailyTallies.forEach() {
            val dailyTally = it.value
            if (!dailyTally.dependentCalculations.isNullOrEmpty()) {
                reportingRulesEngine.fireRules(dailyTally, dailyTallies) { _, _ -> }
            }
        }
    }
    private fun displayIndicators(dailyTallies: Map<String, DailyTally>) {
        lifecycleScope.launch(Dispatchers.Main) {
            val sortedIndicators = dailyTallies.values.toList().sortIndicators()
            val groupedTallies: Map<String, List<DailyTally>> = sortedIndicators.groupBy { it.grouping }
            reportIndicatorsRecyclerAdapter.apply {
                if (groupedTallies.isNotEmpty()) {
                    reportIndicators = groupedTallies.toList()
                }
            }
            progressDialog.dismiss()
        }
    }

    private fun getExtendedIndicatorTallies(): List<DailyTally> {
        val typeToken = object : TypeToken<List<DailyTally>>() {}.type
        val assetFileInputStream = Utils.readAssetContents(requireContext(),
                "configs/reporting/extended-indicators.json")
        return Gson().fromJson(assetFileInputStream, typeToken)
    }

    private fun includeExtendedTallies(dailyTallies: MutableMap<String, DailyTally>, tallyEntry: Map.Entry<String, DailyTally>) {
        if (!dailyTallies.containsKey(tallyEntry.key)) {
            dailyTallies[tallyEntry.key] = tallyEntry.value
        } else if (dailyTallies.containsKey(tallyEntry.key)
                && dailyTallies[tallyEntry.key]?.dependentCalculations?.isEmpty()!!
                && !tallyEntry.value.dependentCalculations.isNullOrEmpty()) {
            dailyTallies[tallyEntry.key]?.dependentCalculations = tallyEntry.value.dependentCalculations
        }
    }
}