package org.smartregister.uniceftunisia.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;

import org.smartregister.uniceftunisia.R;
import org.smartregister.uniceftunisia.activity.HIA2ReportsActivity;
import org.smartregister.uniceftunisia.adapter.MonthlyDraftsAdapter;
import org.smartregister.uniceftunisia.domain.MonthlyTally;
import org.smartregister.uniceftunisia.repository.MonthlyTalliesRepository;
import org.smartregister.uniceftunisia.task.FetchEditedMonthlyTalliesTask;
import org.smartregister.uniceftunisia.task.FetchUneditedMonthlyTalliesTask;
import org.smartregister.util.Utils;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.customcontrols.FontVariant;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

public class DraftMonthlyFragment extends ReportFragment {

    private Button startNewReportEnabled;
    private Button startNewReportDisabled;
    private AlertDialog alertDialog;
    private MonthlyDraftsAdapter draftsAdapter;
    private ListView listView;
    private View noDraftsView;



    public static DraftMonthlyFragment newInstance(@Nullable String reportGrouping) {
        DraftMonthlyFragment fragment = new DraftMonthlyFragment();
        fragment.reportGrouping = reportGrouping;
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragmentview = inflater.inflate(R.layout.sent_monthly_fragment, container, false);

        listView = fragmentview.findViewById(R.id.list);
        noDraftsView = fragmentview.findViewById(R.id.empty_view);
        startNewReportEnabled = fragmentview.findViewById(R.id.start_new_report_enabled);
        startNewReportDisabled = fragmentview.findViewById(R.id.start_new_report_disabled);

        return fragmentview;
    }

    @Override
    public void refreshData() {
        setupEditedDraftsView();
        setupUneditedDraftsView();
    }

    private void updateStartNewReportButton(@Nullable final List<Date> dates) {
        boolean hia2ReportsReady = dates != null && !dates.isEmpty();

        startNewReportEnabled.setVisibility(View.GONE);
        startNewReportDisabled.setVisibility(View.GONE);

        if (hia2ReportsReady) {
            Collections.sort(dates, new Comparator<Date>() {
                @Override
                public int compare(Date lhs, Date rhs) {
                    return rhs.compareTo(lhs);
                }
            });
            startNewReportEnabled.setVisibility(View.VISIBLE);
            startNewReportEnabled.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateResults(dates, monthClickListener);
                }
            });

        } else {
            startNewReportDisabled.setVisibility(View.VISIBLE);
            startNewReportDisabled.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show(Snackbar.make(startNewReportDisabled, getString(R.string.no_monthly_ready), Snackbar.LENGTH_SHORT));
                }
            });
        }
    }

    private void setupUneditedDraftsView() {
        Utils.startAsyncTask(new FetchUneditedMonthlyTalliesTask(reportGrouping, new FetchUneditedMonthlyTalliesTask.TaskListener() {
            @Override
            public void onPostExecute(@NonNull List<Date> dates) {
                updateStartNewReportButton(dates);
            }
        }), null);
    }

    private void setupEditedDraftsView() {
        if (getActivity() != null) {
            ((HIA2ReportsActivity) getActivity()).refreshDraftMonthlyTitle();
        }

        Utils.startAsyncTask(new FetchEditedMonthlyTalliesTask(reportGrouping, this::updateDraftsReportListView), null);
    }

    public void updateDraftsReportListView(@Nullable final List<MonthlyTally> monthlyTallies) {
        if (getActivity() != null && monthlyTallies != null && !monthlyTallies.isEmpty()) {
            noDraftsView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            if (draftsAdapter == null) {
                draftsAdapter = new MonthlyDraftsAdapter(getActivity(), monthlyTallies, monthClickListener);
                listView.setAdapter(draftsAdapter);
            } else {
                draftsAdapter.setList(monthlyTallies);
                draftsAdapter.notifyDataSetChanged();
            }
        } else {
            noDraftsView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }

    private void updateResults(@NonNull final List<Date> list, @NonNull final View.OnClickListener clickListener) {
        if (getActivity() != null) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.month_results, null);

            ListView listView = view.findViewById(R.id.list_view);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.PathDialog);
            builder.setView(view);
            builder.setCancelable(true);

            CustomFontTextView title = new CustomFontTextView(getActivity());
            title.setText(getString(R.string.reports_available));
            title.setGravity(Gravity.START);
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            title.setFontVariant(FontVariant.BOLD);
            title.setPadding(getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin), getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin), getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin), getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin));

            builder.setCustomTitle(title);

            alertDialog = builder.create();

            BaseAdapter baseAdapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return list.size();
                }

                @Override
                public Object getItem(int position) {
                    return list.get(position);
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view;
                    if (convertView == null) {
                        LayoutInflater inflater = (LayoutInflater)
                                getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

                        view = inflater.inflate(R.layout.month_item, null);
                    } else {
                        view = convertView;
                    }

                    TextView tv = view.findViewById(R.id.tv);
                    Date date = list.get(position);
                    String text = MonthlyTalliesRepository.DF_YYYYMM.format(date);
                    tv.setText(text);
                    tv.setTag(date);

                    view.setOnClickListener(clickListener);
                    view.setTag(list.get(position));

                    return view;
                }
            };

            listView.setAdapter(baseAdapter);
            alertDialog.show();
        }

    }

    private final View.OnClickListener monthClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // An NPE Happens here since it take time to open
            if (alertDialog != null) {
                alertDialog.dismiss();
            }

            Object tag = v.getTag();
            if (tag instanceof Date) {
                startMonthlyReportForm((Date) tag, reportGrouping);
            }
        }
    };

    private void show(final Snackbar snackbar) {
        if (snackbar == null) {
            return;
        }

        float textSize = getActivity().getResources().getDimension(R.dimen.snack_bar_text_size);

        View snackbarView = snackbar.getView();
        snackbarView.setMinimumHeight(Float.valueOf(textSize).intValue());

        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        snackbar.show();
    }

    private void startMonthlyReportForm(@NonNull Date date, @Nullable String reportGrouping) {
        if (getActivity() != null) {
            ((HIA2ReportsActivity) getActivity())
                    .startMonthlyReportForm("monthly_report", reportGrouping, date);
        }
    }

}
