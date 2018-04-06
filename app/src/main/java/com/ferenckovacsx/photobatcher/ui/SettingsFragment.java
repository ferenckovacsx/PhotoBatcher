package com.ferenckovacsx.photobatcher.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ferenckovacsx.photobatcher.R;
import com.ferenckovacsx.photobatcher.tools.dirchooser.DirectoryChooserActivity;
import com.ferenckovacsx.photobatcher.tools.dirchooser.DirectoryChooserConfig;

import static android.content.Context.MODE_PRIVATE;


public class SettingsFragment extends Fragment {

    static final int DIRECTORY_PICKER_REQUESTCODE = 100;

    private OnFragmentInteractionListener mListener;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    TextView sheetIdTextView, sourceFolderTextView;
    EditText sheetIdInputEditText;
    String sheetId, sourceFolder, defaultSourceFolder;
    ImageView backButton;

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);

        sheetIdTextView = settingsView.findViewById(R.id.sheet_id_textview);
        sourceFolderTextView = settingsView.findViewById(R.id.source_folder_textview);
        backButton = settingsView.findViewById(R.id.settings_back_imageview);

        defaultSourceFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/NE-TEKVILL";

        sharedPreferences = getActivity().getSharedPreferences("settingsPref", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        sheetId = sharedPreferences.getString("sheetID", "1EjMmkgbJVtekL0j8JTtmFdYAIO38kRzA_27IAznaOE0");
        sourceFolder = sharedPreferences.getString("sourceFolder", defaultSourceFolder);

        sheetIdTextView.setText(sheetId);
        sourceFolderTextView.setText(sourceFolder);

        sheetIdTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetIdInputEditText = new EditText(getContext());
                sheetIdInputEditText.setInputType(InputType.TYPE_CLASS_TEXT);

                final AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setView(sheetIdInputEditText)
                        .setTitle("Sheet ID beállítása")
                        .setPositiveButton("Mentés", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String sheetIdString;
                                sheetIdString = sheetIdInputEditText.getText().toString();
                                sheetIdTextView.setText(sheetIdString);

                                sharedPreferencesEditor.putString("sheetID", sheetIdString);
                                sharedPreferencesEditor.apply();

                                Log.e("DIALOG", "edittext value: " + sheetIdString);
                            }
                        })
                        .setNegativeButton("Vissza", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setNeutralButton("Alapértelmezett", null)
                        .create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        Button button = (dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                        button.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                sheetIdInputEditText.setText("1EjMmkgbJVtekL0j8JTtmFdYAIO38kRzA_27IAznaOE0");
                            }
                        });
                    }
                });
                dialog.show();
            }
        });

        sourceFolderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final Intent chooserIntent = new Intent(getContext(), DirectoryChooserActivity.class);

                final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                        .newDirectoryName("NE-TEKVILL")
                        .allowReadOnlyDirectory(false)
                        .allowNewDirectoryNameModification(true)
                        .build();

                chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config);
                startActivityForResult(chooserIntent, DIRECTORY_PICKER_REQUESTCODE);

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return settingsView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DIRECTORY_PICKER_REQUESTCODE) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                sourceFolder = data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);
                Log.e("SETTINGS", "selected directory: " + sourceFolder);
                sourceFolderTextView.setText(sourceFolder);
                sharedPreferencesEditor.putString("sourceFolder", sourceFolder);
                sharedPreferencesEditor.apply();

            } else {
                // Nothing selected
            }
        }
    }
}
