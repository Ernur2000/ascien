package com.ascien.app.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ascien.app.R;
import com.ascien.app.Utils.Helpers;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;

public class ChangePasswordActivity extends AppCompatActivity {
    Button updateButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        init();
        initProgressBar();
    }

    private void init() {
        updateButton = findViewById(R.id.btn_reset);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedIn()) {
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Log In First", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // Initialize the progress bar
    private void initProgressBar() {
        progressBar = findViewById(R.id.progressBar);
        Sprite circularLoading = new Circle();
        progressBar.setIndeterminateDrawable(circularLoading);
    }


    private boolean isLoggedIn() {
        SharedPreferences preferences = this.getSharedPreferences(Helpers.SHARED_PREF, 0);
        int userValidity = preferences.getInt("userValidity", 0);
        if (userValidity == 1) {
            return true;
        } else {
            return false;
        }
    }

    public void handleBackButton(View view) {
        ChangePasswordActivity.super.onBackPressed();
    }
}
