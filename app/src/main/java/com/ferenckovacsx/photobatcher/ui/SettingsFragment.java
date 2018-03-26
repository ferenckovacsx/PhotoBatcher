package com.ferenckovacsx.photobatcher.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

import static android.content.Context.MODE_PRIVATE;


public class SettingsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    SharedPreferences sharedPreferences;

    TextView sheetIdTextView;
    EditText sheetIdInputEditText;
    String sheetId;
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
        backButton = settingsView.findViewById(R.id.settings_back_imageview);

        sharedPreferences = getActivity().getSharedPreferences("sheetIdPref", MODE_PRIVATE);
        sheetId = sharedPreferences.getString("sheetID", "1EjMmkgbJVtekL0j8JTtmFdYAIO38kRzA_27IAznaOE0");

        sheetIdTextView.setText(sheetId);

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

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("sheetID", sheetIdString);
                                editor.apply();

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
}
