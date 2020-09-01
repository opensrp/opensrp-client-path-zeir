package org.smartregister.uniceftunisia.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.child.activity.BaseActivity;
import org.smartregister.child.toolbar.SimpleToolbar;
import org.smartregister.reporting.ReportingLibrary;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.uniceftunisia.R;
import org.smartregister.uniceftunisia.domain.MonthlyTally;
import org.smartregister.uniceftunisia.domain.Tally;
import org.smartregister.uniceftunisia.util.AppExecutors;
import org.smartregister.uniceftunisia.view.IndicatorCategoryView;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

public class ReportSummaryActivity extends BaseActivity {

    public static final String EXTRA_TALLIES = "tallies";
    public static final String EXTRA_DAY = "tally_day";
    public static final String EXTRA_SUB_TITLE = "sub_title";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_REPORT_GROUPING = "report-grouping";
    private LinkedHashMap<String, ArrayList<Tally>> tallies;

    private String subTitle;
    private String reportGrouping;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SimpleToolbar toolbar = (SimpleToolbar) getToolbar();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setBackground(new ColorDrawable(getResources().getColor(R.color.smart_register_blue)));

        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            Serializable talliesSerializable = extras.getSerializable(EXTRA_TALLIES);
            Serializable tallyDaySerializable = extras.getSerializable(EXTRA_DAY);
            reportGrouping = extras.getString(EXTRA_REPORT_GROUPING);

            if (talliesSerializable != null && talliesSerializable instanceof  ArrayList) {
                ArrayList<MonthlyTally> tallies = (ArrayList<MonthlyTally>) talliesSerializable;
                setTallies(tallies, false);
            }

            if (tallyDaySerializable instanceof Date) {
                fetchIndicatorTalliesForDay((Date) tallyDaySerializable, reportGrouping);
            }

            Serializable submittedBySerializable = extras.getSerializable(EXTRA_SUB_TITLE);
            if (submittedBySerializable instanceof String) {
                subTitle = (String) submittedBySerializable;
            }

            Serializable titleSerializable = extras.getSerializable(EXTRA_TITLE);
            if (titleSerializable instanceof String) {
                toolbar.setTitle((String) titleSerializable);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CustomFontTextView submittedBy = findViewById(R.id.submitted_by);
        if (!TextUtils.isEmpty(this.subTitle)) {
            submittedBy.setVisibility(View.VISIBLE);
            submittedBy.setText(this.subTitle);
        } else {
            submittedBy.setVisibility(View.GONE);
        }
        refreshIndicatorViews();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_report_summary;
    }

    @Override
    protected int getDrawerLayoutId() {
        return R.id.drawer_layout;
    }

    @Override
    protected int getToolbarId() {
        return SimpleToolbar.TOOLBAR_ID;
    }

    @Override
    protected Class onBackActivity() {
        return null;
    }

    public void setTallies(ArrayList<MonthlyTally> tallies) {
        setTallies(tallies, true);
    }

    private void setTallies(@NonNull ArrayList<MonthlyTally> tallies, boolean refreshViews) {
        this.tallies = new LinkedHashMap<>();
        this.tallies.put("", new ArrayList<>());

        Collections.sort(tallies, (lhs, rhs) -> lhs.getIndicator().compareTo(rhs.getIndicator()));

        for (MonthlyTally curTally : tallies) {
            if (curTally != null && !TextUtils.isEmpty(curTally.getIndicator())) {
                this.tallies.get("").add(curTally.getIndicatorTally());
            }
        }

        if (refreshViews) refreshIndicatorViews();
    }

    private void setDailyTallies(@NonNull ArrayList<IndicatorTally> tallies, boolean refreshViews) {
        this.tallies = new LinkedHashMap<>();
        this.tallies.put("", new ArrayList<>());

        Collections.sort(tallies, (lhs, rhs) -> lhs.getIndicatorCode().compareTo(rhs.getIndicatorCode()));

        for (IndicatorTally curTally : tallies) {
            if (curTally != null && !TextUtils.isEmpty(curTally.getIndicatorCode())) {
                Tally tally = new Tally();
                tally.setIndicator(curTally.getIndicatorCode());
                tally.setValue(String.valueOf(curTally.getFloatCount()));

                if (curTally.getId() == null) {
                    tally.setId(0);
                } else {
                    tally.setId(curTally.getId());
                }

                this.tallies.get("").add(tally);
            }
        }

        if (refreshViews) refreshIndicatorViews();
    }

    private void refreshIndicatorViews() {
        LinearLayout indicatorCanvas = findViewById(R.id.indicator_canvas);
        indicatorCanvas.removeAllViews();

        if (tallies != null) {
            for (String curCategoryName : tallies.keySet()) {
                IndicatorCategoryView curCategoryView = new IndicatorCategoryView(this);
                curCategoryView.setTallies(tallies.get(curCategoryName));
                indicatorCanvas.addView(curCategoryView);
            }
        }
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
        // Nothing to do
    }

    @Override
    public void onNoUniqueId() {
        // Nothing to do
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        // Nothing to do
    }

    public void fetchIndicatorTalliesForDay(@NonNull final Date date, @Nullable String reportGrouping) {
        AppExecutors appExecutors = new AppExecutors();
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<IndicatorTally> indicatorTallies = ReportingLibrary.getInstance()
                        .dailyIndicatorCountRepository()
                        .getIndicatorTalliesForDay(date, reportGrouping);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        setDailyTallies(indicatorTallies, true);
                    }
                });
            }
        });
    }
}
