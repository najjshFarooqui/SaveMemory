package com.smnetinfo.savememory.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.smnetinfo.savememory.extras.WebConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by Siva on 3/8/16.
 */

public class SettingsDataSource implements WebConstants {

    private static SettingsDataSource settingsDataSource;
    private static SaveMemorySqliteHelper saveMemorySqliteHelper;
    private SQLiteDatabase sqLiteDatabase;
    private String allColumns[];

    public static SettingsDataSource sharedInstance(Context context) {
        if (settingsDataSource == null) {
            saveMemorySqliteHelper = new SaveMemorySqliteHelper(context);
            settingsDataSource = new SettingsDataSource();
            settingsDataSource.allColumns = new String[1];
            settingsDataSource.allColumns[0] = DATA;
            settingsDataSource.open();
        }
        return settingsDataSource;
    }

    private void open() throws SQLiteException {
        sqLiteDatabase = saveMemorySqliteHelper.getWritableDatabase();
    }

    private void close() {
        sqLiteDatabase.close();
    }

    private void insertSettingsData(String userData) {
        open();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATA, userData);

        try {
            sqLiteDatabase.insert(TABLE_SETTINGS, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSettingsData() {
        open();
        sqLiteDatabase.delete(TABLE_SETTINGS, null, null);
    }

    public void userLoginSuccess() {
        open();
        try {
            JSONObject jsonObject = getSettingsData();
            jsonObject.put(LOGIN_STATUS, true);

            ContentValues contentValues = new ContentValues();
            contentValues.put(DATA, jsonObject.toString());

            sqLiteDatabase.update(TABLE_SETTINGS, contentValues, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateKeepLogged(boolean keepLogged) {
        open();
        try {
            JSONObject jsonObject = getSettingsData();
            jsonObject.put(KEEP_LOGGED, keepLogged);

            ContentValues contentValues = new ContentValues();
            contentValues.put(DATA, jsonObject.toString());

            sqLiteDatabase.update(TABLE_SETTINGS, contentValues, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTOC(boolean tocAccepted) {
        open();
        try {
            JSONObject jsonObject = getSettingsData();
            jsonObject.put(TOC_ACCEPTED, tocAccepted);

            ContentValues contentValues = new ContentValues();
            contentValues.put(DATA, jsonObject.toString());

            sqLiteDatabase.update(TABLE_SETTINGS, contentValues, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePrivacy(boolean privacyAccepted) {
        open();
        try {
            JSONObject jsonObject = getSettingsData();
            jsonObject.put(PRIVACY_ACCEPTED, privacyAccepted);

            ContentValues contentValues = new ContentValues();
            contentValues.put(DATA, jsonObject.toString());

            sqLiteDatabase.update(TABLE_SETTINGS, contentValues, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateFontSize(int fontSize) {
        open();
        try {
            JSONObject jsonObject = getSettingsData();
            jsonObject.put(FONT_SIZE, fontSize);

            ContentValues contentValues = new ContentValues();
            contentValues.put(DATA, jsonObject.toString());

            sqLiteDatabase.update(TABLE_SETTINGS, contentValues, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateWallpaperPath(String path) {
        open();
        try {
            JSONObject jsonObject = getSettingsData();
            jsonObject.put(WALLPAPER_PATH, path);

            ContentValues contentValues = new ContentValues();
            contentValues.put(DATA, jsonObject.toString());

            sqLiteDatabase.update(TABLE_SETTINGS, contentValues, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject getSettingsData() {
        open();
        try (Cursor selected = sqLiteDatabase.query(TABLE_SETTINGS, allColumns, null, null, null, null, null)) {
            if (selected != null && selected.getCount() > 0) {
                selected.moveToFirst();
                return new JSONObject(selected.getString(0));
            } else {
                deleteSettingsData();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(KEEP_LOGGED, false);
                jsonObject.put(LOGIN_STATUS, false);
                jsonObject.put(TOC_ACCEPTED, false);
                jsonObject.put(PRIVACY_ACCEPTED, false);
                jsonObject.put(LANGUAGE_TYPE, LANG_ENGLISH);
                jsonObject.put(FONT_SIZE, FONT_SIZE_MEDIUM);
                jsonObject.put(WALLPAPER_PATH, EMPTY);
                insertSettingsData(jsonObject.toString());
                return getSettingsData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public int languageType() {
        try {
            return Objects.requireNonNull(getSettingsData()).getInt(LANGUAGE_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getFontSize() {
        try {
            return Objects.requireNonNull(getSettingsData()).getInt(FONT_SIZE);
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getWallpaperPath() {
        try {
            return Objects.requireNonNull(getSettingsData()).getString(WALLPAPER_PATH);
        } catch (JSONException e) {
            e.printStackTrace();
            return EMPTY;
        }
    }

    public boolean isLoggedIn() {
        try {
            return Objects.requireNonNull(getSettingsData()).getBoolean(LOGIN_STATUS);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isKeepLogged() {
        try {
            return Objects.requireNonNull(getSettingsData()).getBoolean(KEEP_LOGGED);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isTOCAccepted() {
        try {
            return Objects.requireNonNull(getSettingsData()).getBoolean(TOC_ACCEPTED);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isPrivacyAccpeted() {
        try {
            return Objects.requireNonNull(getSettingsData()).getBoolean(PRIVACY_ACCEPTED);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

}
