package com.ferenckovacsx.android.photobatcher;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity implements SubmitBatchFragment.OnFragmentInteractionListener{

    RecyclerView gridRecyclerView;
    GridLayoutManager gridLayoutManager;
    Toolbar toolbar;
    ImageView submitBatchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

//        toolbar = findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);

        submitBatchButton = findViewById(R.id.done);

        //get current batch from db and save it to an ArrayList<ImageModel>
        ArrayList<ImageModel> currentBatch;
        DatabaseTools databaseTools = new DatabaseTools(ResultActivity.this);
        currentBatch = databaseTools.getCurrentBatch();

//        gridRecyclerView = findViewById(R.id.galleryRecyclerView);
////        gridRecyclerView.setHasFixedSize(true);
//        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
//        gridRecyclerView.setLayoutManager(gridLayoutManager);
//        GalleryAdapter adapter = new GalleryAdapter(getApplicationContext(), currentBatch, getImageViewSize());
//        gridRecyclerView.setAdapter(adapter);

        ResultGridAdapter adapter = new ResultGridAdapter(ResultActivity.this, currentBatch, getImageViewSize());
        GridView gridView = findViewById(R.id.gridview);
        gridView.setAdapter(adapter);

        submitBatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SubmitBatchFragment fragment = new SubmitBatchFragment();
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.add(R.id.container, fragment);
//                transaction.addToBackStack(null);
//                transaction.commit();

                Intent intent = new Intent(ResultActivity.this, SubmitBatchActivity.class);
                startActivity(intent);
            }
        });

    }


    //This method measures screen height and width and sets the size of the cards accordingly.
    //There are 4 columns, so a card should be 1/4 of the screen WIDTH in portrait mode and 1/4 of HEIGHT in landscape mode.
    int getImageViewSize() {

        int cardImageViewSize;
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            cardImageViewSize = (outMetrics.widthPixels / 3) - 20;
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
