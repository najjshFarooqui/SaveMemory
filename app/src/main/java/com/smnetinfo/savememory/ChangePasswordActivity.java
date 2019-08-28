package com.smnetinfo.savememory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText oldPassword, newPassword, confirmNewPassword;
    Button changePasswordButton;
    RelativeLayout change;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmNewPassword = findViewById(R.id.confirmNewPassword);
        changePasswordButton = findViewById(R.id.btnChangrPassword);
        change = findViewById(R.id.change);
        iv = findViewById(R.id.btnBack);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), MyPageActivity.class));
                finishAffinity();
            }
        });

        if (oldPassword.getText().toString().isEmpty() || newPassword.getText().toString().isEmpty() ||
                confirmNewPassword.getText().toString().isEmpty()) {
            Snackbar snackbar = Snackbar
                    .make(change, "please fill all the fields ", Snackbar.LENGTH_LONG);
            snackbar.show();

        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(ChangePasswordActivity.this).create();

            alertDialog.setMessage("Your password has been changed successfully");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }
}
