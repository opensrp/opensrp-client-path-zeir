package org.smartregister.uniceftunisia.reporting.monthly

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.smartregister.uniceftunisia.domain.MonthlyTally
import java.util.*

class MonthlyReportsViewModel(private val monthlyReportsRepository: MonthlyReportsRepository) : ViewModel() {

    val unDraftedMonths = MutableLiveData<List<String>>()

    val draftedMonths = MutableLiveData<List<Pair<String, Date>>>()

    val draftedReportTallies = MutableLiveData<Pair<String, List<MonthlyTally>>>()

    fun fetchUnDraftedMonths() {
        viewModelScope.launch(Dispatchers.IO) {
            unDraftedMonths.postValue(monthlyReportsRepository.fetchUnDraftedMonths())
        }
    }

    fun fetchMonthlyDraftedReportTallies(yearMonth: String) {
        viewModelScope.launch(Dispatchers.IO) {
            draftedReportTallies.postValue(Pair(yearMonth, monthlyReportsRepository.fetchMonthlyDraftedReportTallies(yearMonth)))
        }
    }

    fun fetchDraftedMonths() {
        viewModelScope.launch(Dispatchers.IO) {
            draftedMonths.postValue(monthlyReportsRepository.fetchDraftedMonths())
        }
    }
}
