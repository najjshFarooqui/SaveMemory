package com.smnetinfo.savememory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.smnetinfo.savememory.database.SettingsDataSource;
import com.smnetinfo.savememory.extras.BaseActivity;
import com.smnetinfo.savememory.extras.WebConstants;

public class TOCActivity extends BaseActivity implements WebConstants {

    WebView activityTOCWebView;
    CardView activityTOCAcceptCV;
    ProgressBar activityTOCProgressBar;

    SettingsDataSource settingsDataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toc);

        settingsDataSource = SettingsDataSource.sharedInstance(this);

        activityTOCWebView = findViewById(R.id.activityTOCWebView);

        activityTOCAcceptCV = findViewById(R.id.activityTOCAcceptCV);

        activityTOCProgressBar = findViewById(R.id.activityTOCProgressBar);

        activityTOCWebView.loadUrl(TOC_URL);
        activityTOCWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                activityTOCProgressBar.setVisibility(View.GONE);
            }
        });

        activityTOCAcceptCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsDataSource.updateTOC(true);
                finish();
            }
        });

    }

}
