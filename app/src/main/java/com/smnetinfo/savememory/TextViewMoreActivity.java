package com.smnetinfo.savememory;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smnetinfo.savememory.database.PostsDataSource;
import com.smnetinfo.savememory.extras.BaseActivity;
import com.smnetinfo.savememory.extras.ProgressDialog;
import com.smnetinfo.savememory.extras.WebConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TextViewMoreActivity extends BaseActivity implements WebConstants {

    EditText activityViewMoreContentET;
    AppCompatImageView activityViewMoreBackIV;
    TextView activityViewMoreModifyTV, activityViewMoreDeleteTV;
    CardView activityEditPostMoreCV, activityEditPostMoreContentCV;

    JSONObject postJsonObject = new JSONObject();

    ProgressDialog progressDialog;
    PostsDataSource postsDataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view_more);

        progressDialog = new ProgressDialog(this);
        postsDataSource = PostsDataSource.sharedInstance(this);

        activityViewMoreBackIV = findViewById(R.id.activityViewMoreBackIV);

        activityEditPostMoreCV = findViewById(R.id.activityEditPostMoreCV);
        activityEditPostMoreContentCV = findViewById(R.id.activityEditPostMoreContentCV);

        activityViewMoreModifyTV = findViewById(R.id.activityViewMoreModifyTV);
        activityViewMoreDeleteTV = findViewById(R.id.activityViewMoreDeleteTV);

        activityViewMoreContentET = findViewById(R.id.activityViewMoreContentET);

        try {
            postJsonObject = new JSONObject(getIntent().getExtras().getString(DATA));
            activityViewMoreContentET.setText(postJsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(0).getString(CONTENT_URL));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        activityEditPostMoreCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activityEditPostMoreContentCV.getVisibility() == View.VISIBLE) {
                    activityEditPostMoreContentCV.setVisibility(View.GONE);
                } else {
                    activityEditPostMoreContentCV.setVisibility(View.VISIBLE);
                }
            }
        });

        activityViewMoreModifyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activityViewMoreContentET.isEnabled()) {
                    JSONArray contentArray = new JSONArray();
                    JSONObject contentJsonObject = new JSONObject();
                    try {
                        contentJsonObject.put(URL, activityViewMoreContentET.getText().toString());
                        contentJsonObject.put(URL_SIZE, activityViewMoreContentET.getText().toString().getBytes().length);
                        contentArray.put(contentJsonObject);
                        updatePostApi(postJsonObject.getString(ID), contentArray);
                        activityViewMoreContentET.setEnabled(false);
                        activityViewMoreModifyTV.setText("Modify");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    activityViewMoreContentET.setEnabled(true);
                    activityViewMoreModifyTV.setText("Update");
                }
                activityEditPostMoreContentCV.setVisibility(View.GONE);
            }
        });

        activityViewMoreDeleteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteDialog(postJsonObject.getString(ID));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                activityEditPostMoreContentCV.setVisibility(View.GONE);
            }
        });

        activityViewMoreBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void deleteDialog(final String id) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(true);
        dialog.show();

        CardView dialogDeleteYesCV = dialog.findViewById(R.id.dialogDeleteYesCV);
        CardView dialogDeleteCancelCV = dialog.findViewById(R.id.dialogDeleteCancelCV);

        dialogDeleteYesCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postAllDelete(id);
            }
        });

        dialogDeleteCancelCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void updatePostApi(final String post_id, final JSONArray contentArray) {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("Update-Post", response);
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(STATUS)) {
                                    JSONObject jsonObjectData = jsonObject.getJSONObject(DATA);
                                    postsDataSource.updatePostData(jsonObjectData.getString(ID), jsonObjectData.toString());
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
                    jsonObject.put(POST_ID, post_id);
                    jsonObject.put(CONTENT_ARRAY, contentArray);
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

    private void postAllDelete(final String postId) {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_DELETE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("API-Posts_delete", response);
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(STATUS)) {
                                    postsDataSource.deletePost(postId);
                                    progressDialog.dismiss();
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
                    jsonObject.put(POST_ID, postId);
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
