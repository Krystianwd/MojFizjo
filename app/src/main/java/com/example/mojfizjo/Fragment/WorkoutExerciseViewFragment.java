package com.example.mojfizjo.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mojfizjo.R;


public class WorkoutExerciseViewFragment extends Fragment {

    String exerciseName;
    String time;
    int sets;

    TextView exerciseNameText;

    public WorkoutExerciseViewFragment() {
        // Required empty public constructor
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_workout_exercise_view, container, false);

        assert getArguments() != null;

        exerciseName = getArguments().getString("exerciseName");
        time = getArguments().getString("time");
        sets = getArguments().getInt("sets");

        exerciseNameText = view.findViewById(R.id.exerciseNameText);
        exerciseNameText.setText(exerciseName);

        return view;
    }
}