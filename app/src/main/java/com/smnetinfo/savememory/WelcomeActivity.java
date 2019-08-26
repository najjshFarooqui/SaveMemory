package com.smnetinfo.savememory;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.smnetinfo.savememory.extras.BaseActivity;
import com.smnetinfo.savememory.extras.WebConstants;

public class WelcomeActivity extends BaseActivity implements WebConstants {

    Button activitySplashStartCV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        activitySplashStartCV = findViewById(R.id.activitySplashStartCV);

        activitySplashStartCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, PagerActivity.class));
                finish();
            }
        });

    }

}
