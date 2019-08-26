package com.smnetinfo.savememory;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smnetinfo.savememory.adapter.SpinnerAdapter;
import com.smnetinfo.savememory.database.PostsDataSource;
import com.smnetinfo.savememory.database.SettingsDataSource;
import com.smnetinfo.savememory.database.UserDataSource;
import com.smnetinfo.savememory.extras.BaseActivity;
import com.smnetinfo.savememory.extras.ProgressDialog;
import com.smnetinfo.savememory.extras.WebConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProfileCompletionActivity extends BaseActivity implements WebConstants {  //, AdapterView.OnItemSelectedListener

    EditText activityProfileCompletionNameET;
    Button activityProfileCompletionSaveCV;
    Spinner activityProfileCompletionSexSpinner;
    //activityProfileCompletionDateSpinner,activityProfileCompletionMonthSpinner,activityProfileCompletionYearSpinner,
    ProgressDialog progressDialog;

    UserDataSource userDataSource;
    PostsDataSource postsDataSource;
    SettingsDataSource settingsDataSource;
    TextView genderTv;

    //    int year = -1;
//    int dayOfMonth = -1;
//    int monthOfYear = -1;
    boolean isGenderSet = false;
    int genderChek = 0;
    LinearLayout calendarLayout;
    TextView dobTv;

    JSONObject jsonObject = new JSONObject();
    String[] countryNames = {"Select Gender", "Male", "Female", "Other"};
    int flags[] = {R.drawable.gender_sel, R.drawable.male_icon, R.drawable.female_icon, R.drawable.other_icon};
    private int mYear = -1, mMonth = -1, mDay = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_completion);

        progressDialog = new ProgressDialog(this);
        userDataSource = UserDataSource.sharedInstance(this);
        postsDataSource = PostsDataSource.sharedInstance(this);
        settingsDataSource = SettingsDataSource.sharedInstance(this);

        activityProfileCompletionSaveCV = findViewById(R.id.activityProfileCompletionSaveCV);

        activityProfileCompletionNameET = findViewById(R.id.activityProfileCompletionNameET);

        activityProfileCompletionSexSpinner = findViewById(R.id.activityProfileCompletionSexSpinner);
//        activityProfileCompletionDateSpinner = findViewById(R.id.activityProfileCompletionDateSpinner);
//        activityProfileCompletionYearSpinner = findViewById(R.id.activityProfileCompletionYearSpinner);
//        activityProfileCompletionMonthSpinner = findViewById(R.id.activityProfileCompletionMonthSpinner);
        genderTv = (TextView) findViewById(R.id.genderTv);
        calendarLayout = (LinearLayout) findViewById(R.id.calendarLayout);
        dobTv = (TextView) findViewById(R.id.dobTv);
        SpinnerAdapter customAdapter = new SpinnerAdapter(getApplicationContext(), flags, countryNames);
        activityProfileCompletionSexSpinner.setAdapter(customAdapter);

        activityProfileCompletionSexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (genderChek > 0 && position != 0) {
                    isGenderSet = true;
                    genderTv.setText(countryNames[position]);
                    setJsonObject(GENDER, countryNames[position]);
                }
                genderChek++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //init Spinners
//        final ArrayList<String> dateArrayList = new ArrayList<>();
//        dateArrayList.add("Date");
//        for (int j=1;j<32;j++){
//            dateArrayList.add(""+j);
//        }
//        ArrayAdapter<String> dateSpinnerArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dateArrayList);
//        activityProfileCompletionDateSpinner.setAdapter(dateSpinnerArrayAdapter);
//        activityProfileCompletionDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i==0){
//                    dayOfMonth = -1;
//                }else {
//                    dayOfMonth = Integer.parseInt(dateArrayList.get(i));
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//        final ArrayList<String> monthArrayList= new ArrayList<>();
//        monthArrayList.add("Month");
//        for (int j=1;j<13;j++){
//            monthArrayList.add(""+j);
//        }
//
//        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, monthArrayList);
//        activityProfileCompletionMonthSpinner.setAdapter(spinnerArrayAdapter);
//        activityProfileCompletionMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i==0){
//                    monthOfYear = -1;
//                }else {
//                    monthOfYear = Integer.parseInt(monthArrayList.get(i)) - 1;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//        final ArrayList<String> yearArrayList= new ArrayList<>();
//        yearArrayList.add("Year");
//        for (int j=1940;j<2031;j++){
//            yearArrayList.add(""+j);
//        }
//
//        ArrayAdapter<String> yearArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, yearArrayList);
//        activityProfileCompletionYearSpinner.setAdapter(yearArrayAdapter);
//        activityProfileCompletionYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i==0){
//                    year = -1;
//                }else {
//                    year = Integer.parseInt(yearArrayList.get(i));
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });


//        final ArrayList<String> sexArrayList = new ArrayList<>();
//        sexArrayList.add("Choose");
//        sexArrayList.add("Male");
//        sexArrayList.add("Female");
//        sexArrayList.add("Others");
//        ArrayAdapter<String> sexSpinnerArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, sexArrayList);
//        activityProfileCompletionSexSpinner.setAdapter(sexSpinnerArrayAdapter);
//        activityProfileCompletionSexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i==0){
//                    isGenderSet = false;
//                    removeKey(GENDER);
//                }else {
//                    isGenderSet = true;
//                    setJsonObject(GENDER, sexArrayList.get(i));
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
        //init Spinners

        activityProfileCompletionSaveCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activityProfileCompletionNameET.getText().length() > 0) {
                    String[] names = activityProfileCompletionNameET.getText().toString().split(" ");
                    setJsonObject(FIRST_NAME, names[0]);
                    if (names.length > 1) {
                        setJsonObject(LAST_NAME, names[1]);
                    }
                    if (mDay > -1 && mMonth > -1 && mYear > -1) {
                        //                       Calendar calendar = Calendar.getInstance();
//                        if (calendar.get(Calendar.YEAR) == (mYear)) {
//                            System.out.println("MY_DATE "+mYear);
//                            System.out.println("MY_DATE "+mMonth);
//                            System.out.println("MY_DATE "+mDay);
//                            calendar.set(Calendar.YEAR, mYear);
//                            calendar.set(Calendar.MONTH, mMonth);
//                            calendar.set(Calendar.DAY_OF_MONTH, mDay);
//                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_SUB_FORMAT, Locale.getDefault());
//                            String str = simpleDateFormat.format(calendar.getTime());
//                            System.out.println("MY_DATE "+str);
                        setJsonObject(DOB, dobTv.getText().toString());
                        if (isGenderSet) {
                            profileCompletionAPI();
                        } else {
                            removeKey(GENDER);
                            Toast.makeText(getApplicationContext(), "Gender is not selected", Toast.LENGTH_SHORT).show();
                        }
//                        } else {
//                            removeKey(DOB);
//                            Toast.makeText(getApplicationContext(), "Selected date is out of Bound", Toast.LENGTH_SHORT).show();
//                        }
                    } else {
                        removeKey(DOB);
                        Toast.makeText(getApplicationContext(), "DOB not set", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    removeKey(FIRST_NAME);
                    removeKey(LAST_NAME);
                    Toast.makeText(getApplicationContext(), "Name field is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        calendarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate();
            }
        });

    }

    private void profileCompletionAPI() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, USER_PROFILE_COMPLETE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("API-SignIn", response);
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(STATUS)) {
                                    userDataSource.deleteUserData();
                                    userDataSource.insertUserData(jsonObject.getString(DATA));
                                    settingsDataSource.userLoginSuccess();
                                    getPostsApi();
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
                            startActivity(new Intent(ProfileCompletionActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
                        startActivity(new Intent(ProfileCompletionActivity.this, MainActivity.class));
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

    private void setJsonObject(String key, String value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeKey(String key) {
        jsonObject.remove(key);
    }

//   @Override
//    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
//       Toast.makeText(getApplicationContext(), "Spinner Call "+genderChek, Toast.LENGTH_SHORT).show();
//       if(genderChek > 0) {
//           //Toast.makeText(getApplicationContext(), countryNames[position], Toast.LENGTH_LONG).show();
//           isGenderSet = true;
//           genderTv.setText(countryNames[position]);
//           setJsonObject(GENDER, countryNames[position]);
//       }
//       genderChek++;
//      // System.out.println("GENTER_CHEKE "+genderChek);
//    }

//   @Override
//   public void onNothingSelected(AdapterView<?> arg0) {
//        // TODO Auto-generated method stub
//        }

    public void showDate() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dobTv.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

}
