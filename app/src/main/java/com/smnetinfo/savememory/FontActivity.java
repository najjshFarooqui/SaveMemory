package com.smnetinfo.savememory;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.smnetinfo.savememory.database.SettingsDataSource;
import com.smnetinfo.savememory.extras.BaseActivity;
import com.smnetinfo.savememory.extras.WebConstants;

import java.util.ArrayList;

public class FontActivity extends BaseActivity implements WebConstants {


    Spinner activityFontSizeSpinner;
    AppCompatImageView activityFontBackIV;

    SettingsDataSource settingsDataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font);

        settingsDataSource = SettingsDataSource.sharedInstance(this);

        activityFontBackIV = findViewById(R.id.activityFontBackIV);

        activityFontSizeSpinner = findViewById(R.id.activityFontSizeSpinner);

        //init Spinners
        final ArrayList<String> sizeArrayList = new ArrayList<>();
        sizeArrayList.add("SMALL");
        sizeArrayList.add("MEDIUM");
        sizeArrayList.add("LARGE");
        sizeArrayList.add("VERY LARGE");

        ArrayAdapter<String> dateSpinnerArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, sizeArrayList);
        activityFontSizeSpinner.setAdapter(dateSpinnerArrayAdapter);
        activityFontSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String size = sizeArrayList.get(i);
                switch (size) {
                    case "SMALL":
                        settingsDataSource.updateFontSize(FONT_SIZE_SMALL);
                        break;
                    case "MEDIUM":
                        settingsDataSource.updateFontSize(FONT_SIZE_MEDIUM);
                        break;
                    case "LARGE":
                        settingsDataSource.updateFontSize(FONT_SIZE_LARGE);
                        break;
                    case "VERY LARGE":
                        settingsDataSource.updateFontSize(FONT_SIZE_VLARGE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        switch (settingsDataSource.getFontSize()) {
            case FONT_SIZE_SMALL:
                activityFontSizeSpinner.setSelection(sizeArrayList.indexOf("SMALL"));
                break;
            case FONT_SIZE_MEDIUM:
                activityFontSizeSpinner.setSelection(sizeArrayList.indexOf("MEDIUM"));
                break;
            case FONT_SIZE_LARGE:
                activityFontSizeSpinner.setSelection(sizeArrayList.indexOf("LARGE"));
                break;
            case FONT_SIZE_VLARGE:
                activityFontSizeSpinner.setSelection(sizeArrayList.indexOf("VERY LARGE"));
                break;
        }


        activityFontBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void chooseFontSize() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_font_size);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(true);
        dialog.show();

        TextView dialogFontSizeSmallTV = dialog.findViewById(R.id.dialogFontSizeSmallTV);
        TextView dialogFontSizeMediumTV = dialog.findViewById(R.id.dialogFontSizeMediumTV);
        TextView dialogFontSizeLargeTV = dialog.findViewById(R.id.dialogFontSizeLargeTV);
        TextView dialogFontSizeVeryLargeTV = dialog.findViewById(R.id.dialogFontSizeVeryLargeTV);

        switch (settingsDataSource.getFontSize()) {
            case FONT_SIZE_SMALL:
                dialogFontSizeSmallTV.setTypeface(Typeface.DEFAULT_BOLD);
                break;
            case FONT_SIZE_MEDIUM:
                dialogFontSizeMediumTV.setTypeface(Typeface.DEFAULT_BOLD);
                break;
            case FONT_SIZE_LARGE:
                dialogFontSizeLargeTV.setTypeface(Typeface.DEFAULT_BOLD);
                break;
            case FONT_SIZE_VLARGE:
                dialogFontSizeVeryLargeTV.setTypeface(Typeface.DEFAULT_BOLD);
                break;
        }

        dialogFontSizeSmallTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsDataSource.updateFontSize(FONT_SIZE_SMALL);
                dialog.dismiss();
            }
        });

        dialogFontSizeMediumTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsDataSource.updateFontSize(FONT_SIZE_MEDIUM);
                dialog.dismiss();
            }
        });

        dialogFontSizeLargeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsDataSource.updateFontSize(FONT_SIZE_LARGE);
                dialog.dismiss();
            }
        });

        dialogFontSizeVeryLargeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsDataSource.updateFontSize(FONT_SIZE_VLARGE);
                dialog.dismiss();
            }
        });

    }

}
