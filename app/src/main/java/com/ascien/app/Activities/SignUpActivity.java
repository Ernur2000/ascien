package com.ascien.app.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ascien.app.Models.RegisterRequest;
import com.ascien.app.Models.RegisterResponse;
import com.ascien.app.Network.ApiClient;
import com.ascien.app.R;
import com.ascien.app.Utils.Helpers;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private EditText emailForRegister, usernameForRegister, passwordForRegister;
    private TextView login;
    private ProgressBar progressBar;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
    }

    private void init() {
        progressBar = findViewById(R.id.progressBar);
        emailForRegister = findViewById(R.id.et_registration_email);
        usernameForRegister = findViewById(R.id.et_registration_username);
        passwordForRegister = findViewById(R.id.et_registration_password);
        login = findViewById(R.id.tv_already_have_acc);
        button = findViewById(R.id.btn_register);
        emailForRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (emailForRegister.getText() != null && usernameForRegister.getText() != null && passwordForRegister.getText() != null) {
                    button.setEnabled(true);
                }
            }
        });
        usernameForRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (emailForRegister.getText() != null && usernameForRegister.getText() != null && passwordForRegister.getText() != null) {
                    button.setEnabled(true);
                }
            }
        });
        passwordForRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (emailForRegister.getText() != null && usernameForRegister.getText() != null && passwordForRegister.getText() != null) {
                    button.setEnabled(true);
                }
            }
        });

    }

    public void register(View view) {
        progressBar.setVisibility(View.VISIBLE);
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(emailForRegister.getText().toString());
        registerRequest.setUsername(usernameForRegister.getText().toString());
        registerRequest.setPassword(passwordForRegister.getText().toString());
        Call<RegisterResponse> registerResponseCall = ApiClient.getApi().userRegister(registerRequest);
        registerResponseCall.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                RegisterResponse registerResponse = response.body();
                if (response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                    //Log.d("Aldiyar",response.body().toString() + "");
                    saveUserDataOnSharedPreference();
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.putExtra("email", emailForRegister.getText().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(SignUpActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Throwable " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

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