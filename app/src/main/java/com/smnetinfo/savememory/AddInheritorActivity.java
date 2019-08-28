package com.smnetinfo.savememory;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smnetinfo.savememory.adapter.CountrySpinnerAdapter;
import com.smnetinfo.savememory.database.UserDataSource;
import com.smnetinfo.savememory.extras.AwsS3Util;
import com.smnetinfo.savememory.extras.ProgressDialog;
import com.smnetinfo.savememory.extras.WebConstants;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddInheritorActivity extends AppCompatActivity implements WebConstants {

    AppCompatImageView activityMyWillBackIV;
    CardView fragmentInheritorSaveCV, fragmentInheritorAddMoreCV;
    Spinner fragmentInheritorSexSpinner, fragmentInheritorDateSpinner, fragmentInheritorMonthSpinner,
            fragmentInheritorYearSpinner, fragmentInheritorPhoneCodeSpinner;
    EditText fragmentInheritorFirstNameET, fragmentInheritorLastNameET, fragmentInheritorRelationshipET,
            fragmentInheritorRelationshipInfoET, fragmentInheritorNationalityET, fragmentInheritorInfoET,
            fragmentInheritorEmailET, fragmentInheritorPhoneET;
    AppCompatImageView fragmentInheritorFirstNameEditIV, fragmentInheritorLastNameEditIV, fragmentInheritorRelationshipEditIV,
            fragmentInheritorRelationshipInfoEditIV, fragmentInheritorEmailEditIV, fragmentInheritorPhoneEditIV,
            fragmentInheritorNationalityEditIV, fragmentInheritorInfoEditIV;
    AmazonS3 amazonS3;

    //  BeneficiaryListRecyclerAdapter beneficiaryListRecyclerAdapter;
    ProgressDialog progressDialog;
    boolean isGenderSet = false;
    boolean notLoadingImage = true;
    int year = -1;
    int dayOfMonth = -1;
    int monthOfYear = -1;
    String userImageUrl;
    JSONObject mainJsonObject = new JSONObject();
    UserDataSource userDataSource;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inheritor);
        initialize();
    }

    public void initialize() {
        saveButton = findViewById(R.id.saveButton);
        progressDialog = new ProgressDialog(this);
        amazonS3 = new AwsS3Util().getS3Client(this);
        userDataSource = UserDataSource.sharedInstance(this);
        fragmentInheritorSexSpinner = findViewById(R.id.fragmentInheritorSexSpinner);
        fragmentInheritorDateSpinner = findViewById(R.id.fragmentInheritorDateSpinner);
        fragmentInheritorYearSpinner = findViewById(R.id.fragmentInheritorYearSpinner);
        fragmentInheritorMonthSpinner = findViewById(R.id.fragmentInheritorMonthSpinner);
        fragmentInheritorPhoneCodeSpinner = findViewById(R.id.fragmentInheritorPhoneCodeSpinner);

        fragmentInheritorInfoET = findViewById(R.id.fragmentInheritorInfoET);
        fragmentInheritorEmailET = findViewById(R.id.fragmentInheritorEmailET);
        fragmentInheritorPhoneET = findViewById(R.id.fragmentInheritorPhoneET);
        fragmentInheritorLastNameET = findViewById(R.id.fragmentInheritorLastNameET);
        fragmentInheritorFirstNameET = findViewById(R.id.fragmentInheritorFirstNameET);
        fragmentInheritorNationalityET = findViewById(R.id.fragmentInheritorNationalityET);
        fragmentInheritorRelationshipET = findViewById(R.id.fragmentInheritorRelationshipET);
        fragmentInheritorRelationshipInfoET = findViewById(R.id.fragmentInheritorRelationshipInfoET);

        fragmentInheritorInfoEditIV = findViewById(R.id.fragmentInheritorInfoEditIV);
        fragmentInheritorEmailEditIV = findViewById(R.id.fragmentInheritorEmailEditIV);
        fragmentInheritorPhoneEditIV = findViewById(R.id.fragmentInheritorPhoneEditIV);
        fragmentInheritorLastNameEditIV = findViewById(R.id.fragmentInheritorLastNameEditIV);
        fragmentInheritorFirstNameEditIV = findViewById(R.id.fragmentInheritorFirstNameEditIV);
        fragmentInheritorNationalityEditIV = findViewById(R.id.fragmentInheritorNationalityEditIV);
        fragmentInheritorRelationshipEditIV = findViewById(R.id.fragmentInheritorRelationshipEditIV);
        fragmentInheritorRelationshipInfoEditIV = findViewById(R.id.fragmentInheritorRelationshipInfoEditIV);

        fragmentInheritorSaveCV = findViewById(R.id.fragmentInheritorSaveCV);
        fragmentInheritorAddMoreCV = findViewById(R.id.fragmentInheritorAddMoreCV);
        activityMyWillBackIV = findViewById(R.id.activityMyWillBackIV);

        activityMyWillBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //init Spinners
        final ArrayList<String> dateArrayList = new ArrayList<>();
        dateArrayList.add("Date");
        for (int j = 1; j < 32; j++) {
            dateArrayList.add("" + j);
        }
        ArrayAdapter<String> dateSpinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dateArrayList);
        fragmentInheritorDateSpinner.setAdapter(dateSpinnerArrayAdapter);
        fragmentInheritorDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    dayOfMonth = -1;
                } else {
                    dayOfMonth = Integer.parseInt(dateArrayList.get(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final ArrayList<String> monthArrayList = new ArrayList<>();
        monthArrayList.add("Month");
        for (int j = 1; j < 13; j++) {
            monthArrayList.add("" + j);
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthArrayList);
        fragmentInheritorMonthSpinner.setAdapter(spinnerArrayAdapter);
        fragmentInheritorMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    monthOfYear = -1;
                } else {
                    monthOfYear = Integer.parseInt(monthArrayList.get(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final ArrayList<String> yearArrayList = new ArrayList<>();
        yearArrayList.add("Year");
        for (int j = 1940; j < 2031; j++) {
            yearArrayList.add("" + j);
        }

        ArrayAdapter<String> yearArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearArrayList);
        fragmentInheritorYearSpinner.setAdapter(yearArrayAdapter);
        fragmentInheritorYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    year = -1;
                } else {
                    year = Integer.parseInt(yearArrayList.get(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final ArrayList<String> sexArrayList = new ArrayList<>();
        sexArrayList.add("Choose");
        sexArrayList.add("Male");
        sexArrayList.add("Female");
        sexArrayList.add("Others");
        ArrayAdapter<String> sexSpinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sexArrayList);
        fragmentInheritorSexSpinner.setAdapter(sexSpinnerArrayAdapter);
        fragmentInheritorSexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    isGenderSet = false;
                    removeKey(GENDER);
                } else {
                    isGenderSet = true;
                    setJsonObject(GENDER, sexArrayList.get(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        JSONArray isdJsonArray = new JSONArray();
        final ArrayList<String> isdArrayList = new ArrayList<>();
        String jsonISD;
        try {
            InputStream inputStream = getAssets().open("Country.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            jsonISD = new String(buffer, "UTF-8");
            isdJsonArray = new JSONArray(jsonISD);
            for (int i = 0; i < isdJsonArray.length(); i++) {
                isdArrayList.add(isdJsonArray.getJSONObject(i).getString("dial_code"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        CountrySpinnerAdapter countrySpinnerAdapter = new CountrySpinnerAdapter(this, isdJsonArray);
        fragmentInheritorPhoneCodeSpinner.setAdapter(countrySpinnerAdapter);
        fragmentInheritorPhoneCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    removeKey(ISD_CODE);
                } else {
                    setJsonObject(ISD_CODE, isdArrayList.get(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentInheritorFirstNameET.getText().length() > 0) {
                    setJsonObject(FIRST_NAME, fragmentInheritorFirstNameET.getText().toString());
                    if (fragmentInheritorLastNameET.getText().length() > 0) {
                        setJsonObject(LAST_NAME, fragmentInheritorLastNameET.getText().toString());
                        if (isGenderSet) {
                            if (dayOfMonth > -1 && monthOfYear > -1 && year > -1) {
                                Calendar calendar = Calendar.getInstance();
                                if (calendar.get(Calendar.YEAR) > (year + 2)) {
                                    calendar.set(Calendar.YEAR, year);
                                    calendar.set(Calendar.MONTH, monthOfYear);
                                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_SUB_FORMAT, Locale.getDefault());
                                    setJsonObject(DOB, simpleDateFormat.format(calendar.getTime()));
                                    if (fragmentInheritorRelationshipET.getText().length() > 0) {
                                        setJsonObject(RELATIONSHIP, fragmentInheritorRelationshipET.getText().toString());
                                        setJsonObject(RELATIONSHIP_INFO, fragmentInheritorRelationshipInfoET.getText().toString());
                                        setJsonObject(NATIONALITY, fragmentInheritorNationalityET.getText().toString());
                                        setJsonObject(INFO, fragmentInheritorInfoET.getText().toString());
                                        createBeneficiaryApi();
                                    } else {
                                        removeKey(RELATIONSHIP);
                                        Toast.makeText(AddInheritorActivity.this, "Relationship not set", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    removeKey(DOB);
                                    Toast.makeText(AddInheritorActivity.this, "Selected date is out of Bound", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                removeKey(DOB);
                                Toast.makeText(AddInheritorActivity.this, "DOB not set", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            removeKey(GENDER);
                            Toast.makeText(AddInheritorActivity.this, "Gender is not selected", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        removeKey(LAST_NAME);
                        Toast.makeText(AddInheritorActivity.this, "Last name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    removeKey(FIRST_NAME);
                    Toast.makeText(AddInheritorActivity.this, "First name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fragmentInheritorInfoEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentInheritorInfoET.setEnabled(true);
                fragmentInheritorInfoEditIV.setVisibility(View.GONE);
                fragmentInheritorSaveCV.setVisibility(View.VISIBLE);
            }
        });

        fragmentInheritorEmailEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentInheritorEmailET.setEnabled(true);
                fragmentInheritorEmailEditIV.setVisibility(View.GONE);
                fragmentInheritorSaveCV.setVisibility(View.VISIBLE);
            }
        });

        fragmentInheritorPhoneEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentInheritorPhoneET.setEnabled(true);
                fragmentInheritorPhoneEditIV.setVisibility(View.GONE);
                fragmentInheritorSaveCV.setVisibility(View.VISIBLE);
            }
        });

        fragmentInheritorLastNameEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentInheritorLastNameET.setEnabled(true);
                fragmentInheritorLastNameEditIV.setVisibility(View.GONE);
                fragmentInheritorSaveCV.setVisibility(View.VISIBLE);
            }
        });

        fragmentInheritorFirstNameEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentInheritorFirstNameET.setEnabled(true);
                fragmentInheritorFirstNameEditIV.setVisibility(View.GONE);
                fragmentInheritorSaveCV.setVisibility(View.VISIBLE);
            }
        });

        fragmentInheritorNationalityEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentInheritorNationalityET.setEnabled(true);
                fragmentInheritorNationalityEditIV.setVisibility(View.GONE);
                fragmentInheritorSaveCV.setVisibility(View.VISIBLE);
            }
        });

        fragmentInheritorRelationshipEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentInheritorRelationshipET.setEnabled(true);
                fragmentInheritorRelationshipEditIV.setVisibility(View.GONE);
                fragmentInheritorSaveCV.setVisibility(View.VISIBLE);
            }
        });

        fragmentInheritorRelationshipInfoEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentInheritorRelationshipInfoET.setEnabled(true);
                fragmentInheritorRelationshipInfoEditIV.setVisibility(View.GONE);
                fragmentInheritorSaveCV.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                CropImage.activity(uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.OVAL).setAspectRatio(1, 1)
                        .start(this);
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                CropImage.activity(getCaptureUri())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.OVAL).setAspectRatio(1, 1)
                        .start(this);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                new UploadImage().execute(saveBitmapProfileImage(resultUri));
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("Gallery Image:", error.getLocalizedMessage());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePictureDialog();
            } else {
                Toast.makeText(this, "Cannot get image without Storage Read Permission", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 3) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getCaptureUri());
                startActivityForResult(intent, 2);
            } else {
                Toast.makeText(this, "Cannot capture image without Camera Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setJsonObject(String key, String value) {
        try {
            mainJsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeKey(String key) {
        mainJsonObject.remove(key);
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
                                    if (jsonObject.getJSONArray(DATA).length() > 0) {
                                        fragmentInheritorSaveCV.setVisibility(View.GONE);
                                        fragmentInheritorAddMoreCV.setVisibility(View.VISIBLE);
//                                        beneficiaryListRecyclerAdapter.notifyList(jsonObject.getJSONArray(DATA));
                                    } else {
                                        fragmentInheritorAddMoreCV.setVisibility(View.GONE);
                                        fragmentInheritorSaveCV.setVisibility(View.VISIBLE);
                                    }
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

    private void createBeneficiaryApi() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CREATE_BENEFICIARY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.w("API-Beneficiary", response);
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(STATUS)) {
                                    mainJsonObject = new JSONObject();
                                    fragmentInheritorInfoET.setText("");
                                    fragmentInheritorEmailET.setText("");
                                    fragmentInheritorPhoneET.setText("");
                                    fragmentInheritorLastNameET.setText("");
                                    fragmentInheritorFirstNameET.setText("");
                                    fragmentInheritorNationalityET.setText("");
                                    fragmentInheritorRelationshipET.setText("");
                                    fragmentInheritorRelationshipInfoET.setText("");
                                    fragmentInheritorSexSpinner.setSelection(0);
                                    fragmentInheritorDateSpinner.setSelection(0);
                                    fragmentInheritorYearSpinner.setSelection(0);
                                    fragmentInheritorMonthSpinner.setSelection(0);
                                    fragmentInheritorInfoEditIV.setVisibility(View.GONE);
                                    fragmentInheritorEmailEditIV.setVisibility(View.GONE);
                                    fragmentInheritorPhoneEditIV.setVisibility(View.GONE);
                                    fragmentInheritorLastNameEditIV.setVisibility(View.GONE);
                                    fragmentInheritorFirstNameEditIV.setVisibility(View.GONE);
                                    fragmentInheritorNationalityEditIV.setVisibility(View.GONE);
                                    fragmentInheritorRelationshipEditIV.setVisibility(View.GONE);
                                    fragmentInheritorRelationshipInfoEditIV.setVisibility(View.GONE);
                                    fragmentInheritorInfoET.setEnabled(true);
                                    fragmentInheritorEmailET.setEnabled(true);
                                    fragmentInheritorPhoneET.setEnabled(true);
                                    fragmentInheritorLastNameET.setEnabled(true);
                                    fragmentInheritorFirstNameET.setEnabled(true);
                                    fragmentInheritorNationalityET.setEnabled(true);
                                    fragmentInheritorRelationshipET.setEnabled(true);
                                    fragmentInheritorRelationshipInfoET.setEnabled(true);
//                                    getBeneficiaryApi();
                                    Toast.makeText(AddInheritorActivity.this, "Beneficiary created Successful", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddInheritorActivity.this, jsonObject.getString(DATA), Toast.LENGTH_SHORT).show();
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
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            public byte[] getBody() {
                try {
                    mainJsonObject.put(USER_ID, userDataSource.getUserId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return mainJsonObject.toString().getBytes();
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

    private void choosePictureDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choose_picture);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(true);
        dialog.show();

        CardView dialogChooseCameraTextView = dialog.findViewById(R.id.dialogChooseCameraTextView);
        CardView dialogChooseGalleryTextView = dialog.findViewById(R.id.dialogChooseGalleryTextView);

        dialogChooseCameraTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 3);
                    } else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, getCaptureUri());
                        startActivityForResult(intent, 2);
                    }
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, getCaptureUri());
                    startActivityForResult(intent, 2);
                }
                dialog.dismiss();
            }
        });

        dialogChooseGalleryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                dialog.dismiss();
            }
        });

    }

    private String saveBitmapProfileImage(Uri uri) {
        File folder = new File(Environment.getExternalStorageDirectory() + ROOT_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory() + TEMP_PROFILE_IMAGE_PATH);
        try {
            Bitmap bitmap = resize(MediaStore.Images.Media.getBitmap(getContentResolver(), uri));
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, bytes);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes.toByteArray());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (file.exists()) {
            return file.getPath();
        } else {
            return "";
        }
    }

    private Bitmap resize(Bitmap image) {
        int maxWidth = 1000;
        int maxHeight = 1000;
        int width = image.getWidth();
        int height = image.getHeight();
        float ratioBitmap = (float) width / (float) height;
        float ratioMax = (float) maxWidth / (float) maxHeight;
        int finalWidth = maxWidth;
        int finalHeight = maxHeight;
        if (ratioMax > 1) {
            finalWidth = (int) ((float) maxHeight * ratioBitmap);
        } else {
            finalHeight = (int) ((float) maxWidth / ratioBitmap);
        }
        image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
        return image;
    }

    private Uri getCaptureUri() {
        File folder = new File(Environment.getExternalStorageDirectory() + ROOT_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory() + TEMP_PROFILE_IMAGE_PATH);
        return FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
    }

    /*private void setImageView(String imageUrl){
        fragmentInheritorPB.setVisibility(View.VISIBLE);
        Picasso.with(getContext())
                .load(imageUrl)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(fragmentInheritorImageIV, new Callback() {
                    @Override
                    public void onSuccess() {
                        fragmentInheritorPB.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError() {
                        fragmentInheritorPB.setVisibility(View.GONE);
                    }
                });
    }*/

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return 0;
    }

    @SuppressLint("StaticFieldLeak")
    private class UploadImage extends AsyncTask<String, String, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            notLoadingImage = false;
        }

        @Override
        protected Boolean doInBackground(String... f_url) {
            try {
                File file = new File(f_url[0]);
                String OriginalFileName = USER_IMAGE_PATH + System.currentTimeMillis() + EXT_IMAGE;
                amazonS3.putObject(new PutObjectRequest(BUCKET_NAME, OriginalFileName, file).withCannedAcl(CannedAccessControlList.PublicReadWrite));
                userImageUrl = S3_BUCKET_URL + OriginalFileName;
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            } finally {
                File picture = new File(f_url[0]);
                if (picture.exists()) {
                    picture.delete();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean failed) {
            progressDialog.dismiss();
            notLoadingImage = true;
            if (failed) {
                Toast.makeText(AddInheritorActivity.this, "Error uploading image", Toast.LENGTH_SHORT).show();
            } else {
                setJsonObject(BENEFICIARY_AVATAR, userImageUrl);
                //setImageView(userImageUrl);
            }
        }
    }
}
