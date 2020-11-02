package org.smartregister.uniceftunisia.reporting.monthly

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.smartregister.uniceftunisia.reporting.monthly.domain.MonthlyTally
import java.util.*

class MonthlyReportsViewModel(private val monthlyReportsRepository: MonthlyReportsRepository) : ViewModel() {

    val unDraftedMonths = MutableLiveData<List<String>>()

    val draftedMonths = MutableLiveData<List<Pair<String, Date>>>()

    val draftedReportTallies = MutableLiveData<Pair<String, List<MonthlyTally>>>()

    val sentReportMonths = MutableLiveData<Map<String, List<MonthlyTally>>>()

    val sentReportTallies = MutableLiveData<Pair<String, List<MonthlyTally>>>()

    fun fetchUnDraftedMonths() {
        viewModelScope.launch(Dispatchers.IO) {
            unDraftedMonths.postValue(monthlyReportsRepository.fetchUnDraftedMonths())
        }
    }

    fun fetchDraftedReportTalliesByMonth(yearMonth: String) {
        viewModelScope.launch(Dispatchers.IO) {
            draftedReportTallies.postValue(Pair(yearMonth, monthlyReportsRepository.fetchDraftedReportTalliesByMonth(yearMonth)))
        }
    }

    fun fetchDraftedMonths() {
        viewModelScope.launch(Dispatchers.IO) {
            draftedMonths.postValue(monthlyReportsRepository.fetchDraftedMonths())
        }
    }

    fun fetchAllSentReportMonths() {
        viewModelScope.launch(Dispatchers.IO) {
            sentReportMonths.postValue(monthlyReportsRepository.fetchSentReportMonths())
        }
    }

    fun fetchSentReportTalliesByMonth(yearMonth: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sentReportTallies.postValue(Pair(yearMonth, monthlyReportsRepository.fetchSentReportTalliesByMonth(yearMonth)))
        }
    }
}
