package com.ferenckovacsx.android.photobatcher.tools;

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

import com.ferenckovacsx.android.photobatcher.pojo.ImagePOJO;

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

    Context context;

    public DatabaseTools(Context context) {

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

    public void insertNewScore(String name, String filePath) {

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

    public ArrayList<ImagePOJO> getCurrentBatch() {
        ArrayList<ImagePOJO> scoreEntryList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + DATABASE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ImagePOJO scoreEntry = new ImagePOJO();
                scoreEntry.setImageName(cursor.getString(0));
                scoreEntry.setImagePath(cursor.getString(1));
                scoreEntryList.add(scoreEntry);

            } while (cursor.moveToNext());

        }

        db.close();
        cursor.close();
        return scoreEntryList;
    }

    public void clearTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + DATABASE_NAME);
    }

    public void clearEntry(String fileName, String filePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_NAME, COLUMN_IMAGE_NAME + "=? and "+ COLUMN_IMAGE_PATH + "=?", new String[]{fileName, filePath});
    }
}
