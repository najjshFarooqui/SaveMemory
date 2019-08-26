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

public class PrivacyActivity extends BaseActivity implements WebConstants {

    WebView activityPrivacyWebView;
    CardView activityPrivacyAcceptCV;
    ProgressBar activityPrivacyProgressBar;

    SettingsDataSource settingsDataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        settingsDataSource = SettingsDataSource.sharedInstance(this);

        activityPrivacyWebView = findViewById(R.id.activityPrivacyWebView);

        activityPrivacyAcceptCV = findViewById(R.id.activityPrivacyAcceptCV);

        activityPrivacyProgressBar = findViewById(R.id.activityPrivacyProgressBar);

        activityPrivacyWebView.loadUrl(PRIVACY_URL);
        activityPrivacyWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                activityPrivacyProgressBar.setVisibility(View.GONE);
            }
        });

        activityPrivacyAcceptCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsDataSource.updatePrivacy(true);
                finish();
            }
        });

    }


}
