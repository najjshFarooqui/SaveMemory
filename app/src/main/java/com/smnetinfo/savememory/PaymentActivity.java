package com.smnetinfo.savememory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PaymentActivity extends AppCompatActivity {
    Button btnSubscription;
    String amount = "";

    CardView offerOne, offerThree, offerSix, offerTwelve;
    TextView saveOne, saveThree, saveSix, saveTwelve;
    RelativeLayout oneRR, threeRR, sixRR, twelveRR;
    TextView oneMonth, threeMonths, sixMonths, twelveMonths;
    TextView oneTv, threeTv, sixTv, twelveTv;

    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        btnSubscription = findViewById(R.id.btnSubscription);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyPageActivity.class));
                finish();
            }
        });


        offerOne = findViewById(R.id.offerOne);
        offerThree = findViewById(R.id.offerThree);
        offerSix = findViewById(R.id.offerSix);
        offerTwelve = findViewById(R.id.offerTwelve);


        saveOne = findViewById(R.id.saveOne);
        saveThree = findViewById(R.id.saveThree);
        saveSix = findViewById(R.id.saveSix);
        saveTwelve = findViewById(R.id.saveTwelve);


        oneRR = findViewById(R.id.oneRR);
        threeRR = findViewById(R.id.threeRR);
        sixRR = findViewById(R.id.sixRR);
        twelveRR = findViewById(R.id.twelveRR);


        oneMonth = findViewById(R.id.oneMonth);
        threeMonths = findViewById(R.id.threeMonths);
        sixMonths = findViewById(R.id.sixMonths);
        twelveMonths = findViewById(R.id.twelveMonth);


        oneTv = findViewById(R.id.oneTv);
        threeTv = findViewById(R.id.threeTv);
        sixTv = findViewById(R.id.sixTv);
        twelveTv = findViewById(R.id.twelveTv);

        unchangedViews();

        offerOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unchangedViews();
                saveOne.setVisibility(View.VISIBLE);
                oneRR.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                oneTv.setTextColor(getResources().getColor(R.color.money));
                oneMonth.setVisibility(View.INVISIBLE);
                amount = "$40";

            }
        });


        offerThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unchangedViews();
                saveThree.setVisibility(View.VISIBLE);
                threeRR.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                threeTv.setTextColor(getResources().getColor(R.color.money));
                threeMonths.setVisibility(View.INVISIBLE);
                amount = "$120";
            }
        });


        offerSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unchangedViews();
                saveSix.setVisibility(View.VISIBLE);
                sixRR.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                sixTv.setTextColor(getResources().getColor(R.color.money));
                sixMonths.setVisibility(View.INVISIBLE);
                amount = "$240";
            }
        });


        offerTwelve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unchangedViews();

                saveTwelve.setVisibility(View.VISIBLE);
                twelveRR.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                twelveTv.setTextColor(getResources().getColor(R.color.money));
                twelveMonths.setVisibility(View.INVISIBLE);

                amount = "$480";
            }
        });


        btnSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getBaseContext(), PaymentMethod.class);
                intent.putExtra("AMOUNT", amount);
                startActivity(intent);

            }
        });
    }


    private void unchangedViews() {

        saveOne.setVisibility(View.INVISIBLE);
        saveThree.setVisibility(View.INVISIBLE);
        saveSix.setVisibility(View.INVISIBLE);
        saveTwelve.setVisibility(View.INVISIBLE);


        // saveOne.setTextColor(getResources().getColor(R.color.colorWhite));
        // saveThree.setTextColor(getResources().getColor(R.color.colorWhite));
        // saveSix.setTextColor(getResources().getColor(R.color.colorWhite));
        // saveTwelve.setTextColor(getResources().getColor(R.color.colorWhite));


        oneRR.setBackgroundColor(getResources().getColor(R.color.money));
        threeRR.setBackgroundColor(getResources().getColor(R.color.money));
        sixRR.setBackgroundColor(getResources().getColor(R.color.money));
        twelveRR.setBackgroundColor(getResources().getColor(R.color.money));


        oneTv.setVisibility(View.VISIBLE);
        threeTv.setVisibility(View.VISIBLE);
        sixTv.setVisibility(View.VISIBLE);
        twelveTv.setVisibility(View.VISIBLE);

        oneMonth.setVisibility(View.VISIBLE);
        threeMonths.setVisibility(View.VISIBLE);
        sixMonths.setVisibility(View.VISIBLE);
        twelveMonths.setVisibility(View.VISIBLE);


        oneTv.setTextColor(getResources().getColor(R.color.colorWhite));
        threeTv.setTextColor(getResources().getColor(R.color.colorWhite));
        sixTv.setTextColor(getResources().getColor(R.color.colorWhite));
        twelveTv.setTextColor(getResources().getColor(R.color.colorWhite));


    }

}
