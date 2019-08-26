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
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.smnetinfo.savememory.database.SettingsDataSource;
import com.smnetinfo.savememory.database.UserDataSource;
import com.smnetinfo.savememory.extras.AwsS3Util;
import com.smnetinfo.savememory.extras.BaseActivity;
import com.smnetinfo.savememory.extras.ProgressDialog;
import com.smnetinfo.savememory.extras.WebConstants;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class SignUpActivity extends BaseActivity implements WebConstants {

    Button activitySignUpCreateCV;
    CheckBox activitySignUpPrivacyPolicyCB, activitySignUpTermsCB;
    TextView activitySignUpPrivacyPolicyTV, activitySignUpTermsTV;
    EditText activitySignUpEmailET, activitySignUpPasswordET, activitySignUpVerifyPasswordET;

    AmazonS3 amazonS3;
    ProgressDialog progressDialog;

    boolean notLoadingImage = true;

    String userImageUrl;

    JSONObject jsonObject = new JSONObject();

    UserDataSource userDataSource;
    SettingsDataSource settingsDataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressDialog = new ProgressDialog(this);
        amazonS3 = new AwsS3Util().getS3Client(this);
        userDataSource = UserDataSource.sharedInstance(this);
        settingsDataSource = SettingsDataSource.sharedInstance(this);

        activitySignUpTermsTV = findViewById(R.id.activitySignUpTermsTV);
        activitySignUpPrivacyPolicyTV = findViewById(R.id.activitySignUpPrivacyPolicyTV);

        activitySignUpCreateCV = findViewById(R.id.activitySignUpCreateCV);

        activitySignUpTermsCB = findViewById(R.id.activitySignUpTermsCB);
        activitySignUpPrivacyPolicyCB = findViewById(R.id.activitySignUpPrivacyPolicyCB);

        activitySignUpEmailET = findViewById(R.id.activitySignUpEmailET);
        activitySignUpPasswordET = findViewById(R.id.activitySignUpPasswordET);
        activitySignUpVerifyPasswordET = findViewById(R.id.activitySignUpVerifyPasswordET);

        activitySignUpTermsCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingsDataSource.updateTOC(b);
            }
        });

        activitySignUpPrivacyPolicyCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingsDataSource.updatePrivacy(b);
            }
        });

        activitySignUpPrivacyPolicyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, PrivacyActivity.class));
            }
        });

        activitySignUpTermsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, TOCActivity.class));
            }
        });

        activitySignUpCreateCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activitySignUpEmailET.getText().length() > 0) {
                    if (isValidEmailAddress(activitySignUpEmailET.getText().toString())) {
                        setJsonObject(EMAIL, activitySignUpEmailET.getText().toString());
                        if (activitySignUpPasswordET.getText().length() > 0 && activitySignUpVerifyPasswordET.getText().length() > 0) {
                            if (activitySignUpPasswordET.getText().toString().equals(activitySignUpVerifyPasswordET.getText().toString())) {
                                setJsonObject(PASS, activitySignUpPasswordET.getText().toString());
                                if (activitySignUpPrivacyPolicyCB.isChecked()) {
                                    if (activitySignUpTermsCB.isChecked()) {
                                        createUserApi();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Terms of services not agreed", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Privacy policy not agreed", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                removeKey(PASS);
                                Toast.makeText(getApplicationContext(), "Password doesn't match", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            removeKey(PASS);
                            Toast.makeText(getApplicationContext(), "Password not set", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        removeKey(EMAIL);
                        Toast.makeText(getApplicationContext(), R.string.valid_email, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    removeKey(EMAIL);
                    Toast.makeText(getApplicationContext(), R.string.empty_email, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        activitySignUpTermsCB.setChecked(settingsDataSource.isTOCAccepted());
        activitySignUpPrivacyPolicyCB.setChecked(settingsDataSource.isPrivacyAccpeted());
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

    private boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            new InternetAddress(email).validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    private void createUserApi() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, USER_SIGN_UP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("API-SignUp", response);
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(STATUS)) {
                                    userDataSource.insertUserData(jsonObject.getString(DATA));
                                    settingsDataSource.userLoginSuccess();
//                                    startActivity(new Intent(getApplicationContext(),VerifyEmailActivity.class));
//                                    finish();
                                    Toast.makeText(getApplicationContext(), "SignUp Successful", Toast.LENGTH_SHORT).show();
                                    chekeEmailPopup();
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
        return FileProvider.getUriForFile(SignUpActivity.this, getApplicationContext().getPackageName() + ".provider", file);
    }

    public void chekeEmailPopup() {
        final Dialog dialog = new Dialog(SignUpActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.verify_email_poup);
        Window window = dialog.getWindow();
        ImageView cancelIv = (ImageView) window.findViewById(R.id.cancelIv);
        cancelIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class UploadImage extends AsyncTask<String, String, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
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


}
