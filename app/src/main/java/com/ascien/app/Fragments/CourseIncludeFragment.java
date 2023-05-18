package com.ascien.app.Fragments;


import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ascien.app.Models.TopCourse;
import com.ascien.app.R;

public class CourseIncludeFragment extends Fragment {
    private TopCourse mtopCourse;
    private TextView textView;

    public CourseIncludeFragment(TopCourse topCourse) {
        mtopCourse = topCourse;
    }

    private void init(View view) {
        textView = view.findViewById(R.id.description_tv);
        textView.setText(Html.fromHtml(mtopCourse.getDescription()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_course_include, container, false);
        init(view);
        return view;
    }

}
