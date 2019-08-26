package com.smnetinfo.savememory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.LinearLayout;

import com.smnetinfo.savememory.database.PostsDataSource;
import com.smnetinfo.savememory.database.SettingsDataSource;
import com.smnetinfo.savememory.database.UserDataSource;
import com.smnetinfo.savememory.extras.BaseActivity;
import com.smnetinfo.savememory.extras.WebConstants;

public class MyPageActivity extends BaseActivity implements WebConstants {


    AppCompatImageView activityMyPageBackIV;
    LinearLayout activityMyPageProfileLL, activityMyPageMyWillLL, activityMyPageMyBeneficiaryLL, activityMyPageMyStorageLL,
            activityMyPageLogoutLL, activityMyPageThemeLL, activityMyPageSkinLL, activityMyPageFAQLL, activityMyPageQNALL,
            activityMyPageAboutLL;

    UserDataSource userDataSource;
    PostsDataSource postsDataSource;
    SettingsDataSource settingsDataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        userDataSource = UserDataSource.sharedInstance(this);
        postsDataSource = PostsDataSource.sharedInstance(this);
        settingsDataSource = SettingsDataSource.sharedInstance(this);

        activityMyPageBackIV = findViewById(R.id.activityMyPageBackIV);

        activityMyPageFAQLL = findViewById(R.id.activityMyPageFAQLL);
        activityMyPageQNALL = findViewById(R.id.activityMyPageQNALL);
        activityMyPageSkinLL = findViewById(R.id.activityMyPageSkinLL);
        activityMyPageThemeLL = findViewById(R.id.activityMyPageThemeLL);
        activityMyPageAboutLL = findViewById(R.id.activityMyPageAboutLL);
        activityMyPageLogoutLL = findViewById(R.id.activityMyPageLogoutLL);
        activityMyPageMyWillLL = findViewById(R.id.activityMyPageMyWillLL);
        activityMyPageProfileLL = findViewById(R.id.activityMyPageProfileLL);
        activityMyPageMyStorageLL = findViewById(R.id.activityMyPageMyStorageLL);
        activityMyPageMyBeneficiaryLL = findViewById(R.id.activityMyPageMyBeneficiaryLL);

        activityMyPageThemeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), FontActivity.class));
            }
        });

        activityMyPageSkinLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SkinActivity.class));
            }
        });

        activityMyPageProfileLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });

        activityMyPageMyWillLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddWillActivity.class));
            }
        });

        activityMyPageMyBeneficiaryLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalBroadcastManager.getInstance(MyPageActivity.this).sendBroadcast(new Intent(BROADCAST_MY_INHERITOR_PAGE));
                finish();
            }
        });

        activityMyPageMyStorageLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyStorageActivity.class));
            }
        });

        activityMyPageFAQLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), FAQActivity.class));
            }
        });

        activityMyPageQNALL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), QNAActivity.class));
            }
        });

        activityMyPageAboutLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            }
        });

        activityMyPageLogoutLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(MyPageActivity.this)
                        .setTitle("Do you want to logout?")
                        .setMessage("You will be permanently logged out of SaveMemory app, are you sure?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                userDataSource.deleteUserData();
                                postsDataSource.deletePostData();
                                settingsDataSource.deleteSettingsData();
                                Intent mainToSplash = new Intent(MyPageActivity.this, SplashActivity.class);
                                mainToSplash.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(mainToSplash);
                                dialog.dismiss();
                                finish();
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.kakaoPrimary));
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.kakaoPink));
                    }
                });
                alertDialog.show();
            }
        });

        activityMyPageBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
