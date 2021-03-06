package com.ferenckovacsx.photobatcher.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ferenckovacsx.photobatcher.tools.DatabaseTools;
import com.ferenckovacsx.photobatcher.pojo.ImagePOJO;
import com.ferenckovacsx.photobatcher.R;
import com.ferenckovacsx.photobatcher.tools.GalleryGridAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class GalleryActivity extends AppCompatActivity
        implements
        AddToExistingFragment.OnFragmentInteractionListener,
        AddNewFragment.OnFragmentInteractionListener,
        ScrollableFragment.OnFragmentInteractionListener {

    final String TAG = "GALLERY";

    ImageView deleteButton, addMoreButton;
    int numberOfSelectedImages = 0;
    Button submitBatchButton;
    ArrayList<ImagePOJO> currentBatch;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        submitBatchButton = findViewById(R.id.done);
        deleteButton = findViewById(R.id.delete_button);
        addMoreButton = findViewById(R.id.add_more_button);

        if (numberOfSelectedImages == 0) {
            int color = Color.parseColor("#505050");
            deleteButton.setColorFilter(color);
            deleteButton.setEnabled(false);
        }

        //get current batch from db and save it to an ArrayList<ImagePOJO>
        final DatabaseTools databaseTools = new DatabaseTools(GalleryActivity.this);
        currentBatch = databaseTools.getCurrentBatch();


        final GalleryGridAdapter adapter = new GalleryGridAdapter(GalleryActivity.this, currentBatch, getImageViewSize());
        final GridView gridView = findViewById(R.id.gridview);

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                if (!currentBatch.get(position).isChecked) {
                    currentBatch.get(position).setChecked(true);
                    numberOfSelectedImages += 1;

                    if (numberOfSelectedImages > 0) {
                        int color = Color.parseColor("#000000");
                        deleteButton.setColorFilter(color);
                        deleteButton.setEnabled(true);
                    }

                    adapter.notifyDataSetChanged();

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
                } else {
                    String filePath = currentBatch.get(position).getImagePath();

                    ScrollableFragment scrollableFragment = new ScrollableFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("filepath", filePath);
                    scrollableFragment.setArguments(bundle);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.fragment_container, scrollableFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

        gridView.setAdapter(adapter);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Iterator<ImagePOJO> i = currentBatch.iterator();
                while (i.hasNext()) {
                    ImagePOJO s = i.next();

                    if (s.isChecked()) {
                        databaseTools.clearEntry(s.getImageName(), s.getImagePath());

                        File fileToDelete = new File(s.getImagePath());
                        fileToDelete.delete();

                        i.remove();

                    }
                }

                adapter.notifyDataSetChanged();

                if (currentBatch.size() < 1) {
                    submitBatchButton.setEnabled(false);
                }

                numberOfSelectedImages = 0;

                int color = Color.parseColor("#505050");
                deleteButton.setColorFilter(color);
                deleteButton.setEnabled(false);
            }
        });


        submitBatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // custom dialog
                final Dialog dialog = new Dialog(GalleryActivity.this);
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_select_mode);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView createNewButton = dialog.findViewById(R.id.create_new_button);
                TextView addToExistingButton = dialog.findViewById(R.id.add_to_existing_button);

                createNewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddNewFragment fragment = new AddNewFragment();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.add(R.id.fragment_container, fragment);
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
                        transaction.add(R.id.fragment_container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        addMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(GalleryActivity.this, CameraActivity.class);
                startActivity(cameraIntent);
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
