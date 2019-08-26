package com.smnetinfo.savememory;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.smnetinfo.savememory.extras.BaseActivity;
import com.smnetinfo.savememory.extras.WebConstants;

/**
 * Created by siva on 17/03/18.
 */

public class VideoStreamActivity extends BaseActivity implements WebConstants {

    ProgressDialog progressDialog;
    VideoView activityVideoStreamVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_stream);

        activityVideoStreamVideoView = findViewById(R.id.activityVideoStreamVideoView);

        String video_url = getIntent().getExtras().getString(URL);

        progressDialog = new ProgressDialog(VideoStreamActivity.this);
        progressDialog.setTitle("Video Streaming...");
        progressDialog.setMessage("Buffering...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            MediaController controller = new MediaController(VideoStreamActivity.this);
            controller.setAnchorView(activityVideoStreamVideoView);
            Uri vid = Uri.parse(video_url);
            activityVideoStreamVideoView.setMediaController(controller);
            activityVideoStreamVideoView.setVideoURI(vid);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        activityVideoStreamVideoView.requestFocus();
        activityVideoStreamVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressDialog.dismiss();
                activityVideoStreamVideoView.start();
            }
        });
        activityVideoStreamVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                progressDialog.dismiss();
                finish();
                return false;
            }
        });
    }
}