package com.ascien.app.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ascien.app.Activities.EditProfileActivity;
import com.ascien.app.Activities.MainActivity;
import com.ascien.app.Activities.SignInActivity;
import com.ascien.app.Models.RegisterResponse;
import com.ascien.app.R;
import com.ascien.app.Utils.Helpers;
import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    Button signInButton;
    RelativeLayout signInPlaceholder, accountView;
    TextView userName, cityText;
    MaterialCardView editProfileRelativeLayout, logOutRelativeLayout, shareRelativeLayout;
    private ProgressBar progressBar;
    View view;
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    GoogleSignInClient mGoogleSignInClient;
    private CircleImageView profileImageView;
    FirebaseAuth auth;
    private DatabaseReference databaseReference;
    GoogleSignInAccount acct;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.account_fragment, container, false);
        init(view);
        initProgressBar(view);
        initSwipeRefreshLayout(view);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        getDataFromGoogle();
        getUserInfo();
        return view;
    }

    public void getDataFromGoogle() {
        acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (acct != null) {
            String personName = acct.getDisplayName();
            Log.d("TAG123",personName + "");
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            userName.setText(personName);
            Glide.with(this).load(String.valueOf(personPhoto)).into(profileImageView);
        }
    }

    private boolean isLoggedIn() {
        RegisterResponse registerResponse = new RegisterResponse();
        SharedPreferences preferences = getContext().getSharedPreferences(Helpers.SHARED_PREF, 0);
        String userValidity = preferences.getString("userToken", null);
        String userValidity1 = preferences.getString("regId", registerResponse.getMessage());
        int sign_In = preferences.getInt("requestCode", 0);
        if (userValidity != null || userValidity1 != null || sign_In == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    void init(View view) {
        signInPlaceholder = view.findViewById(R.id.signInPlaceHolder);
        accountView = view.findViewById(R.id.accountView);
        profileImageView = view.findViewById(R.id.displayImageView);
        userName = view.findViewById(R.id.userName);
        cityText = view.findViewById(R.id.city);
        editProfileRelativeLayout = view.findViewById(R.id.editProfileRelativeLayout1);
        //changePasswordRelativeLayout = view.findViewById(R.id.changePasswordRelativeLayout1);
        logOutRelativeLayout = view.findViewById(R.id.logOutRelativeLayout1);
        shareRelativeLayout = view.findViewById(R.id.shareRelativeLayout1);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");

        logOutRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (acct == null){
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getContext(),SignInActivity.class);
                    startActivity(intent);
                    ((Activity) Objects.requireNonNull(getContext())).finish();
                }
                else {
                    signOut();
                }

//                clearAllTheSharedPreferencesValues();
//                switch (view.getId()) {
//                    case R.id.logOutRelativeLayout1:
//                        Objects.requireNonNull(getActivity()).finish();

//                        break;
//                }
            }
        });

        editProfileRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditProfileActivity.class);
                startActivity(intent);
            }
        });


        shareRelativeLayout.setOnClickListener(v -> {
            shareThisCourseOnSocialMedia(view);
        });


        // SignIn button action
        signInButton = view.findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SignInActivity.class);
                startActivity(intent);
            }
        });

    }

    private void getUserInfo() {
        if (acct==null){
            databaseReference.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.getChildrenCount() > 0){
                        if (snapshot.hasChild("name")){
                            String name = snapshot.child("name").getValue().toString();
                            userName.setText(name);
                        } if (snapshot.hasChild("city")){
                            String city = snapshot.child("city").getValue().toString();
                            cityText.setText(city);
                        }

                        if (snapshot.hasChild("image")){
                            String image = Objects.requireNonNull(snapshot.child("image").getValue()).toString();
                            Picasso.get().load(image).into(profileImageView);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener((Activity) Objects.requireNonNull(getContext()), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(),SignInActivity.class);
                        startActivity(intent);
                        ((Activity) Objects.requireNonNull(getContext())).finish();
                    }
                });
    }

    private void initSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.googleAccentColor1,
                R.color.googleAccentColor2,
                R.color.googleAccentColor3,
                R.color.googleAccentColor4);
    }

    // Initialize the progress bar
    private void initProgressBar(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        Sprite circularLoading = new Circle();
        progressBar.setIndeterminateDrawable(circularLoading);
    }

    private void clearAllTheSharedPreferencesValues() {
        final SharedPreferences preferences = getContext().getSharedPreferences(Helpers.SHARED_PREF, 0);
        preferences.edit().remove("userId").apply();
        preferences.edit().remove("userName").apply();
        preferences.edit().remove("userEmail").apply();
        preferences.edit().remove("userToken").apply();
        preferences.edit().remove("regId").apply();
        preferences.edit().clear();
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        //getUserDataFromAPI();
    }

    private void shareThisCourseOnSocialMedia(View view) {
        Intent myIntent = new Intent(Intent.ACTION_SEND);
        myIntent.setType("text/plain");
        startActivity(Intent.createChooser(myIntent, "Share using"));
    }
}
