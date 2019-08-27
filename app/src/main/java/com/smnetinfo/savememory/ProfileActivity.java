package com.smnetinfo.savememory;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.smnetinfo.savememory.extras.BaseActivity;
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

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class ProfileActivity extends BaseActivity implements WebConstants {

    CardView activityProfileSaveTV;
    CardView activityProfileEditLL;
    ImageView activityProfileBackIV;
    LinearLayout calanderDateLayout;
    Spinner activityProfileDateSpinner, activityProfileMonthSpinner, activityProfileYearSpinner, activityProfileSexSpinner,
            activityProfileNationalitySpinner, activityProfileMaritalStatusSpinner, activityProfileISDSpinner;
    EditText activityProfileFirstNameET, activityProfileLastNameET, activityProfileEmailET, activityProfilePhoneET,
            activityProfileCountryET, activityProfileOccupationET, activityProfileAddressET;

    AmazonS3 amazonS3;
    ProgressDialog progressDialog;
    TextView calanderTv;

    boolean isDOBSet = false;
    boolean isGenderSet = false;
    boolean notLoadingImage = true;
    boolean isProfileEditable = true;

    int year;
    int dayOfMonth;
    int monthOfYear;
    private int mYear = -1, mMonth = -1, mDay = -1;

    String userImageUrl;

    JSONObject jsonObject = new JSONObject();

    UserDataSource userDataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        progressDialog = new ProgressDialog(this);
        amazonS3 = new AwsS3Util().getS3Client(this);
        userDataSource = UserDataSource.sharedInstance(this);
        calanderDateLayout = findViewById(R.id.calanderDateLayout);
        calanderTv = findViewById(R.id.calanderTv);


        activityProfileBackIV = findViewById(R.id.activityProfileBackIV);

        activityProfileSaveTV = findViewById(R.id.save);

        activityProfileEditLL = findViewById(R.id.edit);

        activityProfileEmailET = findViewById(R.id.activityProfileEmailET);
        activityProfilePhoneET = findViewById(R.id.activityProfilePhoneET);
        activityProfileCountryET = findViewById(R.id.activityProfileCountryET);
        activityProfileAddressET = findViewById(R.id.activityProfileAddressET);
        activityProfileLastNameET = findViewById(R.id.activityProfileLastNameET);
        activityProfileFirstNameET = findViewById(R.id.activityProfileFirstNameET);
        activityProfileOccupationET = findViewById(R.id.activityProfileOccupationET);


        activityProfileISDSpinner = findViewById(R.id.activityProfileISDSpinner);
        activityProfileSexSpinner = findViewById(R.id.activityProfileSexSpinner);
        ////   activityProfileDateSpinner = findViewById(R.id.activityProfileDateSpinner);
        ////  activityProfileYearSpinner = findViewById(R.id.activityProfileYearSpinner);
        ////   activityProfileMonthSpinner = findViewById(R.id.activityProfileMonthSpinner);
        activityProfileNationalitySpinner = findViewById(R.id.activityProfileNationalitySpinner);
        activityProfileMaritalStatusSpinner = findViewById(R.id.activityProfileMaritalStatusSpinner);

        //init Spinners
        final ArrayList<String> dateArrayList = new ArrayList<>();
        dateArrayList.add("Date");
        for (int j = 1; j < 32; j++) {
            dateArrayList.add("" + j);
        }
        ArrayAdapter<String> dateSpinnerArrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, dateArrayList);
        //activityProfileDateSpinner.setAdapter(dateSpinnerArrayAdapter);
        //activityProfileDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        //    @Override
        //    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //        if (i == 0) {
        //            isDOBSet = false;
        //        } else {
        //            dayOfMonth = Integer.parseInt(dateArrayList.get(i));
        //        }
        //    }
//
        //    @Override
        //    public void onNothingSelected(AdapterView<?> adapterView) {
//
        //    }
        //});
//
        //final ArrayList<String> monthArrayList = new ArrayList<>();
        //monthArrayList.add("Month");
        //for (int j = 1; j < 13; j++) {
        //    monthArrayList.add("" + j);
        //}
        //ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, monthArrayList);
        //activityProfileMonthSpinner.setAdapter(spinnerArrayAdapter);
        //activityProfileMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        //    @Override
        //    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //        if (i == 0) {
        //            isDOBSet = false;
        //        } else {
        //            monthOfYear = Integer.parseInt(monthArrayList.get(i)) - 1;
        //        }
        //    }
//
        //    @Override
        //    public void onNothingSelected(AdapterView<?> adapterView) {
//
        //    }
        //});
//
        //final ArrayList<String> yearArrayList = new ArrayList<>();
        //yearArrayList.add("Year");
        //for (int j = 1940; j < 2031; j++) {
        //    yearArrayList.add("" + j);
        //}
        //ArrayAdapter<String> yearArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, yearArrayList);
        //activityProfileYearSpinner.setAdapter(yearArrayAdapter);
        //activityProfileYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        //    @Override
        //    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //        if (i == 0) {
        //            isDOBSet = false;
        //        } else {
        //            year = Integer.parseInt(yearArrayList.get(i));
        //        }
        //    }
//
        //    @Override
        //    public void onNothingSelected(AdapterView<?> adapterView) {
//
        //    }
        //});

        calanderDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDate();

            }
        });

        final ArrayList<String> sexArrayList = new ArrayList<>();
        sexArrayList.add("Choose");
        sexArrayList.add("Male");
        sexArrayList.add("Female");
        sexArrayList.add("Others");
        ArrayAdapter<String> sexSpinnerArrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, sexArrayList);
        activityProfileSexSpinner.setAdapter(sexSpinnerArrayAdapter);
        activityProfileSexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        final ArrayList<String> martialArrayList = new ArrayList<>();
        martialArrayList.add("Choose");
        martialArrayList.add("Single");
        martialArrayList.add("Married");
        martialArrayList.add("Divorced");
        ArrayAdapter<String> martialSpinnerArrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, martialArrayList);
        activityProfileMaritalStatusSpinner.setAdapter(martialSpinnerArrayAdapter);
        activityProfileMaritalStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    removeKey(MARITAL_STATUS);
                } else {
                    setJsonObject(MARITAL_STATUS, martialArrayList.get(i));
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
        activityProfileISDSpinner.setAdapter(countrySpinnerAdapter);
        activityProfileISDSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        final ArrayList<String> nationalityArrayList = new ArrayList<>();
        String json;
        try {
            nationalityArrayList.add("Choose");
            InputStream inputStream = getAssets().open("Country.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                nationalityArrayList.add(jsonArray.getJSONObject(i).getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> nationalitySpinnerArrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, nationalityArrayList);
        activityProfileNationalitySpinner.setAdapter(nationalitySpinnerArrayAdapter);
        activityProfileNationalitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    removeKey(NATIONALITY);
                } else {
                    setJsonObject(NATIONALITY, nationalityArrayList.get(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //init Spinners

        /*activityProfileFullEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityProfileFullRL.setVisibility(View.GONE);
                activityProfileTopCV.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
                    }else {
                        choosePictureDialog();
                    }
                }else {
                    choosePictureDialog();
                }
            }
        });*/

        activityProfileEditLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isProfileEditable = false;
                activityProfileSaveTV.setVisibility(View.VISIBLE);
                activityProfileEmailET.setEnabled(true);
                activityProfilePhoneET.setEnabled(true);
                activityProfileAddressET.setEnabled(true);
                activityProfileCountryET.setEnabled(true);
                activityProfileLastNameET.setEnabled(true);
                activityProfileFirstNameET.setEnabled(true);
                activityProfileOccupationET.setEnabled(true);
            }
        });

        activityProfileSaveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isProfileEditable) {
                    isProfileEditable = false;
                    activityProfilePhoneET.setEnabled(true);
                    activityProfileCountryET.setEnabled(true);
                } else {
                    if (notLoadingImage) {
                        if (activityProfileEmailET.getText().length() > 0) {
                            if (isValidEmailAddress(activityProfileEmailET.getText().toString())) {
                                setJsonObject(EMAIL, activityProfileEmailET.getText().toString());
                                if (isGenderSet) {
                                    if (isDOBSet) {
                                        // Calendar calendar = Calendar.getInstance();
                                        //z if (calendar.get(Calendar.YEAR) > (year + 2)) {
                                        //     calendar.set(Calendar.YEAR, year);
                                        //     calendar.set(Calendar.MONTH, monthOfYear);
                                        //     calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                        // SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_SUB_FORMAT, Locale.getDefault());
                                        setJsonObject(DOB, calanderTv.getText().toString());
                                            setJsonObject(PHONE_NO, activityProfilePhoneET.getText().toString());
                                            setJsonObject(FIRST_NAME, activityProfileFirstNameET.getText().toString());
                                            setJsonObject(LAST_NAME, activityProfileLastNameET.getText().toString());
                                            setJsonObject(ADDRESS, activityProfileAddressET.getText().toString());
                                            setJsonObject(OCCUPATION, activityProfileOccupationET.getText().toString());
                                            setJsonObject(CURRENT_COUNTRY, activityProfileCountryET.getText().toString());
                                            updateUserApi();
                                        // }     else {
                                        //     removeKey(DOB);
                                        //     isDOBSet = false;
                                        //     Toast.makeText(getApplicationContext(), "Selected date is out of Bound", Toast.LENGTH_SHORT).show();
                                        //      }
                                    } else {
                                        removeKey(DOB);
                                        Toast.makeText(getApplicationContext(), "DOB not set", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    removeKey(GENDER);
                                    Toast.makeText(getApplicationContext(), R.string.gender_not_set, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                removeKey(EMAIL);
                                Toast.makeText(getApplicationContext(), R.string.valid_email, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            removeKey(EMAIL);
                            Toast.makeText(getApplicationContext(), R.string.empty_email, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Profile image is still uploading", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        activityProfileBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //init Details
        JSONObject jsonObject = userDataSource.getUserData();
        try {
            isDOBSet = true;
            if (!jsonObject.isNull(FIRST_NAME)) {
                activityProfileFirstNameET.setText(jsonObject.getString(FIRST_NAME) + " " + jsonObject.getString(LAST_NAME));
            }
            if (!jsonObject.isNull(LAST_NAME)) {
                activityProfileLastNameET.setText(jsonObject.getString(LAST_NAME));
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Calendar dateCal = Calendar.getInstance();
            if (!jsonObject.isNull(DOB)) {
                dateCal.setTime(simpleDateFormat.parse(jsonObject.getString(DOB)));
                activityProfileDateSpinner.setSelection(getIndex(activityProfileDateSpinner, String.valueOf(dateCal.get(Calendar.DAY_OF_MONTH))));
                activityProfileMonthSpinner.setSelection(getIndex(activityProfileMonthSpinner, String.valueOf(dateCal.get(Calendar.MONTH))) + 1);
                activityProfileYearSpinner.setSelection(getIndex(activityProfileYearSpinner, String.valueOf(dateCal.get(Calendar.YEAR))));
            }
            if (!jsonObject.isNull(EMAIL)) {
                activityProfileEmailET.setText(jsonObject.getString(EMAIL));
            }
            if (!jsonObject.isNull(PHONE_NO)) {
                activityProfilePhoneET.setText(jsonObject.getString(PHONE_NO));
            }
            if (!jsonObject.isNull(ISD_CODE)) {
                activityProfileISDSpinner.setSelection(isdArrayList.indexOf(jsonObject.getString(ISD_CODE)));
            }
            if (!jsonObject.isNull(NATIONALITY)) {
                activityProfileNationalitySpinner.setSelection(getIndex(activityProfileNationalitySpinner, jsonObject.getString(NATIONALITY)));
            }
            if (!jsonObject.isNull(MARITAL_STATUS)) {
                activityProfileMaritalStatusSpinner.setSelection(getIndex(activityProfileMaritalStatusSpinner, jsonObject.getString(MARITAL_STATUS)));
            }
            if (!jsonObject.isNull(CURRENT_COUNTRY)) {
                activityProfileCountryET.setText(jsonObject.getString(CURRENT_COUNTRY));
            }
            if (!jsonObject.isNull(GENDER)) {
                activityProfileSexSpinner.setSelection(getIndex(activityProfileSexSpinner, jsonObject.getString(GENDER)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //init Details
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                new ProfileActivity.UploadImage().execute(saveBitmapProfileImage(resultUri));
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
                Toast.makeText(getApplicationContext(), "Cannot get image without Storage Read Permission", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 3) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getCaptureUri());
                startActivityForResult(intent, 2);
            } else {
                Toast.makeText(getApplicationContext(), "Cannot capture image without Camera Permission", Toast.LENGTH_SHORT).show();
            }
        }
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

    private boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            new InternetAddress(email).validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    private void updateUserApi() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, USER_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("API-Profile", response);
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(STATUS)) {
                                    userDataSource.deleteUserData();
                                    userDataSource.insertUserData(jsonObject.getString(DATA));
                                    isProfileEditable = true;
                                    activityProfileEmailET.setEnabled(false);
                                    activityProfilePhoneET.setEnabled(false);
                                    activityProfileCountryET.setEnabled(false);
                                    activityProfileAddressET.setEnabled(false);
                                    activityProfileLastNameET.setEnabled(false);
                                    activityProfileFirstNameET.setEnabled(false);
                                    activityProfileOccupationET.setEnabled(false);
                                    activityProfileSaveTV.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Profile Updated Successful", Toast.LENGTH_SHORT).show();
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
                    jsonObject.put(ID, userDataSource.getUserId());
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

    private String saveBitmapProfileImage(Uri uri) {
        File folder = new File(Environment.getExternalStorageDirectory() + ROOT_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory() + TEMP_PROFILE_IMAGE_PATH);
        try {
            Bitmap bitmap = resize(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri));
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

    private Uri getCaptureUri() {
        File folder = new File(Environment.getExternalStorageDirectory() + ROOT_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory() + TEMP_PROFILE_IMAGE_PATH);
        return FileProvider.getUriForFile(ProfileActivity.this, getApplicationContext().getPackageName() + ".provider", file);
    }

    private void forgotPasswordDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_forgot_password);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(true);
        dialog.show();

        final EditText dialogForgotPasswordET = dialog.findViewById(R.id.dialogForgotPasswordET);

        CardView dialogForgotPasswordSubmitCV = dialog.findViewById(R.id.dialogForgotPasswordSubmitCV);
        CardView dialogForgotPasswordCancelCV = dialog.findViewById(R.id.dialogForgotPasswordCancelCV);

        dialogForgotPasswordSubmitCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogForgotPasswordET.getText().length() > 0) {
                    if (isValidEmailAddress(dialogForgotPasswordET.getText().toString())) {
                        forgotPasswordApi(dialogForgotPasswordET.getText().toString());
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "Email id is invalid", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Email id is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogForgotPasswordCancelCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void forgotPasswordApi(final String emailId) {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, USER_PASSWORD_FORGOT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("API-Posts", response);
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(STATUS)) {
                                    Toast.makeText(getApplicationContext(), "Please check your email", Toast.LENGTH_SHORT).show();
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
                    jsonObject.put(EMAIL, emailId);
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

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
            }
        }
        return index;
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
                Toast.makeText(getApplicationContext(), "Error uploading image", Toast.LENGTH_SHORT).show();
            } else {
                setJsonObject(AVATAR_PATH, userImageUrl);
            }
        }
    }


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
                        calanderTv.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

}
