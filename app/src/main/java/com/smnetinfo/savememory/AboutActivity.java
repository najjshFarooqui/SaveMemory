package com.smnetinfo.savememory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.smnetinfo.savememory.extras.BaseActivity;
import com.smnetinfo.savememory.extras.WebConstants;

public class AboutActivity extends BaseActivity implements WebConstants {

    ImageView activityAboutBackCV;

    //   WebView activityAboutWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        activityAboutBackCV = findViewById(R.id.activityAboutBackCV);

//        activityAboutWebView = findViewById(R.id.activityAboutWebView);
//
//        activityAboutWebView.loadUrl(ABOUT_URL);
//        activityAboutWebView.setWebViewClient(new WebViewClient() {
//            public void onPageFinished(WebView view, String url) {
//
//            }
//        });

        activityAboutBackCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

}
