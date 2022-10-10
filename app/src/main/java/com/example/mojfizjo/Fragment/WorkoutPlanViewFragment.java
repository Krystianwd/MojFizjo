package com.example.mojfizjo.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mojfizjo.Adapters.WorkoutPlanViewFragmentRecyclerViewAdapter;
import com.example.mojfizjo.Models.ExerciseModel;
import com.example.mojfizjo.R;

import java.util.ArrayList;

public class WorkoutPlanViewFragment extends Fragment {
    public WorkoutPlanViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_plan_view, container, false);
        assert getArguments() != null;

        TextView planNameText =  view.findViewById(R.id.planNameText);
        String planName = getArguments().getString("planName");
        planNameText.setText(planName);

        ArrayList<ExerciseModel> exerciseModels = (ArrayList<ExerciseModel>) getArguments().getSerializable("exerlist");
        RecyclerView recyclerView = view.findViewById(R.id.workout_plan_view_recyclerview);
        WorkoutPlanViewFragmentRecyclerViewAdapter adapter = new WorkoutPlanViewFragmentRecyclerViewAdapter(view.getContext(), exerciseModels, requireActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }
}