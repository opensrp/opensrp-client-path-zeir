package org.smartregister.path.reporting.monthly.indicator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.smartregister.path.reporting.monthly.MonthlyReportsRepository
import org.smartregister.path.reporting.monthly.domain.DailyTally
import org.smartregister.path.reporting.monthly.domain.MonthlyTally

class ReportIndicatorsViewModel : ViewModel() {

    private val monthlyReportsRepository = MonthlyReportsRepository.getInstance()

    val monthlyTalliesMap = MutableLiveData<MutableMap<String, MonthlyTally>>()

    val dailyTalliesMap = MutableLiveData<MutableMap<String, DailyTally>>()

    val yearMonth = MutableLiveData<String>()

    val day = MutableLiveData<String>()

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