package com.smnetinfo.savememory.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.smnetinfo.savememory.R;
import com.smnetinfo.savememory.TextViewMoreActivity;
import com.smnetinfo.savememory.adapter.PostListRecyclerAdapter;
import com.smnetinfo.savememory.database.PostsDataSource;
import com.smnetinfo.savememory.database.SettingsDataSource;
import com.smnetinfo.savememory.database.UserDataSource;
import com.smnetinfo.savememory.extras.AwsS3Util;
import com.smnetinfo.savememory.extras.GetPathFromUri;
import com.smnetinfo.savememory.extras.ProgressDialog;
import com.smnetinfo.savememory.extras.WebConstants;
import com.squareup.picasso.Picasso;
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
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class MemoriesFragment extends Fragment implements WebConstants {

    EditText fragmentMemoriesPostET;
    RecyclerView fragmentMemoriesPostsRV;
    DonutProgress fragmentMemoriesAudioRecProgress;
    AppCompatImageView fragmentMemoriesAudioRecPauseIV;
    RelativeLayout fragmentMemoriesPostTextRL, fragmentMemoriesPostAudioRL;
    TextView fragmentMemoriesAudioRecTimerTV, fragmentMemoriesAudioRecSendTV, fragmentMemoriesClearSearchTV;
    AppCompatImageView fragmentMemoriesPostIV, fragmentMemoriesPostAddAttachmentIV, fragmentMemoriesWallpaperIV,
            fragmentMemoriesAudioRecCloseIV;
    CardView fragmentMemoriesPostCV, fragmentMemoriesPostAudioErrorCV,
            fragmentMemoriesPostAttachmentCV, fragmentMemoriesPostAddAttachmentCV, fragmentMemoriesPostGalleryImageCV,
            fragmentMemoriesPostGalleryVideoCV, fragmentMemoriesPostGalleryAudioCV, fragmentMemoriesPostDocCV, fragmentMemoriesPostCameraCV,
            fragmentMemoriesPostRecorderCV, fragmentMemoriesPostAudioRecorderCV, fragmentMemoriesAudioRecCV, fragmentMemoriesAudioRecorderCV,
            fragmentMemoriesAudioRecSendCV;

    LinearLayoutManager linearLayoutManager;
    PostListRecyclerAdapter postListRecyclerAdapter;

    UserDataSource userDataSource;
    PostsDataSource postsDataSource;
    SettingsDataSource settingsDataSource;

    AmazonS3 amazonS3;
    ProgressDialog progressDialog;

    CountDownTimer countDownTimer;
    MediaRecorder audioRecorder = null;

    boolean isVoice = false;
    boolean isAttachment = false;
    boolean isAudioRecorder = true;
    boolean notSendingMessage = true;
    boolean isAudioRecording = false;

    int currentPostType = POST_TYPE_TEXT;

    BroadcastReceiver searchResult;
    private int secondsTwo = 0;
    private int minutesTwo = 10;
    private int timerProgress = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memories, container, false);

        progressDialog = new ProgressDialog(getContext());
        amazonS3 = new AwsS3Util().getS3Client(getContext());
        userDataSource = UserDataSource.sharedInstance(getContext());
        postsDataSource = PostsDataSource.sharedInstance(getContext());
        settingsDataSource = SettingsDataSource.sharedInstance(getContext());

        fragmentMemoriesPostTextRL = view.findViewById(R.id.fragmentMemoriesPostTextRL);
        fragmentMemoriesPostAudioRL = view.findViewById(R.id.fragmentMemoriesPostAudioRL);

        fragmentMemoriesPostsRV = view.findViewById(R.id.fragmentMemoriesPostsRV);

        fragmentMemoriesAudioRecPauseIV = view.findViewById(R.id.fragmentMemoriesAudioRecPauseIV);

        fragmentMemoriesClearSearchTV = view.findViewById(R.id.fragmentMemoriesClearSearchTV);
        fragmentMemoriesAudioRecSendTV = view.findViewById(R.id.fragmentMemoriesAudioRecSendTV);
        fragmentMemoriesAudioRecTimerTV = view.findViewById(R.id.fragmentMemoriesAudioRecTimerTV);

        fragmentMemoriesAudioRecProgress = view.findViewById(R.id.fragmentMemoriesAudioRecProgress);

        fragmentMemoriesPostCV = view.findViewById(R.id.fragmentMemoriesPostCV);
        fragmentMemoriesPostDocCV = view.findViewById(R.id.fragmentMemoriesPostDocCV);
        //  fragmentMemoriesPostTextCV = view.findViewById(R.id.fragmentMemoriesPostTextCV);
        fragmentMemoriesAudioRecCV = view.findViewById(R.id.fragmentMemoriesAudioRecCV);
        fragmentMemoriesPostCameraCV = view.findViewById(R.id.fragmentMemoriesPostCameraCV);
        fragmentMemoriesAudioRecSendCV = view.findViewById(R.id.fragmentMemoriesAudioRecSendCV);
        fragmentMemoriesPostRecorderCV = view.findViewById(R.id.fragmentMemoriesPostRecorderCV);
        fragmentMemoriesAudioRecorderCV = view.findViewById(R.id.fragmentMemoriesAudioRecorderCV);
        fragmentMemoriesPostAudioErrorCV = view.findViewById(R.id.fragmentMemoriesPostAudioErrorCV);
        fragmentMemoriesPostAttachmentCV = view.findViewById(R.id.fragmentMemoriesPostAttachmentCV);
        fragmentMemoriesPostGalleryImageCV = view.findViewById(R.id.fragmentMemoriesPostGalleryImageCV);
        fragmentMemoriesPostGalleryVideoCV = view.findViewById(R.id.fragmentMemoriesPostGalleryVideoCV);
        fragmentMemoriesPostGalleryAudioCV = view.findViewById(R.id.fragmentMemoriesPostGalleryAudioCV);
        fragmentMemoriesPostAudioRecorderCV = view.findViewById(R.id.fragmentMemoriesPostAudioRecorderCV);
        fragmentMemoriesPostAddAttachmentCV = view.findViewById(R.id.fragmentMemoriesPostAddAttachmentCV);

        fragmentMemoriesPostET = view.findViewById(R.id.fragmentMemoriesPostET);

        fragmentMemoriesPostIV = view.findViewById(R.id.fragmentMemoriesPostIV);
        fragmentMemoriesWallpaperIV = view.findViewById(R.id.fragmentMemoriesWallpaperIV);
        fragmentMemoriesAudioRecCloseIV = view.findViewById(R.id.fragmentMemoriesAudioRecCloseIV);
        fragmentMemoriesPostAddAttachmentIV = view.findViewById(R.id.fragmentMemoriesPostAddAttachmentIV);

        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        //linearLayoutManager.setStackFromEnd(true);
        fragmentMemoriesPostsRV.setLayoutManager(linearLayoutManager);
        postListRecyclerAdapter = new PostListRecyclerAdapter(getContext(), postsDataSource.getPosts());
        fragmentMemoriesPostsRV.setAdapter(postListRecyclerAdapter);
        fragmentMemoriesPostsRV.scrollToPosition(postListRecyclerAdapter.getItemCount() - 1);
        postListRecyclerAdapter.setOnClickListener(new PostListRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(JSONObject jsonObject, int position) {

            }

            @Override
            public void onLongClick(JSONObject jsonObject, int position) {
                showPostInfo(jsonObject);
            }
        });

        fragmentMemoriesPostET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (currentPostType == POST_TYPE_TEXT) {
                    if (charSequence.toString().trim().length() > 0) {
                        isAttachment = false;
                        isAudioRecorder = false;
                        fragmentMemoriesPostIV.setImageResource(R.drawable.image_send);
                    } else {
                        isAttachment = true;
                        //isAudioRecorder = true;
                        //fragmentMemoriesPostIV.setImageResource(R.drawable.image_add);
                        isAudioRecorder = false;
                        fragmentMemoriesPostIV.setImageResource(R.drawable.image_send);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        fragmentMemoriesPostET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }
        });

        fragmentMemoriesPostET.setOnTouchListener(new View.OnTouchListener() {
            int oldX = 0, oldY = 0, newX = 0, newY = 0;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    oldX = (int) motionEvent.getX();
                    oldY = (int) motionEvent.getY();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    newX = (int) motionEvent.getX();
                    newY = (int) motionEvent.getY();
                    if ((newX > (oldX + 10) || newX < (oldX - 10)) && (newY > (oldY + 10) || newY < (oldY - 10))) {
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    fragmentMemoriesPostAttachmentCV.setVisibility(View.GONE);
                    fragmentMemoriesPostAddAttachmentIV.setRotation(0);
                    fragmentMemoriesPostAddAttachmentIV.setColorFilter(ContextCompat.getColor(getContext(), R.color.newPrimaryColor), PorterDuff.Mode.SRC_IN);
                }
                return false;
            }
        });

        fragmentMemoriesPostET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if (focused) {
                    fragmentMemoriesPostAttachmentCV.setVisibility(View.GONE);
                    fragmentMemoriesPostAddAttachmentIV.setRotation(0);
                    fragmentMemoriesPostAddAttachmentIV.setColorFilter(ContextCompat.getColor(getContext(), R.color.newPrimaryColor), PorterDuff.Mode.SRC_IN);
                }
            }
        });

        fragmentMemoriesPostCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notSendingMessage) {
                    notSendingMessage = false;
                    if (!isAudioRecorder) {
                        if (fragmentMemoriesPostET.getText().length() > 0) {
                            JSONArray contentArray = new JSONArray();
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put(URL, fragmentMemoriesPostET.getText().toString());
                                jsonObject.put(URL_SIZE, fragmentMemoriesPostET.getText().toString().getBytes().length);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            contentArray.put(jsonObject);
                            createPostApi(POST_TYPE_TEXT, contentArray);
                            fragmentMemoriesPostET.setText("");
                        }
                    }
                }
            }
        });

        fragmentMemoriesPostCV.setOnTouchListener(new View.OnTouchListener() {
            int oldX = 0, oldY = 0, newX = 0, newY = 0;
            boolean showError = false;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    oldX = (int) motionEvent.getX();
                    oldY = (int) motionEvent.getY();
                    if (isAudioRecorder) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            } else {
                                if (getContext().checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 3);
                                } else {
                                    showError = true;
                                    recordAudio(true);
                                }
                            }
                        } else {
                            showError = true;
                            recordAudio(true);
                        }
                        notSendingMessage = true;
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    newX = (int) motionEvent.getX();
                    newY = (int) motionEvent.getY();
                    if ((newX > (oldX + 50) || newX < (oldX - 50)) && (newY > (oldY + 50) || newY < (oldY - 50))) {
                        if (isAudioRecorder) {
                            if (isAudioRecording) {
                                recordAudio(false);
                            } else {
                                if (showError) {
                                    showError = false;
                                    showAudioError();
                                }
                            }
                            notSendingMessage = true;
                        }
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (isAudioRecorder) {
                        if (isAudioRecording) {
                            recordAudio(false);
                        } else {
                            if (showError) {
                                showError = false;
                                showAudioError();
                            }
                        }
                    }
                    notSendingMessage = true;
                }
                return false;
            }
        });

        fragmentMemoriesPostAudioRecorderCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentMemoriesPostAddAttachmentCV.performClick();
                fragmentMemoriesAudioRecCV.setVisibility(View.VISIBLE);
                fragmentMemoriesAudioRecSendTV.setTextColor(getResources().getColor(R.color.grey));
                fragmentMemoriesAudioRecSendCV.setCardBackgroundColor(getResources().getColor(R.color.textShade));
            }
        });

        fragmentMemoriesAudioRecorderCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        if (getContext().checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 3);
                        } else {
                            recordAudio(true);
                        }
                    }
                } else {
                    recordAudio(true);
                }
                notSendingMessage = true;
            }
        });

        fragmentMemoriesAudioRecPauseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordAudio(false);
                fragmentMemoriesAudioRecSendTV.setTextColor(getResources().getColor(android.R.color.white));
                fragmentMemoriesAudioRecSendCV.setCardBackgroundColor(getResources().getColor(R.color.newPrimaryColor));
            }
        });

        fragmentMemoriesAudioRecSendCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAudioRecording) {
                    audioSend();
                    fragmentMemoriesAudioRecCloseIV.performClick();
                }
            }
        });

        fragmentMemoriesAudioRecCloseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordAudio(false);
                fragmentMemoriesAudioRecCV.setVisibility(View.GONE);
            }
        });

        fragmentMemoriesPostAddAttachmentCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fragmentMemoriesPostAttachmentCV.getVisibility() == View.VISIBLE) {
                    fragmentMemoriesPostAttachmentCV.setVisibility(View.GONE);
                    fragmentMemoriesPostAddAttachmentIV.setRotation(0);
                    fragmentMemoriesPostAddAttachmentIV.setColorFilter(ContextCompat.getColor(getContext(), R.color.newPrimaryColor), PorterDuff.Mode.SRC_IN);
                } else {
                    fragmentMemoriesPostAttachmentCV.setVisibility(View.VISIBLE);
                    fragmentMemoriesPostAddAttachmentIV.setRotation(45);
                    fragmentMemoriesPostAddAttachmentIV.setColorFilter(ContextCompat.getColor(getContext(), R.color.red), PorterDuff.Mode.SRC_IN);
                }
                fragmentMemoriesPostET.clearFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        fragmentMemoriesPostGalleryImageCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPostType = POST_TYPE_IMAGE;
                selectAttachment();
            }
        });

        fragmentMemoriesPostCameraCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPostType = POST_TYPE_IMAGE;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 2);
                    } else {
                        chooseCameraType();
                    }
                } else {
                    chooseCameraType();
                }
            }
        });

        fragmentMemoriesPostGalleryVideoCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPostType = POST_TYPE_VIDEO;
                selectAttachment();
            }
        });

        fragmentMemoriesPostRecorderCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPostType = POST_TYPE_VIDEO;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 2);
                    } else {
                        captureAttachment();
                    }
                } else {
                    captureAttachment();
                }
            }
        });

        fragmentMemoriesPostDocCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NormalFilePickActivity.class);
                intent.putExtra(Constant.MAX_NUMBER, 1);
                intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
                startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
            }
        });

        fragmentMemoriesPostGalleryAudioCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPostType = POST_TYPE_AUDIO;
                selectAttachment();
            }
        });

        searchResult = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null) {
                    if (BROADCAST_SEARCH_RESULT.equals(intent.getAction())) {
                        fragmentMemoriesClearSearchTV.setVisibility(View.VISIBLE);
                        String keyword = intent.getExtras().getString(SEARCH_TEXT);
                        searchApi(keyword);
                    }
                }
            }
        };

        fragmentMemoriesClearSearchTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentMemoriesClearSearchTV.setVisibility(View.GONE);
                getPostsApi();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(searchResult, new IntentFilter(BROADCAST_SEARCH_RESULT));
        //init wallpaper
        postListRecyclerAdapter.notifyMessage(postsDataSource.getPosts());
        if (!settingsDataSource.getWallpaperPath().equals(EMPTY)) {
            Picasso.with(getContext())
                    .load(new File(settingsDataSource.getWallpaperPath()))
                    .into(fragmentMemoriesWallpaperIV);
        }
        //init wallpaper
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (requestCode == 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectAttachment();
                } else {
                    currentPostType = POST_TYPE_TEXT;
                    Toast.makeText(getContext(), "Cannot process without Storage Permission", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == 2) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    captureAttachment();
                } else {
                    currentPostType = POST_TYPE_TEXT;
                    Toast.makeText(getContext(), "Cannot capture image without Camera Permission", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == 3) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    currentPostType = POST_TYPE_TEXT;
                    Toast.makeText(getContext(), "Cannot record audio without Audio Record Permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri uri = data.getData();
                    isAudioRecorder = false;
                    fragmentMemoriesPostIV.setImageResource(R.drawable.image_send);
                    switch (currentPostType) {
                        case POST_TYPE_IMAGE:
                            if (data.getData() != null) {
                                String image_name = "IMG_" + userDataSource.getUserId() + System.currentTimeMillis();

                                JSONArray contentArray = new JSONArray();
                                contentArray.put(saveBitmapPosts(uri, image_name));
                                uploadAttachment(POST_TYPE_IMAGE, contentArray);
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
                                    uploadAttachment(POST_TYPE_IMAGE, contentArray);
                                }
                            }
                            break;
                        case POST_TYPE_VIDEO:
                            String current_video_path = new GetPathFromUri().getPath(getContext(), uri);
                            String video_name = "VID_" + userDataSource.getUserId() + System.currentTimeMillis();
                            File new_video_file = new File(Environment.getExternalStorageDirectory() + VIDEO_PATH + video_name + EXT_VIDEO);
                            copyFile(current_video_path, new_video_file.getAbsolutePath(), VIDEO_PATH);

                            JSONArray contentArray = new JSONArray();
                            contentArray.put(new_video_file.getAbsolutePath());
                            //Add multiple videos here
                            uploadAttachment(POST_TYPE_VIDEO, contentArray);
                            break;
                        case POST_TYPE_AUDIO:
                            String current_audio_path = new GetPathFromUri().getPath(getContext(), uri);
                            String audio_name = "AUD_" + userDataSource.getUserId() + System.currentTimeMillis();
                            File new_audio_file = new File(Environment.getExternalStorageDirectory() + AUDIO_PATH + audio_name + EXT_AUDIO);
                            copyFile(current_audio_path, new_audio_file.getAbsolutePath(), AUDIO_PATH);

                            contentArray = new JSONArray();
                            contentArray.put(new_audio_file.getAbsolutePath());
                            //Add multiple audios here
                            isVoice = false;
                            uploadAttachment(POST_TYPE_AUDIO, contentArray);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            currentPostType = POST_TYPE_TEXT;
            fragmentMemoriesPostAttachmentCV.setVisibility(View.GONE);
            fragmentMemoriesPostAddAttachmentIV.setRotation(0);
            fragmentMemoriesPostAddAttachmentIV.setColorFilter(ContextCompat.getColor(getContext(), R.color.newPrimaryColor), PorterDuff.Mode.SRC_IN);
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                isAudioRecorder = false;
                fragmentMemoriesPostIV.setImageResource(R.drawable.image_send);
                switch (currentPostType) {
                    case POST_TYPE_IMAGE:
                        String image_name = "IMG_" + userDataSource.getUserId() + System.currentTimeMillis();

                        JSONArray contentArray = new JSONArray();
                        contentArray.put(saveBitmapPosts(getCaptureImageUri(), image_name));
                        uploadAttachment(POST_TYPE_IMAGE, contentArray);
                        break;
                    case POST_TYPE_VIDEO:
                        String video_name = "VID_" + userDataSource.getUserId() + System.currentTimeMillis();
                        File new_video_file = new File(Environment.getExternalStorageDirectory() + VIDEO_PATH + video_name + EXT_VIDEO);

                        File current_file = new File(Environment.getExternalStorageDirectory() + TEMP_VIDEO_PATH);

                        String current_video_path = current_file.getAbsolutePath();
                        copyFile(current_video_path, new_video_file.getAbsolutePath(), VIDEO_PATH);

                        contentArray = new JSONArray();
                        contentArray.put(new_video_file.getAbsolutePath());
                        uploadAttachment(POST_TYPE_VIDEO, contentArray);
                        break;
                }
            }
            currentPostType = POST_TYPE_TEXT;
            fragmentMemoriesPostAttachmentCV.setVisibility(View.GONE);
            fragmentMemoriesPostAddAttachmentIV.setRotation(0);
            fragmentMemoriesPostAddAttachmentIV.setColorFilter(ContextCompat.getColor(getContext(), R.color.newPrimaryColor), PorterDuff.Mode.SRC_IN);
        } else if (requestCode == Constant.REQUEST_CODE_PICK_FILE) {
            if (resultCode == RESULT_OK) {
                ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                String current_doc_path = list.get(0).getPath();

                JSONArray contentArray = new JSONArray();
                contentArray.put(current_doc_path);
                uploadAttachment(POST_TYPE_DOC, contentArray);
            }
        }
    }

    private void showPostInfo(final JSONObject jsonObject) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_post_info);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(true);
        dialog.show();

        TextView dialogPostInfoEditTV = dialog.findViewById(R.id.dialogPostInfoEditTV);
        TextView dialogPostInfoDeleteTV = dialog.findViewById(R.id.dialogPostInfoDeleteTV);

        try {
            if (jsonObject.getInt(TYPE) == POST_TYPE_TEXT) {
                dialogPostInfoEditTV.setVisibility(View.VISIBLE);
            } else {
                dialogPostInfoEditTV.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialogPostInfoEditTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), TextViewMoreActivity.class).putExtra(DATA, jsonObject.toString()));
                dialog.dismiss();
            }
        });

        dialogPostInfoDeleteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteDialog(jsonObject.getString(ID));
                    dialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void deleteDialog(final String id) {
        final Dialog dialog = new Dialog(getContext());
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

    private void chooseCameraType() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_camera_type);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(true);
        dialog.show();

        CardView dialogChooseCameraImageTV = dialog.findViewById(R.id.dialogChooseCameraImageTV);
        CardView dialogChooseCameraVideoTV = dialog.findViewById(R.id.dialogChooseCameraVideoTV);

        dialogChooseCameraImageTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPostType = POST_TYPE_IMAGE;
                captureAttachment();
                dialog.dismiss();
            }
        });

        dialogChooseCameraVideoTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPostType = POST_TYPE_VIDEO;
                captureAttachment();
                dialog.dismiss();
            }
        });

    }

    private void selectAttachment() {
        switch (currentPostType) {
            case POST_TYPE_IMAGE:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                break;
            case POST_TYPE_VIDEO:
                intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), 1);
                break;
            case POST_TYPE_AUDIO:
                intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Audio"), 1);
                break;
        }
    }

    private void captureAttachment() {
        switch (currentPostType) {
            case POST_TYPE_IMAGE:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getCaptureImageUri());
                startActivityForResult(intent, 2);
                break;
            case POST_TYPE_VIDEO:
                intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getCaptureVideoUri());
                startActivityForResult(intent, 2);
                break;
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
                                /*amazonS3.putObject(new PutObjectRequest(BUCKET_NAME, thumbUrlName, thumbLocalPath).withCannedAcl(CannedAccessControlList.PublicReadWrite));
                                String thumb_url = S3_BUCKET_URL + thumbUrlName;*/
                                jsonObject.put(THUMB_URL, thumbLocalPath);
                                break;
                            case POST_TYPE_AUDIO:
                                s3FileName = POST_AUDIO_PATH + "AUD_" + userDataSource.getUserId() + System.currentTimeMillis() + EXT_AUDIO;
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
                    createPostApi(post_type, s3URLJsonArray);
                } else {
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        };
        connectionThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void createPostApi(final int post_type, final JSONArray contentArray) {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CREATE_POST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("API-Posts", response);
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(STATUS)) {
                                    notSendingMessage = true;
                                    JSONObject jsonObjectData = jsonObject.getJSONObject(DATA);
                                    postsDataSource.insertPostData(jsonObjectData.getString(ID), jsonObjectData.toString());
                                    postListRecyclerAdapter.notifyMessage(postsDataSource.getPosts());
                                    fragmentMemoriesPostsRV.smoothScrollToPosition(postListRecyclerAdapter.getItemCount());
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
                    jsonObject.put(TYPE, post_type);
                    jsonObject.put(CONTENT_ARRAY, contentArray);
                    if (post_type == POST_TYPE_AUDIO) {
                        jsonObject.put(IS_VOICE, isVoice);
                        jsonObject.put(DURATION, audioDuration(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(0).getString(URL)));
                    }
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
                                    getPostsApi();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
                                    postsDataSource.deletePostData();
                                    JSONArray jsonArrayData = jsonObject.getJSONArray(DATA);
                                    for (int i = 0; i < jsonArrayData.length(); i++) {
                                        JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);
                                        postsDataSource.insertPostData(jsonObjectData.getString(ID), jsonObjectData.toString());
                                    }
                                    postListRecyclerAdapter.notifyMessage(postsDataSource.getPosts());
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void searchApi(final String search_text) {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEARCH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("API-Search", response);
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(STATUS)) {
                                    postsDataSource.deletePostData();
                                    JSONArray jsonArrayData = jsonObject.getJSONObject(DATA).getJSONArray(POSTS);
                                    for (int i = 0; i < jsonArrayData.length(); i++) {
                                        JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);
                                        postsDataSource.insertPostData(jsonObjectData.getString(ID), jsonObjectData.toString());
                                    }
                                    postListRecyclerAdapter.notifyMessage(postsDataSource.getPosts());
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
                    jsonObject.put(SEARCH_TEXT, search_text);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void recordAudio(boolean isRecord) {
        minutesTwo = 10;
        secondsTwo = 0;
        try {
            if (isRecord) {
                isAudioRecording = true;
                /*fragmentMemoriesPostTextRL.setVisibility(View.GONE);
                fragmentMemoriesPostAudioRL.setVisibility(View.VISIBLE);*/
                fragmentMemoriesAudioRecorderCV.setVisibility(View.GONE);

                File folder = new File(Environment.getExternalStorageDirectory() + ROOT_PATH);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                audioRecorder = new MediaRecorder();
                audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                audioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                audioRecorder.setMaxDuration(600000);
                audioRecorder.setOutputFile(Environment.getExternalStorageDirectory() + TEMP_AUDIO_PATH);
                audioRecorder.prepare();
                audioRecorder.start();
                countDownTimer = new CountDownTimer(600000, 1000) {
                    @Override
                    public void onTick(long l) {
                        timerProgress = timerProgress + 1000;
                        if (secondsTwo == 0) {
                            if (minutesTwo == 10) {
                                fragmentMemoriesAudioRecTimerTV.setText(minutesTwo + ":00");
                            } else {
                                fragmentMemoriesAudioRecTimerTV.setText("0" + minutesTwo + ":00");
                            }
                        } else {
                            if (secondsTwo < 10) {
                                fragmentMemoriesAudioRecTimerTV.setText("0" + minutesTwo + ":0" + secondsTwo);
                            } else {
                                fragmentMemoriesAudioRecTimerTV.setText("0" + minutesTwo + ":" + secondsTwo);
                            }
                        }
                        if (secondsTwo > 0) {
                            secondsTwo--;
                        } else {
                            secondsTwo = 59;
                            minutesTwo--;
                        }
                        fragmentMemoriesAudioRecProgress.setProgress(timerProgress);
                    }

                    @Override
                    public void onFinish() {
                        recordAudio(false);
                    }
                }.start();
            } else {
                isAudioRecording = false;
                /*fragmentMemoriesPostAudioRL.setVisibility(View.GONE);
                fragmentMemoriesPostTextRL.setVisibility(View.VISIBLE);*/
                fragmentMemoriesAudioRecorderCV.setVisibility(View.VISIBLE);

                if (audioRecorder != null) {
                    try {
                        audioRecorder.stop();
                        audioRecorder.release();
                        audioRecorder = null;
                        countDownTimer.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void audioSend() {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + TEMP_AUDIO_PATH);
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(new FileInputStream(file).getFD());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
            int duration = mediaPlayer.getDuration();

            if (duration > 1000) {
                String audio_name = "AUD_" + userDataSource.getUserId() + System.currentTimeMillis();
                File folder = new File(Environment.getExternalStorageDirectory() + AUDIO_PATH);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                File newFile = new File(Environment.getExternalStorageDirectory() + AUDIO_PATH + audio_name + EXT_AUDIO);
                if (file.exists()) {
                    file.renameTo(newFile);
                }

                JSONArray contentArray = new JSONArray();
                contentArray.put(newFile.getPath());

                isVoice = true;
                uploadAttachment(POST_TYPE_AUDIO, contentArray);
            } else {
                Toast.makeText(getContext(), "No Audio Avaialable to send", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAudioError() {
        fragmentMemoriesPostAudioErrorCV.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fragmentMemoriesPostAudioErrorCV.setVisibility(View.GONE);
            }
        }, 1000);
    }

    @NonNull
    private String saveBitmapPosts(Uri uri, String image_id) {
        File folder = new File(Environment.getExternalStorageDirectory() + IMAGE_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory() + IMAGE_PATH + image_id + EXT_IMAGE);
        try {
            Bitmap bitmap = resize(MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri), 1000, 1000);
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

    private Uri getCaptureImageUri() {
        File folder = new File(Environment.getExternalStorageDirectory() + ROOT_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory() + ROOT_PATH + "temp_attach_img" + EXT_IMAGE);
        return FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file);
    }

    private Uri getCaptureVideoUri() {
        File folder = new File(Environment.getExternalStorageDirectory() + ROOT_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory() + TEMP_VIDEO_PATH);
        return FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file);
    }

    private String getFileExtension(String file) {
        String extension = "";
        int i = file.lastIndexOf('.');
        if (i > 0) {
            extension = file.substring(i + 1);
        }
        return extension;
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

    private String audioDuration(String url) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            int duration;
            mediaPlayer.setDataSource(url);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();
            mediaPlayer.release();
            int sec = duration / 1000;
            if (sec < 60) {
                if (sec < 10) {
                    return "00:0" + sec;
                } else {
                    return "00:" + sec;
                }
            } else {
                int min = sec / 60;
                int remainingSeconds = sec - (min * 60);
                return min + ":" + remainingSeconds;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "0:00";
        }
    }

}
