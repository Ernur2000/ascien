package com.ascien.app.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ascien.app.Models.Purchase;
import com.ascien.app.Network.ApiClient;
import com.ascien.app.R;
import com.ascien.app.Utils.Helpers;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Response;

public class CoursePurchaseActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private int mCourseId;

    private final static int FCR = 1;
    WebView webView;
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private static final String TAG = CoursePurchaseActivity.class.getSimpleName();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri[] results = null;
            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == this.mUploadMessage) {
                    return;
                }
                Uri result = null;
                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "activity :" + e,
                            Toast.LENGTH_LONG).show();
                }
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
        return;
    }

    private void purchaseCourse() {
        progressBar.setVisibility(View.VISIBLE);
        SharedPreferences preferences = this.getSharedPreferences(Helpers.SHARED_PREF, 0);
        String authToken = preferences.getString("userToken", null);
        Call<Purchase> purchaseCall = ApiClient.getApi().purchaseCourse("Bearer " + authToken, mCourseId);
        purchaseCall.enqueue(new retrofit2.Callback<Purchase>() {
            @Override
            public void onResponse(Call<Purchase> call, @NotNull Response<Purchase> response) {

                Purchase purchase = response.body();
                if (purchase == null) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(CoursePurchaseActivity.this, "Вы уже купили этот курс", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Purchase", purchase.getData().getPayment_url() + "");
                    String url = purchase.getData().getPayment_url();
                    progressBar.setVisibility(View.INVISIBLE);
                    openWebView(url);
                }

            }

            @Override
            public void onFailure(Call<Purchase> call, Throwable t) {
                Toast.makeText(CoursePurchaseActivity.this, "Failed" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void openWebView(String url) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }

    @SuppressLint({"SetJavaScriptEnabled", "WrongViewCast"})

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_purchase);
        init();
        initProgressBar();
        getThecourseId();
        //setupCoursePurchaseWebView();
        purchaseCourse();
    }

    private void init() {
        webView = findViewById(R.id.coursePurchaseWebView);
        webView.setVerticalScrollBarEnabled(false);
    }

    private void initProgressBar() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Sprite circularLoading = new Circle();
        progressBar.setIndeterminateDrawable(circularLoading);
    }

    private void getThecourseId() {
        this.mCourseId = (int) getIntent().getSerializableExtra("courseId");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("courseId", this.mCourseId);
        setResult(RESULT_OK, intent);
        finish();
    }

    public class Callback extends WebViewClient {
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater myMenuInflater = getMenuInflater();
        myMenuInflater.inflate(R.menu.super_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void handleBackButton(View view) {
        CoursePurchaseActivity.super.onBackPressed();
    }
}
