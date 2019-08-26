package com.smnetinfo.savememory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.smnetinfo.savememory.extras.BaseActivity;
import com.smnetinfo.savememory.extras.WebConstants;
import com.squareup.picasso.Picasso;

public class ImageActivity extends BaseActivity implements WebConstants {


    GestureImageView activityImageView;
    AppCompatImageView activityImageCloseIV;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);


        activityImageView = findViewById(R.id.activityImageView);

        activityImageCloseIV = findViewById(R.id.activityImageCloseIV);

        Picasso.with(this)
                .load(getIntent().getExtras().getString(URL))
                .into(activityImageView);

        activityImageCloseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
