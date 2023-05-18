package com.ascien.app.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ascien.app.Models.Lessons;
import com.ascien.app.R;

import java.util.List;

public class CourseCurriculumLessonAdapter extends RecyclerView.Adapter<CourseCurriculumLessonAdapter.ViewHolder> {
    private static final String TAG = "CourseCurriculumLessonAdapter";

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mLessonTItle, lessonDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mLessonTItle = itemView.findViewById(R.id.courseCurriculumLessonCellTitlte);
            this.lessonDuration = itemView.findViewById(R.id.lessonDuration);
        }
    }

    private List<Lessons> mLesson;
    private Context mContext;

    public CourseCurriculumLessonAdapter(Context context, List<Lessons> lesson) {
        this.mLesson = lesson;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_curriculum_lesson_cell, parent, false);
        final CourseCurriculumLessonAdapter.ViewHolder holder = new CourseCurriculumLessonAdapter.ViewHolder(view);
        return holder;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Lessons currentLesson = mLesson.get(position);
        holder.mLessonTItle.setText(currentLesson.getTitle());
        holder.lessonDuration.setText(currentLesson.getDuration());
    }

    @Override
    public int getItemCount() {
        return mLesson.size();
    }
}
