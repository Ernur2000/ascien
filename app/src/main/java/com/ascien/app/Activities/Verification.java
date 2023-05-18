package com.ascien.app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ascien.app.R;

public class Verification extends AppCompatActivity {
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        init();
    }
    private void init(){
        webView = findViewById(R.id.webView);
        webView.loadUrl("http://178.18.248.183/password/reset");
    }
}