package com.ferenckovacsx.photobatcher.ui;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.ferenckovacsx.photobatcher.pojo.BatchPOJO;
import com.ferenckovacsx.photobatcher.tools.ACTVadapter;
import com.ferenckovacsx.photobatcher.tools.DatabaseTools;
import com.ferenckovacsx.photobatcher.pojo.ImagePOJO;
import com.ferenckovacsx.photobatcher.R;
import com.ferenckovacsx.photobatcher.tools.Utilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.ferenckovacsx.photobatcher.tools.Utilities.getFormattedDate;

public class AddToExistingFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    private OnFragmentInteractionListener mListener;

    ProgressDialog mProgress;
    AutoCompleteTextView selectBatchACTV;
    EditText uploadDateEditText, modifiedEditText, imageCountEditText, noteEditText;
    ImageView backButton;
    Button submitButton;


    GoogleAccountCredential mCredential;

    final String TAG = "ADDtoEXISTING";
    String requestType;
    String spreadsheetID;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS};

    com.ferenckovacsx.photobatcher.tools.ACTVadapter ACTVadapter;
    ArrayList<BatchPOJO> listOfBatches;
    ArrayList<ImagePOJO> currentBatch;

    DatabaseTools databaseTools;

    SharedPreferences sharedPreferences;

    String rangeToUpdate, rootFolder;
    String originalNote; //append new note to existing note (if exists)
    int originalImageCount; //new imagecount = original + current

    public AddToExistingFragment() {
    }


    public static AddToExistingFragment newInstance(String param1, String param2) {
        AddToExistingFragment fragment = new AddToExistingFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View submitBatchView = inflater.inflate(R.layout.fragment_add_to_existing, container, false);

        submitButton = submitBatchView.findViewById(R.id.submit_existing);
        selectBatchACTV = submitBatchView.findViewById(R.id.select_batch_ACTV);
        uploadDateEditText = submitBatchView.findViewById(R.id.upload_date_edittext);
        modifiedEditText = submitBatchView.findViewById(R.id.last_modified_date_edittext);
        noteEditText = submitBatchView.findViewById(R.id.add_note_edittext);
        imageCountEditText = submitBatchView.findViewById(R.id.add_to_existing_image_count_edittext);
        backButton = submitBatchView.findViewById(R.id.add_to_existing_back_iv);

        databaseTools = new DatabaseTools(getContext());
        currentBatch = databaseTools.getCurrentBatch();

        sharedPreferences = getActivity().getSharedPreferences("settingsPref", MODE_PRIVATE);
        spreadsheetID = sharedPreferences.getString("sheetID", "1EjMmkgbJVtekL0j8JTtmFdYAIO38kRzA_27IAznaOE0");
        rootFolder = sharedPreferences.getString("sourceFolder", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/NE-TEKVILL");


        imageCountEditText.setText(String.valueOf(currentBatch.size()));

        uploadDateEditText.setClickable(false);
        uploadDateEditText.setFocusable(false);
        uploadDateEditText.setFocusableInTouchMode(false);

        modifiedEditText.setClickable(false);
        modifiedEditText.setFocusable(false);
        modifiedEditText.setFocusableInTouchMode(false);

        submitButton.setEnabled(false);

        mProgress = new ProgressDialog(getContext());
        mProgress.setMessage("Adatok lekérdezése...");

        listOfBatches = new ArrayList<>();

        requestType = "GET";
//        startApiRequest();

        Log.i(TAG, "list of batches onCreateView: " + listOfBatches.size());

        if (listOfBatches.size() > 1) {
            ACTVadapter = new ACTVadapter(getContext(), R.layout.actv_row_item, listOfBatches);
            selectBatchACTV.setAdapter(ACTVadapter);

            selectBatchACTV.requestFocus();

            selectBatchACTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectBatchACTV.showDropDown();
                }
            });


            selectBatchACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    submitButton.setEnabled(true);

                    requestType = "GETROW";
                    startApiRequest();

                    originalImageCount = ACTVadapter.getItem(position).imageCount;
                    originalNote = ACTVadapter.getItem(position).note;

                    selectBatchACTV.setText(ACTVadapter.getItem(position).batchID);
                    uploadDateEditText.setText(ACTVadapter.getItem(position).uploadDate);
                    modifiedEditText.setText(ACTVadapter.getItem(position).lastModifiedDate);
                }
            });
        } else {
            selectBatchACTV.setFocusable(false);
            selectBatchACTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Adatbázis üres!", Toast.LENGTH_LONG).show();
                }
            });
        }


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "back onclick");
                getActivity().onBackPressed();
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestType = "SET";
                startApiRequest();
            }
        });


        return submitBatchView;
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
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void startApiRequest() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Log.i(TAG, "No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute(requestType);
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                getContext(), Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getActivity().getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                startApiRequest();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    public void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Log.i(TAG,
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    startApiRequest();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        startApiRequest();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    startApiRequest();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(getContext());
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(getContext());
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                getActivity(),
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<String, Void, String> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(transport, jsonFactory, credential).setApplicationName("Google Sheets API Android Quickstart").build();
        }

        /**
         * Background task to call Google Sheets API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected String doInBackground(String... params) {
            try {

//                String requestType = params[0];

                Log.i(TAG, "doInBackground request type: " + requestType);

                if (requestType.equals("SET")) {

                    Log.i(TAG, "SETDATA original count: " + originalImageCount);
                    Log.i(TAG, "SETDATA original note: " + originalNote);

                    String batchName = selectBatchACTV.getText().toString();
                    String uploadDate = getFormattedDate();
                    String originalUploadDate = uploadDateEditText.getText().toString();

                    //add original imagecount to new imagecount
                    int imageCount = Integer.parseInt(imageCountEditText.getText().toString()) + originalImageCount;

                    String note = noteEditText.getText().toString();
                    if (!note.equals("")) {
                        if (originalNote != null) {
                            note = originalNote + "\n\nÚj megjegyzés (hozzáadva: " + uploadDate + "): \n" + noteEditText.getText().toString();
                        } else {
                            note = "\n\nÚj megjegyzés (hozzáadva: " + uploadDate + "): \n" + noteEditText.getText().toString();
                        }
                    }

                    return setData(batchName, imageCount, originalUploadDate, note, uploadDate);
                } else if (requestType.equals("GET")) {
                    getBatchesFromApi();
                    return "getData()";
                } else if (requestType.equals("GETROW")) {
                    getRowToUpdate();
                    return "getRow()";
                }
                return "";


            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return "doInBackground error";
            }
        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         *
         * @return List of names and majors
         * @throws IOException
         */
        private String getBatchesFromApi() throws IOException {

            Log.i(TAG, "getData");

//            String spreadsheetId = "1EjMmkgbJVtekL0j8JTtmFdYAIO38kRzA_27IAznaOE0";
            String range = "A2:E";

            ValueRange response = this.mService.spreadsheets().values().get(spreadsheetID, range).execute();

            List<List<Object>> values = response.getValues();

            Log.i(TAG, "getValues: " + values.toString());

            if (values != null) {
                for (List row : values) {

                    //0 - ID / 1 - count / 2 - date / 3 - note / 4 - modified
                    BatchPOJO selectedBatch = new BatchPOJO();

                    try {
                        selectedBatch.setBatchID(row.get(0).toString());
                    } catch (Exception e) {
                        Log.i(TAG, "batchID Eexception");
                    }

                    try {
                        selectedBatch.setImageCount(Integer.valueOf(row.get(1).toString()));
                    } catch (Exception e) {
                        Log.i(TAG, "count Eexception");
                    }

                    try {
                        selectedBatch.setUploadDate(row.get(2).toString());
                    } catch (Exception e) {
                        Log.i(TAG, "uploaddate Eexception");
                    }

                    try {
                        selectedBatch.setNote(row.get(3).toString());
                    } catch (Exception e) {
                        Log.i(TAG, "note Eexception");
                    }

                    try {
                        selectedBatch.setLastModifiedDate(row.get(4).toString());
                    } catch (Exception e) {
                        Log.i(TAG, "lastmod Eexception");
                    }

                    listOfBatches.add(selectedBatch);
                }
            }

            ACTVadapter.notifyDataSetChanged();

            Log.i(TAG, "list of batches size: " + listOfBatches.size());

            return response.toString();
        }


        private String getRowToUpdate() throws IOException {

            Log.i(TAG, "getRow");

//            String spreadsheetId = "1EjMmkgbJVtekL0j8JTtmFdYAIO38kRzA_27IAznaOE0";
            String range = "A2:E";
            String selectedBatchID = selectBatchACTV.getText().toString();

            ValueRange response = this.mService.spreadsheets().
                    values().get(spreadsheetID, range).execute();

            List<List<Object>> values = response.getValues();

            Log.i(TAG, "getValues: " + values.toString());

            if (response.getRange().contains(selectedBatchID)) {
                Log.i(TAG, "IT'S A MATCH: " + response.getRange());
            }

            int i = 0;
            if (values != null) {
                for (List row : values) {
                    i += 1;
                    //0 - ID / 1 - count / 2 - date / 3 - note / 4 - modified
                    if (row.get(0).equals(selectedBatchID)) {
                        Log.i(TAG, "IT'S A MATCH! i= " + i);
                        rangeToUpdate = "A" + (i + 1) + ":E" + (i + 1);
                        Log.i(TAG, "range to update: " + rangeToUpdate);
                    }
                }
            }

            return rangeToUpdate;
        }


        private String setData(String batchID, int numberOfImages, String dateAdded, String note, String dateModified) throws IOException {

            Log.i(TAG, "setData");

//            String spreadsheetId = "1EjMmkgbJVtekL0j8JTtmFdYAIO38kRzA_27IAznaOE0";

            // The A1 notation of a range to search for a logical table of data.
            // Values will be appended after the last row of the table.
            String range = rangeToUpdate;

            // How the input data should be interpreted.
            String valueInputOption = "USER_ENTERED";

            //for the values that you want to input, create a list of object lists
            List<List<Object>> rowToUpdate = new ArrayList<>();

            //Where each value represents the list of objects that is to be written to a range
            //I simply want to edit a single row, so I use a single list of objects
            List<Object> rowData = new ArrayList<>();
            rowData.add(batchID);
            rowData.add(numberOfImages);
            rowData.add(dateAdded);
            rowData.add(note);
            rowData.add(dateModified);

            //add data to row
            rowToUpdate.add(rowData);

            ValueRange requestBody = new ValueRange().setValues(rowToUpdate);


            Sheets.Spreadsheets.Values.Update request = mService.spreadsheets().values().update(spreadsheetID, range, requestBody);
            request.setValueInputOption(valueInputOption);

            UpdateValuesResponse response = request.execute();

            Log.i(TAG, "append response: " + response.toString());

            return "SET";
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(String output) {
            mProgress.dismiss();
            if (output == null) {
                Log.i(TAG, "No results returned.");
            } else if (output.equals("SET")) {

                //move files from temporary folder to final location
                for (int i = 0; i < currentBatch.size(); i++) {
                    Log.i(TAG, currentBatch.get(i).getImagePath());
                    Log.i(TAG, currentBatch.get(i).getImageName());

                    String newImageLocation = rootFolder + "/" + selectBatchACTV.getText().toString() + "/" + currentBatch.get(i).getImageName();

                    try {
                        Utilities.moveFile(currentBatch.get(i).getImagePath(), newImageLocation, selectBatchACTV.getText().toString());

                        //notify external memory to scan for new image
                        final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        final Uri contentUri = Uri.parse(newImageLocation);
                        scanIntent.setData(contentUri);
                        getActivity().sendBroadcast(scanIntent);

                    } catch (IOException io) {
                        Log.e(TAG, "There was an error in moving the files");
                        io.printStackTrace();
                    }
                }


                //clear temp from database
                databaseTools.clearTable();

                // custom dialog
                final Dialog dialog = new Dialog(getContext());
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_success);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                Button okButton = dialog.findViewById(R.id.ok_button);

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent mainActivityIntent = new Intent(getContext(), MainActivity.class);
                        startActivity(mainActivityIntent);
                        getActivity().finish();
                    }
                });

                dialog.show();

                Log.i(TAG, "Response: " + output);
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            REQUEST_AUTHORIZATION);
                } else {
                    Log.i(TAG, "The following error occurred:\n" + mLastError.getMessage());
//                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
//                    alertDialog.setTitle("Hiba");
//                    alertDialog.setMessage(mLastError.getMessage());
//                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//                    alertDialog.show();
                }
            } else {
                Log.i(TAG, "Request cancelled.");
            }
        }
    }
}
