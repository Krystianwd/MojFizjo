package com.example.mojfizjo.Fragment.PlansFragment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mojfizjo.Adapters.BrowsePlanExercisesRecyclerViewAdapter;
import com.example.mojfizjo.Fragment.ExercisesFragment;
import com.example.mojfizjo.MainActivity;
import com.example.mojfizjo.Models.ExerciseModel;
import com.example.mojfizjo.Models.PlanModelDB;
import com.example.mojfizjo.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddNewPlanFragment extends Fragment implements SetDateToRemindDialog.DialogListener{
    Boolean receivedIsAddingToPlan = false;
    Boolean isEditingExistingPlan = false;
    String receivedPlanId ="";
    String receivedPlanName="";

    EditText planName;

    ArrayList<ExerciseModel> exerciseModels = new ArrayList<>();
    View main_view;
    BrowsePlanExercisesRecyclerViewAdapter adapter;
    //autentykacja
    FirebaseAuth mAuth;

    FirebaseFirestore db;

    public AddNewPlanFragment(){
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();

        main_view = inflater.inflate(R.layout.fragment_add_new_plan, container, false);
        Button submitPlanButton = main_view.findViewById(R.id.submitPlan);
        Button addNewExercise = main_view.findViewById(R.id.addExerciseToPlan);
        planName = main_view.findViewById(R.id.planNameToAdd);

        if(getArguments() != null){

            //tryb edycji istniejacego planu
            try {
                isEditingExistingPlan = getArguments().getBoolean("isEditingExistingPlan");
            } catch (Exception e) {
                Log.e(TAG, "onCreateView: " + e);
            }
            if (isEditingExistingPlan) {
                try {
                    exerciseModels = (ArrayList<ExerciseModel>) getArguments().getSerializable("exerlist");
                    receivedPlanId = getArguments().getString("receivedPlanId");
                    receivedPlanName = getArguments().getString("receivedPlanName");
                    Log.d(TAG, receivedPlanId);
                    planName.setText(receivedPlanName);
                } catch (Exception e) {
                    Log.e(TAG, "Edit plan: " + e.getMessage());
                }
            }

            RecyclerView recyclerView = main_view.findViewById(R.id.addNewPlanRecyclerView);
            adapter = new BrowsePlanExercisesRecyclerViewAdapter(main_view.getContext(), exerciseModels, requireActivity());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(main_view.getContext()));

            try {
                receivedIsAddingToPlan = getArguments().getBoolean("isAddingToPlan");
            } catch (Exception e) {
                Log.e(TAG, "Adding to plan: " + e);
            }
            if (receivedIsAddingToPlan) {
                exerciseModels.add((ExerciseModel) getArguments().getSerializable("exercise"));

                receivedIsAddingToPlan = false;
            }

        }

        //Log.d(ContentValues.TAG, "onClick: Passed boolean" +receivedIsAddingToPlan);

        addNewExercise.setOnClickListener(view -> {
            //stworzenie instancji fragmentu widoku kategorii
            Fragment fragment = new ExercisesFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("isAddingToPlan", true);
            bundle.putBoolean("isEditingExistingPlan", isEditingExistingPlan);
            if(isEditingExistingPlan){
                bundle.putString("receivedPlanId", receivedPlanId);
                bundle.putString("receivedPlanName", receivedPlanName);
                bundle.putSerializable("exerlist", exerciseModels);
            }
            fragment.setArguments(bundle);
            //przelaczenie widoku na ten fragment
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frame_layout, fragment).addToBackStack("addExercise");
            fragmentTransaction.commit();
        });
        submitPlanButton.setOnClickListener(view -> {

            //tryb edycji istniejacego planu
            if(isEditingExistingPlan){
                DocumentReference editedPlan = db.collection("plans").document(receivedPlanId);
                //pobranie planu z bd
                editedPlan.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            //model planu
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            PlanModelDB receivedPlanModel = document.toObject(PlanModelDB.class);
                            assert receivedPlanModel != null;
                            receivedPlanModel.setPlanId(document.getId());

                            //aktualizacja danych
                            String  planNameString = planName.getText().toString();
                            receivedPlanModel.setPlanName(planNameString);
                            receivedPlanModel.setExercises(exerciseModels);

                            //aktualizacja w bazie danych
                            editedPlan.set(receivedPlanModel)
                                    .addOnFailureListener(e -> Log.e(TAG, "onFailure: Nie mozna zaktualizowac planu"))
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(requireActivity(),getResources().getString(R.string.zaktualizowano_plan) + " " + planNameString, Toast.LENGTH_SHORT).show();
                                        exerciseModels.clear();
                                        ((MainActivity) requireActivity()).setUpPLanModels();
                                    });

                        } else {
                            Log.e(TAG, "No such document");
                        }
                    } else {
                        Log.e(TAG, "get failed with ", task.getException());
                    }
                });
            }

            //tryb dodawania nowego planu
            else {
                SetDateToRemindDialog setDateToRemindDialog = new SetDateToRemindDialog();
                setDateToRemindDialog.show(getParentFragmentManager(), "setDateToRemindDialog");
            }
        });
        return main_view;
    }

    @Override
    public void applyText(ArrayList<String> selectedDays, String selectedHour) {
                Map<String,Boolean> days = new HashMap<>();
        for (String selectedDay:selectedDays) {
            days.put(selectedDay,false);
        }
                String  planNameString = planName.getText().toString();

                Map<String, Object> plan = new HashMap<>();
                plan.put("planName",planNameString);
                plan.put("exercises",exerciseModels);
                plan.put("remindDay",days);
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
                        .addOnFailureListener(e -> Log.e(TAG, "onFailure: Nie dodano nowy plan"))
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(requireActivity(),getResources().getString(R.string.dodano_plan) + " " + planNameString, Toast.LENGTH_SHORT).show();
                            exerciseModels.clear();
                            adapter.notifyDataSetChanged();
                        });
    }

    @Override
    public void applyTextSkip() {

    }
}

