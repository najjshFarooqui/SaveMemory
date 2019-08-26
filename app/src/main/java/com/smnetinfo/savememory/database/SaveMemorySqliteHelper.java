package com.smnetinfo.savememory.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.smnetinfo.savememory.extras.WebConstants;

/**
 * Created by Siva on 3/8/16.
 */
class SaveMemorySqliteHelper extends SQLiteOpenHelper implements WebConstants {

    private String user, settings, posts;

    SaveMemorySqliteHelper(Context context) {
        super(context, "SaveMemoryTemp4", null, 1);

        user = "CREATE TABLE " + TABLE_USER + "(" + DATA + " TEXT);";

        settings = "CREATE TABLE " + TABLE_SETTINGS + "(" + DATA + " TEXT);";

        posts = "CREATE TABLE " + TABLE_POSTS + "(" + ID + " TEXT PRIMARY KEY,"
                + DATA + " TEXT);";

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(user);
        sqLiteDatabase.execSQL(settings);
        sqLiteDatabase.execSQL(posts);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        onCreate(sqLiteDatabase);
    }

}
