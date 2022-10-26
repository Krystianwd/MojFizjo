package com.example.mojfizjo.Fragment.PlansFragment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mojfizjo.Adapters.BrowsePlanExercisesRecyclerViewAdapter;
import com.example.mojfizjo.Fragment.CategoryViewFragment;
import com.example.mojfizjo.Fragment.ExercisesFragment;
import com.example.mojfizjo.MainActivity;
import com.example.mojfizjo.Models.ExerciseModel;
import com.example.mojfizjo.Models.PlanModel;
import com.example.mojfizjo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddNewPlanFragment extends Fragment {
    Boolean receivedIsAddingToPlan =false;
    ArrayList<ExerciseModel> exerciseModels = new ArrayList<ExerciseModel>();

    public AddNewPlanFragment(){
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View main_view = inflater.inflate(R.layout.fragment_add_new_plan, container, false);
        Button submitPlanButton = main_view.findViewById(R.id.submitPlan);
        Button addNewExercise = main_view.findViewById(R.id.addExerciseToPlan);
        RecyclerView recyclerView = main_view.findViewById(R.id.addNewPlanRecyclerView);
        BrowsePlanExercisesRecyclerViewAdapter adapter = new BrowsePlanExercisesRecyclerViewAdapter(main_view.getContext(), exerciseModels, requireActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(main_view.getContext()));
        try {
            receivedIsAddingToPlan = getArguments().getBoolean("isAddingToPlan");}
        catch (Exception e){
            Log.d(TAG, "onCreateView: "+e);
        }
        if (receivedIsAddingToPlan){
            exerciseModels.add((ExerciseModel) getArguments().getSerializable("exercise"));

            receivedIsAddingToPlan = false;
        }

        Log.d(ContentValues.TAG, "onClick: Passed boolean" +receivedIsAddingToPlan);

        addNewExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //stworzenie instancji fragmentu widoku kategorii
                Fragment fragment = new ExercisesFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isAddingToPlan", true);
                fragment.setArguments(bundle);
                //przelaczenie widoku na ten fragment
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.frame_layout, fragment).addToBackStack("addExercise");
                fragmentTransaction.commit();
            }
        });
        submitPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0;i<exerciseModels.size();i++){
                    Log.d(TAG, "onClick: "+exerciseModels.get(i).getExerciseName());
                }
                EditText planName = (EditText) main_view.findViewById(R.id.planNameToAdd);
                String  planNameString = planName.getText().toString();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> plan = new HashMap<>();
                plan.put("planName",planNameString);
                plan.put("exercises",exerciseModels);
                db.collection("plans")
                        .add(plan)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: Nie dodano nowy plan");
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(requireActivity(),"Dodano plan o nazwie "+planNameString, Toast.LENGTH_SHORT).show();
                                exerciseModels.clear();
                                adapter.notifyDataSetChanged();
                            }
                        });

            }
        });
        return main_view;
    }
}

