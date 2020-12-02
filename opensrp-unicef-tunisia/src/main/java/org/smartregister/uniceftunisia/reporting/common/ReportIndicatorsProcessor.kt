package org.smartregister.uniceftunisia.reporting.common

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.smartregister.reporting.domain.CompositeIndicatorTally
import org.smartregister.reporting.domain.IndicatorTally
import org.smartregister.reporting.exception.MultiResultProcessorException
import org.smartregister.reporting.processor.MultiResultProcessor
import java.util.*

/**
 * This subclass of [MultiResultProcessor] is used to process daily multi-select indicator values. These values are stored in the format
 * "["bcg","age",1]", where the first & second column returned are indicator groupings
 * in the example  bcg and age are vaccine groupings whereas the third column is the count.
 *
 * The two indicator groups should be a string or have default affinity to  [android.database.Cursor.FIELD_TYPE_STRING]
 * as described [https://www.sqlite.org/datatype3.html].
 *
 * The third column should have affinity to either [android.database.Cursor.FIELD_TYPE_INTEGER] or [android.database.Cursor.FIELD_TYPE_FLOAT] in SQLite
 *
 */
class ReportIndicatorsProcessor : MultiResultProcessor {

    override fun canProcess(cols: Int, colNames: Array<String>) =
            cols == 3 && colNames.size == 3 && colNames[2].contains("count")

    @Throws(MultiResultProcessorException::class)
    override fun processMultiResultTally(compositeIndicatorTally: CompositeIndicatorTally): List<IndicatorTally> {
        val compositeTallies = Gson().fromJson<ArrayList<List<Any>>>(compositeIndicatorTally.valueSet, object : TypeToken<List<List<Any>>>() {}.type)

        compositeTallies.removeAt(0)// Remove the column names from processing

        val tallies = ArrayList<IndicatorTally>()

        compositeTallies.forEach { compositeTally ->
            val tally = IndicatorTally().apply {
                createdAt = compositeIndicatorTally.createdAt
                grouping = compositeIndicatorTally.grouping
                if (compositeTally.size == 3) {
                    indicatorCode = "${compositeIndicatorTally.indicatorCode}_${compositeTally[0]}_${compositeTally[1]}"
                    count = getCount(compositeTally, compositeIndicatorTally)
                }
            }
            tallies.add(tally)
        }
        return tallies
    }

    private fun getCount(compositeTally: List<Any>, compositeIndicatorTally: CompositeIndicatorTally) =
            when (val indicatorValue = compositeTally[2]) {
                is Int -> indicatorValue
                is Double -> indicatorValue.toInt()
                else -> throw MultiResultProcessorException(indicatorValue, compositeIndicatorTally)
            }
}