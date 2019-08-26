package com.smnetinfo.savememory;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.smnetinfo.savememory.extras.ProgressDialog;
import com.smnetinfo.savememory.extras.WebConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class LoginActivity extends BaseActivity implements WebConstants {


    Button activityLoginCV;
    CheckBox activityLoginKeepLoggedCB;
    EditText activityLoginEmailET, activityLoginPasswordET;
    LinearLayout activityLoginForgotPasswordLL, activityLoginSignUpLL;

    ProgressDialog progressDialog;

    UserDataSource userDataSource;
    PostsDataSource postsDataSource;
    SettingsDataSource settingsDataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        userDataSource = UserDataSource.sharedInstance(this);
        postsDataSource = PostsDataSource.sharedInstance(this);
        settingsDataSource = SettingsDataSource.sharedInstance(this);

        activityLoginCV = findViewById(R.id.activityLoginCV);

        activityLoginSignUpLL = findViewById(R.id.activityLoginSignUpLL);
        activityLoginForgotPasswordLL = findViewById(R.id.activityLoginForgotPasswordLL);

        activityLoginEmailET = findViewById(R.id.activityLoginEmailET);
        activityLoginPasswordET = findViewById(R.id.activityLoginPasswordET);

        activityLoginKeepLoggedCB = findViewById(R.id.activityLoginKeepLoggedCB);

        activityLoginKeepLoggedCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean keepLogged) {
                settingsDataSource.updateKeepLogged(keepLogged);
            }
        });

        activityLoginCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activityLoginEmailET.getText().length() > 0) {
                    if (isValidEmailAddress(activityLoginEmailET.getText().toString())) {
                        if (activityLoginPasswordET.getText().length() > 0) {
                            loginUserApi();
                        } else {
                            activityLoginPasswordET.setError("Password field is empty!");
                        }
                    } else {
                        activityLoginEmailET.setError("Invalid email!");
                    }
                } else {
                    activityLoginEmailET.setError("Email field is empty!");
                }
            }
        });

        activityLoginForgotPasswordLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        activityLoginSignUpLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (settingsDataSource.isLoggedIn()) {
            finish();
        }
    }

    private void loginUserApi() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, USER_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("API-SignIn", response);
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(STATUS)) {
                                    userDataSource.insertUserData(jsonObject.getString(DATA));
                                    settingsDataSource.userLoginSuccess();
                                    getUserEmailVerified();
                                    Toast.makeText(getApplicationContext(), "SignIn Successful", Toast.LENGTH_SHORT).show();
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
                    jsonObject.put(EMAIL, activityLoginEmailET.getText().toString());
                    jsonObject.put(PASS, activityLoginPasswordET.getText().toString());
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

    private void getUserEmailVerified() {
        progressDialog.show();
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
                                        startActivity(new Intent(LoginActivity.this, VerifyEmailActivity.class));
                                        finish();
                                    }
                                } else {
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
        progressDialog.show();
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
                                        startActivity(new Intent(LoginActivity.this, ProfileCompletionActivity.class));
                                        finish();
                                    }
                                } else {
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
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_POST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("API-Posts", response);
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(STATUS)) {
                                    JSONArray jsonArrayData = jsonObject.getJSONArray(DATA);
                                    for (int i = 0; i < jsonArrayData.length(); i++) {
                                        JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);
                                        postsDataSource.insertPostData(jsonObjectData.getString(ID), jsonObjectData.toString());
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
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

    private boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            new InternetAddress(email).validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

}
