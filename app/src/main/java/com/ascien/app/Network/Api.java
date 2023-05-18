package com.ascien.app.Network;

import android.text.Editable;

import com.ascien.app.JSONSchemas.CourseSchema;
import com.ascien.app.JSONSchemas.LessonCompletionSchema;
import com.ascien.app.JSONSchemas.StatusSchema;
import com.ascien.app.Models.CategoryCourse;
import com.ascien.app.Models.DetailPublicCourse;
import com.ascien.app.Models.LoginRequest;
import com.ascien.app.Models.LoginResponse;
import com.ascien.app.Models.Purchase;
import com.ascien.app.Models.RegisterRequest;
import com.ascien.app.Models.RegisterResponse;
import com.ascien.app.Models.TopCourse;
import com.ascien.app.Models.UpdateProfileRequest;
import com.ascien.app.Models.WishListCourse;
import com.ascien.app.Models.WishListResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    // API PREFIX FOR OFFLINE
    String BASE_URL_PREFIX = "https://api.ascien.xyz/api/";

    // BASE URL
    String BASE_URL = BASE_URL_PREFIX + "/api/";

    // BASE URL FOR QUIZ
    String QUIZ_BASE_URL = BASE_URL_PREFIX + "/home/quiz_mobile_web_view/";

    // BASE URL FOR COURSE PURCHASE VIEW
    String COURSE_PURCHASE_URL = BASE_URL_PREFIX + "/home/course_purchase/";

    @POST("user/login")
    Call<LoginResponse> userLogin(@Body LoginRequest loginRequest);

    @POST("user/register")
    Call<RegisterResponse> userRegister(@Body RegisterRequest registerRequest);


    @POST("profile/student/update")
    Call<LoginResponse> profileUpdate(@Header("Authorization") String authHeader, @Body UpdateProfileRequest updateProfileRequest);

    @Multipart
    @POST("profile/student/update")
    Call<LoginResponse> avatarProfile(@Header("Authorization") String authHeader,
                                      @Part MultipartBody.Part userImage);

    @GET("courses/getOnlyCategories")
    Call<List<CategoryCourse>> getCategories_new();

    @GET("courses/getTopCourses")
    Call<List<TopCourse>> getTopCourse_new();

    @GET("courses/search")
    Call<List<TopCourse>> getCourseByCategory(@Query("category_id") int categoryId);

    @GET("courses/search")
    Call<List<TopCourse>> getFilteredCourse(
            @Query("q") String searchString,
            @Query("category_id") String category_id,
            @Query("price") String selectedPrice
    );

    @GET("courses/search")
    Call<List<TopCourse>> getAllCourses();

    @POST("student/wishlist/store/{course_id}")
    Call<WishListResponse> likeCourse(@Header("Authorization") String authHeader,
                                      @Path("course_id") int courseId);

    @POST("student/wishlist/delete/{wish_id}")
    Call<WishListResponse> unLikeCourse(@Header("Authorization") String authHeader,
                                        @Path("wish_id") int wish_id);

    @GET("student/wishlist/get")
    Call<List<WishListCourse>> getMyWishList(@Header("Authorization") String authHeader);


    @GET("courses/getDetailPublicCourse/{course_id}")
    Call<DetailPublicCourse> getDetailPublicCourse(@Path("course_id") int courseId);

    @POST("student/generate/paymentUrl/{course_id}")
    Call<Purchase> purchaseCourse(@Header("Authorization") String authHeader,
                                  @Path("course_id") int courseId);

    // Api call for fetching Course list from Search String
    @GET("courses_by_search_string")
    Call<List<CourseSchema>> getCoursesBySearchString(@Query("search_string") Editable searchString);

    // Api call for Fetching My Wishlist
    @GET("toggle_wishlist_items")
    Call<StatusSchema> toggleWishListItems(@Query("auth_token") String authToken, @Query("course_id") int courseId);

    // Api call for Saving course progress with lesson completion status
    @GET("save_course_progress")
    Call<LessonCompletionSchema> saveCourseProgress(@Query("auth_token") String authToken, @Query("lesson_id") int lessonId, @Query("progress") int progress);

}

