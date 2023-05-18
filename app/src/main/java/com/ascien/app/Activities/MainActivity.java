package com.ascien.app.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ascien.app.Fragments.AccountFragment;
import com.ascien.app.Fragments.CourseFragment;
import com.ascien.app.Fragments.MyCourseFragment;
import com.ascien.app.Fragments.WishlistFragment;
import com.ascien.app.JSONSchemas.CourseSchema;
import com.ascien.app.Models.Course;
import com.ascien.app.Models.DifficultyLevel;
import com.ascien.app.Models.Language;
import com.ascien.app.Models.Price;
import com.ascien.app.Models.Rating;
import com.ascien.app.Models.TopCourse;
import com.ascien.app.Network.Api;
import com.ascien.app.Network.ApiClient;
import com.ascien.app.R;
import com.ascien.app.Utils.Helpers;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private BottomSheetBehavior bottomSheetBehavior;

    private Spinner categorySpinner;
    private Spinner priceSpinner;
    private Spinner difficultyLevelSpinner;
    private Spinner languageSpinner;
    private Spinner ratingSpinner;

    private String selectedCategory;
    private String selectedPrice;
    private String selectedDifficultyLevel;
    private String selectedLanguage;
    private String selectedRating;


    Button showSearchBoxButton;
    Button hideSearchBoxButton;
    EditText searchStringInputField;
    Button backButton;
    ImageView applicationLogo;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton filterButton;
    ImageButton closeFilterViewButton;
    Button filterApplyButton;
    Button filterResetButton;
    ArrayAdapter<String> categoryAdapter;
    ArrayAdapter<Price> priceAdapter;
    ArrayAdapter<DifficultyLevel> difficultyLevelArrayAdapter;
    ArrayAdapter<Language> languageArrayAdapter;
    ArrayAdapter<Rating> ratingArrayAdapter;
    // this is the search string
    Editable searchString;
    View bottomSheetView;
    private ArrayList<Course> mCourses = new ArrayList<>();
    private ArrayList<TopCourse> CourseNew = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        getSupportFragmentManager().beginTransaction().replace(R.id.homePageFrameLayout, new CourseFragment()).commit();

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this.navListener);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.d(TAG, "Bottom Sheet Collapsed");
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.d(TAG, "Bottom Sheet Expanded");
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.d(TAG, "Bottom Sheet Dragging");
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        final ArrayList<String> amCategory = new ArrayList<>();
        amCategory.add("Все");
        amCategory.add("Финансы и бухгалтерский учет");
        amCategory.add("Разработка");
        amCategory.add("Бизнес");
        amCategory.add("ИТ и ПО");
        amCategory.add("Офисное программное обеспечение");
        amCategory.add("Личностный рост");
        amCategory.add("Дизайн");
        amCategory.add("Маркетинг");
        amCategory.add("Стиль жизни");
        amCategory.add("Фотография и видео");
        amCategory.add("Здоровье и фитнес");
        amCategory.add("Музыка");
        amCategory.add("Учебные и академические дисциплины");
        amCategory.add("ЕНТ");
        initializeCategorySpinner(amCategory);
        getPrice();
        getDifficultyLevel();
        getLanguage();
        getRating();

        closeFilterViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(bottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        filterApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(bottomSheetBehavior.STATE_COLLAPSED);
                filterCourseMethod();

            }
        });

        filterResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFilter();
            }
        });
    }

    private void init() {
        showSearchBoxButton = findViewById(R.id.showSearchBarButton);
        hideSearchBoxButton = findViewById(R.id.hideSearchBarButton);
        applicationLogo = findViewById(R.id.applicationLogo);
        backButton = findViewById(R.id.backButton);
        searchStringInputField = findViewById(R.id.searchStringInputField);

        filterButton = findViewById(R.id.floatingFilterButton);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomSheetView = findViewById(R.id.bottomSheet);
        closeFilterViewButton = findViewById(R.id.filterViewCloseButton);
        filterApplyButton = findViewById(R.id.filterApplyButton);
        filterResetButton = findViewById(R.id.filterResetButton);

        searchStringInputField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchCourse();
                    return true;
                }
                return false;
            }
        });
        backButton.setVisibility(View.GONE);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }

    public void handleFilterButton(View view) {
        switch (bottomSheetBehavior.getState()) {
            case BottomSheetBehavior.STATE_COLLAPSED:
                bottomSheetBehavior.setState(bottomSheetBehavior.STATE_EXPANDED);
                bottomSheetView.bringToFront();
                break;
            case BottomSheetBehavior.STATE_EXPANDED:
                bottomSheetBehavior.setState(bottomSheetBehavior.STATE_COLLAPSED);
                break;
        }
    }

    private void initializeCategorySpinner(final ArrayList<String> mCategory) {
        categorySpinner = findViewById(R.id.filterCategorySpinner);
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mCategory);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //getSelectedCategory(mCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initializePriceSpinner(final ArrayList<Price> mPrice) {
        priceSpinner = findViewById(R.id.filterPriceSpinner);
        priceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mPrice);
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priceSpinner.setAdapter(priceAdapter);
        priceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getSelectedPrice(mPrice.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initializeDifficultySpinner(final ArrayList<DifficultyLevel> mDifficultyLevel) {
        difficultyLevelSpinner = findViewById(R.id.filterLevelSpinner);
        difficultyLevelArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mDifficultyLevel);
        difficultyLevelArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultyLevelSpinner.setAdapter(difficultyLevelArrayAdapter);
        difficultyLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getSelectedDifficultyLevel(mDifficultyLevel.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initializeLanguageSpinner(final ArrayList<Language> mLanguage) {
        languageSpinner = findViewById(R.id.filterLanguageSpinner);
        languageArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mLanguage);
        languageArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageArrayAdapter);
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getSelectedLanguage(mLanguage.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initializeRatingSpinner(final ArrayList<Rating> mRating) {
        ratingSpinner = findViewById(R.id.filterRatingSpinner);
        ratingArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mRating);
        ratingArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ratingSpinner.setAdapter(ratingArrayAdapter);
        ratingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getSelectedRating(mRating.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void getPrice() {
        // Making empty array of category
        final ArrayList<Price> mPrice = new ArrayList<>();
        mPrice.add(new Price(0, "all", "Все"));
        mPrice.add(new Price(1, "free", "Беслатные"));
        mPrice.add(new Price(2, "paid", "Платные"));
        initializePriceSpinner(mPrice);
    }

    private void getDifficultyLevel() {
        // Making empty array of category
        final ArrayList<DifficultyLevel> mDifficultyLevel = new ArrayList<>();
        mDifficultyLevel.add(new DifficultyLevel(0, "all", "Все"));
        mDifficultyLevel.add(new DifficultyLevel(1, "beginner", "Начальный"));
        mDifficultyLevel.add(new DifficultyLevel(2, "advanced", "Сложный"));
        mDifficultyLevel.add(new DifficultyLevel(3, "intermediate", "Средний"));
        initializeDifficultySpinner(mDifficultyLevel);
    }

    private void getLanguage() {
        // Making empty array of Language
        final ArrayList<Language> mLanguage = new ArrayList<>();
        mLanguage.add(new Language(0, "all", "Все"));
        mLanguage.add(new Language(0, "all", "Руский"));
        mLanguage.add(new Language(0, "all", "English"));
        mLanguage.add(new Language(0, "all", "Қазақша"));
        initializeLanguageSpinner(mLanguage);
    }

    private void getRating() {
        // Making empty array of Rating
        final ArrayList<Rating> mRating = new ArrayList<>();

        mRating.add(new Rating(0, 0, "Все"));
        mRating.add(new Rating(1, 1, "⭐️"));
        mRating.add(new Rating(2, 2, "⭐⭐️"));
        mRating.add(new Rating(3, 3, "⭐️⭐⭐"));
        mRating.add(new Rating(4, 4, "⭐️⭐⭐⭐"));
        mRating.add(new Rating(5, 5, "⭐️⭐⭐⭐⭐"));
        initializeRatingSpinner(mRating);
    }

    public void getSelectedPrice(Price price) {
        this.selectedPrice = price.getValue();
    }

    public void getSelectedDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.selectedDifficultyLevel = difficultyLevel.getValue();
    }

    public void getSelectedLanguage(Language language) {
        this.selectedLanguage = language.getValue();
    }

    public void getSelectedRating(Rating rating) {
        if (rating.getValue() == 0) {
            this.selectedRating = "all";
        } else {
            this.selectedRating = Integer.toString(rating.getValue());
        }
    }


    private void filterCourseMethod() {
        String searchedString = searchString + "";
        //progressBar.setVisibility(View.VISIBLE);
        Call<List<TopCourse>> topCourseCall = ApiClient.getApi().getFilteredCourse(searchedString, selectedCategory, selectedPrice);
        topCourseCall.enqueue(new Callback<List<TopCourse>>() {
            @Override
            public void onResponse(Call<List<TopCourse>> call, Response<List<TopCourse>> response) {
                CourseNew = new ArrayList<>();
                List<TopCourse> topCourses = response.body();
                for (TopCourse t : topCourses) {
                    CourseNew.add(new TopCourse(t.getId(), t.getUser_id(), t.getTitle(), t.getCategory_id(), t.getShort_description(), t.getLanguage(), t.getDescription(), t.getLevel(), t.getImage(), t.getIntro_video(), t.getRequirements(), t.getWhat_will_learn(), t.getIs_free(), t.getPrice(), t.getSale_price(), t.getCertificate(), t.getCreated_at(), t.getUpdated_at(), t.getStatus(), t.getInstructor()));
                }
                //progressBar.setVisibility(View.INVISIBLE);
                //reloadCourses(CourseNew);
                Intent intent = new Intent(MainActivity.this, CoursesActivity.class);
                intent.putExtra("CourseNew", CourseNew);
                MainActivity.this.startActivity(intent);
            }

            @Override
            public void onFailure(Call<List<TopCourse>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                //progressBar.setVisibility(View.INVISIBLE);
            }
        });
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchStringInputField.getWindowToken(), 0);
    }

    private void resetFilter() {
        categorySpinner.setSelection(0, true);
        priceSpinner.setSelection(0, true);
        difficultyLevelSpinner.setSelection(0, true);
        languageSpinner.setSelection(0, true);
        ratingSpinner.setSelection(0, true);
    }

    public void searchCourse() {
        searchString = searchStringInputField.getText();
        getCourseBySearchString(searchString);
    }

    public void showSearchBox(View view) {
        // Showing the search input field
        searchStringInputField.setVisibility(View.VISIBLE);
        searchStringInputField.setFocusableInTouchMode(true);
        searchStringInputField.requestFocus();
        // Hiding the application logo
        applicationLogo.setVisibility(View.GONE);
        //Hiding the search button
        showSearchBoxButton.setVisibility(View.GONE);
        //Show hide search box button
        hideSearchBoxButton.setVisibility(View.VISIBLE);
        // Showing keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchStringInputField, InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideSearchBox(View view) {
        searchStringInputField.setVisibility(View.GONE);
        applicationLogo.setVisibility(View.VISIBLE);
        showSearchBoxButton.setVisibility(View.VISIBLE);
        hideSearchBoxButton.setVisibility(View.GONE);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchStringInputField.getWindowToken(), 0);
    }

    private void getCourseBySearchString(Editable searchString) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        Api api = retrofit.create(Api.class);
        Call<List<CourseSchema>> call = api.getCoursesBySearchString(searchString);
        call.enqueue(new Callback<List<CourseSchema>>() {
            @Override
            public void onResponse(Call<List<CourseSchema>> call, Response<List<CourseSchema>> response) {
                mCourses = new ArrayList<>();
                List<CourseSchema> courseSchemas = response.body();
                for (CourseSchema m : courseSchemas) {
                    mCourses.add(new Course(m.getId(), m.getTitle(), m.getThumbnail(), m.getPrice(), m.getInstructorName(), m.getRating(), m.getNumberOfRatings(), m.getTotalEnrollment(), m.getShareableLink(), m.getCourseOverviewProvider(), m.getCourseOverviewUrl()));
                }

                Intent intent = new Intent(getApplicationContext(), CoursesActivity.class);
                intent.putExtra("Course", mCourses);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }

            @Override
            public void onFailure(Call<List<CourseSchema>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
            }
        });

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchStringInputField.getWindowToken(), 0);
    }

    public FloatingActionButton getFloatingActionButton() {
        return filterButton;
    }

    public BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()) {

                        case R.id.navigation_course:
                            filterButton.show();
                            selectedFragment = new CourseFragment();
                            break;
                        case R.id.navigation_my_course:
                            filterButton.hide();
                            selectedFragment = new MyCourseFragment();
                            break;
                        case R.id.navigation_wishlist:
                            filterButton.hide();
                            selectedFragment = new WishlistFragment();
                            break;
                        case R.id.navigation_account:
                            filterButton.hide();
                            selectedFragment = new AccountFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.homePageFrameLayout, selectedFragment).commit();
                    return true;
                }
            };

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(Helpers.SHARED_PREF, 0);
        String token = sharedPreferences.getString("userToken", null);
        if (token != null) {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }
}