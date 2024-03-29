package com.smnetinfo.savememory;

import android.Manifest;
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
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.smnetinfo.savememory.database.UserDataSource;
import com.smnetinfo.savememory.extras.AwsS3Util;
import com.smnetinfo.savememory.extras.BaseActivity;
import com.smnetinfo.savememory.extras.ProgressDialog;
import com.smnetinfo.savememory.extras.WebConstants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditBeneficiaryActivity extends BaseActivity implements WebConstants {

    ProgressBar activityEditBeneficiaryPB;
    LinearLayout activityEditBeneficiarySexLL;
    TextView activityEditBeneficiaryDOBTV, activityEditBeneficiarySexTV;
    AppCompatImageView activityEditBeneficiaryBackIV, activityEditBeneficiaryImageIV;
    EditText activityEditBeneficiaryFirstNameET, activityEditBeneficiaryLastNameET, activityEditBeneficiaryNationalityET, activityEditBeneficiaryInfoET,
            activityEditBeneficiaryRelationshipET, activityEditBeneficiaryRelationshipInfoET;
    CardView activityEditBeneficiaryDOBCV, activityEditBeneficiaryGenderMaleCV, activityEditBeneficiaryGenderFemaleCV, activityEditBeneficiaryCreateCV,
            activityEditBeneficiaryGenderOtherCV, activityEditBeneficiarySexCV, activityEditBeneficiaryImageCV;

    AmazonS3 amazonS3;
    ProgressDialog progressDialog;

    boolean isDOBSet = false;
    boolean isGenderSet = false;
    boolean notLoadingImage = true;

    String userImageUrl;

    JSONObject jsonObject = new JSONObject();

    UserDataSource userDataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_beneficiary);

        progressDialog = new ProgressDialog(this);
        amazonS3 = new AwsS3Util().getS3Client(this);
        userDataSource = UserDataSource.sharedInstance(this);

        activityEditBeneficiarySexTV = findViewById(R.id.activityEditBeneficiarySexTV);
        activityEditBeneficiaryDOBTV = findViewById(R.id.activityEditBeneficiaryDOBTV);

        activityEditBeneficiaryPB = findViewById(R.id.activityEditBeneficiaryPB);

        activityEditBeneficiarySexLL = findViewById(R.id.activityEditBeneficiarySexLL);

        activityEditBeneficiaryBackIV = findViewById(R.id.activityEditBeneficiaryBackIV);
        activityEditBeneficiaryImageIV = findViewById(R.id.activityEditBeneficiaryImageIV);

        activityEditBeneficiaryInfoET = findViewById(R.id.activityEditBeneficiaryInfoET);
        activityEditBeneficiaryLastNameET = findViewById(R.id.activityEditBeneficiaryLastNameET);
        activityEditBeneficiaryFirstNameET = findViewById(R.id.activityEditBeneficiaryFirstNameET);
        activityEditBeneficiaryNationalityET = findViewById(R.id.activityEditBeneficiaryNationalityET);
        activityEditBeneficiaryRelationshipET = findViewById(R.id.activityEditBeneficiaryRelationshipET);
        activityEditBeneficiaryRelationshipInfoET = findViewById(R.id.activityEditBeneficiaryRelationshipInfoET);

        activityEditBeneficiarySexCV = findViewById(R.id.activityEditBeneficiarySexCV);
        activityEditBeneficiaryDOBCV = findViewById(R.id.activityEditBeneficiaryDOBCV);
        activityEditBeneficiaryImageCV = findViewById(R.id.activityEditBeneficiaryImageCV);
        activityEditBeneficiaryCreateCV = findViewById(R.id.activityEditBeneficiaryCreateCV);
        activityEditBeneficiaryGenderMaleCV = findViewById(R.id.activityEditBeneficiaryGenderMaleCV);
        activityEditBeneficiaryGenderOtherCV = findViewById(R.id.activityEditBeneficiaryGenderOtherCV);
        activityEditBeneficiaryGenderFemaleCV = findViewById(R.id.activityEditBeneficiaryGenderFemaleCV);

        try {
            jsonObject = new JSONObject(getIntent().getExtras().getString(DATA));
            activityEditBeneficiaryDOBTV.setText(jsonObject.getString(DOB));
            activityEditBeneficiaryInfoET.setText(jsonObject.getString(INFO));
            activityEditBeneficiarySexTV.setText(jsonObject.getString(GENDER));
            activityEditBeneficiaryLastNameET.setText(jsonObject.getString(LAST_NAME));
            activityEditBeneficiaryFirstNameET.setText(jsonObject.getString(FIRST_NAME));
            activityEditBeneficiaryNationalityET.setText(jsonObject.getString(NATIONALITY));
            activityEditBeneficiaryRelationshipET.setText(jsonObject.getString(RELATIONSHIP));
            //activityEditBeneficiaryRelationshipInfoET.setText(jsonObject.getString(RELATIONSHIP_INFO));
            if (!jsonObject.isNull(BENEFICIARY_AVATAR)) {
                setImageView(jsonObject.getString(BENEFICIARY_AVATAR));
            }
            isDOBSet = true;
            isGenderSet = true;
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                if (calendar.get(Calendar.YEAR) > (year + 2)) {
                    isDOBSet = true;
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_SUB_FORMAT, Locale.getDefault());
                    activityEditBeneficiaryDOBTV.setText(simpleDateFormat.format(calendar.getTime()));
                    setJsonObject(DOB, simpleDateFormat.format(calendar.getTime()));
                } else {
                    removeKey(DOB);
                    isDOBSet = false;
                    Toast.makeText(getApplicationContext(), "Selected date is out of Bound", Toast.LENGTH_SHORT).show();
                }
            }
        };

        activityEditBeneficiaryGenderMaleCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isGenderSet = true;
                setJsonObject(GENDER, "male");
                activityEditBeneficiarySexTV.setText("Male");
                activityEditBeneficiarySexLL.setVisibility(View.GONE);
            }
        });

        activityEditBeneficiaryGenderFemaleCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isGenderSet = true;
                setJsonObject(GENDER, "female");
                activityEditBeneficiarySexTV.setText("Female");
                activityEditBeneficiarySexLL.setVisibility(View.GONE);
            }
        });

        activityEditBeneficiaryGenderOtherCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isGenderSet = true;
                setJsonObject(GENDER, "others");
                activityEditBeneficiarySexTV.setText("Others");
                activityEditBeneficiarySexLL.setVisibility(View.GONE);
            }
        });

        activityEditBeneficiaryDOBCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(EditBeneficiaryActivity.this, dateSetListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        activityEditBeneficiarySexCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activityEditBeneficiarySexLL.getVisibility() == View.VISIBLE) {
                    activityEditBeneficiarySexLL.setVisibility(View.GONE);
                } else {
                    activityEditBeneficiarySexLL.setVisibility(View.VISIBLE);
                }
            }
        });

        activityEditBeneficiaryImageCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    } else {
                        choosePictureDialog();
                    }
                } else {
                    choosePictureDialog();
                }
            }
        });

        activityEditBeneficiaryCreateCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notLoadingImage) {
                    if (activityEditBeneficiaryFirstNameET.getText().length() > 0) {
                        setJsonObject(FIRST_NAME, activityEditBeneficiaryFirstNameET.getText().toString());
                        if (activityEditBeneficiaryLastNameET.getText().length() > 0) {
                            setJsonObject(LAST_NAME, activityEditBeneficiaryLastNameET.getText().toString());
                            if (isGenderSet) {
                                if (isDOBSet) {
                                    if (activityEditBeneficiaryRelationshipET.getText().length() > 0) {
                                        setJsonObject(RELATIONSHIP, activityEditBeneficiaryRelationshipET.getText().toString());
                                        setJsonObject(RELATIONSHIP_INFO, activityEditBeneficiaryRelationshipInfoET.getText().toString());
                                        if (activityEditBeneficiaryNationalityET.getText().length() > 0) {
                                            setJsonObject(NATIONALITY, activityEditBeneficiaryNationalityET.getText().toString());
                                            if (activityEditBeneficiaryInfoET.getText().length() > 0) {
                                                setJsonObject(INFO, activityEditBeneficiaryInfoET.getText().toString());
                                                createBeneficiaryApi();
                                            } else {
                                                removeKey(INFO);
                                                Toast.makeText(getApplicationContext(), "Info not set", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            removeKey(NATIONALITY);
                                            Toast.makeText(getApplicationContext(), "Nationality not set", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        removeKey(RELATIONSHIP);
                                        Toast.makeText(getApplicationContext(), "Relationship not set", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    removeKey(DOB);
                                    Toast.makeText(getApplicationContext(), "DOB not set", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                removeKey(GENDER);
                                Toast.makeText(getApplicationContext(), "Gender is not selected", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            removeKey(LAST_NAME);
                            Toast.makeText(getApplicationContext(), "Last name cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        removeKey(FIRST_NAME);
                        Toast.makeText(getApplicationContext(), "First name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Image still uploading", Toast.LENGTH_SHORT).show();
                }
            }
        });

        activityEditBeneficiaryBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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

    private void createBeneficiaryApi() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CREATE_BENEFICIARY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("API-Beneficiary", response);
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(STATUS)) {
                                    Toast.makeText(getApplicationContext(), "Beneficiary created Successful", Toast.LENGTH_SHORT).show();
                                    finish();
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
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 3);
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

    private void setImageView(String imageUrl) {
        activityEditBeneficiaryPB.setVisibility(View.VISIBLE);
        Picasso.with(this)
                .load(imageUrl)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(activityEditBeneficiaryImageIV, new Callback() {
                    @Override
                    public void onSuccess() {
                        activityEditBeneficiaryPB.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        activityEditBeneficiaryPB.setVisibility(View.GONE);
                    }
                });
    }

    private Uri getCaptureUri() {
        File folder = new File(Environment.getExternalStorageDirectory() + ROOT_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory() + TEMP_PROFILE_IMAGE_PATH);
        return FileProvider.getUriForFile(EditBeneficiaryActivity.this, getApplicationContext().getPackageName() + ".provider", file);
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
                setJsonObject(BENEFICIARY_AVATAR, userImageUrl);
                setImageView(userImageUrl);
            }
        }
    }

}
