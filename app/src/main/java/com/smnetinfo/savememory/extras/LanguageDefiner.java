package com.smnetinfo.savememory.extras;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.smnetinfo.savememory.database.SettingsDataSource;

public class LanguageDefiner implements WebConstants {

    private Context context;
    private SettingsDataSource settingsDataSource;

    public LanguageDefiner(Context context) {
        this.context = context;
        settingsDataSource = SettingsDataSource.sharedInstance(context);
    }

    public void setUpTextResource(TextView textView, int english, int korean, int japanese) {
        switch (settingsDataSource.languageType()) {
            case LANG_ENGLISH:
                textView.setText(context.getResources().getString(english));
                break;
            case LANG_KOREAN:
                textView.setText(context.getResources().getString(korean));
                break;
            case LANG_JAPANESE:
                textView.setText(context.getResources().getString(japanese));
                break;
        }
    }

    public void setUpText(TextView textView, String english, String korean, String japanese) {
        switch (settingsDataSource.languageType()) {
            case LANG_ENGLISH:
                textView.setText(english);
                break;
            case LANG_KOREAN:
                textView.setText(korean);
                break;
            case LANG_JAPANESE:
                textView.setText(japanese);
                break;
        }
    }

    public String getStringResource(int english, int korean, int japanese) {
        String string = "";
        switch (settingsDataSource.languageType()) {
            case LANG_ENGLISH:
                string = context.getResources().getString(english);
                break;
            case LANG_KOREAN:
                string = context.getResources().getString(korean);
                break;
            case LANG_JAPANESE:
                string = context.getResources().getString(japanese);
                break;
        }
        return string;
    }

    public String getString(String english, String korean, String japanese) {
        String string = english;
        switch (settingsDataSource.languageType()) {
            case LANG_ENGLISH:
                string = english;
                break;
            case LANG_KOREAN:
                string = korean;
                break;
            case LANG_JAPANESE:
                string = japanese;
                break;
        }
        return string;
    }

    public void setUpEditTextResource(EditText editText, int english, int korean, int japanese) {
        switch (settingsDataSource.languageType()) {
            case LANG_ENGLISH:
                editText.setHint(context.getResources().getString(english));
                break;
            case LANG_KOREAN:
                editText.setHint(context.getResources().getString(korean));
                break;
            case LANG_JAPANESE:
                editText.setHint(context.getResources().getString(japanese));
                break;
        }
    }

    public void showToastResource(int english, int korean, int japanese) {
        switch (settingsDataSource.languageType()) {
            case LANG_ENGLISH:
                Toast.makeText(context, english, Toast.LENGTH_SHORT).show();
                break;
            case LANG_KOREAN:
                Toast.makeText(context, korean, Toast.LENGTH_SHORT).show();
                break;
            case LANG_JAPANESE:
                Toast.makeText(context, japanese, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
