package com.ascien.app.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ascien.app.Adapters.CourseRequirementsAdapter;
import com.ascien.app.Models.CourseDetails;
import com.ascien.app.Models.TopCourse;
import com.ascien.app.R;
public class CourseRequirementFragment extends Fragment {
    private TopCourse topCourse;
    private TextView textView;
    public CourseRequirementFragment(TopCourse topCourse) {
        this.topCourse = topCourse;
    }

    private void init(View view) {
        textView = view.findViewById(R.id.requirements_tv);
        textView.setText(topCourse.getRequirements());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_course_requirement, container, false);
        init(view);
        return view;
    }
}
