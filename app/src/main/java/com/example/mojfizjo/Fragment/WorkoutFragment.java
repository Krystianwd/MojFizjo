package com.example.mojfizjo.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mojfizjo.Adapters.WorkoutFragmentPlanRecyclerViewAdapter;
import com.example.mojfizjo.Models.PlanModel;
import com.example.mojfizjo.R;

import java.util.ArrayList;


public class WorkoutFragment extends Fragment {
    ArrayList<PlanModel> planModels = new ArrayList<>();
    public WorkoutFragment() {
        // Required empty public constructor
    }
    public WorkoutFragment(ArrayList<PlanModel> planModels) {
        this.planModels = planModels;
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workout, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.main_plan_recycler_view);
        WorkoutFragmentPlanRecyclerViewAdapter adapter = new WorkoutFragmentPlanRecyclerViewAdapter(view.getContext(), planModels, requireActivity());
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }

}