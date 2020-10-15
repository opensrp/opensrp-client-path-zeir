package org.smartregister.uniceftunisia.task;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication;
import org.smartregister.uniceftunisia.domain.MonthlyTally;
import org.smartregister.uniceftunisia.repository.MonthlyTalliesRepository;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-29
 */

public class FetchEditedMonthlyTalliesTask extends AsyncTask<Void, Void, List<MonthlyTally>> {

    private final TaskListener taskListener;
    private final String reportGrouping;

    public FetchEditedMonthlyTalliesTask(@Nullable String reportGrouping, @NonNull TaskListener taskListener) {
        this.taskListener = taskListener;
        this.reportGrouping = reportGrouping;
    }

    @Override
    protected List<MonthlyTally> doInBackground(Void... params) {
        MonthlyTalliesRepository monthlyTalliesRepository = UnicefTunisiaApplication.getInstance().monthlyTalliesRepository();
        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.DAY_OF_MONTH, 1); // Set date to first day of this month
        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.SECOND, 59);
        endDate.set(Calendar.MILLISECOND, 999);
        endDate.add(Calendar.DATE, -1); // Move the date to last day of last month

        return monthlyTalliesRepository.findEditedDraftMonths(null, endDate.getTime(), reportGrouping);
    }

    @Override
    protected void onPostExecute(List<MonthlyTally> monthlyTallies) {
        super.onPostExecute(monthlyTallies);
        taskListener.onPostExecute(monthlyTallies);
    }

    public interface TaskListener {
        void onPostExecute(List<MonthlyTally> monthlyTallies);
    }
}