package org.smartregister.uniceftunisia.reporting.monthly.indicator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.smartregister.uniceftunisia.domain.MonthlyTally
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository

class ReportIndicatorsViewModel(private val monthlyReportsRepository: MonthlyReportsRepository) : ViewModel() {
    val monthlyTalliesMap = MutableLiveData<MutableMap<String, MonthlyTally>>()

    val yearMonth = MutableLiveData<String>()

    suspend fun saveMonthlyDraft(): Boolean {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            monthlyReportsRepository.saveMonthlyDraft(monthlyTalliesMap.value, yearMonth.value)
        }
    }
}