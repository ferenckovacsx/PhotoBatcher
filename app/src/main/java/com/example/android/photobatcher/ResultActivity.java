package com.example.android.photobatcher;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    RecyclerView gridRecyclerView;
    GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //get current batch from db and save it to an ArrayList<ImageModel>
        ArrayList<ImageModel> currentBatch;
        DatabaseTools databaseTools = new DatabaseTools(ResultActivity.this);
        currentBatch = databaseTools.getCurrentBatch();

        gridRecyclerView = findViewById(R.id.galleryRecyclerView);
//        gridRecyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        gridRecyclerView.setLayoutManager(gridLayoutManager);
        GalleryAdapter adapter = new GalleryAdapter(getApplicationContext(), currentBatch, getImageViewSize());
        gridRecyclerView.setAdapter(adapter);
    }

    private ArrayList<ImageModel> prepareData() {

        ArrayList<ImageModel> listOfImages = new ArrayList<>();
        return listOfImages;
    }

    //This method measures screen height and width and sets the size of the cards accordingly.
    //There are 4 columns, so a card should be 1/4 of the screen WIDTH in portrait mode and 1/4 of HEIGHT in landscape mode.
    int getImageViewSize() {

        int cardImageViewSize;
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            cardImageViewSize = outMetrics.widthPixels / 3;
        } else {
            cardImageViewSize = (outMetrics.heightPixels - getStatusBarHeight() - 10) / 3;
        }
        return cardImageViewSize;
    }

    //Status bar height should be substracted from the screen height in case of landscape mode.
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
