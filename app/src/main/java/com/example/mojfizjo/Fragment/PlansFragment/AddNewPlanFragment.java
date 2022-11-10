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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mojfizjo.Adapters.BrowsePlanExercisesRecyclerViewAdapter;
import com.example.mojfizjo.Fragment.ExercisesFragment;
import com.example.mojfizjo.Models.ExerciseModel;
import com.example.mojfizjo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddNewPlanFragment extends Fragment implements SetDateToRemindDialog.DialogListener{
    Boolean receivedIsAddingToPlan =false;
    ArrayList<ExerciseModel> exerciseModels = new ArrayList<>();
    View main_view;
    BrowsePlanExercisesRecyclerViewAdapter adapter;
    //autentykacja
    FirebaseAuth mAuth;

    public AddNewPlanFragment(){
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.fragment_add_new_plan, container, false);
        Button submitPlanButton = main_view.findViewById(R.id.submitPlan);
        Button addNewExercise = main_view.findViewById(R.id.addExerciseToPlan);
        RecyclerView recyclerView = main_view.findViewById(R.id.addNewPlanRecyclerView);
        adapter = new BrowsePlanExercisesRecyclerViewAdapter(main_view.getContext(), exerciseModels, requireActivity());
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
                SetDateToRemindDialog setDateToRemindDialog = new SetDateToRemindDialog();
                setDateToRemindDialog.show(getParentFragmentManager(),"setDateToRemindDialog");
            }
        });
        return main_view;
    }

    @Override
    public void applyText(ArrayList<String> selectedDays, String selectedHour) {
                EditText planName = main_view.findViewById(R.id.planNameToAdd);
                String  planNameString = planName.getText().toString();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> plan = new HashMap<>();
                plan.put("planName",planNameString);
                plan.put("exercises",exerciseModels);
                plan.put("remindDay",selectedDays);
                plan.put("remindHour",selectedHour);
                //ustawienie nazwy uzytkownika
                String uid = "";
                mAuth = FirebaseAuth.getInstance();
                mAuth.setLanguageCode(getResources().getString(R.string.jezyk));
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) uid = currentUser.getUid();
                plan.put("userID", uid);

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

    @Override
    public void applyTextSkip() {

    }
}

