package com.ascien.app.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ascien.app.Fragments.AccountFragment;
import com.ascien.app.Models.LoginResponse;
import com.ascien.app.R;
import com.ascien.app.Utils.Helpers;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    EditText emailAddressForLogin, passwordForLogin;
    ImageView applicationLogo;
    TextView loginTitle, register;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    int RC_SIGN_IN = 1000;
    private FirebaseAuth mAuth;
    Button login;

    private ProgressBar progressBar;
    private static final String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);
        init();
        initProgressBar();
        logInByGoogle();
        logIn();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(currentUser != null || account !=null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void init() {
        emailAddressForLogin = findViewById(R.id.et_login_email);
        passwordForLogin = findViewById(R.id.et_login_password);
        applicationLogo = findViewById(R.id.applicationLogo);
        //loginTitle = findViewById(R.id.loginTitle);
        register = findViewById(R.id.tv_create_acc);
        mAuth = FirebaseAuth.getInstance();
        login = findViewById(R.id.btn_login);
    }

    // Initialize the progress bar
    private void initProgressBar() {
        progressBar = findViewById(R.id.progressBar);
        Sprite circularLoading = new Circle();
        progressBar.setIndeterminateDrawable(circularLoading);
    }

    public void logInByGoogle() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void resetPassword(View view) {
        Intent intent = new Intent(SignInActivity.this, ChangePasswordActivity.class);
        startActivity(intent);
    }

    public void signUp(View view) {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private void logIn() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email,password;
                email = String.valueOf(emailAddressForLogin.getText());
                password = String.valueOf(passwordForLogin.getText());
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(SignInActivity.this,"Enter email",Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(SignInActivity.this,"Enter password",Toast.LENGTH_LONG).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "signInWithEmail:success");
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
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
            try{
                task.getResult(ApiException.class);
                finish();
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);

            } catch (ApiException e) {
                Log.w("Error", "signInResult:failed code=" + e);
            }
//            handleSignInResult(task);
//            SharedPreferences preferences = getApplicationContext().getSharedPreferences(Helpers.SHARED_PREF, 0);
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putInt("requestCode", RC_SIGN_IN);
//            editor.commit();

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account!=null){
                finish();
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
                Log.w("Status", "success");
            }
        } catch (ApiException e) {
            Log.w("Error", "signInResult:failed code=" + e);
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        SharedPreferences sharedPreferences = getSharedPreferences(Helpers.SHARED_PREF, 0);
//        String token = sharedPreferences.getString("userToken", null);
//        if (token != null) {
//            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }
}
