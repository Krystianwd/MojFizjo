package com.example.mojfizjo.Fragment;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_workout_plan_view, container, false);
        assert getArguments() != null;

        TextView planNameText =  view.findViewById(R.id.planNameText);
        String planName = getArguments().getString("planName");
        planNameText.setText(planName);

        Button endSessionButton = view.findViewById(R.id.endSessionButton);
        endSessionButton.setOnClickListener(view -> {
            HomeFragment fragment = new HomeFragment();
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commit();
        });

        ArrayList<ExerciseModel> exerciseModels = (ArrayList<ExerciseModel>) getArguments().getSerializable("exerlist");
        Log.d(TAG, String.valueOf(exerciseModels.size()));
        ArrayList<String> exercisesFinished = getArguments().getStringArrayList("exercisesFinished");
        RecyclerView recyclerView = view.findViewById(R.id.workout_plan_view_recyclerview);
        WorkoutPlanViewFragmentRecyclerViewAdapter adapter = new WorkoutPlanViewFragmentRecyclerViewAdapter(view.getContext(), exerciseModels, exercisesFinished, planName, requireActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }
}