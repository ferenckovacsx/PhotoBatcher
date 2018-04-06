package com.ferenckovacsx.photobatcher.tools;

import android.content.Context;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.ferenckovacsx.photobatcher.pojo.ImagePOJO;
import com.ferenckovacsx.photobatcher.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ferenckovacsx on 2018-02-26.
 */

public class GalleryGridAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ImagePOJO> dataSet;
    static int cardImageViewSize;
    ImageView galleryImageView;
    FrameLayout overlay;

    private static class ViewHolder {


    }

    public GalleryGridAdapter(Context context, ArrayList<ImagePOJO> dataSet, int cardImageViewSize) {
        this.context = context;
        this.dataSet = dataSet;
        GalleryGridAdapter.cardImageViewSize = cardImageViewSize;
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
        overlay = gridView.findViewById(R.id.grid_item_overlay);

        android.view.ViewGroup.LayoutParams layoutParams = galleryImageView.getLayoutParams();
        layoutParams.width = cardImageViewSize;
        layoutParams.height = cardImageViewSize;
        galleryImageView.setLayoutParams(layoutParams);
        galleryImageView.requestLayout();

        if (dataSet.get(position).isChecked()) {
            galleryImageView.setPadding(15, 15, 15, 15);
            overlay.setForeground(ContextCompat.getDrawable(context, R.drawable.grid_item_overlay));
        } else {
            galleryImageView.setPadding(0, 0, 0, 0);
            overlay.setForeground(null);
        }


        Uri uri = Uri.fromFile(new File(dataSet.get(position).getImagePath()));
        Picasso.with(context).load(uri).resize(cardImageViewSize, cardImageViewSize).centerCrop().into(galleryImageView);

        return gridView;
    }
}
