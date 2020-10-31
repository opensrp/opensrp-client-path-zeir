package org.smartregister.uniceftunisia.reporting

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import java.util.*

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
object ViewModelUtil {
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
fun String.getResourceId(context: Context): Int =
        context.resources.getIdentifier(this.toLowerCase(Locale.getDefault())
                .replace(" ", "_").replace("/", ""),
                "string", context.packageName)

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