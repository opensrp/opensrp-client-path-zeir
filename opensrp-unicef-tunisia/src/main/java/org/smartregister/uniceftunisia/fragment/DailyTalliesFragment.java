package org.smartregister.uniceftunisia.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.smartregister.uniceftunisia.R;
import org.smartregister.uniceftunisia.activity.HIA2ReportsActivity;
import org.smartregister.uniceftunisia.activity.ReportSummaryActivity;
import org.smartregister.uniceftunisia.adapter.ExpandedListAdapter;
import org.smartregister.uniceftunisia.util.AppExecutors;
import org.smartregister.reporting.ReportingLibrary;
import org.smartregister.reporting.dao.ReportIndicatorDaoImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

public class DailyTalliesFragment extends ReportFragment {

    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd MMMM yyyy");
    private ExpandableListView expandableListView;
    private ArrayList<Date> dailyTallies;
    private ProgressDialog progressDialog;
    private AppExecutors appExecutors;

    public static DailyTalliesFragment newInstance(@Nullable String reportGrouping) {
        DailyTalliesFragment fragment = new DailyTalliesFragment();
        fragment.reportGrouping = reportGrouping;
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.report_expandable_list_view, container, false);
        expandableListView = fragmentView.findViewById(R.id.expandable_list_view);
        expandableListView.setBackgroundColor(getResources().getColor(R.color.white));

        return fragmentView;
    }

    @NonNull
    private ArrayList<Date> fetchLast3MonthsDailyTallies() {
        Calendar startDate = Calendar.getInstance();

        startDate.set(Calendar.DAY_OF_MONTH, 1);
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);
        startDate.add(Calendar.MONTH, -1 * HIA2ReportsActivity.MONTH_SUGGESTION_LIMIT);
        return ReportingLibrary.getInstance().dailyIndicatorCountRepository()
                .findDaysWithIndicatorCounts(new SimpleDateFormat(ReportIndicatorDaoImpl.DAILY_TALLY_DATE_FORMAT)
                        , startDate.getTime()
                        , Calendar.getInstance().getTime(), reportGrouping);
    }

    private void displayDailyTallies(@NonNull ArrayList<Date> daysWithTallies) {
        dailyTallies = daysWithTallies;
        updateExpandableList();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            updateExpandableList();
        }
    }

    private void updateExpandableList() {
        updateExpandableList(formatListData());
    }

    @SuppressWarnings("unchecked")
    private void updateExpandableList(@NonNull final LinkedHashMap<String, List<ExpandedListAdapter.ItemData<String, Date>>> map) {
        if (expandableListView == null) {
            return;
        }

        ExpandedListAdapter<String, String, Date> expandableListAdapter = new ExpandedListAdapter(getActivity(), map, R.layout.daily_tally_header, R.layout.daily_tally_item);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Object tag = v.getTag(R.id.item_data);
                if (tag instanceof Date) {
                    Date date = (Date) tag;
                    String dayString = DAY_FORMAT.format(date);

                    String title = String.format(getString(R.string.daily_tally_), dayString);
                    Intent intent = new Intent(getActivity(), ReportSummaryActivity.class);
                    intent.putExtra(ReportSummaryActivity.EXTRA_DAY, date);
                    intent.putExtra(ReportSummaryActivity.EXTRA_TITLE, title);
                    intent.putExtra(ReportSummaryActivity.EXTRA_REPORT_GROUPING, reportGrouping);
                    startActivity(intent);
                }

                return true;
            }
        });
        expandableListAdapter.notifyDataSetChanged();
    }

    @NonNull
    private LinkedHashMap<String, List<ExpandedListAdapter.ItemData<String, Date>>> formatListData() {
        Map<String, List<ExpandedListAdapter.ItemData<String, Date>>> map = new HashMap<>();
        Map<Long, String> sortMap = new TreeMap<>(new Comparator<Comparable>() {
            @SuppressWarnings("unchecked")
            public int compare(Comparable a, Comparable b) {
                return b.compareTo(a);
            }
        });

        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);

        if (dailyTallies != null) {
            for (Date day : dailyTallies) {
                String monthString = monthFormat.format(day);
                if (!map.containsKey(monthString)) {
                    map.put(monthString, new ArrayList<ExpandedListAdapter.ItemData<String, Date>>());
                }

                map.get(monthString).add(new ExpandedListAdapter.ItemData<>(DAY_FORMAT.format(day), day));
                sortMap.put(day.getTime(), monthString);
            }
        }

        LinkedHashMap<String, List<ExpandedListAdapter.ItemData<String, Date>>> sortedMap = new LinkedHashMap<>();
        for (Long curKey : sortMap.keySet()) {
            List<ExpandedListAdapter.ItemData<String, Date>> list = map.get(sortMap.get(curKey));
            if (list != null) {
                Collections.sort(list, new Comparator<ExpandedListAdapter.ItemData<String, Date>>() {
                    @Override
                    public int compare(ExpandedListAdapter.ItemData<String, Date> lhs,
                                       ExpandedListAdapter.ItemData<String, Date> rhs) {
                        return lhs.getTagData().compareTo(rhs.getTagData());
                    }
                });
                sortedMap.put(sortMap.get(curKey), list);
            }
        }

        return sortedMap;
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.please_wait_message));
    }

    private void showProgressDialog() {
        try {
            if (progressDialog == null) {
                initializeProgressDialog();
            }

            progressDialog.show();
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void hideProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void refreshData() {
        if (appExecutors == null) {
            appExecutors = new AppExecutors();
        }

        showProgressDialog();
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final ArrayList<Date> datesWithTallies = fetchLast3MonthsDailyTallies();

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                        displayDailyTallies(datesWithTallies);
                    }
                });
            }
        });
    }

}
