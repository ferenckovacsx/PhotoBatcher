package com.ferenckovacsx.photobatcher.tools;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.ferenckovacsx.photobatcher.R;
import com.ferenckovacsx.photobatcher.pojo.BatchPOJO;

import java.util.ArrayList;
import java.util.List;

public class ACTVadapter extends ArrayAdapter<BatchPOJO> {
    private final Context mContext;
    private final List<BatchPOJO> mDepartments;
    private final List<BatchPOJO> mDepartmentsAll;
    private final int mLayoutResourceId;

    public ACTVadapter(Context context, int resource, List<BatchPOJO> departments) {
        super(context, resource, departments);
        this.mContext = context;
        this.mLayoutResourceId = resource;
        this.mDepartments = new ArrayList<>(departments);
        this.mDepartmentsAll = new ArrayList<>(departments);
    }

    public int getCount() {
        return mDepartments.size();
    }

    public BatchPOJO getItem(int position) {
        return mDepartments.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(mLayoutResourceId, parent, false);
            }
            BatchPOJO department = getItem(position);
            TextView name = (TextView) convertView.findViewById(R.id.actv_item_textview);
            name.setText(department.batchID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public String convertResultToString(Object resultValue) {
                return ((BatchPOJO) resultValue).batchID;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<BatchPOJO> departmentsSuggestion = new ArrayList<>();
                if (constraint != null) {
                    for (BatchPOJO department : mDepartmentsAll) {
                        if (department.batchID.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            departmentsSuggestion.add(department);
                        }
                    }
                    filterResults.values = departmentsSuggestion;
                    filterResults.count = departmentsSuggestion.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mDepartments.clear();
                if (results != null && results.count > 0) {
                    // avoids unchecked cast warning when using mDepartments.addAll((ArrayList<BatchPOJO>) results.values);
                    for (Object object : (List<?>) results.values) {
                        if (object instanceof BatchPOJO) {
                            mDepartments.add((BatchPOJO) object);
                        }
                    }
                    notifyDataSetChanged();
                } else if (constraint == null) {
                    // no filter, add entire original list back in
                    mDepartments.addAll(mDepartmentsAll);
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}