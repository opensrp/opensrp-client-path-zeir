package org.smartregister.uniceftunisia.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.smartregister.uniceftunisia.R;
import org.smartregister.uniceftunisia.domain.MonthlyTally;
import org.smartregister.uniceftunisia.repository.MonthlyTalliesRepository;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-12-02
 */
public class MonthlyDraftsAdapter extends BaseAdapter {

    private Activity activity;
    private List<MonthlyTally> list;
    private View.OnClickListener onMonthlyDraftClickListener;

    public MonthlyDraftsAdapter(@NonNull Activity activity, @NonNull List<MonthlyTally> list, @NonNull View.OnClickListener onMonthlyDraftClickListener) {
        this.activity = activity;
        this.onMonthlyDraftClickListener = onMonthlyDraftClickListener;
        setList(list);
    }

    public void setList(List<MonthlyTally> list) {
        this.list = list;
        if (this.list != null) {
            Collections.sort(list, new Comparator<MonthlyTally>() {
                @Override
                public int compare(MonthlyTally lhs, MonthlyTally rhs) {
                    return rhs.getMonth().compareTo(lhs.getMonth());
                }
            });
        }
    }

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
        SimpleDateFormat df = new SimpleDateFormat("MMM yyyy");
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.month_draft_item, null);
        } else {
            view = convertView;
        }

        TextView tv = view.findViewById(R.id.tv);
        TextView startedAt = view.findViewById(R.id.month_draft_started_at);
        MonthlyTally date = list.get(position);
        String text = df.format(date.getMonth());
        String startDate = MonthlyTalliesRepository.DF_DDMMYY.format(date.getCreatedAt());
        String started = activity.getString(R.string.started);
        tv.setText(text);
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        tv.setTag(text);
        startedAt.setText(started + " " + startDate);

        view.setOnClickListener(onMonthlyDraftClickListener);
        view.setTag(date.getMonth());

        return view;
    }
}
