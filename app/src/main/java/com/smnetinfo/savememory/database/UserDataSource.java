package com.smnetinfo.savememory.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.smnetinfo.savememory.extras.WebConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Siva on 3/8/16.
 */

public class UserDataSource implements WebConstants {

    private static UserDataSource userDataSource;
    private static SaveMemorySqliteHelper saveMemorySqliteHelper;
    private SQLiteDatabase sqLiteDatabase;
    private String allColumns[];

    public static UserDataSource sharedInstance(Context context) {
        if (userDataSource == null) {
            saveMemorySqliteHelper = new SaveMemorySqliteHelper(context);
            userDataSource = new UserDataSource();
            userDataSource.allColumns = new String[1];
            userDataSource.allColumns[0] = DATA;
            userDataSource.open();
        }
        return userDataSource;
    }

    private void open() throws SQLiteException {
        sqLiteDatabase = saveMemorySqliteHelper.getWritableDatabase();
    }

    private void close() {
        sqLiteDatabase.close();
    }

    public void insertUserData(String userData) {
        open();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATA, userData);

        try {
            sqLiteDatabase.insert(TABLE_USER, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteUserData() {
        open();
        sqLiteDatabase.delete(TABLE_USER, null, null);
    }

    public void updateUserName(String name) {
        open();
        try {
            JSONObject jsonObject = getUserData();
            jsonObject.put(NAME, name);

            ContentValues contentValues = new ContentValues();
            contentValues.put(DATA, jsonObject.toString());

            sqLiteDatabase.update(TABLE_USER, contentValues, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject getUserData() {
        open();
        try (Cursor selected = sqLiteDatabase.query(TABLE_USER, allColumns, null, null, null, null, null)) {
            if (selected != null && selected.getCount() > 0) {
                selected.moveToFirst();
                return new JSONObject(selected.getString(0));
            } else {
                return new JSONObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public String getUserId() {
        try {
            return getUserData().getString(ID);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
