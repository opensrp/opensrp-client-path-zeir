package org.smartregister.uniceftunisia.reporting.annual.coverage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.annual_report_list_item.*
import org.smartregister.uniceftunisia.R
import org.smartregister.uniceftunisia.reporting.annual.coverage.domain.VaccineCoverage

class AnnualCoverageRecyclerAdapter :
        RecyclerView.Adapter<AnnualCoverageRecyclerAdapter.VaccineCoverageViewHolder>() {

    var vaccineCoverageReports: List<VaccineCoverage> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaccineCoverageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.annual_report_list_item, parent, false)
        return VaccineCoverageViewHolder(view)
    }

    override fun onBindViewHolder(holder: VaccineCoverageViewHolder, position: Int) = holder.setupView(vaccineCoverageReports[position])

    override fun getItemCount() = vaccineCoverageReports.size

    inner class VaccineCoverageViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun setupView(vaccineCoverage: VaccineCoverage) {
            with(vaccineCoverage) {
                containerView.tag = this
                vaccineTextView.text = vaccine
                vaccinatedNumberTextView.text = vaccinated
                coverageTextView.apply {
                    text = coverage
                    setTextColor(ContextCompat.getColor(context, coverageColorResource))
                }
            }
        }
    }
}