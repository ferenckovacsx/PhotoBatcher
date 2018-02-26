package com.example.android.photobatcher;

/**
 * Created by ferenckovacsx on 2018-02-23.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ferenckovacsx on 2018-01-27.
 */

public class DatabaseTools extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "batchDB";
    private static final String COLUMN_IMAGE_NAME = "Name";
    private static final String COLUMN_IMAGE_PATH = "Path";

    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + DATABASE_NAME + "(Name TEXT NULL, Path TEXT NULL)";
    private static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + DATABASE_NAME;

    DatabaseTools(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e("DBTOOLS", "constructor called");

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.e("DBTOOLS", "table was created");
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DELETE_TABLE);
        onCreate(sqLiteDatabase);
    }

    void insertNewScore(String name, String filePath) {

        Log.e("DBTOOLS", "new score inserted");

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        try {
            contentValues.put(COLUMN_IMAGE_NAME, name);
            contentValues.put(COLUMN_IMAGE_PATH, filePath);
            db.insert(DATABASE_NAME, null, contentValues);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    ArrayList<ImageModel> getCurrentBatch() {
        ArrayList<ImageModel> scoreEntryList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + DATABASE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ImageModel scoreEntry = new ImageModel();
                scoreEntry.setImageName(cursor.getString(0));
                scoreEntry.setImagePath(cursor.getString(1));
                scoreEntryList.add(scoreEntry);

            } while (cursor.moveToNext());

        }

        db.close();
        cursor.close();
        return scoreEntryList;
    }
}
