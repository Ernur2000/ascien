package com.ascien.app.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ascien.app.Models.RegisterRequest;
import com.ascien.app.Models.RegisterResponse;
import com.ascien.app.Network.ApiClient;
import com.ascien.app.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TemporaryActivity extends AppCompatActivity {
    private EditText emailForRegister, usernameForRegister, passwordForRegister;
    private TextView login;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporary);
        textView = findViewById(R.id.email);
        Bundle argument = getIntent().getExtras();
        String name = argument.get("email").toString();
        textView.setText(name);
        init();
    }

    private void init() {
        emailForRegister = findViewById(R.id.et_registration_email);
        usernameForRegister = findViewById(R.id.et_registration_username);
        passwordForRegister = findViewById(R.id.et_registration_password);
        login = findViewById(R.id.tv_already_have_acc);
    }

    public void verifyLogIn(View view) {
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
                    Toast.makeText(TemporaryActivity.this, "Verification send to email", Toast.LENGTH_LONG).show();
                    Log.d("Aldiyar", response.body().toString() + "");

                } else {
                    Toast.makeText(TemporaryActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(TemporaryActivity.this, "Throwable " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}