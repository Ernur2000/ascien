package com.ascien.app.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ascien.app.Adapters.CoursesAdapter;
import com.ascien.app.Models.Category;
import com.ascien.app.Models.CategoryCourse;
import com.ascien.app.Models.Course;
import com.ascien.app.Models.DifficultyLevel;
import com.ascien.app.Models.Language;
import com.ascien.app.Models.Price;
import com.ascien.app.Models.Rating;
import com.ascien.app.Models.TopCourse;
import com.ascien.app.R;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class CoursesActivity extends AppCompatActivity {
    //vars
    private static final String TAG = "Course-List";
    private RecyclerView recyclerViewForFilteredCourses = null;
    private ArrayList<Course> mCourses = new ArrayList<>();
    private ArrayList<Category> mCategory = new ArrayList<>();
    private ArrayList<CategoryCourse> CategoryNew = new ArrayList<>();
    private ArrayList<TopCourse> CourseNew = new ArrayList<>();
    private TextView numberOfFilteredCourses;
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
    private RecyclerView recyclerViewForCategories = null;

    Button showSearchBoxButton;
    Button hideSearchBoxButton;
    EditText searchStringInputField;
    Button backButton;
    //ImageView applicationLogo;
    FloatingActionButton filterButton;
    View bottomSheetView;
    ImageButton closeFilterViewButton;
    Button filterApplyButton;
    Button filterResetButton;
    ArrayAdapter categoryAdapter;
    ArrayAdapter<Price> priceAdapter;
    ArrayAdapter<DifficultyLevel> difficultyLevelArrayAdapter;
    ArrayAdapter<Language> languageArrayAdapter;
    ArrayAdapter<Rating> ratingArrayAdapter;
    // this is the search string
    Editable searchString;

    private BottomSheetBehavior bottomSheetBehavior;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
        init();
        initProgressBar();
        // The filtered course object is being passed from another activity or adapter
        getCourseObject();

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:

                    case BottomSheetBehavior.STATE_EXPANDED:
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
                //filterCourse();
                //filterCourseMethod();
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
        //applicationLogo = findViewById(R.id.applicationLogo);
        backButton = findViewById(R.id.backButton);
        backButton.setVisibility(View.VISIBLE);

        searchStringInputField = findViewById(R.id.searchStringInputField);
        filterButton = findViewById(R.id.floatingFilterButton);
        bottomSheetView = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
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
    }

    public void handleBackButton(View view) {
        CoursesActivity.super.onBackPressed();
    }

    // Initialize the progress bar
    private void initProgressBar() {
        progressBar = findViewById(R.id.progressBar);
        Sprite circularLoading = new Circle();
        progressBar.setIndeterminateDrawable(circularLoading);
    }

    @SuppressLint("SetTextI18n")
    private void getCourseObject() {
        // Initializing the fetched course object to mCourses
        recyclerViewForFilteredCourses = findViewById(R.id.recyclerViewForFilteredCourses);
        CourseNew = (ArrayList<TopCourse>) getIntent().getSerializableExtra("CourseNew");

        // Updating the course result text
        numberOfFilteredCourses = findViewById(R.id.filterResultTitle);
        numberOfFilteredCourses.setText("Показано " + CourseNew.size() + " курсов");

        // Initializing the recyclerview
        reloadCourses(CourseNew);
    }

    @SuppressLint("SetTextI18n")
    private void reloadCourses(ArrayList<TopCourse> mCourses) {
        // Updating the course result text
        numberOfFilteredCourses = findViewById(R.id.filterResultTitle);
        numberOfFilteredCourses.setText("Показано " + mCourses.size() + " курсов");

        // Initializing the recyclerview
        initFilteredCourseRecyclerView();
    }

    private void initFilteredCourseRecyclerView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        CoursesAdapter adapter = new CoursesAdapter(CoursesActivity.this, CourseNew);
        recyclerViewForFilteredCourses.setAdapter(adapter);
        recyclerViewForFilteredCourses.setLayoutManager(layoutManager);
    }


    public void searchCourse() {
        searchString = searchStringInputField.getText();
        //filterCourse();
        //filterCourseMethod();
    }

    public void showSearchBox(View view) {
        // Showing the search input field
        searchStringInputField.setVisibility(View.VISIBLE);
        searchStringInputField.setFocusableInTouchMode(true);
        searchStringInputField.requestFocus();
        // Hiding the application logo
        //applicationLogo.setVisibility(View.GONE);
        //Hiding the search button
        showSearchBoxButton.setVisibility(View.GONE);
        //Show hide search box button
        hideSearchBoxButton.setVisibility(View.VISIBLE);
        // Toggling keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchStringInputField, InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideSearchBox(View view) {
        // Hiding the search input field
        searchStringInputField.setVisibility(View.GONE);
        // Showing the application logo
        //applicationLogo.setVisibility(View.VISIBLE);
        //Showing the search button
        showSearchBoxButton.setVisibility(View.VISIBLE);
        //Hide hide search box button
        hideSearchBoxButton.setVisibility(View.GONE);
        // clearing the search box text
        searchStringInputField.getText().clear();
        // Toggling keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchStringInputField.getWindowToken(), 0);
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

    // Filter pages elements initializing
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


    private void resetFilter() {
        categorySpinner.setSelection(0, true);
        priceSpinner.setSelection(0, true);
        difficultyLevelSpinner.setSelection(0, true);
        //languageSpinner.setSelection(0, true);
        ratingSpinner.setSelection(0, true);
    }
}
