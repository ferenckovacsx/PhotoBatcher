package com.ferenckovacsx.android.photobatcher;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity
        implements
        AddToExistingFragment.OnFragmentInteractionListener,
        AddNewFragment.OnFragmentInteractionListener {

    ImageView  deleteButton, addMoreButton;
    Boolean isToggled = false;
    int numberOfSelectedImages = 0;
    Button submitBatchButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        submitBatchButton = findViewById(R.id.done);
        deleteButton = findViewById(R.id.delete_button);
        addMoreButton = findViewById(R.id.add_more_button);

        if (numberOfSelectedImages == 0) {
            int color = Color.parseColor("#505050");
            deleteButton.setColorFilter(color);
            deleteButton.setEnabled(false);
        }

        //get current batch from db and save it to an ArrayList<ImageModel>
        final ArrayList<ImageModel> currentBatch;
        DatabaseTools databaseTools = new DatabaseTools(ResultActivity.this);
        currentBatch = databaseTools.getCurrentBatch();

        final ResultGridAdapter adapter = new ResultGridAdapter(ResultActivity.this, currentBatch, getImageViewSize());
        final GridView gridView = findViewById(R.id.gridview);

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Log.i("onItemLongClick", "is it checked: " + currentBatch.get(position).isChecked);

                if (!currentBatch.get(position).isChecked) {
                    currentBatch.get(position).setChecked(true);
                    numberOfSelectedImages += 1;

//
//                    for (int i = 0; i < currentBatch.size(); i++) {
//                        if (currentBatch.get(i).isChecked()) {
//                        }
//                    }

                    Log.i("onItemLongClick", "number of selected images: " + numberOfSelectedImages);

                    if (numberOfSelectedImages > 0) {
                        int color = Color.parseColor("#000000");
                        deleteButton.setColorFilter(color);
                        deleteButton.setEnabled(true);
                    }

                    adapter.notifyDataSetChanged();
                    Log.i("onItemLongClick", "is it checked: " + currentBatch.get(position).isChecked);

                    return true;
                }
                return true;

            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (numberOfSelectedImages > 0) {

                    if (currentBatch.get(position).isChecked) {
                        currentBatch.get(position).setChecked(false);
                        numberOfSelectedImages -= 1;
                    } else {
                        currentBatch.get(position).setChecked(true);
                        numberOfSelectedImages += 1;
                    }

                    adapter.notifyDataSetChanged();


                    if (numberOfSelectedImages == 0) {
                        int color = Color.parseColor("#505050");
                        deleteButton.setColorFilter(color);
                        deleteButton.setEnabled(false);

                    }
                }
            }
        });

        gridView.setAdapter(adapter);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < currentBatch.size(); i++) {
                    if (currentBatch.get(i).isChecked()) {
                        currentBatch.remove(i);
                    }
                }

                adapter.notifyDataSetChanged();

                int color = Color.parseColor("#505050");
                deleteButton.setColorFilter(color);
                deleteButton.setEnabled(false);
            }
        });


        submitBatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // custom dialog
                final Dialog dialog = new Dialog(ResultActivity.this);
                dialog.setContentView(R.layout.custom_dialog);

                ImageView createNewButton = dialog.findViewById(R.id.create_new_button);
                ImageView addToExistingButton = dialog.findViewById(R.id.add_to_existing_button);

                createNewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddNewFragment fragment = new AddNewFragment();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.add(R.id.container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        dialog.dismiss();
                    }
                });

                addToExistingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddToExistingFragment fragment = new AddToExistingFragment();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.add(R.id.container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        dialog.dismiss();
                    }
                });

                dialog.show();

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
