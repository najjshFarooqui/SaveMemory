package com.smnetinfo.savememory;

import android.Manifest;
import android.arch.core.BuildConfig;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smnetinfo.savememory.database.PostsDataSource;
import com.smnetinfo.savememory.database.SettingsDataSource;
import com.smnetinfo.savememory.database.UserDataSource;
import com.smnetinfo.savememory.extras.BaseActivity;
import com.smnetinfo.savememory.extras.WebConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends BaseActivity implements WebConstants {

    TextView versionCode;

    UserDataSource userDataSource;
    PostsDataSource postsDataSource;
    SettingsDataSource settingsDataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        versionCode = findViewById(R.id.versionCode);
        versionCode.setText("v" + BuildConfig.VERSION_CODE);

        userDataSource = UserDataSource.sharedInstance(this);
        postsDataSource = PostsDataSource.sharedInstance(this);
        settingsDataSource = SettingsDataSource.sharedInstance(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                login();
            }
        } else {
            login();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (requestCode == 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    login();
                } else {
                    Toast.makeText(getApplicationContext(), "Cannot open without Storage Permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void login() {
        if (!settingsDataSource.isKeepLogged()) {
            userDataSource.deleteUserData();
            postsDataSource.deletePostData();
            settingsDataSource.deleteSettingsData();
        }
        if (settingsDataSource.isLoggedIn()) {
            getUserEmailVerified();
        } else {
            startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            finish();
        }
    }

    private void getUserEmailVerified() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, USER_EMAIL_VERIFIED,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("API-Posts", response);
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(STATUS)) {
                                    if (jsonObject.getJSONObject(DATA).getBoolean(IS_ACTIVE)) {
                                        getUserCompletion();
                                    } else {
                                        startActivity(new Intent(SplashActivity.this, VerifyEmailActivity.class));
                                        finish();
                                    }
                                } else {
                                    finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
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

    private void getUserCompletion() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, USER_CHECK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("API-Posts", response);
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(STATUS)) {
                                    if (jsonObject.getBoolean(DATA)) {
                                        getPostsApi();
                                    } else {
                                        startActivity(new Intent(SplashActivity.this, ProfileCompletionActivity.class));
                                        finish();
                                    }
                                } else {
                                    finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
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

    private void getPostsApi() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_POST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("API-Posts", response);
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(STATUS)) {
                                    postsDataSource.deletePostData();
                                    JSONArray jsonArrayData = jsonObject.getJSONArray(DATA);
                                    for (int i = 0; i < jsonArrayData.length(); i++) {
                                        JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);
                                        postsDataSource.insertPostData(jsonObjectData.getString(ID), jsonObjectData.toString());
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
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
