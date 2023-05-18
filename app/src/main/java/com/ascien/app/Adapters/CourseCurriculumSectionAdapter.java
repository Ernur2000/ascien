package com.ascien.app.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ascien.app.JSONSchemas.LessonSchema;
import com.ascien.app.Models.Lessons;
import com.ascien.app.Models.Section;
import com.ascien.app.Models.Sections;
import com.ascien.app.R;
import com.ascien.app.Utils.Helpers;

import java.util.ArrayList;
import java.util.List;

public class CourseCurriculumSectionAdapter extends RecyclerView.Adapter<CourseCurriculumSectionAdapter.ViewHolder>{
    private static final String TAG = "CourseCurriculumSectionAdapter";
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mSectionTItle;
        private RecyclerView mLessonRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mSectionTItle = itemView.findViewById(R.id.sectionTitle);
            this.mLessonRecyclerView = itemView.findViewById(R.id.sectionWiseLessonsRecyclerView);
        }
    }

    private ArrayList<Sections> mSection;
    private Context mContext;
    public CourseCurriculumSectionAdapter(Context context, ArrayList<Sections> section) {
        mContext = context;
        mSection = section;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_curriculum_section_cell, parent, false);
        final CourseCurriculumSectionAdapter.ViewHolder holder = new CourseCurriculumSectionAdapter.ViewHolder(view);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Sections currentSection = mSection.get(position);
        holder.mSectionTItle.setText("● "+currentSection.getTitle());
        List mLessons = currentSection.getLessons();
        CourseCurriculumLessonAdapter adapter = new CourseCurriculumLessonAdapter(mContext, mLessons);
        holder.mLessonRecyclerView.setAdapter(adapter);
        holder.mLessonRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        setHeight(mLessons.size(), holder.mLessonRecyclerView);
    }

    private void setHeight(int numberOfItems, RecyclerView mRecyclerView) {
        int pixels = Helpers.convertDpToPixel((numberOfItems) + 10); // numberOfItems is the number of categories and the 90 is each items height with the margin of top and bottom. Extra 10 dp for readability
        ViewGroup.LayoutParams params1 = mRecyclerView.getLayoutParams();
        mRecyclerView.setMinimumHeight(249);
        mRecyclerView.requestLayout();
    }

    @Override
    public int getItemCount() {
        return mSection.size();
    }
}
