package com.ascien.app.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ascien.app.Models.TopCourse;
import com.bumptech.glide.Glide;
import com.ascien.app.Models.Course;
import com.ascien.app.R;
import com.ascien.app.Activities.CourseDetailsActivity;

import java.util.ArrayList;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.ViewHolder> {
    private static final String TAG = "Courses List Adapter";
    private static final String TAG2 = "Checker";

    //vars
    private Context mContext;
    private ArrayList<TopCourse> mCourses = new ArrayList<>();

    public CoursesAdapter(Context context, ArrayList<TopCourse> courses) {
        mCourses = courses;
        mContext = context;

    }

    @NonNull
    @Override
    public CoursesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_cell, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TopCourse currentCourse = mCourses.get(holder.getAdapterPosition());
                switchToCourseDetailsActivity(currentCourse);
            }
        });
        return holder;
    }

    private void switchToCourseDetailsActivity(TopCourse currentCourse) {
        Intent intent = new Intent(mContext, CourseDetailsActivity  .class);
        intent.putExtra("TopCourse", currentCourse);
        mContext.startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CoursesAdapter.ViewHolder holder, final int position) {
        final TopCourse currentCourse = mCourses.get(position);
        Glide.with(mContext)
                .asBitmap()
                .load(currentCourse.getImage())
                .into(holder.image);

        holder.name.setText(currentCourse.getTitle());
        if (currentCourse.getPrice()!=null){
            holder.coursePrice.setText(currentCourse.getPrice()+" т");
        }
        else{
            holder.coursePrice.setText("Бесплатно");
        }
        holder.instructorName.setText(currentCourse.getInstructor().getName());

    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name;
        TextView coursePrice;
        TextView instructorName;
        TextView rating;
        TextView totalNumberOfRating;
        RatingBar starRating;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.courseThumbnail);
            name = itemView.findViewById(R.id.courseTitle);
            coursePrice = itemView.findViewById(R.id.coursePrice);
            instructorName = itemView.findViewById(R.id.instructorName);
            rating = itemView.findViewById(R.id.numericRating);
            totalNumberOfRating = itemView.findViewById(R.id.totalNumberOfRatingByUsers);
            starRating = itemView.findViewById(R.id.starRating);
        }
    }
}
