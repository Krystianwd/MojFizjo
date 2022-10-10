package com.example.mojfizjo.Fragment.PlansFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mojfizjo.Adapters.BrowsePlanExercisesRecyclerViewAdapter;
import com.example.mojfizjo.Models.ExerciseModel;
import com.example.mojfizjo.R;

import java.util.ArrayList;

public class BrowsePlanExercisesFragment extends Fragment {
    public BrowsePlanExercisesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.browse_plans_exercises, container, false);
        ArrayList<ExerciseModel> exerciseModels = (ArrayList<ExerciseModel>) getArguments().getSerializable("exerlist");
        RecyclerView recyclerView = view.findViewById(R.id.browse_plans_exercises_recycler_view);
        BrowsePlanExercisesRecyclerViewAdapter adapter = new BrowsePlanExercisesRecyclerViewAdapter(view.getContext(), exerciseModels, requireActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }
}
