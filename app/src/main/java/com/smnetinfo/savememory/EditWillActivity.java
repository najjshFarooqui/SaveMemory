package com.smnetinfo.savememory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class EditWillActivity extends BaseActivity implements WebConstants {

    EditText activityEditWillTitleET, activityEditWillDescriptionET;
    AppCompatImageView activityEditWillBackIV;
    TextView activityEditWillDeleteTV, activityEditWillEditTV, activityEditWillOpenAttachTV;
    CardView activityEditWillMoreCV, activityEditWillMoreContentCV, activityEditWillOpenAttachCV;

    ProgressDialog progressDialog;

    private JSONObject currentWillJSONObject = new JSONObject();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_will);

        progressDialog = new ProgressDialog(this);

        activityEditWillBackIV = findViewById(R.id.activityEditWillBackIV);

        activityEditWillTitleET = findViewById(R.id.activityEditWillTitleET);
        activityEditWillDescriptionET = findViewById(R.id.activityEditWillDescriptionET);

        activityEditWillEditTV = findViewById(R.id.activityEditWillEditTV);
        activityEditWillDeleteTV = findViewById(R.id.activityEditWillDeleteTV);
        activityEditWillOpenAttachTV = findViewById(R.id.activityEditWillOpenAttachTV);

        activityEditWillMoreCV = findViewById(R.id.activityEditWillMoreCV);
        activityEditWillOpenAttachCV = findViewById(R.id.activityEditWillOpenAttachCV);
        activityEditWillMoreContentCV = findViewById(R.id.activityEditWillMoreContentCV);

        //init
        try {
            currentWillJSONObject = new JSONObject(getIntent().getExtras().getString(DATA));

            activityEditWillTitleET.setText(currentWillJSONObject.getString(TITLE));
            activityEditWillDescriptionET.setText(currentWillJSONObject.getString(DESCRIPTION));

            switch (currentWillJSONObject.getInt(TYPE)) {
                case POST_TYPE_TEXT:
                    activityEditWillOpenAttachCV.setVisibility(View.GONE);
                    break;
                case POST_TYPE_IMAGE:
                    activityEditWillOpenAttachTV.setText("Open Image");
                    activityEditWillOpenAttachCV.setVisibility(View.VISIBLE);
                    activityEditWillOpenAttachCV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                startActivity(new Intent(getApplicationContext(), ImageActivity.class).putExtra(URL, currentWillJSONObject.getString(CONTENT_URL)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
                case POST_TYPE_DOC:
                    activityEditWillOpenAttachTV.setText("Open Doc");
                    activityEditWillOpenAttachCV.setVisibility(View.VISIBLE);
                    activityEditWillOpenAttachCV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                startActivity(new Intent(getApplicationContext(), FileWebView.class).putExtra(URL, currentWillJSONObject.getString(CONTENT_URL)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
                case POST_TYPE_VIDEO:
                    activityEditWillOpenAttachTV.setText("Open Video");
                    activityEditWillOpenAttachCV.setVisibility(View.VISIBLE);
                    activityEditWillOpenAttachCV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                startActivity(new Intent(getApplicationContext(), VideoStreamActivity.class).putExtra(URL, currentWillJSONObject.getString(CONTENT_URL)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        }
        //init

        /*activityEditWillDocumentCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(currentWillJSONObject.getInt(TYPE)==POST_TYPE_IMAGE) {
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObjectContent = new JSONObject();
                        jsonObjectContent.put(CONTENT_URL, currentWillJSONObject.getString(CONTENT_URL));
                        jsonArray.put(jsonObjectContent);
                        startActivity(new Intent(EditWillActivity.this, ImagesActivity.class).putExtra(DATA, jsonArray.toString()));
                    }else if(currentWillJSONObject.getInt(TYPE)==POST_TYPE_DOC){
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(currentWillJSONObject.getString(CONTENT_URL)), "application/pdf");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });*/

        activityEditWillMoreCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activityEditWillMoreContentCV.getVisibility() == View.VISIBLE) {
                    activityEditWillMoreContentCV.setVisibility(View.GONE);
                } else {
                    activityEditWillMoreContentCV.setVisibility(View.VISIBLE);
                }
            }
        });

        activityEditWillEditTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activityEditWillTitleET.isEnabled()) {
                    activityEditWillEditTV.setText("Edit");
                    activityEditWillTitleET.setEnabled(false);
                    activityEditWillDescriptionET.setEnabled(false);
                    try {
                        if (activityEditWillTitleET.getText().length() > 0) {
                            currentWillJSONObject.put(TITLE, activityEditWillTitleET.getText().toString());
                            if (activityEditWillDescriptionET.getText().length() > 0) {
                                currentWillJSONObject.put(DESCRIPTION, activityEditWillDescriptionET.getText().toString());
                                updateWillApi();
                            } else {
                                Toast.makeText(getApplicationContext(), "Description is empty", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Title is empty", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    activityEditWillEditTV.setText("Update");
                    activityEditWillTitleET.setEnabled(true);
                    activityEditWillDescriptionET.setEnabled(true);
                }
                activityEditWillMoreContentCV.setVisibility(View.GONE);
            }
        });

        activityEditWillDeleteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteWillApi();
                activityEditWillMoreContentCV.setVisibility(View.GONE);
            }
        });

        activityEditWillBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void updateWillApi() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WILL_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("API-Posts", response);
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(STATUS)) {

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
                return currentWillJSONObject.toString().getBytes();
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

    private void deleteWillApi() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WILL_DELETE,
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
                    jsonObject.put(ID, currentWillJSONObject.getString(ID));
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
