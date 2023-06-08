package com.ascien.app.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

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
    EditText cityEditText;
    TextInputEditText firstNameEditText;
    CircleImageView displayImageView;
    Uri filePath;
    private Uri imageUri;
    private Bitmap bitmap;
    private ProgressBar progressBar;
    private StorageReference storageProfilePicsRef;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private StorageTask uploadTask;
    private String myUri = "";
    GoogleSignInAccount acct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();
        initProgressBar();
        getLoggedInUserData();

        getUserInfo();
        
    }

    private void getUserInfo() {
        if (acct == null){
            databaseReference.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.getChildrenCount() > 0){
                        if (snapshot.hasChild("image")){
                            String image = Objects.requireNonNull(snapshot.child("image").getValue()).toString();
                            Picasso.get().load(image).into(displayImageView);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private void init() {
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        storageProfilePicsRef = FirebaseStorage.getInstance().getReference().child("Profile Pic");
        SharedPreferences preferences = this.getSharedPreferences(Helpers.SHARED_PREF, 0);
        String name = preferences.getString("userName", null);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        cityEditText = findViewById(R.id.cityEditText);
        acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct == null){
            databaseReference.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild("name")){
                        String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                        firstNameEditText.setText(name);
                    }
                    if (snapshot.hasChild("city")){
                        String city = Objects.requireNonNull(snapshot.child("city").getValue()).toString();
                        cityEditText.setText(city);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        cityEditText.setText(cityEditText.getText());
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
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            imageUri = result.getUri();
//            displayImageView.setImageURI(imageUri);
//            uploadDisplayImage.setVisibility(View.VISIBLE);
//        }
//        else{
//            Toast.makeText(this,"Error try again",Toast.LENGTH_LONG).show();
//        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void profileUpdate() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Set your profile");
        progressDialog.setMessage("Please wait, while we are setting your data");
        progressDialog.show();

        if (bitmap!= null){
            final StorageReference fileRef = storageProfilePicsRef
                    .child(Objects.requireNonNull(auth.getCurrentUser()).getUid() + ".jpg");

            uploadTask = fileRef.putFile(getImageUri(this,bitmap));

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        myUri = downloadUrl.toString();

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("name",String.valueOf(firstNameEditText.getText()));
                        userMap.put("city",String.valueOf(cityEditText.getText()));
                        userMap.put("image",myUri);


                        databaseReference.child(auth.getCurrentUser().getUid()).updateChildren(userMap);

                        progressDialog.dismiss();
                        onBackPressed();
                    }
                }
            });

        }else {
            progressDialog.dismiss();
            Toast.makeText(this,"Image not selected",Toast.LENGTH_LONG).show();
        }
//        SharedPreferences preferences = this.getSharedPreferences(Helpers.SHARED_PREF, 0);
//        String token = preferences.getString("userToken", null);
//        progressBar.setVisibility(View.VISIBLE);
//        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
//        updateProfileRequest.setName(firstNameEditText.getText().toString());
//        updateProfileRequest.setPassword(passwordEditText.getText().toString());
//        Call<LoginResponse> loginResponseCall = ApiClient.getApi().profileUpdate("Bearer " + token, updateProfileRequest);
//        loginResponseCall.enqueue(new Callback<LoginResponse>() {
//            @Override
//            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
//                progressBar.setVisibility(View.INVISIBLE);
//                if (response.isSuccessful()) {
//                    Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
//                    initViewElementsWithUserInfo(updateProfileRequest);
//                    Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(EditProfileActivity.this, "Get error reason", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginResponse> call, Throwable t) {
//                progressBar.setVisibility(View.INVISIBLE);
//                Toast.makeText(EditProfileActivity.this, "An Error Occured" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

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
        cityEditText.setText(cityEditText.getText());
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
