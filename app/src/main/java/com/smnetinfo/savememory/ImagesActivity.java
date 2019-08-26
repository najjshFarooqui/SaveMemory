package com.smnetinfo.savememory;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import com.smnetinfo.savememory.adapter.ImagesPagerAdapter;
import com.smnetinfo.savememory.extras.BaseActivity;
import com.smnetinfo.savememory.extras.WebConstants;

import org.json.JSONArray;
import org.json.JSONException;

public class ImagesActivity extends BaseActivity implements WebConstants {

    ViewPager activityImagesViewPager;
    AppCompatImageView activityImagesGridIV, activityImagesCloseIV;

    ImagesPagerAdapter imagesPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        activityImagesGridIV = findViewById(R.id.activityImagesGridIV);
        activityImagesCloseIV = findViewById(R.id.activityImagesCloseIV);

        activityImagesViewPager = findViewById(R.id.activityImagesViewPager);

        try {
            JSONArray jsonArray = new JSONArray(getIntent().getExtras().getString(DATA));
            imagesPagerAdapter = new ImagesPagerAdapter(this, jsonArray);
            activityImagesViewPager.setAdapter(imagesPagerAdapter);
            activityImagesViewPager.setCurrentItem(getIntent().getExtras().getInt(POSITION));

            activityImagesGridIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ImagesActivity.this, ImageGridActivity.class);
                    intent.putExtra(ID, getIntent().getExtras().getString(ID));
                    intent.putExtra(DATA, getIntent().getExtras().getString(DATA));
                    startActivity(intent);
                    finish();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        activityImagesCloseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
