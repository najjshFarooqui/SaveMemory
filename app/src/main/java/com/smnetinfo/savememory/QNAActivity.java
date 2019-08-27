package com.smnetinfo.savememory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smnetinfo.savememory.extras.BaseActivity;
import com.smnetinfo.savememory.extras.ProgressDialog;
import com.smnetinfo.savememory.extras.WebConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class QNAActivity extends BaseActivity implements WebConstants {

    EditText activityQNAAskET;
    ProgressDialog progressDialog;
    ImageView activityQNABackCV;
    Button activityQNAAskCV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna);

        progressDialog = new ProgressDialog(this);

        activityQNAAskET = findViewById(R.id.activityQNAAskET);

        activityQNAAskCV = findViewById(R.id.activityQNAAskCV);
        activityQNABackCV = findViewById(R.id.activityQNABackCV);

        activityQNAAskCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFAQApi(activityQNAAskET.getText().toString());
            }
        });

        activityQNABackCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void createFAQApi(final String question) {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ASK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("API-Posts", response);
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(STATUS)) {
                                    finish();
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
                    jsonObject.put(TITLE, question);
                    jsonObject.put(DESCRIPTION, "");
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
