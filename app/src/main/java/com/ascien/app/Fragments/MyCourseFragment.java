package com.ascien.app.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ascien.app.Adapters.MyCourseAdapter;
import com.ascien.app.Models.MyCourse;
import com.ascien.app.R;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;

import java.util.ArrayList;

public class MyCourseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "MyCourseFragment";
    GridView myCoursesGridLayout;
    private ProgressBar progressBar;
    RelativeLayout myCourseView;
    RelativeLayout signInPlaceholder;
    RelativeLayout mEmptyContentArea;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.my_course_fragment, container, false);
        init(view);
        initSwipeRefreshLayout(view);
        initProgressBar(view);
        initArrayData();
        return view;
    }

    private void initSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.googleAccentColor1,
                R.color.googleAccentColor2,
                R.color.googleAccentColor3,
                R.color.googleAccentColor4);
    }

    private void init(View view) {
        myCoursesGridLayout = view.findViewById(R.id.myCoursesGridLayout);
        myCourseView = view.findViewById(R.id.myCourseView);
        mEmptyContentArea = view.findViewById(R.id.emptyContentArea);
    }

    private void initArrayData() {
        final ArrayList<MyCourse> mMyCourse = new ArrayList<>();
        mMyCourse.add(new MyCourse(15, "Криптовалюты для начинающих", "https://s0.rbk.ru/v6_top_pics/media/img/8/98/756595258828988.png", "150", "1.56", (float) 1.5, 7, 7, 6, 7, 8, "asdas", "youtube", "https://www.youtube.com/watch?v=_KyAA425fxY"));
        mMyCourse.add(new MyCourse(1, "SQL for Beginners", "data:image/jpeg;base64,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", "150", "1.56", (float) 1.5, 7, 7, 8, 7, 8, "asdas", "youtube", "https://www.youtube.com/watch?v=_KyAA425fxY"));
        initMyCourseGridView(mMyCourse);
    }

    private void initProgressBar(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        Sprite circularLoading = new Circle();
        progressBar.setIndeterminateDrawable(circularLoading);
    }

    private void initMyCourseGridView(ArrayList<MyCourse> myCourses) {
        MyCourseAdapter adapter = new MyCourseAdapter(getActivity(), myCourses);
        myCoursesGridLayout.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
