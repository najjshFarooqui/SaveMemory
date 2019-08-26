package com.smnetinfo.savememory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

import com.smnetinfo.savememory.database.PostsDataSource;
import com.smnetinfo.savememory.database.SettingsDataSource;
import com.smnetinfo.savememory.database.UserDataSource;
import com.smnetinfo.savememory.extras.BaseActivity;
import com.smnetinfo.savememory.extras.WebConstants;

public class VerifyEmailActivity extends BaseActivity implements WebConstants {

    CardView activityEmailVerifyCV;

    UserDataSource userDataSource;
    PostsDataSource postsDataSource;
    SettingsDataSource settingsDataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        userDataSource = UserDataSource.sharedInstance(this);
        postsDataSource = PostsDataSource.sharedInstance(this);
        settingsDataSource = SettingsDataSource.sharedInstance(this);

        activityEmailVerifyCV = findViewById(R.id.activityEmailVerifyCV);

        activityEmailVerifyCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Verification Email Sent", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
