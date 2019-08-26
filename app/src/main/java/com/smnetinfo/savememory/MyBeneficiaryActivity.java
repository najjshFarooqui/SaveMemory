package com.smnetinfo.savememory;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smnetinfo.savememory.adapter.BeneficiaryListRecyclerAdapter;
import com.smnetinfo.savememory.database.UserDataSource;
import com.smnetinfo.savememory.extras.BaseActivity;
import com.smnetinfo.savememory.extras.ProgressDialog;
import com.smnetinfo.savememory.extras.WebConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyBeneficiaryActivity extends BaseActivity implements WebConstants {

    CardView activityMyBeneficiaryAddCV;
    RecyclerView activityMyBeneficiaryRV;
    AppCompatImageView activityMyBeneficiaryBackIV;

    BeneficiaryListRecyclerAdapter beneficiaryListRecyclerAdapter;

    UserDataSource userDataSource;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_beneficiary);

        progressDialog = new ProgressDialog(this);
        userDataSource = UserDataSource.sharedInstance(this);

        activityMyBeneficiaryRV = findViewById(R.id.activityMyBeneficiaryRV);

        activityMyBeneficiaryAddCV = findViewById(R.id.activityMyBeneficiaryAddCV);

        activityMyBeneficiaryBackIV = findViewById(R.id.activityMyBeneficiaryBackIV);

        activityMyBeneficiaryAddCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyBeneficiaryActivity.this, AddBeneficiaryActivity.class));
            }
        });

        activityMyBeneficiaryBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        beneficiaryListRecyclerAdapter = new BeneficiaryListRecyclerAdapter(this, new JSONArray());
        activityMyBeneficiaryRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        activityMyBeneficiaryRV.setAdapter(beneficiaryListRecyclerAdapter);
        beneficiaryListRecyclerAdapter.setOnClickListener(new BeneficiaryListRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(JSONObject jsonObject) {
                startActivity(new Intent(MyBeneficiaryActivity.this, EditBeneficiaryActivity.class).putExtra(DATA, jsonObject.toString()));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getBeneficiaryApi();
    }

    private void getBeneficiaryApi() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_BENEFICIARY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("API-Beneficiary", response);
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(STATUS)) {
                                    beneficiaryListRecyclerAdapter.notifyList(jsonObject.getJSONArray(DATA));
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

}
