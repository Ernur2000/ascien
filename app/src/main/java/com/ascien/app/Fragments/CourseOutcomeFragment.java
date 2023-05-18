package com.ascien.app.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ascien.app.Models.TopCourse;
import com.ascien.app.R;

public class CourseOutcomeFragment extends Fragment {

    private TextView textView;
    private TopCourse topCourse;

    public CourseOutcomeFragment(TopCourse topCourse) {
        this.topCourse = topCourse;
    }

    private void init(View view) {
        textView = view.findViewById(R.id.what_will_learn_tv);
        textView.setText(topCourse.getWhat_will_learn());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_course_outcome, container, false);
        init(view);
        return view;
    }
}
