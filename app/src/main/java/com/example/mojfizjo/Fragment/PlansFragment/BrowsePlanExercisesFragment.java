package com.example.mojfizjo.Fragment.PlansFragment;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mojfizjo.Adapters.BrowsePlanExercisesRecyclerViewAdapter;
import com.example.mojfizjo.MainActivity;
import com.example.mojfizjo.Models.ExerciseModel;
import com.example.mojfizjo.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BrowsePlanExercisesFragment extends Fragment implements EditExerciseDialog.DialogListener {
    ArrayList<ExerciseModel> exerciseModels;

    public BrowsePlanExercisesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.browse_plans_exercises, container, false);
        exerciseModels = (ArrayList<ExerciseModel>) getArguments().getSerializable("exerlist");
        String planId = getArguments().getString("planId");
        RecyclerView recyclerView = view.findViewById(R.id.browse_plans_exercises_recycler_view);
        BrowsePlanExercisesRecyclerViewAdapter adapter = new BrowsePlanExercisesRecyclerViewAdapter(view.getContext(), planId, exerciseModels, requireActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }

    @Override
    public void editExercise(String name, String sets, String time, int position, String planId) {
        String exerciseName = exerciseModels.get(position).getExerciseName();
        ArrayList<ExerciseModel> newExerciseModels = exerciseModels;
        newExerciseModels.get(position).setExerciseName(name);
        newExerciseModels.get(position).setSets(Integer.parseInt(sets));
        newExerciseModels.get(position).setTime(time);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("plans").document(planId).update("exercises", newExerciseModels).addOnSuccessListener(task -> {
            Toast.makeText(requireActivity(), "Zaktualizowano ćwiczenie o nazwie: " + exerciseName, Toast.LENGTH_SHORT).show();
            ((MainActivity) requireActivity()).setUpPLanModels();
            newExerciseModels.clear();
        });
    }
}
