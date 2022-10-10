package com.example.mojfizjo.Fragment.PlansFragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mojfizjo.Adapters.MainPlanRecyclerViewAdapter;
import com.example.mojfizjo.Models.PlanModel;
import com.example.mojfizjo.R;

import java.util.ArrayList;

public class PlansFragment extends Fragment {
    ArrayList<PlanModel> planModels = new ArrayList<>();
    public PlansFragment() {
        // Required empty public constructor
    }
    public PlansFragment(ArrayList<PlanModel> planModels) {
        this.planModels = planModels;
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_plans, container, false);
        Button addPlanButton = view.findViewById(R.id.addPlan);
        RecyclerView recyclerView = view.findViewById(R.id.main_plan_recycler_view);
        MainPlanRecyclerViewAdapter adapter = new MainPlanRecyclerViewAdapter(view.getContext(), planModels, requireActivity());
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        addPlanButton.setOnClickListener(view1 -> {
            Fragment fragment = new AddNewPlanFragment();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment, "AddPlanFragment").addToBackStack(null).commit();
        });
        return view;


    }

}
