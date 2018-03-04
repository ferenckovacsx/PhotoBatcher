package com.ferenckovacsx.android.photobatcher;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ferenckovacsx on 2018-02-23.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private ArrayList<ImageModel> galleryList;
    private Context context;
    static int cardImageViewSize;

    GalleryAdapter(Context context, ArrayList<ImageModel> galleryList, int cardImageViewSize) {
        this.galleryList = galleryList;
        this.context = context;
        GalleryAdapter.cardImageViewSize = cardImageViewSize;
    }

    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GalleryAdapter.ViewHolder viewHolder, int i) {
        viewHolder.title.setText(galleryList.get(i).getImageName());
        viewHolder.cardImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Uri uri = Uri.fromFile(new File(galleryList.get(i).getImagePath()));

//        Bitmap bitmap = BitmapFactory.decodeFile(galleryList.get(i).getImagePath());
        Picasso.with(context).load(uri).into(viewHolder.cardImageView);
//        viewHolder.cardImageView.setImageBitmap(bitmap);

    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView cardImageView;

        public ViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.gallery_item_textview);
            cardImageView = view.findViewById(R.id.gallery_item_imageview);

            android.view.ViewGroup.LayoutParams layoutParams = cardImageView.getLayoutParams();
            layoutParams.width = cardImageViewSize;
            layoutParams.height = cardImageViewSize;
            cardImageView.setLayoutParams(layoutParams);
            cardImageView.requestLayout();
        }
    }
}