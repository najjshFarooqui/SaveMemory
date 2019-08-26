package com.smnetinfo.savememory;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.services.s3.AmazonS3;
import com.smnetinfo.savememory.adapter.MainViewPagerAdapter;
import com.smnetinfo.savememory.database.PostsDataSource;
import com.smnetinfo.savememory.database.SettingsDataSource;
import com.smnetinfo.savememory.database.UserDataSource;
import com.smnetinfo.savememory.extras.AwsS3Util;
import com.smnetinfo.savememory.extras.ProgressDialog;
import com.smnetinfo.savememory.extras.WebConstants;
import com.squareup.picasso.Picasso;

import java.io.File;

public class MainActivity extends AppCompatActivity implements WebConstants {

    CardView activityMainSearchCV;
    EditText activityMainSearchET;
    ViewPager activityMainViewPager;
    TextView activityMainMemoryTabTV, activityMainWillTabTV, activityMainInheritorTabTV;
    View activityMainMemoryTabView, activityMainWillTabView, activityMainInheritorTabView;
    RelativeLayout activityMainMemoryTabRL, activityMainWillTabRL, activityMainInheritorTabRL;
    AppCompatImageView activityMainProfileImageIV, activityMainWallpaperIV, activityMainSearchIV;

    MainViewPagerAdapter mainViewPagerAdapter;

    UserDataSource userDataSource;
    PostsDataSource postsDataSource;
    SettingsDataSource settingsDataSource;

    AmazonS3 amazonS3;
    ProgressDialog progressDialog;

    BroadcastReceiver pageSelector;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        amazonS3 = new AwsS3Util().getS3Client(this);
        userDataSource = UserDataSource.sharedInstance(this);
        postsDataSource = PostsDataSource.sharedInstance(this);
        settingsDataSource = SettingsDataSource.sharedInstance(this);

        activityMainSearchCV = findViewById(R.id.activityMainSearchCV);

        activityMainSearchET = findViewById(R.id.activityMainSearchET);

        activityMainViewPager = findViewById(R.id.activityMainViewPager);

        activityMainWillTabTV = findViewById(R.id.activityMainWillTabTV);
        activityMainMemoryTabTV = findViewById(R.id.activityMainMemoryTabTV);
        activityMainInheritorTabTV = findViewById(R.id.activityMainInheritorTabTV);

        activityMainSearchIV = findViewById(R.id.activityMainSearchIV);
        activityMainWallpaperIV = findViewById(R.id.activityMainWallpaperIV);
        activityMainProfileImageIV = findViewById(R.id.activityMainProfileImageIV);

        activityMainWillTabRL = findViewById(R.id.activityMainWillTabRL);
        activityMainMemoryTabRL = findViewById(R.id.activityMainMemoryTabRL);
        activityMainInheritorTabRL = findViewById(R.id.activityMainInheritorTabRL);

        activityMainWillTabView = findViewById(R.id.activityMainWillTabView);
        activityMainMemoryTabView = findViewById(R.id.activityMainMemoryTabView);
        activityMainInheritorTabView = findViewById(R.id.activityMainInheritorTabView);

        //init
        mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        activityMainViewPager.setAdapter(mainViewPagerAdapter);
        activityMainViewPager.setCurrentItem(0);
        activityMainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        activityMainWillTabView.setVisibility(View.GONE);
                        activityMainMemoryTabView.setVisibility(View.VISIBLE);
                        activityMainInheritorTabView.setVisibility(View.GONE);
                        activityMainWillTabTV.setTextColor(getResources().getColor(R.color.newDimText));
                        activityMainMemoryTabTV.setTextColor(getResources().getColor(android.R.color.white));
                        activityMainInheritorTabTV.setTextColor(getResources().getColor(R.color.newDimText));
                        break;
                    case 1:
                        activityMainMemoryTabView.setVisibility(View.GONE);
                        activityMainWillTabView.setVisibility(View.VISIBLE);
                        activityMainInheritorTabView.setVisibility(View.GONE);
                        activityMainMemoryTabTV.setTextColor(getResources().getColor(R.color.newDimText));
                        activityMainWillTabTV.setTextColor(getResources().getColor(android.R.color.white));
                        activityMainInheritorTabTV.setTextColor(getResources().getColor(R.color.newDimText));
                        break;
                    case 2:
                        activityMainWillTabView.setVisibility(View.GONE);
                        activityMainMemoryTabView.setVisibility(View.GONE);
                        activityMainInheritorTabView.setVisibility(View.VISIBLE);
                        activityMainWillTabTV.setTextColor(getResources().getColor(R.color.newDimText));
                        activityMainMemoryTabTV.setTextColor(getResources().getColor(R.color.newDimText));
                        activityMainInheritorTabTV.setTextColor(getResources().getColor(android.R.color.white));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        activityMainMemoryTabRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityMainViewPager.setCurrentItem(0, true);
            }
        });
        activityMainWillTabRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityMainViewPager.setCurrentItem(1, true);
            }
        });
        activityMainInheritorTabRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityMainViewPager.setCurrentItem(2, true);
            }
        });
        //init

        activityMainProfileImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MyPageActivity.class));
            }
        });

        pageSelector = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null) {
                    if (BROADCAST_MY_INHERITOR_PAGE.equals(intent.getAction())) {
                        activityMainViewPager.setCurrentItem(2, false);
                    }
                }
            }
        };

        activityMainSearchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activityMainSearchCV.getVisibility() == View.VISIBLE) {
                    activityMainSearchCV.setVisibility(View.GONE);
                    LocalBroadcastManager.getInstance(MainActivity.this)
                            .sendBroadcast(new Intent(BROADCAST_SEARCH_RESULT)
                                    .putExtra(SEARCH_TEXT, activityMainSearchET.getText().toString()));
                    activityMainSearchET.setText("");
                } else {
                    activityMainSearchCV.setVisibility(View.VISIBLE);
                }
            }
        });

        activityMainSearchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (event.getKeyCode()) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            activityMainSearchCV.setVisibility(View.GONE);
                            LocalBroadcastManager.getInstance(MainActivity.this)
                                    .sendBroadcast(new Intent(BROADCAST_SEARCH_RESULT)
                                            .putExtra(SEARCH_TEXT, activityMainSearchET.getText().toString()));
                            activityMainSearchET.setText("");
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (settingsDataSource.isLoggedIn()) {
            LocalBroadcastManager.getInstance(this).registerReceiver(pageSelector, new IntentFilter(BROADCAST_MY_INHERITOR_PAGE));
            //init wallpaper
            if (!settingsDataSource.getWallpaperPath().equals(EMPTY)) {
                Picasso.with(this)
                        .load(new File(settingsDataSource.getWallpaperPath()))
                        .into(activityMainWallpaperIV);
            }
            //init wallpaper
        } else {
            finish();
        }
    }

}
