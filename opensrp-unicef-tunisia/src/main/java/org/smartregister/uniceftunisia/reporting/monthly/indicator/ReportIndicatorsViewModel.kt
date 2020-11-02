package org.smartregister.uniceftunisia.reporting.monthly.indicator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository
import org.smartregister.uniceftunisia.reporting.monthly.domain.MonthlyTally

class ReportIndicatorsViewModel(private val monthlyReportsRepository: MonthlyReportsRepository) : ViewModel() {

    val monthlyTalliesMap = MutableLiveData<MutableMap<String, MonthlyTally>>()

    val yearMonth = MutableLiveData<String>()

    /**
     * Save the draft locally. Set option [sync] to true to sync the saved record to the server. This
     * will update the date_sent field of the table and create a monthly report event.
     */
    suspend fun saveMonthlyDraft(sync: Boolean = false): Boolean {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            monthlyReportsRepository.saveMonthlyDraft(monthlyTalliesMap.value, yearMonth.value, sync)
        }
    }
}