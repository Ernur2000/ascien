package com.ascien.app.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ascien.app.Adapters.WishlistAdapter;
import com.ascien.app.JSONSchemas.StatusSchema;
import com.ascien.app.Models.WishListCourse;
import com.ascien.app.Network.Api;
import com.ascien.app.Network.ApiClient;
import com.ascien.app.R;
import com.ascien.app.Utils.Helpers;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WishlistFragment extends Fragment implements WishlistAdapter.RemoveItemFromWishList, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "WishlistFragment";
    GridView myWishlistGridLayout;
    private ProgressBar progressBar;
    TextView myCoursesLabel;
    RelativeLayout myWishlistView, signInPlaceholder, mEmptyContentArea;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private WishListCourse wishListCourse;

    WishlistAdapter.RemoveItemFromWishList mRemoveFromWishlist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.wishlist_fragment, container, false);
        init(view);
        initSwipeRefreshLayout(view);
        initProgressBar(view);
        getMyWishList();
        return view;
    }

    private void init(View view) {
        myWishlistGridLayout = view.findViewById(R.id.myCoursesGridLayout);
        myCoursesLabel = view.findViewById(R.id.myCoursesLabel);
        myWishlistView = view.findViewById(R.id.myWishlistView);
        mEmptyContentArea = view.findViewById(R.id.emptyContentArea);
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

    private void initProgressBar(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        Sprite circularLoading = new Circle();
        progressBar.setIndeterminateDrawable(circularLoading);
    }

    private void initMyWishlistGridView(ArrayList<WishListCourse> mWishList) {
        WishlistAdapter adapter = new WishlistAdapter(getActivity(), mWishList, this, wishListCourse);
        myWishlistGridLayout.invalidateViews();
        myWishlistGridLayout.setAdapter(adapter);
    }

    public void getMyWishList() {
        progressBar.setVisibility(View.VISIBLE);
        ArrayList<WishListCourse> mWishList = new ArrayList<>();
        SharedPreferences preferences = getContext().getSharedPreferences(Helpers.SHARED_PREF, 0);
        String authToken = preferences.getString("userToken", null);
//        Call<List<WishListCourse>> call = ApiClient.getApi().getMyWishList("Bearer " + authToken);
//        call.enqueue(new Callback<List<WishListCourse>>() {
//            @Override
//            public void onResponse(Call<List<WishListCourse>> call, Response<List<WishListCourse>> response) {
//                List<WishListCourse> myCourseListSchema = response.body();
//
//
//                for (WishListCourse t : myCourseListSchema) {
//                    Log.d("DEBUG_WISHLIST", "onResponse: ");
////                     Log.d("DEBUG_WISHLIST", "onResponse: " + myCourseListSchema.get(0) );
//
//                    mWishList.add(new WishListCourse(t.getId(), t.getUser_id(), t.getCourse_id(), t.getCreated_at(), t.getUpdated_at(), t.getTopCourse()));
//                }
//                if (mWishList.size() > 0) {
//                    initMyWishlistGridView(mWishList);
//                    myWishlistGridLayout.setVisibility(View.VISIBLE);
//                    mEmptyContentArea.setVisibility(View.GONE);
//                } else {
//                    myWishlistGridLayout.setVisibility(View.GONE);
//                    mEmptyContentArea.setVisibility(View.VISIBLE);
//                }
//
//                progressBar.setVisibility(View.INVISIBLE);
//                mSwipeRefreshLayout.setRefreshing(false);
//
//
//            }
//
//            @Override
//            public void onFailure(Call<List<WishListCourse>> call, Throwable t) {
//                mSwipeRefreshLayout.setRefreshing(false);
//                progressBar.setVisibility(View.INVISIBLE);
//            }
//        });
    }

    @Override
    public void removeFromWishList(int courseId) {
        // Auth Token
        SharedPreferences preferences = getContext().getSharedPreferences(Helpers.SHARED_PREF, 0);
        String authToken = preferences.getString("userToken", "");
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        Api api = retrofit.create(Api.class);
        Call<StatusSchema> call = api.toggleWishListItems(authToken, courseId);
        call.enqueue(new Callback<StatusSchema>() {
            @Override
            public void onResponse(Call<StatusSchema> call, Response<StatusSchema> response) {
                //getMyWishlist();
            }

            @Override
            public void onFailure(Call<StatusSchema> call, Throwable t) {
                Log.d(TAG, "Wishlist removed Failed");
            }
        });
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
