package org.smartregister.uniceftunisia.task;

import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.uniceftunisia.activity.HIA2ReportsActivity;
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication;
import org.smartregister.uniceftunisia.repository.MonthlyTalliesRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-29
 */

public class FetchUneditedMonthlyTalliesTask extends AsyncTask<Void, Void, List<Date>>{

    private final TaskListener taskListener;
    private final String reportGrouping;

    public FetchUneditedMonthlyTalliesTask(@Nullable String reportGrouping, @NonNull TaskListener taskListener) {
        this.reportGrouping = reportGrouping;
        this.taskListener = taskListener;
    }

    @Override
    protected List<Date> doInBackground(Void... params) {
        MonthlyTalliesRepository monthlyTalliesRepository = UnicefTunisiaApplication
                .getInstance().monthlyTalliesRepository();
        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.DAY_OF_MONTH, 1);
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);
        startDate.add(Calendar.MONTH, -1 * HIA2ReportsActivity.MONTH_SUGGESTION_LIMIT);

        Calendar endDate = Calendar.getInstance();
        // should be 1, setting temporarily to 30
        endDate.set(Calendar.DAY_OF_MONTH, 30);// Set date to first day of this month
        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.SECOND, 59);
        endDate.set(Calendar.MILLISECOND, 999);
        endDate.add(Calendar.DATE, -1);// Move the date to last day of last month

        return monthlyTalliesRepository.findUneditedDraftMonths(startDate.getTime(), endDate.getTime(), reportGrouping);
    }

    @Override
    protected void onPostExecute(@NonNull List<Date> dates) {
        taskListener.onPostExecute(dates);
    }

    public interface TaskListener {
        void onPostExecute(@NonNull List<Date> dates);
    }
}

