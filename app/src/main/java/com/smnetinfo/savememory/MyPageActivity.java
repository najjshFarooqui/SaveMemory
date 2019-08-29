package com.smnetinfo.savememory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.RelativeLayout;

import com.smnetinfo.savememory.database.PostsDataSource;
import com.smnetinfo.savememory.database.SettingsDataSource;
import com.smnetinfo.savememory.database.UserDataSource;
import com.smnetinfo.savememory.extras.BaseActivity;
import com.smnetinfo.savememory.extras.WebConstants;

public class MyPageActivity extends BaseActivity implements WebConstants {


    AppCompatImageView activityMyPageBackIV;
    RelativeLayout activityMyPageProfileLL, activityMyPageMyWillLL, activityMyPageMyBeneficiaryLL, activityMyPageMyStorageLL,
            activityMyPageLogoutLL, activityMyPageThemeLL, activityMyPageSkinLL, activityMyPageFAQLL, activityMyPageQNALL,
            activityMyPageAboutLL,
            activityChangePassword, activityLockScreen, activityPayment, activityChangeLanguage, activityFontSize;

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

        activityMyPageFAQLL = findViewById(R.id.activityFaq);
        activityMyPageQNALL = findViewById(R.id.activityAsk);
        activityChangePassword = findViewById(R.id.activityChangePassword);
        activityLockScreen = findViewById(R.id.activityLockScreen);
        activityMyPageAboutLL = findViewById(R.id.activityAboutSaveMemory);
        activityMyPageLogoutLL = findViewById(R.id.logoutLL);
        activityChangeLanguage = findViewById(R.id.activityChangeLanguage);
        activityMyPageProfileLL = findViewById(R.id.activityMyPageProfileLL);
        activityMyPageMyStorageLL = findViewById(R.id.activityMyPageMyStorageLL);
        activityPayment = findViewById(R.id.activityPayment);
        activityFontSize = findViewById(R.id.activityFontSize);

        activityChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), ChangePasswordActivity.class));
            }
        });


        activityChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ChangeLanguageActivity.class));


            }
        });

        activityFontSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), FontAndSizeActivity.class));

            }
        });

        activityPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PaymentActivity.class));


            }
        });


        activityMyPageProfileLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
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

        activityLockScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LockScreenAcivity.class));


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
