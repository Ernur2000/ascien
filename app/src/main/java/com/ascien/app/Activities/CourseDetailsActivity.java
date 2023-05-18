package com.ascien.app.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.ascien.app.Adapters.CourseCurriculumSectionAdapter;
import com.ascien.app.Adapters.ViewPagerAdapter;
import com.ascien.app.Models.CourseDetails;
import com.ascien.app.Models.DetailPublicCourse;
import com.ascien.app.Models.Section;
import com.ascien.app.Models.Sections;
import com.ascien.app.Models.TopCourse;
import com.ascien.app.Models.WishListCourse;
import com.ascien.app.Models.WishListResponse;
import com.ascien.app.Network.ApiClient;
import com.ascien.app.R;
import com.ascien.app.Utils.BounceInterpolator;
import com.ascien.app.Utils.Helpers;
import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseDetailsActivity extends AppCompatActivity {
    private TopCourse mCourse;
    private WishListCourse wishListCourse;
    private TextView mcourseTitle;
    private TextView mNumberOfEnrolledStudentNumber;
    private TextView mNumericRating;
    private TextView mTotalNumberOfRatingByUsers;
    private TextView mCoursePrice;
    private TextView language;
    private ImageView mCourseBanner;
    private RatingBar mStarRating;
    private ImageButton mPlayCoursePreview;
    private ImageButton mWishlistThisCourse;
    private ImageButton mShareThisCourse;
    private ImageButton mBackToCourseList;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private TabLayout mTabLayout;
    private ProgressBar progressBar;
    private ArrayList<CourseDetails> mEachCourseDetail = new ArrayList<>();
    private ArrayList<DetailPublicCourse> mEachDetailCourse = new ArrayList<>();
    private ArrayList<Section> mSections = new ArrayList<>();
    private ArrayList<Sections> sectionsNew = new ArrayList<>();
    private RecyclerView mSectionRecyclerView;
    private Button mBuyThisCourseButton;
    private TopCourse topCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        initElements();
        initProgressBar();
        // The filtered course object is being passed from another activity or adapter
        getCourseObject();
        setupViewPager(mCourse);
        initSectionRecyclerView();

    }

    private void initElements() {
        mBackToCourseList = findViewById(R.id.backToAllCoursesList);
        mcourseTitle = findViewById(R.id.courseTitle);
        mNumberOfEnrolledStudentNumber = findViewById(R.id.numberOfEnrolledStudentNumber);
        mNumericRating = findViewById(R.id.numericRating);
        mTotalNumberOfRatingByUsers = findViewById(R.id.totalNumberOfRatingByUsers);
        mCoursePrice = findViewById(R.id.coursePrice);
        mCourseBanner = findViewById(R.id.courseBannerImage);
        language = findViewById(R.id.language_tv);
        mStarRating = findViewById(R.id.starRating);
        mPlayCoursePreview = findViewById(R.id.playCoursePreview);
        mWishlistThisCourse = findViewById(R.id.wishlistThisCourse);
        mShareThisCourse = findViewById(R.id.shareThisCourse);
        mShareThisCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareThisCourseOnSocialMedia(view);
            }
        });
        mSectionRecyclerView = findViewById(R.id.courseCurriculumSectionRecyclerView);
        mBuyThisCourseButton = findViewById(R.id.buyThisCourseButton);
    }

    private void setupViewPager(TopCourse topCourse) {
        mViewPager = findViewById(R.id.courseViewPager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), topCourse);
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.courseViewPagerTabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initProgressBar() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Sprite circularLoading = new Circle();
        progressBar.setIndeterminateDrawable(circularLoading);
    }

    public void handleBackButton(View view) {
        CourseDetailsActivity.super.onBackPressed();
    }

    private void getCourseObject() {
        if (getIntent().hasExtra("courseId")) {
            int courseId = (int) getIntent().getSerializableExtra("courseId");
            getDetailCourse(mCourse.getId(), mCourse);
        } else {
            mCourse = (TopCourse) getIntent().getSerializableExtra("TopCourse");
            Log.d("CourseInFo", mCourse + "");
            mcourseTitle.setText(mCourse.getTitle());
            language.setText(mCourse.getLanguage());
            Glide.with(this)
                    .asBitmap()
                    .load("https://ascien.s3.us-east-2.amazonaws.com/" + mCourse.getImage())
                    .into(mCourseBanner);
            mPlayCoursePreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playPreview();
                }
            });
            if (mCourse.getPrice() != null) {
                mCoursePrice.setText(mCourse.getPrice() + " т");
            } else {
                mCoursePrice.setText("Бесплатно");
            }
            getDetailCourse(mCourse.getId(), mCourse);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getCourseObject();
    }

    private void playPreview() {
        Intent intent = new Intent(getApplicationContext(), PopUpActivity.class);
        intent.putExtra("TopCourse", mCourse);
        startActivity(intent);
    }

    private void shareThisCourseOnSocialMedia(View view) {
        Intent myIntent = new Intent(Intent.ACTION_SEND);
        myIntent.setType("text/plain");
        String shareBody = "ascien";
        String shareSub = mCourse.getTitle();
        myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
        myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(myIntent, "Share using"));
    }

    public void getDetailCourse(Integer courseId, TopCourse topCourse) {
        progressBar.setVisibility(View.VISIBLE);
//        Call<DetailPublicCourse> call = ApiClient.getApi().getDetailPublicCourse(courseId);
//        call.enqueue(new Callback<DetailPublicCourse>() {
//            @Override
//            public void onResponse(Call<DetailPublicCourse> call, Response<DetailPublicCourse> response) {
//                progressBar.setVisibility(View.INVISIBLE);
//                DetailPublicCourse detailPublicCourses = response.body();
//                Log.d("Detail Course", detailPublicCourses + "");
//                mEachDetailCourse.add(new DetailPublicCourse(topCourse, sectionsNew));
//                sectionsNew = new ArrayList<>();
//                List<Sections> sections = detailPublicCourses.getSections();
//                for (Sections s : sections) {
//                    sectionsNew.add(new Sections(s.getId(), s.getCourse_id(), s.getTitle(), s.getDescription(), s.getLessons()));
//                }
//                setupViewPager(topCourse);
//                initSectionRecyclerView();
//
//            }
//
//            @Override
//            public void onFailure(Call<DetailPublicCourse> call, Throwable t) {
//                progressBar.setVisibility(View.INVISIBLE);
//                Toast.makeText(CourseDetailsActivity.this, "Failed" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                Log.d("Error", t.getLocalizedMessage() + "");
//            }
//        });


    }

    private void initSectionRecyclerView() {
        CourseCurriculumSectionAdapter adapter = new CourseCurriculumSectionAdapter(getApplicationContext(), sectionsNew);
        mSectionRecyclerView.setAdapter(adapter);
        mSectionRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        setHeight(mSections.size(), mSectionRecyclerView);
    }

    private void setHeight(int numberOfItems, RecyclerView mRecyclerView) {
        int pixels = Helpers.convertDpToPixel((numberOfItems * 40) + 10); // numberOfItems is the number of categories and the 90 is each items height with the margin of top and bottom. Extra 10 dp for readability
        ViewGroup.LayoutParams params1 = mRecyclerView.getLayoutParams();
        mRecyclerView.setMinimumHeight(pixels);
        mRecyclerView.requestLayout();
    }

    public void handleWishListButton(View view) {
        // Auth Token
        SharedPreferences preferences = getSharedPreferences(Helpers.SHARED_PREF, 0);
        String authToken = preferences.getString("userToken", "invalid");
        if (authToken == null) {
            Toast.makeText(this, "Please Login First", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CourseDetailsActivity.this, SignInActivity.class);
            startActivity(intent);
        } else {
            //toggleWishListItem();
            likedWishListItem();
            //unLikedWishListItem();
        }
    }

    private void likedWishListItem() {
        progressBar.setVisibility(View.VISIBLE);
        SharedPreferences preferences = this.getSharedPreferences(Helpers.SHARED_PREF, 0);
        SharedPreferences.Editor editor = preferences.edit();
        String authToken = preferences.getString("userToken", null);
        Call<WishListResponse> wishListResponseCall = ApiClient.getApi().likeCourse("Bearer " + authToken, mCourse.getId());
        wishListResponseCall.enqueue(new Callback<WishListResponse>() {
            @Override
            public void onResponse(Call<WishListResponse> call, Response<WishListResponse> response) {
                WishListResponse wishListResponse = response.body();
                if (wishListResponse.isSuccess()) {
                    int Id = preferences.getInt("WishId", 0);
                    if (Id == mCourse.getId()) {
                        mWishlistThisCourse.setImageResource(R.drawable.wishlist_filled);
                        Toast.makeText(CourseDetailsActivity.this, "This course already in your wishlist", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("WishListResponse", wishListResponse.getMessage());
                        mWishlistThisCourse.setImageResource(R.drawable.wishlist_filled);
                        setBounceAnimationOnButton(mWishlistThisCourse);
                        editor.putInt("WishId", mCourse.getId());
                        Toast.makeText(CourseDetailsActivity.this, "Added To Wishlist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("WishListResponse", wishListResponse.getMessage());
                    mWishlistThisCourse.setImageResource(R.drawable.wishlist_empty);
                    setBounceAnimationOnButton(mWishlistThisCourse);
                    Toast.makeText(CourseDetailsActivity.this, "Removed From Wishlist", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<WishListResponse> call, Throwable t) {
                Toast.makeText(CourseDetailsActivity.this, "Like was failed" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setBounceAnimationOnButton(ImageButton imageButton) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        BounceInterpolator interpolator = new BounceInterpolator(0.2, 30);
        bounceAnim.setInterpolator(interpolator);
        imageButton.startAnimation(bounceAnim);
    }

    public void handleBuyNow(View view) {
        Intent intent = new Intent(CourseDetailsActivity.this, CoursePurchaseActivity.class);
        intent.putExtra("courseId", mCourse.getId());
        startActivity(intent);
    }
}
