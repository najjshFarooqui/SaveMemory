package com.smnetinfo.savememory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
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
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.smnetinfo.savememory.extras.GetPathFromUri;
import com.smnetinfo.savememory.extras.ProgressDialog;
import com.smnetinfo.savememory.extras.WebConstants;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddWillActivity extends BaseActivity implements WebConstants {


    CardView activityMyWillSaveCV;
    AppCompatImageView activityMyWillBackIV;
    EditText activityMyWillTitleET, activityMyWillContentET;
    LinearLayout activityMyWillImageLL, activityMyWillDocLL, activityMyWillVideoLL;
    ImageView vAddIv, vCancelIv;

    AmazonS3 amazonS3;
    ProgressDialog progressDialog;

    UserDataSource userDataSource;

    int finalPostType;
    JSONArray finalContentArray = new JSONArray();
    JSONObject newWillJSONObject = new JSONObject();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_will);

        progressDialog = new ProgressDialog(this);
        amazonS3 = new AwsS3Util().getS3Client(this);
        userDataSource = UserDataSource.sharedInstance(this);

        activityMyWillSaveCV = findViewById(R.id.activityMyWillSaveCV);

        activityMyWillDocLL = findViewById(R.id.activityMyWillDocLL);
        activityMyWillImageLL = findViewById(R.id.activityMyWillImageLL);
        activityMyWillVideoLL = findViewById(R.id.activityMyWillVideoLL);

        activityMyWillBackIV = findViewById(R.id.activityMyWillBackIV);

        activityMyWillTitleET = findViewById(R.id.activityMyWillTitleET);
        activityMyWillContentET = findViewById(R.id.activityMyWillContentET);

        vCancelIv = (ImageView) findViewById(R.id.vCancelIv);
        vAddIv = (ImageView) findViewById(R.id.vAddIv);

        activityMyWillImageLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (activityMyWillTitleET.getText().length() > 0) {
                        newWillJSONObject.put(TITLE, activityMyWillTitleET.getText().toString());
                        if (activityMyWillContentET.getText().length() > 0) {
                            newWillJSONObject.put(DESCRIPTION, activityMyWillContentET.getText().toString());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                                } else {
                                    Intent intent = new Intent();
                                    intent.setType("image/*");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                                }
                            } else {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Description is empty", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Title is empty", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        activityMyWillDocLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (activityMyWillTitleET.getText().length() > 0) {
                        newWillJSONObject.put(TITLE, activityMyWillTitleET.getText().toString());
                        if (activityMyWillContentET.getText().length() > 0) {
                            newWillJSONObject.put(DESCRIPTION, activityMyWillContentET.getText().toString());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                                } else {
                                    Intent intent = new Intent(AddWillActivity.this, NormalFilePickActivity.class);
                                    intent.putExtra(Constant.MAX_NUMBER, 1);
                                    intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
                                    startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
                                }
                            } else {
                                Intent intent = new Intent(AddWillActivity.this, NormalFilePickActivity.class);
                                intent.putExtra(Constant.MAX_NUMBER, 1);
                                intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
                                startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Description is empty", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Title is empty", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        activityMyWillVideoLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (activityMyWillTitleET.getText().length() > 0) {
                        newWillJSONObject.put(TITLE, activityMyWillTitleET.getText().toString());
                        if (activityMyWillContentET.getText().length() > 0) {
                            newWillJSONObject.put(DESCRIPTION, activityMyWillContentET.getText().toString());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                                } else {
                                    Intent intent = new Intent();
                                    intent.setType("video/*");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(Intent.createChooser(intent, "Select Video"), 2);
                                }
                            } else {
                                Intent intent = new Intent();
                                intent.setType("video/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Video"), 2);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Description is empty", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Title is empty", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        activityMyWillSaveCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (activityMyWillTitleET.getText().length() > 0) {
                        newWillJSONObject.put(TITLE, activityMyWillTitleET.getText().toString());
                        if (activityMyWillContentET.getText().length() > 0) {
                            newWillJSONObject.put(DESCRIPTION, activityMyWillContentET.getText().toString());
                            createWillApi();
                        } else {
                            Toast.makeText(getApplicationContext(), "Description is empty", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Title is empty", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        vAddIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMyWillImageLL.setVisibility(View.VISIBLE);
                activityMyWillDocLL.setVisibility(View.VISIBLE);
                activityMyWillVideoLL.setVisibility(View.VISIBLE);
                vCancelIv.setVisibility(View.VISIBLE);
                vAddIv.setVisibility(View.INVISIBLE);
            }
        });
        vCancelIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMyWillImageLL.setVisibility(View.INVISIBLE);
                activityMyWillDocLL.setVisibility(View.INVISIBLE);
                activityMyWillVideoLL.setVisibility(View.INVISIBLE);
                vCancelIv.setVisibility(View.INVISIBLE);
                vAddIv.setVisibility(View.VISIBLE);
            }
        });


        activityMyWillBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (requestCode == 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                } else {
                    Toast.makeText(getApplicationContext(), "Cannot process without Storage Permission", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == 2) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Video"), 2);
                } else {
                    Toast.makeText(getApplicationContext(), "Cannot capture image without Camera Permission", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == 3) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(this, NormalFilePickActivity.class);
                    intent.putExtra(Constant.MAX_NUMBER, 1);
                    intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
                    startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
                } else {
                    Toast.makeText(getApplicationContext(), "Cannot process without Storage Permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri uri = data.getData();
                    String image_name = "WILL_IMG_" + userDataSource.getUserId() + System.currentTimeMillis();

                    JSONArray contentArray = new JSONArray();
                    contentArray.put(saveBitmapPosts(uri, image_name));
                    uploadAttachment(POST_TYPE_IMAGE, contentArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String current_video_path = new GetPathFromUri().getPath(this, uri);
                String video_name = "WILL_VID_" + userDataSource.getUserId() + System.currentTimeMillis();
                File new_video_file = new File(Environment.getExternalStorageDirectory() + VIDEO_PATH + video_name + EXT_VIDEO);
                copyFile(current_video_path, new_video_file.getAbsolutePath(), VIDEO_PATH);

                JSONArray contentArray = new JSONArray();
                contentArray.put(new_video_file.getAbsolutePath());
                //Add multiple videos here
                uploadAttachment(POST_TYPE_VIDEO, contentArray);
            }
        } else if (requestCode == Constant.REQUEST_CODE_PICK_FILE) {
            if (resultCode == RESULT_OK) {
                try {
                    ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    String current_doc_path = list.get(0).getPath();

                    JSONArray contentArray = new JSONArray();
                    contentArray.put(current_doc_path);
                    uploadAttachment(POST_TYPE_DOC, contentArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadAttachment(final int post_type, final JSONArray jsonArray) {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, JSONArray> connectionThread = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.show();
            }

            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONArray s3URLJsonArray = new JSONArray();
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = new JSONObject();
                        File file = new File(jsonArray.getString(i));
                        String s3FileName = "";
                        switch (post_type) {
                            case POST_TYPE_IMAGE:
                                s3FileName = POST_IMAGE_PATH + "IMG_" + userDataSource.getUserId() + System.currentTimeMillis() + EXT_IMAGE;
                                break;
                            case POST_TYPE_VIDEO:
                                s3FileName = POST_VIDEO_PATH + "VID_" + userDataSource.getUserId() + System.currentTimeMillis() + EXT_VIDEO;

                                String thumbUrlName = POST_THUMB_PATH + "THU_" + System.currentTimeMillis() + EXT_IMAGE;
                                String thumbLocalPath = thumbFromVideo(file.getAbsolutePath());
                                jsonObject.put(THUMB_URL, thumbLocalPath);
                                break;
                            case POST_TYPE_DOC:
                                String extension = getFileExtension(file.getAbsolutePath());
                                s3FileName = POST_DOC_PATH + "DOC_" + userDataSource.getUserId() + System.currentTimeMillis() + "." + extension;
                                break;
                        }
                        amazonS3.putObject(new PutObjectRequest(BUCKET_NAME, s3FileName, file).withCannedAcl(CannedAccessControlList.PublicReadWrite));
                        String attachment_url = S3_BUCKET_URL + s3FileName;
                        jsonObject.put(URL, attachment_url);
                        jsonObject.put(URL_SIZE, file.length());
                        s3URLJsonArray.put(jsonObject);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return s3URLJsonArray;
            }

            @Override
            protected void onPostExecute(JSONArray s3URLJsonArray) {
                super.onPostExecute(s3URLJsonArray);
                progressDialog.dismiss();
                if (s3URLJsonArray.length() > 0) {
                    finalPostType = post_type;
                    finalContentArray = s3URLJsonArray;
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        };
        connectionThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void createWillApi() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CREATE_WILL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("API-Posts", response);
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(STATUS)) {
                                    progressDialog.dismiss();
                                    addWillPopup();
                                    //   finish();
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
                    newWillJSONObject.put(USER_ID, userDataSource.getUserId());
                    newWillJSONObject.put(TYPE, finalPostType);
                    if (finalContentArray.length() > 0) {
                        newWillJSONObject.put(CONTENT_URL, finalContentArray.getJSONObject(0).getString(URL));
                        newWillJSONObject.put(CONTENT_SIZE, finalContentArray.getJSONObject(0).getString(URL_SIZE));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return newWillJSONObject.toString().getBytes();
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

    @NonNull
    private String saveBitmapPosts(Uri uri, String image_id) {
        File folder = new File(Environment.getExternalStorageDirectory() + IMAGE_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory() + IMAGE_PATH + image_id + EXT_IMAGE);
        try {
            Bitmap bitmap = resize(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri), 1000, 1000);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
            file.createNewFile();
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (file.exists()) {
            return file.getPath();
        } else {
            return "";
        }
    }

    private Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
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
        } else {
            return image;
        }
    }

    private Uri getCaptureImageUri() {
        File folder = new File(Environment.getExternalStorageDirectory() + ROOT_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory() + ROOT_PATH + "temp_attach_img" + EXT_IMAGE);
        return FileProvider.getUriForFile(AddWillActivity.this, getApplicationContext().getPackageName() + ".provider", file);
    }

    private String getFileExtension(String file) {
        String extension = "";
        int i = file.lastIndexOf('.');
        if (i > 0) {
            extension = file.substring(i + 1);
        }
        return extension;
    }

    private void copyFile(String from, String to, String path) {
        try {
            File newPath = new File(Environment.getExternalStorageDirectory() + path);
            if (!newPath.exists()) {
                newPath.mkdirs();
            }
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                File source = new File(from);
                File destination = new File(to);
                if (source.exists()) {
                    FileChannel src = new FileInputStream(source).getChannel();
                    FileChannel dst = new FileOutputStream(destination).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String thumbFromVideo(String path) {
        File folder = new File(Environment.getExternalStorageDirectory() + THUMB_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory() + THUMB_PATH + System.currentTimeMillis() + EXT_IMAGE);
        try {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
            byte[] byteArray = bytes.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public void addWillPopup() {
        final Dialog dialog = new Dialog(AddWillActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_will_popup);
        Window window = dialog.getWindow();
        LinearLayout closeLayout = (LinearLayout) window.findViewById(R.id.closeLayout);
        LinearLayout addAnotherLayout = (LinearLayout) window.findViewById(R.id.addAnotherLayout);
        addAnotherLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(AddWillActivity.this, AddWillActivity.class);
                startActivity(intent);
                finish();
            }
        });
        closeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();
    }
}
