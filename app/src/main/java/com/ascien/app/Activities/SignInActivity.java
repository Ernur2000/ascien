package com.ascien.app.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ascien.app.Models.LoginRequest;
import com.ascien.app.Models.LoginResponse;
import com.ascien.app.Network.ApiClient;
import com.ascien.app.R;
import com.ascien.app.Utils.Helpers;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    EditText emailAddressForLogin, passwordForLogin;
    ImageView applicationLogo;
    TextView loginTitle, register;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 0;

    private ProgressBar progressBar;
    private static final String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);
        init();
        initProgressBar();
        logInByGoogle();
    }

    private void init() {
        emailAddressForLogin = findViewById(R.id.et_login_email);
        passwordForLogin = findViewById(R.id.et_login_password);
        applicationLogo = findViewById(R.id.applicationLogo);
        //loginTitle = findViewById(R.id.loginTitle);
        register = findViewById(R.id.tv_create_acc);
    }

    // Initialize the progress bar
    private void initProgressBar() {
        progressBar = findViewById(R.id.progressBar);
        Sprite circularLoading = new Circle();
        progressBar.setIndeterminateDrawable(circularLoading);
    }

    public void logInByGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void openDialogFragment(View view) {
        Intent intent = new Intent(SignInActivity.this, Verification.class);
        startActivity(intent);
    }

    public void signUp(View view) {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void logIn(View view) {
        progressBar.setVisibility(View.VISIBLE);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(emailAddressForLogin.getText().toString());
        loginRequest.setPassword(passwordForLogin.getText().toString());
        Call<LoginResponse> loginResponseCall = ApiClient.getApi().userLogin(loginRequest);

        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                if (response.isSuccessful()) {
                    Toast.makeText(SignInActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                    initPreferences(loginResponse);


                    //Log.d("Ali",TOKEN + "");
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignInActivity.this, "Login Failed", Toast.LENGTH_LONG).show();

                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(SignInActivity.this, "Throwable " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.d("Aldiyar", t.getLocalizedMessage() + "");
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void initPreferences(LoginResponse loginResponse) {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(Helpers.SHARED_PREF, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userMessage", loginResponse.getMessage());
        editor.putInt("usedId", loginResponse.data.user.getId());
        editor.putString("userName", loginResponse.data.user.getName());
        editor.putString("userEmail", loginResponse.data.user.getEmail());
        editor.putString("userToken", loginResponse.data.getToken());
        editor.commit();
    }

    public void regByGoogle(View view) {
        switch (view.getId()) {
            case R.id.enter_with_google:
                signIn();
                Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Reqest Code", requestCode + "");
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            SharedPreferences preferences = getApplicationContext().getSharedPreferences(Helpers.SHARED_PREF, 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("requestCode", RC_SIGN_IN);
            editor.commit();

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
        } catch (ApiException e) {
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(Helpers.SHARED_PREF, 0);
        String token = sharedPreferences.getString("userToken", null);
        if (token != null) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
