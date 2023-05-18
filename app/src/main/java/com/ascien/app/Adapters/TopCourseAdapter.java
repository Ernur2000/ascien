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
import com.ascien.app.Activities.CourseDetailsActivity;
import com.ascien.app.R;

import java.util.ArrayList;

public class TopCourseAdapter extends RecyclerView.Adapter<TopCourseAdapter.ViewHolder> {

    //vars
    private Context mContext;
    private ArrayList<TopCourse> mTopCourses;

    public TopCourseAdapter(Context context, ArrayList<TopCourse> topCourses) {
        mContext = context;
        mTopCourses = topCourses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_course_cell, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TopCourse currentTopCourse = mTopCourses.get(holder.getAdapterPosition());
                switchToCourseDetailsView(currentTopCourse);
            }
        });
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final TopCourse currentTopCourse = mTopCourses.get(position);
        Glide.with(mContext)
                .asBitmap()
                .load(currentTopCourse.getImage())
                .into(holder.image);

        holder.name.setText(currentTopCourse.getTitle());

        if(currentTopCourse.getPrice()!=null){
            holder.coursePrice.setText(currentTopCourse.getPrice()+" т");
        }else{
            holder.coursePrice.setText("Бесплатно");
        }
        holder.instructor.setText(currentTopCourse.getInstructor().getName());
    }

    @Override
    public int getItemCount() {
        return mTopCourses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name,instructor;
        TextView coursePrice;
        RatingBar topCourseRating;
        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_view);
            name = itemView.findViewById(R.id.name);
            instructor = itemView.findViewById(R.id.instructor);
            coursePrice = itemView.findViewById(R.id.topCoursePrice);
            topCourseRating = itemView.findViewById(R.id.topCourseRating);
        }
    }

    private void switchToCourseDetailsView(TopCourse currentTopCourse) {
        Intent intent = new Intent(mContext, CourseDetailsActivity.class);
        intent.putExtra("TopCourse",currentTopCourse);
        mContext.startActivity(intent);
    }
}
