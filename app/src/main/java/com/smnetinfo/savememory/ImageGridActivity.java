package com.smnetinfo.savememory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.smnetinfo.savememory.adapter.ImagesGridRecyclerAdapter;
import com.smnetinfo.savememory.database.PostsDataSource;
import com.smnetinfo.savememory.database.UserDataSource;
import com.smnetinfo.savememory.extras.AwsS3Util;
import com.smnetinfo.savememory.extras.BaseActivity;
import com.smnetinfo.savememory.extras.ProgressDialog;
import com.smnetinfo.savememory.extras.WebConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageGridActivity extends BaseActivity implements WebConstants {

    TextView activityImagesGridEditTV, headingTv;
    RecyclerView activityImagesGridRV;
    AppCompatImageView activityImagesGridBackIV;
    CardView activityImagesGridEditCV, activityImagesGridAddCV, activityImagesGridDeleteCV;

    ImagesGridRecyclerAdapter imagesGridRecyclerAdapter;

    int cardSize;
    String postId;
    int gridSize = 1;
    boolean isEdit = false;

    AmazonS3 amazonS3;

    JSONArray imagesJSONArray = new JSONArray();
    JSONArray selectedContentArray = new JSONArray();

    ProgressDialog progressDialog;

    UserDataSource userDataSource;
    PostsDataSource postsDataSource;

    List<JSONObject> selectedImages = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_grid);

        progressDialog = new ProgressDialog(this);
        amazonS3 = new AwsS3Util().getS3Client(this);
        userDataSource = UserDataSource.sharedInstance(this);
        postsDataSource = PostsDataSource.sharedInstance(this);

        activityImagesGridRV = findViewById(R.id.activityImagesGridRV);

        activityImagesGridEditTV = findViewById(R.id.activityImagesGridEditTV);

        activityImagesGridAddCV = findViewById(R.id.activityImagesGridAddCV);
        activityImagesGridEditCV = findViewById(R.id.activityImagesGridEditCV);
        activityImagesGridDeleteCV = findViewById(R.id.activityImagesGridDeleteCV);

        activityImagesGridBackIV = findViewById(R.id.activityImagesGridBackIV);
        headingTv = (TextView) findViewById(R.id.headingTv);

        try {
            postId = getIntent().getExtras().getString(ID);
            imagesJSONArray = new JSONArray(getIntent().getExtras().getString(DATA));
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float screenWidth = displayMetrics.widthPixels;
            cardSize = (int) (screenWidth / gridSize);
            if (imagesJSONArray.length() == 1) {
                headingTv.setText(imagesJSONArray.length() + " Image");
            } else {
                headingTv.setText(imagesJSONArray.length() + " Images");
            }

            imagesGridRecyclerAdapter = new ImagesGridRecyclerAdapter(this, imagesJSONArray, cardSize, isEdit);
            activityImagesGridRV.setLayoutManager(new GridLayoutManager(this, gridSize));
            activityImagesGridRV.setAdapter(imagesGridRecyclerAdapter);
            imagesGridRecyclerAdapter.setOnClickListener(new ImagesGridRecyclerAdapter.OnClickListener() {
                @Override
                public void onClick(JSONObject jsonObject, int position) {
                    /*Intent intent = new Intent(ImageGridActivity.this, ImagesActivity.class);
                    intent.putExtra(DATA,imagesJSONArray.toString());
                    intent.putExtra(POSITION,position);
                    startActivity(intent);*/
                }

                @Override
                public void onSelect(JSONObject jsonObject, int position) {
                    selectedImages.add(jsonObject);
                }

                @Override
                public void onDeselect(JSONObject jsonObject, int position) {
                    selectedImages.remove(jsonObject);
                }
            });

            activityImagesGridEditCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isEdit) {
                        isEdit = false;
                        activityImagesGridEditTV.setText("Edit");
                        activityImagesGridAddCV.setVisibility(View.VISIBLE);
                        activityImagesGridDeleteCV.setVisibility(View.GONE);
                    } else {
                        isEdit = true;
                        activityImagesGridEditTV.setText("Cancel");
                        activityImagesGridAddCV.setVisibility(View.GONE);
                        activityImagesGridDeleteCV.setVisibility(View.VISIBLE);
                    }
                    imagesGridRecyclerAdapter.enableEdit(isEdit);
                }
            });

            activityImagesGridDeleteCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (selectedImages.size() > 0) {
                        selectedContentArray = new JSONArray();
                        for (int i = 0; i < selectedImages.size(); i++) {
                            try {
                                selectedContentArray.put(selectedImages.get(i).getString(ID));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (selectedContentArray.length() > 0) {
                            chekeEmailPopup(selectedContentArray.length());

                        } else {
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No images selected to delete", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            activityImagesGridAddCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        } else {
                            choosePictureDialog();
                        }
                    } else {
                        choosePictureDialog();
                    }
                }
            });

            activityImagesGridBackIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (requestCode == 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choosePictureDialog();
                } else {
                    Toast.makeText(getApplicationContext(), "Cannot process without Storage Permission", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == 2) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, getCaptureImageUri());
                    startActivityForResult(intent, 2);
                } else {
                    Toast.makeText(getApplicationContext(), "Cannot capture image without Camera Permission", Toast.LENGTH_SHORT).show();
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
                    if (data.getData() != null) {
                        String image_name = "IMG_" + userDataSource.getUserId() + System.currentTimeMillis();

                        JSONArray contentArray = new JSONArray();
                        contentArray.put(saveBitmapPosts(uri, image_name));
                        uploadAttachment(contentArray);
                    } else {
                        if (data.getClipData() != null) {
                            ClipData clipData = data.getClipData();
                            JSONArray contentArray = new JSONArray();
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                ClipData.Item item = clipData.getItemAt(i);
                                uri = item.getUri();
                                String image_name = "IMG_" + userDataSource.getUserId() + System.currentTimeMillis();
                                contentArray.put(saveBitmapPosts(uri, image_name));
                            }
                            uploadAttachment(contentArray);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                String image_name = "IMG_" + userDataSource.getUserId() + System.currentTimeMillis();
                JSONArray contentArray = new JSONArray();
                contentArray.put(saveBitmapPosts(getCaptureImageUri(), image_name));
                uploadAttachment(contentArray);
            }
        }
    }

    private void choosePictureDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choose_image);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(true);
        dialog.show();

        CardView dialogChooseImageCameraTextView = dialog.findViewById(R.id.dialogChooseImageCameraTextView);
        CardView dialogChooseImageGalleryTextView = dialog.findViewById(R.id.dialogChooseImageGalleryTextView);

        dialogChooseImageCameraTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 2);
                    } else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, getCaptureImageUri());
                        startActivityForResult(intent, 2);
                    }
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, getCaptureImageUri());
                    startActivityForResult(intent, 2);
                }
                dialog.dismiss();
            }
        });

        dialogChooseImageGalleryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                dialog.dismiss();
            }
        });

    }

    private void updatePostApi(final JSONArray contentArray) {
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
                                    postsDataSource.updatePostData(postId, jsonObjectData.toString());
                                    imagesGridRecyclerAdapter.notifyList(postsDataSource.getPostData(postId).getJSONArray(CONTENT_ARRAY));
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

    private void postDelete() {
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
                                    if (jsonObject.getBoolean(POST_DELETE_STATUS)) {
                                        postsDataSource.deletePost(postId);
                                        finish();
                                    } else {
                                        JSONObject jsonObjectData = jsonObject.getJSONObject(DATA);
                                        postsDataSource.updatePostData(postId, jsonObjectData.toString());
                                        imagesGridRecyclerAdapter.notifyList(postsDataSource.getPostData(postId).getJSONArray(CONTENT_ARRAY));
                                        imagesGridRecyclerAdapter.enableEdit(false);
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
                    jsonObject.put(POST_ID, postId);
                    jsonObject.put(CONTENT_ARRAY, selectedContentArray);
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

    private void uploadAttachment(final JSONArray jsonArray) {
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
                        String s3FileName = POST_IMAGE_PATH + "IMG_" + userDataSource.getUserId() + System.currentTimeMillis() + EXT_IMAGE;
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
                    updatePostApi(s3URLJsonArray);
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        };
        connectionThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        return FileProvider.getUriForFile(ImageGridActivity.this, getApplicationContext().getPackageName() + ".provider", file);
    }


    public void chekeEmailPopup(int imgLength) {
        final Dialog dialog = new Dialog(ImageGridActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_video_popup);
        Window window = dialog.getWindow();
        TextView deleteImgTv = (TextView) window.findViewById(R.id.deleteImgTv);
        if (imgLength == 1) {
            deleteImgTv.setText("Delete " + imgLength + " Image");
        } else {
            deleteImgTv.setText("Delete " + imgLength + " Images");
        }
        Button deleteButton = (Button) window.findViewById(R.id.deleteButton);
        Button cancelButton = (Button) window.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                postDelete();
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
