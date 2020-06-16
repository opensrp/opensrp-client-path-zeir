package org.smartregister.uniceftunisia.adapter;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.uniceftunisia.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */
public class ExpandedListAdapter<K, L, T> extends BaseExpandableListAdapter {

    private final Context context;
    private LinkedHashMap<K, List<ItemData<L, T>>> map = new LinkedHashMap<>();
    private List<K> headers = new ArrayList<>();
    private final int headerLayout;
    private final int childLayout;
    private boolean isChildSelectable = true;


    public ExpandedListAdapter(Context context, LinkedHashMap<K, List<ItemData<L, T>>> map, int headerLayout, int childLayout) {
        this.context = context;
        if (map != null && !map.isEmpty()) {
            this.map = map;
            for (Map.Entry<K, List<ItemData<L, T>>> entry : map.entrySet()) {
                this.headers.add(entry.getKey());
            }
        }

        this.headerLayout = headerLayout;
        this.childLayout = childLayout;
    }

    public void setChildSelectable(boolean isChildSelectable) {
        this.isChildSelectable = isChildSelectable;
    }

    @Override
    public ItemData<L, T> getChild(int groupPosition, int childPosititon) {
        return this.map.get(this.headers.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View providedConvertView, ViewGroup parent) {
        View convertView = providedConvertView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(childLayout, null);
        }

        ItemData<L, T> childObject = getChild(groupPosition, childPosition);
        if (childObject != null) {
            String text = null;
            String details = null;

            if (childObject.getLabelData() instanceof String) {
                text = (String) getChild(groupPosition, childPosition).getLabelData();
            } else if (childObject.getLabelData() instanceof Pair) {
                Pair<String, String> pair = (Pair<String, String>) getChild(groupPosition, childPosition).getLabelData();
                text = pair.first;
                details = pair.second;
            } else if (childObject.getLabelData() instanceof Triple) {
                Triple<String, String, String> triple = (Triple<String, String, String>) getChild(groupPosition, childPosition).getLabelData();
                text = triple.getLeft();
                details = triple.getMiddle();
            }

            populateTitleView(convertView, text);
            populateDetailView(convertView, childObject, details);
        }

        setBottomDivider(groupPosition, childPosition, convertView);
        convertView.setTag(R.id.item_data, childObject.getTagData());

        return convertView;
    }

    private void setBottomDivider(int groupPosition, int childPosition, View convertView) {
        View dividerBottom = convertView.findViewById(R.id.adapter_divider_bottom);
        if (dividerBottom != null) {
            boolean lastChild = (getChildrenCount(groupPosition) - 1) == childPosition;
            if (lastChild) {
                dividerBottom.setVisibility(View.VISIBLE);
            } else {
                dividerBottom.setVisibility(View.GONE);
            }
        }
    }

    private void populateTitleView(View convertView, String text) {
        View tvView = convertView.findViewById(R.id.tv_sentMonthlyItem_title);
        if (tvView != null && text != null) {
            TextView tv = (TextView) tvView;
            tv.setText(text);
            convertView.setTag(text);
        }
    }

    private void populateDetailView(View convertView, ItemData<L, T> childObject, String details) {
        View detailView = convertView.findViewById(R.id.tv_sentMonthlyItem_details);
        if (detailView != null && details != null) {
            TextView detailTextView = (TextView) detailView;
            detailTextView.setText(details);

            detailTextView.setTextColor(context.getResources().getColor(R.color.black));
            if (childObject.isFinalized()) {
                detailTextView.setTextColor(context.getResources().getColor(R.color.bluetext));
            }
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.map
                .get(this.headers.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.headers.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.headers.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View providedConvertView, ViewGroup parent) {
        View convertView = providedConvertView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(headerLayout, null);
        }

        Object group = getGroup(groupPosition);

        if (group != null) {
            String header = null;

            if (group instanceof String) {
                header = group.toString();
            } else if (group instanceof Pair) {
                Pair<String, String> pair = (Pair<String, String>) group;
                header = pair.first;
            }

            View tvView = convertView.findViewById(R.id.tv_sentMonthlyHeader_title);
            if (tvView != null) {
                TextView tv = (TextView) tvView;
                tv.setText(header);
                convertView.setTag(header);
            }
        }

        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return isChildSelectable;
    }

    public static class ItemData<L, T> {
        private final L labelData;
        private final T tagData;
        private boolean finalized = false;

        public ItemData(L labelData, T tagData) {
            this.labelData = labelData;
            this.tagData = tagData;
        }

        public L getLabelData() {
            return labelData;
        }

        public T getTagData() {
            return tagData;
        }

        public void setFinalized(boolean finalized) {
            this.finalized = finalized;
        }

        public boolean isFinalized() {
            return finalized;
        }
    }
}
