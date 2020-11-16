package org.smartregister.uniceftunisia.reporting.annual.coverage

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_annual_report.*
import kotlinx.coroutines.launch
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.reporting.ReportsDao
import org.smartregister.uniceftunisia.reporting.ReportsDao.dateFormatter
import org.smartregister.uniceftunisia.reporting.annual.AnnualReportViewModel
import org.smartregister.uniceftunisia.reporting.annual.coverage.domain.CoverageTarget
import org.smartregister.uniceftunisia.reporting.annual.coverage.domain.CoverageTargetType
import org.smartregister.uniceftunisia.reporting.annual.coverage.domain.CoverageTargetType.ONE_TWO_YEAR_TARGET
import org.smartregister.uniceftunisia.reporting.annual.coverage.domain.CoverageTargetType.UNDER_ONE_TARGET
import org.smartregister.uniceftunisia.reporting.annual.coverage.domain.VaccineCoverage
import org.smartregister.uniceftunisia.reporting.common.ReportingUtils
import org.smartregister.uniceftunisia.reporting.common.findTarget
import org.smartregister.uniceftunisia.reporting.common.showToast
import java.util.*

class AnnualCoverageFragment : Fragment() {

    private lateinit var coverageTargetDialog: CoverageTargetDialog
    private val currentTargets = mutableMapOf<String, CoverageTarget>()
    private val annualCoverageRecyclerAdapter = AnnualCoverageRecyclerAdapter()
    private val viewModel by activityViewModels<AnnualReportViewModel>
    { ReportingUtils.createFor(AnnualReportViewModel()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_annual_report, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        annualCoverageRecyclerView.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(context)
            adapter = annualCoverageRecyclerAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        reportYearsSpinner.apply {
            val reportYears = ReportsDao.getDistinctReportMonths()
                    .map { dateFormatter("yyyy").format(dateFormatter("MMMM yyyy").parse(it)!!) }
                    .distinct()
                    .asReversed()

            adapter = ReportYearsAdapter(reportYears)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    (view.tag as String).toInt().let {
                        viewModel.selectedYear.value = it
                        viewModel.getYearCoverageTargets(it)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        }

        underOneTargetTextView.apply {
            text = getString(R.string.under_one_target, getString(R.string.not_defined))
            formatText()
            setOnClickListener { editCoverageTarget() }
        }
        oneTwoYearsTargetTextView.apply {
            text = getString(R.string.one_two_years_target, getString(R.string.not_defined))
            formatText()
            setOnClickListener { editCoverageTarget() }
        }
    }

    private fun editCoverageTarget() {
        viewModel.selectedYear.value?.let { viewModel.getYearCoverageTargets(it) }
        coverageTargetDialog.launchDialog()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        coverageTargetDialog = CoverageTargetDialog()
        viewModel.run {
            vaccineCoverageReports.observe(viewLifecycleOwner, {
                val labelList = listOf(VaccineCoverage(
                        vaccine = getString(R.string.vaccine),
                        vaccinated = getString(R.string.vaccinated),
                        coverage = getString(R.string.coverage)
                ))
                annualCoverageRecyclerAdapter.vaccineCoverageReports = labelList.plus(it)
            })
            yearTargets.observe(viewLifecycleOwner, { targets ->
                currentTargets.clear()
                currentTargets.putAll(targets.associateBy { it.targetType.name })
                coverageTargetDialog.updateTargets(targets)
                underOneTargetTextView.updateTargetLabel(targets, UNDER_ONE_TARGET, R.string.under_one_target)
                oneTwoYearsTargetTextView.updateTargetLabel(targets, ONE_TWO_YEAR_TARGET, R.string.one_two_years_target)
            })
        }
    }

    private fun TextView.updateTargetLabel(targets: List<CoverageTarget>, coverageTargetType: CoverageTargetType, stringResource: Int) {
        targets.findTarget(coverageTargetType).also {
            text = getString(stringResource, if (it.isEmpty()) getString(R.string.not_defined) else it)
            if (it.isEmpty()) formatText()
        }
    }

    private fun TextView.formatText() {
        val spannableString = SpannableString(text)
        val startIndex = spannableString.indexOf(":") + 1
        spannableString.apply {
            setSpan(UnderlineSpan(), startIndex, spannableString.length, 0)
            setSpan(ForegroundColorSpan(ContextCompat.getColor(context,
                    R.color.cso_error_red)), startIndex, spannableString.length, 0)
        }
        text = spannableString
    }

    private inner class CoverageTargetDialog {

        lateinit var underOneTargetEditText: EditText
        lateinit var oneTwoYearsTargetEditText: EditText
        lateinit var dialog: AlertDialog

        init {
            createDialog()
        }

        fun createDialog() {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_annual_coverage_target, null)
            val builder = AlertDialog.Builder(requireActivity(), R.style.PathDialog).apply {
                setView(dialogView)
                setCancelable(true)
            }
            dialog = builder.create()
            with(dialogView) {
                underOneTargetEditText = findViewById<EditText>(R.id.underOneTargetEditText).apply {
                    addTextChangeListener(UNDER_ONE_TARGET)
                }

                oneTwoYearsTargetEditText = findViewById<EditText>(R.id.oneTwoYearsTargetEditText).apply {
                    addTextChangeListener(ONE_TWO_YEAR_TARGET)
                }
                val underOneTargetLabel = findViewById<TextView>(R.id.underOneTargetLabel)
                val oneTwoYearsTargetLabel = findViewById<TextView>(R.id.oneTwoYearsTargetLabel)

                viewModel.selectedYear.observe(viewLifecycleOwner, {
                    underOneTargetLabel.text = getString(R.string.set_under_one_target, it)
                    oneTwoYearsTargetLabel.text = getString(R.string.set_one_two_year_target, it)
                    viewModel.getVaccineCoverageReports(it)
                })

                findViewById<Button>(R.id.cancelButton).setOnClickListener { dialog.dismiss() }
                findViewById<Button>(R.id.okButton).setOnClickListener {
                    if (underOneTargetEditText.validate() && oneTwoYearsTargetEditText.validate()) {
                        lifecycleScope.launch {
                            if (viewModel.saveCoverageTarget(currentTargets.values.toList())) {
                                viewModel.run {
                                    getYearCoverageTargets(selectedYear.value!!)
                                    getVaccineCoverageReports(selectedYear.value!!)
                                }
                                dialog.dismiss()
                              
                            } else requireContext().showToast(R.string.error_saving_target)
                        }
                    }
                }
            }
        }

        private fun EditText.addTextChangeListener(coverageTargetType: CoverageTargetType) {
            addTextChangedListener {
                val currentTarget = it.toString()
                if (currentTarget.isNotEmpty()) {
                    val value = currentTargets.getOrPut(coverageTargetType.name) {
                        CoverageTarget(
                                targetType = coverageTargetType,
                                year = viewModel.selectedYear.value!!,
                                target = currentTarget.toInt()
                        )
                    }
                    currentTargets[coverageTargetType.name] = value.apply { target = currentTarget.toInt() }
                }
            }
        }

        private fun EditText.validate() = when {
            this.text.isEmpty() -> {
                error = getString(R.string.specify_target_error)
                false
            }
            else -> {
                error = null
                true
            }
        }

        fun updateTargets(coverageTargets: List<CoverageTarget>) {
            coverageTargets.run {
                underOneTargetEditText.updateValue(this, UNDER_ONE_TARGET)
                oneTwoYearsTargetEditText.updateValue(this, ONE_TWO_YEAR_TARGET)
            }
        }

        private fun EditText.updateValue(coverageTargets: List<CoverageTarget>, targetType: CoverageTargetType) =
                setText(coverageTargets.findTarget(targetType))

        fun launchDialog() = dialog.show()

    }

    private inner class ReportYearsAdapter(private val reportYears: List<String>) : BaseAdapter() {
        override fun getCount() = reportYears.size

        override fun getItem(position: Int) = reportYears[position]

        override fun getItemId(position: Int) = reportYears[position].hashCode().toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return ((convertView
                    ?: LayoutInflater.from(parent.context).inflate(R.layout.app_spinner_item, parent, false)) as TextView)
                    .apply {
                        setTextColor(ContextCompat.getColor(context, R.color.black_text_color))
                        val item = getItem(position)
                        tag = item
                        text = when (item) {
                            dateFormatter("yyyy").format(Date()) ->
                                getString(R.string.current_report_year, item)
                            else -> item
                        }
                    }
        }
    }
}