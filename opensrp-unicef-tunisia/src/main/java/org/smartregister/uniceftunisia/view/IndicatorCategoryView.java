package org.smartregister.uniceftunisia.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.smartregister.uniceftunisia.R;
import org.smartregister.uniceftunisia.domain.Tally;
import org.smartregister.uniceftunisia.util.AppReportUtils;

import java.util.ArrayList;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

public class IndicatorCategoryView extends LinearLayout {
    private Context context;
    private TableLayout indicatorTable;
    private ArrayList<Tally> tallies;

    public IndicatorCategoryView(Context context) {
        super(context);
        init(context);
    }

    public IndicatorCategoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IndicatorCategoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IndicatorCategoryView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_indicator_category, this, true);
        indicatorTable = findViewById(R.id.indicator_table);
    }

    public void setTallies(ArrayList<Tally> tallies) {
        this.tallies = tallies;
        refreshIndicatorTable();
    }

    private void refreshIndicatorTable() {
        if (tallies != null) {
            for (Tally curTally : tallies) {
                TableRow dividerRow = new TableRow(context);
                View divider = new View(context);
                TableRow.LayoutParams params = (TableRow.LayoutParams) divider.getLayoutParams();
                if (params == null) params = new TableRow.LayoutParams();
                params.width = TableRow.LayoutParams.MATCH_PARENT;
                params.height = getResources().getDimensionPixelSize(R.dimen.indicator_table_divider_height);
                params.span = 3;
                divider.setLayoutParams(params);
                divider.setBackgroundColor(getResources().getColor(R.color.client_list_header_dark_grey));
                dividerRow.addView(divider);
                indicatorTable.addView(dividerRow);

                TableRow curRow = new TableRow(context);

                TextView nameTextView = new TextView(context);
                nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.indicator_table_contents_text_size));
                nameTextView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                nameTextView.setPadding(
                        getResources().getDimensionPixelOffset(R.dimen.table_row_side_margin),
                        getResources().getDimensionPixelSize(R.dimen.table_contents_text_v_margin),
                        getResources().getDimensionPixelSize(R.dimen.table_row_middle_margin),
                        getResources().getDimensionPixelSize(R.dimen.table_contents_text_v_margin));
                int resourceId = this.getResources().getIdentifier(AppReportUtils.getStringIdentifier(curTally.getIndicator()), "string", getContext().getPackageName());
                String name = resourceId != 0 ? getResources().getString(resourceId) : curTally.getIndicator();
                nameTextView.setText(name);
                nameTextView.setTextColor(getResources().getColor(R.color.client_list_grey));
                curRow.addView(nameTextView);

                TextView valueTextView = new TextView(context);
                valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.indicator_table_contents_text_size));
                valueTextView.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
                valueTextView.setPadding(
                        getResources().getDimensionPixelSize(R.dimen.table_row_middle_margin),
                        getResources().getDimensionPixelSize(R.dimen.table_contents_text_v_margin),
                        getResources().getDimensionPixelSize(R.dimen.table_row_side_margin),
                        getResources().getDimensionPixelSize(R.dimen.table_contents_text_v_margin));
                valueTextView.setTextColor(getResources().getColor(R.color.client_list_grey));
                valueTextView.setText(curTally.getValue());
                curRow.addView(valueTextView);
                indicatorTable.addView(curRow);
            }
        }
    }
}
