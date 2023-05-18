package com.ascien.app.Adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ascien.app.Fragments.CourseIncludeFragment;
import com.ascien.app.Fragments.CourseOutcomeFragment;
import com.ascien.app.Fragments.CourseRequirementFragment;
import com.ascien.app.Models.CourseDetails;
import com.ascien.app.Models.TopCourse;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private CourseDetails mCourseDetails;
    private TopCourse topCourse;
    public ViewPagerAdapter(FragmentManager fm, TopCourse topCourse) {
        super(fm);
        this.topCourse = topCourse;
    }
    private int mCurrentPosition;
    @Override
    public Fragment getItem(int position) {
        mCurrentPosition = position;
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new CourseIncludeFragment(topCourse);
                break;
            case 1:
                fragment = new CourseOutcomeFragment(topCourse);
                break;
            case 2:
                fragment = new CourseRequirementFragment(topCourse);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String tabTitle = null;
        switch (position){
            case 0:
                tabTitle = "Содержит";
                break;
            case 1:
                tabTitle = "Результаты";
                break;
            case 2:
                tabTitle = "Требования";
                break;
        }
        return tabTitle;
    }
}
