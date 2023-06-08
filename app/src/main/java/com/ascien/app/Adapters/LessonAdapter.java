package com.ascien.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ascien.app.Activities.FullScreenLessonPlayerActivity;
import com.ascien.app.JSONSchemas.LessonSchema;
import com.ascien.app.Models.TopCourse;
import com.ascien.app.R;
import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.ViewHolder> {
    private TopCourse mCourse;
    private static final String TAG = "LessonAdapter";
    private OnLessonClickListener mOnLessonClickListener;
    boolean isFirstLesson;
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mLessonTItle;
        private TextView mLessonDuration;
        private TextView mLessonSerialNumber;
        private CheckBox mLessonCompletionCheckbox;
        private OnLessonClickListener onLessonClickListener;

        public ViewHolder(@NonNull View itemView, final OnLessonClickListener onLessonClickListener) {
            super(itemView);
            this.mLessonTItle = itemView.findViewById(R.id.lessonTitle);
            this.mLessonDuration = itemView.findViewById(R.id.lessonDuration);
            this.mLessonSerialNumber = itemView.findViewById(R.id.serialNumber);
            this.onLessonClickListener = onLessonClickListener;
            this.mLessonCompletionCheckbox = itemView.findViewById(R.id.lessonCompletionCheckbox);
            Log.d("Coldplay", "ViewHolder Method");
        }
    }

    interface
    OnLessonClickListener {
        void onLessonClick(LessonSchema eachLesson, ViewHolder viewHolder);
        void onLessonCompletionListener(LessonSchema eachLesson, boolean lessonCompletionStatus);
    }

    private List<LessonSchema> mLesson;
    private Context mContext;
    private int lessonCounter = 0;
    public LessonAdapter(Context context, List<LessonSchema> lesson, OnLessonClickListener onLessonClickListener, boolean isFirst, int lessonFrom) {
        this.mLesson = lesson;
        this.mContext = context;
        this.mOnLessonClickListener = onLessonClickListener;
        this.isFirstLesson = isFirst;
        this.lessonCounter = lessonFrom;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_cell, parent, false);
        final LessonAdapter.ViewHolder holder = new LessonAdapter.ViewHolder(view, mOnLessonClickListener);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnLessonClickListener.onLessonClick(mLesson.get(holder.getAdapterPosition()), holder);
                gotoVideo();
            }
        });
        return holder;
    }
    private void gotoVideo() {
        TopCourse.Instructor instructor = new TopCourse.Instructor();
        mCourse = new TopCourse(1,1,"asdasd",2,"asdasd","asdas","asdas","Aasas","asdasd","https://media.istockphoto.com/id/1328175450/video/investor-checking-cryptocurrency-price-index-on-mobile-phone-screen-cryptocurrency-future.jpg?s=640x640&k=20&c=JE42FQ89Xrtx25T1NkRjRv5_dTNOTYABKiTO6RgqHlE=","asdas","Aasas",1,"asda","asd","ASdas","sdfsd","sdfsd0","sdfsdf",instructor);
        Intent intent = new Intent(mContext, FullScreenLessonPlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("videoURl", "https://media.istockphoto.com/id/1328175450/video/investor-checking-cryptocurrency-price-index-on-mobile-phone-screen-cryptocurrency-future.jpg?s=640x640&k=20&c=JE42FQ89Xrtx25T1NkRjRv5_dTNOTYABKiTO6RgqHlE=");
        mContext.startActivity(intent);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mLessonSerialNumber.setText(Integer.toString(lessonCounter));
        lessonCounter++;
        if (isFirstLesson){
            mOnLessonClickListener.onLessonClick(mLesson.get(holder.getAdapterPosition()), holder);
            isFirstLesson = false;
        }

        final LessonSchema currentLesson = mLesson.get(position);

        holder.mLessonTItle.setText(currentLesson.getTitle());
        if (currentLesson.getLessonType().equals("other")){
            holder.mLessonDuration.setText("📎 Attachment");
        }else if(currentLesson.getLessonType().equals("quiz")){
            holder.mLessonDuration.setText("📝 Quiz");
        }else{
            holder.mLessonDuration.setText(currentLesson.getDuration());
        }
        if (currentLesson.getIsCompleted() == 1){
            holder.mLessonCompletionCheckbox.setChecked(true);
        }else{
            holder.mLessonCompletionCheckbox.setChecked(false);
        }

        holder.mLessonCompletionCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnLessonClickListener.onLessonCompletionListener(currentLesson, holder.mLessonCompletionCheckbox.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLesson.size();
    }
}
