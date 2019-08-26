package com.smnetinfo.savememory.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.smnetinfo.savememory.extras.WebConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Siva on 3/8/16.
 */

public class PostsDataSource implements WebConstants {

    private static PostsDataSource postsDataSource;
    private static SaveMemorySqliteHelper saveMemorySqliteHelper;
    private SQLiteDatabase sqLiteDatabase;
    private String allColumns[];

    public static PostsDataSource sharedInstance(Context context) {
        if (postsDataSource == null) {
            saveMemorySqliteHelper = new SaveMemorySqliteHelper(context);
            postsDataSource = new PostsDataSource();
            postsDataSource.allColumns = new String[2];
            postsDataSource.allColumns[0] = ID;
            postsDataSource.allColumns[1] = DATA;
            postsDataSource.open();
        }
        return postsDataSource;
    }

    private void open() throws SQLiteException {
        sqLiteDatabase = saveMemorySqliteHelper.getWritableDatabase();
    }

    private void close() {
        sqLiteDatabase.close();
    }

    public void insertPostData(String id, String postData) {
        open();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(DATA, postData);

        try {
            sqLiteDatabase.insert(TABLE_POSTS, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deletePost(String id) {
        open();
        String where = ID + " ='" + id + "'";
        sqLiteDatabase.delete(TABLE_POSTS, where, null);
    }

    public void updatePostData(String id, String postData) {
        open();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(DATA, postData);

        String where = ID + " ='" + id + "'";

        try {
            sqLiteDatabase.update(TABLE_POSTS, contentValues, where, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deletePostData() {
        open();
        sqLiteDatabase.delete(TABLE_POSTS, null, null);
    }

    public JSONObject getPostData(String id) {
        open();
        String where = ID + " ='" + id + "'";
        try (Cursor selected = sqLiteDatabase.query(TABLE_POSTS, allColumns, where, null, null, null, null)) {
            if (selected != null && selected.getCount() > 0) {
                selected.moveToFirst();
                return new JSONObject(selected.getString(1));
            } else {
                return new JSONObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public List<JSONObject> getPosts() {
        open();
        String lastDate = "";
        List<JSONObject> jsonObjectList = new ArrayList<>();
        try (Cursor selected = sqLiteDatabase.query(TABLE_POSTS, allColumns, null, null, null, null, null)) {
            if (selected != null && selected.getCount() > 0) {
                selected.moveToFirst();
                for (int i = 0; i < selected.getCount(); i++) {
                    JSONObject jsonObject = new JSONObject(selected.getString(1));
                    if (i == 0) {
                        jsonObjectList.add(addDateContent(jsonObject.getString(CREATED_AT)));
                        lastDate = jsonObject.getString(CREATED_AT);
                        jsonObjectList.add(jsonObject);
                    } else {
                        if (currentDateBigger(lastDate, jsonObject.getString(CREATED_AT))) {
                            jsonObjectList.add(addDateContent(jsonObject.getString(CREATED_AT)));
                            lastDate = jsonObject.getString(CREATED_AT);
                            jsonObjectList.add(jsonObject);
                        } else {
                            jsonObjectList.add(jsonObject);
                        }
                    }
                    selected.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObjectList;
    }

    private boolean currentDateBigger(String lastDate, String currentDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
        Date date;
        Calendar dateCal = Calendar.getInstance();
        Date todayDate;
        Calendar todayDateCal = Calendar.getInstance();
        boolean isBigger = true;
        try {
            date = simpleDateFormat.parse(lastDate);
            dateCal.setTime(date);
            todayDate = simpleDateFormat.parse(currentDate);
            todayDateCal.setTime(todayDate);
            int years = todayDateCal.get(Calendar.YEAR) - dateCal.get(Calendar.YEAR);
            if (years == 0) {
                int months = todayDateCal.get(Calendar.MONTH) - dateCal.get(Calendar.MONTH);
                if (months == 0) {
                    int days = todayDateCal.get(Calendar.DATE) - dateCal.get(Calendar.DATE);
                    isBigger = days != 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isBigger;
    }

    private JSONObject addDateContent(String date) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CREATED_AT, date);
        jsonObject.put(TYPE, POST_TYPE_DATE);
        return jsonObject;
    }

}
