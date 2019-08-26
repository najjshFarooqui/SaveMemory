package com.smnetinfo.savememory;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.smnetinfo.savememory.adapter.OnBoardViewPagerAdapter;
import com.smnetinfo.savememory.extras.BaseActivity;
import com.smnetinfo.savememory.extras.WebConstants;

public class PagerActivity extends BaseActivity implements WebConstants {

    ViewPager activityViewPager;
    TextView activityPagerLetsGoTV;
    CardView pager1CV, pager2CV, pager3CV, pager4CV, pager5CV, border1Cv, border2Cv, border3Cv, border4Cv, border5Cv;

    OnBoardViewPagerAdapter onBoardViewPagerAdapter;

    int currentPosition = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        pager1CV = findViewById(R.id.pager1CV);
        pager2CV = findViewById(R.id.pager2CV);
        pager3CV = findViewById(R.id.pager3CV);
        pager4CV = findViewById(R.id.pager4CV);
        pager5CV = findViewById(R.id.pager5CV);
        border1Cv = findViewById(R.id.border1Cv);
        border2Cv = findViewById(R.id.border2Cv);
        border3Cv = findViewById(R.id.border3Cv);
        border4Cv = findViewById(R.id.border4Cv);
        border5Cv = findViewById(R.id.border5Cv);

        activityPagerLetsGoTV = findViewById(R.id.activityPagerLetsGoTV);

        activityViewPager = findViewById(R.id.activityViewPager);

        onBoardViewPagerAdapter = new OnBoardViewPagerAdapter(getSupportFragmentManager());
        activityViewPager.setAdapter(onBoardViewPagerAdapter);
        activityViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 4) {
                    activityPagerLetsGoTV.setVisibility(View.VISIBLE);
                } else {
                    activityPagerLetsGoTV.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                switch (position) {
                    case 0:
                        pager1CV.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        border1Cv.setCardBackgroundColor(getResources().getColor(R.color.colorPurple));
                        pager2CV.setCardBackgroundColor(getResources().getColor(R.color.grey));
                        border2Cv.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        pager3CV.setCardBackgroundColor(getResources().getColor(R.color.grey));
                        border3Cv.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        pager4CV.setCardBackgroundColor(getResources().getColor(R.color.grey));
                        border4Cv.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        pager5CV.setCardBackgroundColor(getResources().getColor(R.color.grey));
                        border5Cv.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        break;
                    case 1:
                        pager1CV.setCardBackgroundColor(getResources().getColor(R.color.grey));
                        border1Cv.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        pager2CV.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        border2Cv.setCardBackgroundColor(getResources().getColor(R.color.colorPurple));
                        pager3CV.setCardBackgroundColor(getResources().getColor(R.color.grey));
                        border3Cv.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        pager4CV.setCardBackgroundColor(getResources().getColor(R.color.grey));
                        border4Cv.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        pager5CV.setCardBackgroundColor(getResources().getColor(R.color.grey));
                        border5Cv.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        break;
                    case 2:
                        pager1CV.setCardBackgroundColor(getResources().getColor(R.color.grey));
                        border1Cv.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        pager2CV.setCardBackgroundColor(getResources().getColor(R.color.grey));
                        border2Cv.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        pager3CV.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        border3Cv.setCardBackgroundColor(getResources().getColor(R.color.colorPurple));
                        pager4CV.setCardBackgroundColor(getResources().getColor(R.color.grey));
                        border4Cv.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        pager5CV.setCardBackgroundColor(getResources().getColor(R.color.grey));
                        border5Cv.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        break;
                    case 3:
                        pager1CV.setCardBackgroundColor(getResources().getColor(R.color.grey));
                        border1Cv.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        pager2CV.setCardBackgroundColor(getResources().getColor(R.color.grey));
                        border2Cv.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        pager3CV.setCardBackgroundColor(getResources().getColor(R.color.grey));
                        border3Cv.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        pager4CV.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        border4Cv.setCardBackgroundColor(getResources().getColor(R.color.colorPurple));
                        pager5CV.setCardBackgroundColor(getResources().getColor(R.color.grey));
                        border5Cv.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        break;
                    case 4:
                        pager1CV.setCardBackgroundColor(getResources().getColor(R.color.grey));
                        border1Cv.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        pager2CV.setCardBackgroundColor(getResources().getColor(R.color.grey));
                        border2Cv.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        pager3CV.setCardBackgroundColor(getResources().getColor(R.color.grey));
                        border3Cv.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        pager4CV.setCardBackgroundColor(getResources().getColor(R.color.grey));
                        border4Cv.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        pager5CV.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        border5Cv.setCardBackgroundColor(getResources().getColor(R.color.colorPurple));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        activityPagerLetsGoTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPosition == 4) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                } else {
                    activityViewPager.setCurrentItem(currentPosition++);
                }
            }
        });

    }

}
