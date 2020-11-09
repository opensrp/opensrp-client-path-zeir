package org.smartregister.uniceftunisia.reporting.common

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.smartregister.AllConstants
import org.smartregister.domain.Event
import org.smartregister.uniceftunisia.reporting.monthly.MonthlyReportsRepository
import org.smartregister.uniceftunisia.reporting.monthly.domain.MonthlyTally
import org.smartregister.uniceftunisia.util.AppConstants
import org.smartregister.uniceftunisia.util.AppJsonFormUtils
import timber.log.Timber

/**
 * String constants
 */
const val MONTHLY_TALLIES = "monthly_tallies"
const val MONTHLY_REPORT = "monthly_report"
const val YEAR_MONTH = "year_month"
const val SHOW_DATA = "show_data"

/**
 * Utility method for creating ViewModel Factory
 */
@Suppress("UNCHECKED_CAST")
object ReportingUtils {

    @JvmStatic
    fun <T : ViewModel> createFor(viewModel: T): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(viewModel::class.java)) {
                    return viewModel as T
                }
                throw IllegalArgumentException("unexpected viewModel class $modelClass")
            }
        }
    }

    @JvmStatic
    fun processMonthlyReportEvent(event: Event) {
        event.details[AppConstants.EventType.MONTHLY_REPORT]?.let { monthlyReportString ->
            try {
                val monthlyReportJson = JSONObject(monthlyReportString)
                val typeToken = object : TypeToken<Map<String, MonthlyTally>>() {}.type
                val yearMonth = monthlyReportJson.getString(AppConstants.KEY.YEAR_MONTH)
                val monthlyTalliesJsonArray = JSONArray(monthlyReportJson.getString(AppConstants.KEY.MONTHLY_TALLIES))
                (0 until monthlyTalliesJsonArray.length())
                        .map { monthlyTalliesJsonArray.getJSONObject(it) }
                        .map { JSONObject().put(it.getString(AllConstants.INDICATOR), it) }
                        .map { AppJsonFormUtils.gson.fromJson<Map<String, MonthlyTally>>(it.toString(), typeToken) }
                        .forEach { MonthlyReportsRepository.getInstance().saveMonthlyDraft(it, yearMonth, true) }
            } catch (e: JSONException) {
                Timber.e(e)
            }
        }
    }
}

/**
 * This extension method applied on [String] class allows you to convert a date string with named months
 * to numbered month and vice versa
 *      Example October 2020 will be converted to 2020-10
 *      and 20119-01 will be converted to January 2019
 */
fun String.convertToNamedMonth(hasHyphen: Boolean = false): String {
    val yearAndMonth = this.split(if (hasHyphen) "-" else " ")
    val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    return if (hasHyphen)
        "${months[yearAndMonth.last().toInt() - 1]} ${(yearAndMonth.first())}"
    else
        "${yearAndMonth.last()}-${(months.indexOf(yearAndMonth.first()) + 1).toString().padStart(2, '0')}"
}

/**
 * Return a formatted string identifier
 */
fun String.getResourceId(context: Context): Int = try {
    context.resources.getIdentifier(this.replace(" ", "_").replace("/", ""), "string", context.packageName)
} catch (throwable: Throwable) {
    Timber.e("String Resource for $this is not found. Specify it on strings.xml file.$throwable")
    0
}


/**
 * Translate string if string used to get translated value for year month
 */
fun String.translateString(context: Context): String {
    val textSplit = this.split(" ")
    if (textSplit.size == 2) {
        textSplit.run {
            val resourceId = first().getResourceId(context)
            return if (resourceId != 0)
                "${context.resources.getString(resourceId)} ${last()}"
            else this@translateString
        }
    }
    return this
}

/**
 * Activity context extensions to display toast messages
 */
fun Context.showToast(resourceId: Int, duration: Int = Toast.LENGTH_LONG) =
        Toast.makeText(this, getString(resourceId), duration).show()


/**
 * Activity context extensions to display SnackBar messages
 */
fun View.showSnackBar(resourceId: Int, duration: Int = Snackbar.LENGTH_LONG) =
        Snackbar.make(this, this.context.getString(resourceId), duration).show()

/**
 * LiveData Extension to observer data once for the [observer] subscribed to the given [lifecycleOwner]
 * */

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

/**
 * LiveData Extension to remove [observer] subscribed to the [lifecycleOwner] before re-observing data
 * */

fun <T> LiveData<T>.reObserve(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    removeObserver(observer)
    observe(lifecycleOwner, observer)
}

fun List<MonthlyTally>.sortIndicators() = this.sortedBy {
    it.indicator.substringAfter("index_")
            .split("_").first().toInt()
}