package com.ascien.app.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ascien.app.Models.LoginResponse;
import com.ascien.app.Models.UpdateProfileRequest;
import com.ascien.app.Network.ApiClient;
import com.ascien.app.R;
import com.ascien.app.Utils.Helpers;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 2342;
    private static final int PICK_IMAGE_REQUEST = 22;
    Button chooseDisplayImage, uploadDisplayImage, submitButton;
    EditText passwordEditText;
    TextInputEditText firstNameEditText;
    CircleImageView displayImageView;
    Uri filePath;
    private Bitmap bitmap;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();
        initProgressBar();
        getLoggedInUserData();
    }

    private void getLoggedInUserData() {
        if (isLoggedIn()) {
        } else {
            Toast.makeText(this, "Please Login First", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    public void profileUpdate() {
        SharedPreferences preferences = this.getSharedPreferences(Helpers.SHARED_PREF, 0);
        String token = preferences.getString("userToken", null);
        progressBar.setVisibility(View.VISIBLE);
        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
        updateProfileRequest.setName(firstNameEditText.getText().toString());
        updateProfileRequest.setPassword(passwordEditText.getText().toString());
        Call<LoginResponse> loginResponseCall = ApiClient.getApi().profileUpdate("Bearer " + token, updateProfileRequest);
        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    initViewElementsWithUserInfo(updateProfileRequest);
                    Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(EditProfileActivity.this, "Get error reason", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(EditProfileActivity.this, "An Error Occured" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void init() {
        SharedPreferences preferences = this.getSharedPreferences(Helpers.SHARED_PREF, 0);
        String name = preferences.getString("userName", null);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        firstNameEditText.setText(name);
        passwordEditText.setText(passwordEditText.getText());
        chooseDisplayImage = findViewById(R.id.chooseDisplayImage);
        displayImageView = findViewById(R.id.displayImageView);
        uploadDisplayImage = findViewById(R.id.uploadDisplayImage);
        submitButton = findViewById(R.id.submitButton);
        requestStoragePermission();

        chooseDisplayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChoose();
            }
        });

        uploadDisplayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadUserImage();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //submitEditProfileForm();
                profileUpdate();
            }
        });
    }

    private void initProgressBar() {
        progressBar = findViewById(R.id.progressBar);
        Sprite circularLoading = new Circle();
        progressBar.setIndeterminateDrawable(circularLoading);
    }


    private boolean isLoggedIn() {
        SharedPreferences preferences = this.getSharedPreferences(Helpers.SHARED_PREF, 0);
        String userValidity = preferences.getString("userToken", null);
        if (userValidity != null) {
            return true;
        } else {
            return false;
        }
    }


    private void initViewElementsWithUserInfo(UpdateProfileRequest updateProfileRequest) {
        progressBar.setVisibility(View.VISIBLE);

        firstNameEditText.setText(firstNameEditText.getText());
        passwordEditText.setText(passwordEditText.getText());
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granter", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Not Granter", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                displayImageView.setImageBitmap(bitmap);
                uploadDisplayImage.setVisibility(View.VISIBLE);

            } catch (IOException e) {

            }
        }
    }

    private void showFileChoose() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select A Display Image"), PICK_IMAGE_REQUEST);
    }

    private String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();

        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;

    }

    private void uploadUserImage() {
        Toast.makeText(this, "Uploading....", Toast.LENGTH_SHORT).show();
        String mediaPath = getPath(filePath);
        // Auth Token
        SharedPreferences preferences = this.getSharedPreferences(Helpers.SHARED_PREF, 0);
        String authToken = preferences.getString("userToken", null);
        File file = new File(mediaPath);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileData = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        Call<LoginResponse> loginResponseCall = ApiClient.getApi().avatarProfile("Bearer " + authToken, fileData);
        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    //getLoggedInUserData();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Image uploading failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Image uploading failed" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void handleBackButton(View view) {
        EditProfileActivity.super.onBackPressed();
    }
}
