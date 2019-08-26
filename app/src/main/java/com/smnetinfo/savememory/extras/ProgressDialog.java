package com.smnetinfo.savememory.extras;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.smnetinfo.savememory.R;
import com.wang.avi.AVLoadingIndicatorView;
import com.wang.avi.indicators.BallRotateIndicator;

/**
 * Created by Siva on 4/8/16.
 */
public class ProgressDialog {

    private Context context;
    private Dialog progressDialog;

    public ProgressDialog(Context context) {
        this.context = context;
    }

    public void show() {
        progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress_dialog_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        TextView progressTextView = progressDialog.findViewById(R.id.progressTextView);
        progressTextView.setVisibility(View.GONE);
        AVLoadingIndicatorView progressLoading = progressDialog.findViewById(R.id.progressLoading);
        BallRotateIndicator ballRotateIndicator = new BallRotateIndicator();
        progressLoading.setIndicator(ballRotateIndicator);
        progressLoading.smoothToShow();
    }

    public void show(String title) {
        progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress_dialog_layout);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        TextView progressTextView = progressDialog.findViewById(R.id.progressTextView);
        progressTextView.setVisibility(View.VISIBLE);
        progressTextView.setText(title);
        AVLoadingIndicatorView progressLoading = progressDialog.findViewById(R.id.progressLoading);
        BallRotateIndicator ballRotateIndicator = new BallRotateIndicator();
        progressLoading.setIndicator(ballRotateIndicator);
        progressLoading.smoothToShow();
    }

    public void dismiss() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
