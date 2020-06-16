package org.smartregister.uniceftunisia.processor;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.smartregister.uniceftunisia.util.AppConstants;
import org.smartregister.reporting.domain.CompositeIndicatorTally;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.reporting.exception.MultiResultProcessorException;
import org.smartregister.reporting.processor.MultiResultProcessor;

import java.util.ArrayList;
import java.util.List;


/**
 * This processor is able to processor queries where the first & second column returned are indicator groupings
 * eg. vaccine as the first column, gender as the second column while the third column contains the count.
 * The two indicator groups should be a string or have default affinity to {@link android.database.Cursor}.FIELD_TYPE_STRING
 * as described https://www.sqlite.org/datatype3.html.
 * <p>
 * The third column should have affinity to either Cursor.FIELD_TYPE_INTEGER or Cursor.FIELD_TYPE_FLOAT in SQLite
 * <p>
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-12-10
 */

public class TripleResultProcessor implements MultiResultProcessor {

    @Override
    public boolean canProcess(int cols, @NonNull String[] colNames) {
        return cols == 3 && colNames.length == 3 && colNames[2].contains("count");
    }

    @NonNull
    @Override
    public List<IndicatorTally> processMultiResultTally(@NonNull CompositeIndicatorTally compositeIndicatorTally) throws MultiResultProcessorException {
        ArrayList<Object[]> compositeTallies = new Gson().fromJson(compositeIndicatorTally.getValueSet(), new TypeToken<List<Object[]>>(){}.getType());

        // Remove the column names from processing
        compositeTallies.remove(0);

        List<IndicatorTally> tallies = new ArrayList<>();

        for (Object[] compositeTally: compositeTallies) {
            IndicatorTally indicatorTally = new IndicatorTally();
            indicatorTally.setCreatedAt(compositeIndicatorTally.getCreatedAt());
            indicatorTally.setGrouping(compositeIndicatorTally.getGrouping());

            if (compositeTally.length == 3) {

                indicatorTally.setIndicatorCode(compositeIndicatorTally.getIndicatorCode() + AppConstants.MultiResultProcessor.GROUPING_SEPARATOR + compositeTally[0] + AppConstants.MultiResultProcessor.GROUPING_SEPARATOR + compositeTally[1]);

                Object indicatorValue = compositeTally[2];
                if (indicatorValue instanceof Integer) {
                    indicatorTally.setCount((int) indicatorValue);
                } else if (indicatorValue instanceof Double) {
                    indicatorTally.setCount(((Double) indicatorValue).floatValue());
                } else {
                    throw new MultiResultProcessorException(indicatorValue, compositeIndicatorTally);
                }

                tallies.add(indicatorTally);
            }
        }

        return tallies;
    }
}
