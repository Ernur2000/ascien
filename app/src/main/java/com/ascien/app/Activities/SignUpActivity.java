package com.ascien.app.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ascien.app.Models.RegisterRequest;
import com.ascien.app.Models.RegisterResponse;
import com.ascien.app.Network.ApiClient;
import com.ascien.app.R;
import com.ascien.app.Utils.Helpers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private EditText emailForRegister, usernameForRegister, passwordForRegister;
    private TextView login;
    private ProgressBar progressBar;
    private Button button;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        progressBar = findViewById(R.id.progressBar);
        emailForRegister = findViewById(R.id.et_registration_email);
        usernameForRegister = findViewById(R.id.et_registration_username);
        passwordForRegister = findViewById(R.id.et_registration_password);
        login = findViewById(R.id.tv_already_have_acc);
        button = findViewById(R.id.btn_register);
//        emailForRegister.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (emailForRegister.getText() != null && usernameForRegister.getText() != null && passwordForRegister.getText() != null) {
//                    button.setEnabled(true);
//                }
//            }
//        });
//        usernameForRegister.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (emailForRegister.getText() != null && usernameForRegister.getText() != null && passwordForRegister.getText() != null) {
//                    button.setEnabled(true);
//                }
//            }
//        });
//        passwordForRegister.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (emailForRegister.getText() != null && usernameForRegister.getText() != null && passwordForRegister.getText() != null) {
//                    button.setEnabled(true);
//                }
//            }
        //});


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email,password;
                email = String.valueOf(emailForRegister.getText());
                password = String.valueOf(passwordForRegister.getText());
                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Account created.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                    startActivity(intent);
                                    finish();
                                    HashMap<String, Object> userMap = new HashMap<>();
                                    userMap.put("name",String.valueOf(usernameForRegister.getText()));
                                    databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);

                                }
                            }
                        });
            }
        });
    }

    public void register(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Account created.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
//        RegisterRequest registerRequest = new RegisterRequest();
//        registerRequest.setEmail(emailForRegister.getText().toString());
//        registerRequest.setUsername(usernameForRegister.getText().toString());
//        registerRequest.setPassword(passwordForRegister.getText().toString());
//        Call<RegisterResponse> registerResponseCall = ApiClient.getApi().userRegister(registerRequest);
//        registerResponseCall.enqueue(new Callback<RegisterResponse>() {
//            @Override
//            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
//                RegisterResponse registerResponse = response.body();
//                if (response.isSuccessful()) {
//                    Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
//                    //Log.d("Aldiyar",response.body().toString() + "");
//                    saveUserDataOnSharedPreference();
//                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
//                    intent.putExtra("email", emailForRegister.getText().toString());
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(SignUpActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();
//                }
//                progressBar.setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onFailure(Call<RegisterResponse> call, Throwable t) {
//                Toast.makeText(SignUpActivity.this, "Throwable " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                progressBar.setVisibility(View.INVISIBLE);
//            }
//        });


    public void signIn(View view) {
        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(intent);
    }

    private void saveUserDataOnSharedPreference() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(Helpers.SHARED_PREF, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userToken", "sdfsdfsdfs");
        editor.commit();
    }
}