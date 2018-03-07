package com.ferenckovacsx.android.photobatcher.tools;

/**
 * Created by ferenckovacsx on 2018-03-04.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ferenckovacsx.android.photobatcher.pojo.BatchPOJO;
import com.ferenckovacsx.android.photobatcher.R;

import java.util.ArrayList;

public class CustomACTVAdapter extends ArrayAdapter<BatchPOJO> {

    final String TAG = "AutocompleteCustomArrayAdapter.java";

    Context mContext;
    int layoutResourceId;
    ArrayList<BatchPOJO> data = null;

    public CustomACTVAdapter(Context mContext, int layoutResourceId, ArrayList<BatchPOJO> data) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {

            if (convertView == null) {
                // inflate the layout
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }

            // object item based on the position
            BatchPOJO objectItem = data.get(position);

            // get the TextView and then set the text (item name) and tag (item ID) values
            TextView textViewItem = convertView.findViewById(R.id.textViewItem);
            textViewItem.setText(objectItem.batchID);


        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;

    }
}
