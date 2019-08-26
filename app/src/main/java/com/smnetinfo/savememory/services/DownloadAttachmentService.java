package com.smnetinfo.savememory.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;

import com.smnetinfo.savememory.extras.WebConstants;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Siva on 25/8/16.
 */
public class DownloadAttachmentService extends IntentService implements WebConstants {

    public DownloadAttachmentService() {
        super("Download");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int count;
        String post_id = intent.getExtras().getString(POST_ID);
        String post_url = intent.getExtras().getString(POST_URL);
        String post_path = intent.getExtras().getString(POST_PATH);
        String post_ext = intent.getExtras().getString(POST_EXT);
        try {
            URL url = new URL(post_url);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();

            File folder = new File(Environment.getExternalStorageDirectory() + post_path);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File file = new File(Environment.getExternalStorageDirectory() + post_path + post_id + post_ext);
            if (file.exists()) {
                file.delete();
            }
            InputStream inputStream = new BufferedInputStream(url.openStream());
            OutputStream outputStream = new FileOutputStream(file);
            byte data[] = new byte[1024];
            while ((count = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, count);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        stopSelf();
    }
}
