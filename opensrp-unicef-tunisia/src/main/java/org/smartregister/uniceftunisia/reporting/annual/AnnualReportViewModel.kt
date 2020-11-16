package org.smartregister.uniceftunisia.reporting.annual

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.smartregister.uniceftunisia.reporting.ReportsDao
import org.smartregister.uniceftunisia.reporting.annual.coverage.VaccineCoverageTargetRepository
import org.smartregister.uniceftunisia.reporting.annual.coverage.domain.CoverageTarget
import org.smartregister.uniceftunisia.reporting.annual.coverage.domain.VaccineCoverage
import java.util.*

class AnnualReportViewModel : ViewModel() {

    private val annualReportRepository = AnnualReportRepository.getInstance()
    private val vaccineCoverageTargetRepository = VaccineCoverageTargetRepository.getInstance()
    val yearTargets = MutableLiveData<List<CoverageTarget>>()
    val vaccineCoverageReports = MutableLiveData<List<VaccineCoverage>>()

    val selectedYear by lazy {
        MutableLiveData<Int>().apply {
            postValue(ReportsDao.dateFormatter("yyyy").format(Date()).toInt())
        }
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

    suspend fun saveCoverageTarget(coverageTargets: List<CoverageTarget>): Boolean {
        return vaccineCoverageTargetRepository.saveCoverageTarget(coverageTargets)
    }
}