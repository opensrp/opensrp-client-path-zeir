package org.smartregister.pathzeir.reporting.annual

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.smartregister.pathzeir.reporting.annual.coverage.domain.CoverageTarget
import org.smartregister.pathzeir.reporting.annual.coverage.domain.VaccineCoverage
import org.smartregister.pathzeir.reporting.annual.coverage.repository.AnnualReportRepository
import org.smartregister.pathzeir.reporting.annual.coverage.repository.VaccineCoverageTargetRepository
import org.smartregister.pathzeir.reporting.common.ReportingUtils.dateFormatter
import java.util.*

class AnnualReportViewModel : ViewModel() {

    private val annualReportRepository = AnnualReportRepository.getInstance()
    private val vaccineCoverageTargetRepository = VaccineCoverageTargetRepository.getInstance()
    val yearTargets = MutableLiveData<List<CoverageTarget>>()
    val vaccineCoverageReports = MutableLiveData<List<VaccineCoverage>>()

    val selectedYear by lazy {
        MutableLiveData<Int>().apply {
            postValue(dateFormatter("yyyy").format(Date()).toInt())
        }
    }

    suspend fun getReportYears() = withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
        annualReportRepository.getReportYears()
    }

    fun getVaccineCoverageReports(year: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            vaccineCoverageReports.postValue(annualReportRepository.getVaccineCoverage(year))
        }
    }

    fun getYearCoverageTargets(year: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            yearTargets.postValue(vaccineCoverageTargetRepository.getCoverageTarget(year))
        }
    }
}