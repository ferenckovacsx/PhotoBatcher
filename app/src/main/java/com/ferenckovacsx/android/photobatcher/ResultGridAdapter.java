package com.ferenckovacsx.android.photobatcher;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ferenckovacsx on 2018-02-26.
 */

public class ResultGridAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ImageModel> dataSet;
    static int cardImageViewSize;

    ImageView galleryImageView;
    TextView galleryTextView;

    private static class ViewHolder {


    }

    public ResultGridAdapter(Context context, ArrayList<ImageModel> dataSet, int cardImageViewSize) {
        this.context = context;
        this.dataSet = dataSet;
        ResultGridAdapter.cardImageViewSize = cardImageViewSize;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return dataSet.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);
            gridView = inflater.inflate(R.layout.gallery_item_layout, parent, false);

            GridView grid = (GridView) parent;
            int size = grid.getRequestedColumnWidth();

        } else {

            gridView = convertView;
        }

        galleryImageView = gridView.findViewById(R.id.gallery_item_imageview);
        galleryTextView = gridView.findViewById(R.id.gallery_item_textview);

        android.view.ViewGroup.LayoutParams layoutParams = galleryImageView.getLayoutParams();
        layoutParams.width = cardImageViewSize;
        layoutParams.height = cardImageViewSize;
        galleryImageView.setLayoutParams(layoutParams);
        galleryImageView.requestLayout();


        galleryTextView.setText(dataSet.get(position).getImageName());

        Uri uri = Uri.fromFile(new File(dataSet.get(position).getImagePath()));
        Picasso.with(context).load(uri).into(galleryImageView);

        return gridView;
    }
}
