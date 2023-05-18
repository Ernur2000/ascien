package com.ascien.app.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ascien.app.Activities.CourseDetailsActivity;
import com.ascien.app.Models.TopCourse;
import com.ascien.app.Models.WishListCourse;
import com.ascien.app.Models.WishListResponse;
import com.ascien.app.Network.ApiClient;
import com.ascien.app.R;
import com.ascien.app.Utils.Helpers;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishlistAdapter extends BaseAdapter {
    //vars

    private Context mContext;
    private ArrayList<WishListCourse> mWishLists;
    private WishListCourse mwishListCourse;
    RemoveItemFromWishList mRemoveItemFromWishList = null;

    public WishlistAdapter(Context context, ArrayList<WishListCourse> wishList, RemoveItemFromWishList removeItemFromWishList, WishListCourse wishListCourse) {
        mContext = context;
        mWishLists = wishList;
        mRemoveItemFromWishList = removeItemFromWishList;
        mwishListCourse = wishListCourse;
    }

    @Override
    public int getCount() {
        return mWishLists.size();
    }

    @Override
    public Object getItem(int i) {
        return mWishLists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View gridView = view;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.wishlist_cell, null);

        }

        ImageView imageView = gridView.findViewById(R.id.image_view);
        TextView name = gridView.findViewById(R.id.courseTitle);
        //ImageButton toggleWishListButton = gridView.findViewById(R.id.toggleWishList);
        Spinner moreButton = gridView.findViewById(R.id.moreButton);

        final WishListCourse wishList = mWishLists.get(i);
        final TopCourse topCourse = wishList.getTopCourse();
        Glide.with(mContext)
                .asBitmap()
                .load(wishList.getTopCourse().getImage())
                .into(imageView);
        Log.d("Image", wishList.getId() + "");
        Log.d("Image", wishList.getId() + "");
        name.setText(wishList.getTopCourse().getTitle());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CourseDetailsActivity.class);
                intent.putExtra("TopCourse", topCourse);
                mContext.startActivity(intent);
            }
        });

        // init Spinner
        initMoreOptionSpinner(moreButton, wishList);

        return gridView;
    }

    private void initMoreOptionSpinner(Spinner moreButton, final WishListCourse wishList) {
        // initiating the options for option menu
        List<String> moreOptions = new ArrayList<>();
        moreOptions.add(0, "Choose Option");
        moreOptions.add(1, "Remove From Wishlist");

        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, moreOptions);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moreButton.setAdapter(dataAdapter);
        moreButton.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemIdAtPosition(i) == 1) {
                    showConfirmationAlert(wishList.getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showConfirmationAlert(final int courseId) {
        new AlertDialog.Builder(mContext)
                .setTitle("Confirmation")
                .setMessage("Do you really want to remove this course from your wishlist?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        SharedPreferences preferences = mContext.getSharedPreferences(Helpers.SHARED_PREF, 0);
                        String authToken = preferences.getString("userToken", null);
                        Call<List<WishListCourse>> wishListCourseCall = ApiClient.getApi().getMyWishList("Bearer " + authToken);
                        wishListCourseCall.enqueue(new Callback<List<WishListCourse>>() {
                            @Override
                            public void onResponse(Call<List<WishListCourse>> call, Response<List<WishListCourse>> response) {
                                List<WishListCourse> wishListCourse = response.body();
                                SharedPreferences preferences = mContext.getSharedPreferences(Helpers.SHARED_PREF, 0);
                                SharedPreferences.Editor editor = preferences.edit();
                                for (WishListCourse w : wishListCourse) {
                                    int Id = w.getId();
                                    editor.putInt("id", Id);
                                }
                                int id = preferences.getInt("id", 0);
                                Log.d("Tag", id + "");
                                unLikedWishListItem(id);

                            }

                            @Override
                            public void onFailure(Call<List<WishListCourse>> call, Throwable t) {

                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void unLikedWishListItem(int id) {
        SharedPreferences preferences = this.mContext.getSharedPreferences(Helpers.SHARED_PREF, 0);
        String authToken = preferences.getString("userToken", null);
        Call<WishListResponse> wishListResponseCall = ApiClient.getApi().unLikeCourse("Bearer " + authToken, id);

        wishListResponseCall.enqueue(new Callback<WishListResponse>() {
            @Override
            public void onResponse(Call<WishListResponse> call, Response<WishListResponse> response) {
                WishListResponse wishListResponse = response.body();
                if (wishListResponse.isSuccess()) {
                    Log.d("WishListResponse", wishListResponse.getMessage());
                } else {
                }
            }

            @Override
            public void onFailure(Call<WishListResponse> call, Throwable t) {
            }
        });
    }

    public interface RemoveItemFromWishList {
        void removeFromWishList(int courseId);
    }
}
