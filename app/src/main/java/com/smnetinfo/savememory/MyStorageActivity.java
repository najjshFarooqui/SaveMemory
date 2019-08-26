package com.smnetinfo.savememory;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smnetinfo.savememory.database.UserDataSource;
import com.smnetinfo.savememory.extras.BaseActivity;
import com.smnetinfo.savememory.extras.ProgressDialog;
import com.smnetinfo.savememory.extras.WebConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class MyStorageActivity extends BaseActivity implements WebConstants {

    AppCompatImageView activityMyStorageBackIV;
    TextView activityMyStorageAvailableSpaceTV;
    PieChartView activityMyStorageSpacePieChartView;

    ProgressDialog progressDialog;
    UserDataSource userDataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_storage);

        progressDialog = new ProgressDialog(this);
        userDataSource = UserDataSource.sharedInstance(this);

        activityMyStorageBackIV = findViewById(R.id.activityMyStorageBackIV);

        activityMyStorageAvailableSpaceTV = findViewById(R.id.activityMyStorageAvailableSpaceTV);

        activityMyStorageSpacePieChartView = findViewById(R.id.activityMyStorageSpacePieChartView);

        activityMyStorageBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getAvailableSpaceApi();
    }

    private void getAvailableSpaceApi() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_SPACE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("API-Profile", response);
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(STATUS)) {
                                    initSpaceChart(jsonObject.getJSONObject(DATA).getLong(USED_SPACE),
                                            jsonObject.getJSONObject(DATA).getLong(REMAINING_SPACE));
                                } else {
                                    Toast.makeText(getApplicationContext(), jsonObject.getString(DATA), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            public byte[] getBody() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(USER_ID, userDataSource.getUserId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObject.toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void initSpaceChart(long used, long available) {
        List<SliceValue> values = new ArrayList<>();
        values.add(new SliceValue().setTarget(used).setColor(getResources().getColor(R.color.usedStorage)));
        values.add(new SliceValue().setTarget(available).setColor(getResources().getColor(R.color.availableStorage)));

        PieChartData pieChartData = new PieChartData(values);
        pieChartData.setHasLabels(false);
        pieChartData.setHasLabelsOnlyForSelected(false);
        pieChartData.setHasLabelsOutside(false);
        pieChartData.setHasCenterCircle(true);
        pieChartData.setCenterCircleScale(0f);
        pieChartData.setSlicesSpacing(0);
        pieChartData.setCenterCircleColor(Color.WHITE);

        activityMyStorageSpacePieChartView.setPieChartData(pieChartData);
        activityMyStorageSpacePieChartView.startDataAnimation();
        activityMyStorageAvailableSpaceTV.setText("Remaining storage: " + getByteToSize(available) + "/" + getByteToSize((used + available)) + " Total Storage");
    }

    private String getByteToSize(double byteInput) {
        DecimalFormat decimalFormat = new DecimalFormat("##.00");
        if (byteInput > 1024) {
            double kb = byteInput / 1024;
            if (kb > 1024) {
                double mb = kb / 1024;
                if (mb > 1024) {
                    double gb = mb / 1024;
                    if (gb > 1024) {
                        double tb = gb / 1024;
                        return decimalFormat.format(tb) + "TB";
                    } else {
                        return decimalFormat.format(gb) + "GB";
                    }
                } else {
                    return decimalFormat.format(mb) + "MB";
                }
            } else {
                return decimalFormat.format(kb) + "KB";
            }
        } else {
            return byteInput + "B";
        }
    }
}
