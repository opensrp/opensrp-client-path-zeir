package org.smartregister.uniceftunisia.reporting.annual

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.smartregister.uniceftunisia.reporting.annual.coverage.VaccineCoverage

class AnnualReportViewModel(private val annualReportRepository: AnnualReportRepository) : ViewModel() {

    val vaccineCoverageReports by lazy {
        MutableLiveData<List<VaccineCoverage>>().apply {
            viewModelScope.launch(Dispatchers.IO) {
                postValue(annualReportRepository.getVaccineCoverage())
            }
        }
    }
}