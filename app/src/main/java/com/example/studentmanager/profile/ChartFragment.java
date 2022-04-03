package com.example.studentmanager.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.studentmanager.R;
import com.example.studentmanager.profile.views.ChartView;

public class ChartFragment extends Fragment {

    private int passed;
    private int failed;
    private int ungraded;

    public ChartFragment() {
        // Required empty public constructor
    }

    public static ChartFragment newInstance() {
        return new ChartFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passed = getArguments().getInt("passed", 0);
        failed = getArguments().getInt("failed", 0);
        ungraded = getArguments().getInt("ungraded", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FrameLayout chartHolder = view.findViewById(R.id.chart_holder);

        ChartView chartView = new ChartView(getContext(), passed, failed, ungraded);

        chartHolder.addView(chartView);
    }
}