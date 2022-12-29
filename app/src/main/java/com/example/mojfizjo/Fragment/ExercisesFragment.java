package com.example.mojfizjo.Fragment;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.constraintlayout.helper.widget.Flow;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mojfizjo.Category;
import com.example.mojfizjo.Models.ExerciseModel;
import com.example.mojfizjo.R;

import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ExercisesFragment extends Fragment implements View.OnClickListener{


    public ExercisesFragment() {
        // Required empty public constructor
    }

    View view;

    boolean receivedIsEditingExistingPlan = false;
    Button addCustomExercise;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_exercise, container, false);

        //uchwyt do bazy danych z dokumentami Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //uchwyt do bazy danych z grafika Firebase Storage
        StorageReference mStorage = FirebaseStorage.getInstance().getReference();
        //uchwyt do ukladu glownego
        ConstraintLayout exerciseFragmentLayout = view.findViewById(R.id.exerciseFragmentLayout);
        //uchwyt do Flow
        Flow exerciseFragmentFlow = view.findViewById(R.id.exerciseFragmentFlow);
        //pobranie kategorii z bd
        db.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());

                            //pobranie danych kategorii
                            Category category = document.toObject(Category.class);
                            category.setID(document.getId());
                            String ID = category.getID();
                            String name = category.getName();
                            String iconLink = category.getIconLink();
                            Log.d(TAG, ID + name + iconLink);

                            //stworzenie nowego przycisku
                            ImageButton new_generated_category_button = new ImageButton(view.getContext());
                            new_generated_category_button.setSaveEnabled(true);
                            //ustawienie opisu na podstawie pobranej nazwy kategorii
                            new_generated_category_button.setContentDescription(name);
                            //ustawienie tagu z ID kategorii
                            new_generated_category_button.setTag(ID);

                            //tymczasowy obrazek
                            new_generated_category_button.setImageResource(R.drawable.loading_circle);
                            //dodanie tla
                            new_generated_category_button.setScaleType(ImageView.ScaleType.valueOf("FIT_CENTER"));
                            new_generated_category_button.setBackgroundResource(R.drawable.category_selector);

                            //ustawienie parametrow wielkosci na 0 - Flow automatycznie ustawi wielkosc
                            new_generated_category_button.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                            new_generated_category_button.setMinimumHeight(0);
                            new_generated_category_button.setMinimumWidth(0);

                            //pobranie obrazka z serwera na podstawie linku pobranego z bd
                            Task<Uri> downloadUrlTask = mStorage.child(iconLink).getDownloadUrl();
                            //ustawienie obrazka na przycisk
                            downloadUrlTask.addOnSuccessListener(result -> Picasso.get().load(result).fit().into(new_generated_category_button));
                            downloadUrlTask.addOnFailureListener(e -> {
                                Log.w(TAG, "downloadUrlTask:failure", e);
                                new_generated_category_button.setImageResource(R.drawable.ic_error_outline);
                            });

                            //dodanie nowego ID
                            int newButtonId = View.generateViewId();
                            new_generated_category_button.setId(newButtonId);
                            //dodanie funkcjonalnosci
                            new_generated_category_button.setOnClickListener(this);

                            //dodanie przycisku do ukladu i Flow
                            exerciseFragmentLayout.addView(new_generated_category_button);
                            exerciseFragmentFlow.addView(new_generated_category_button);
                        }
                    } else {
                        Log.w(TAG, "Error getting documents: ", task.getException());
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, "downloadDBCollectionTask:failure", e));

        return view;
    }

    @Override
    public void onClick(View view) {

        Log.d(TAG, String.valueOf(view.getId()));

        //pobranie nazwy wcisnietego przycisku i ID kategorii
        String selectedButton = (String) view.getContentDescription();
        String categoryID = (String) view.getTag();

        //stworzenie instancji fragmentu widoku kategorii
        Fragment fragment = new CategoryViewFragment();
        //przekazanie parametru z nazwa przycisku i ID kategorii do tej instancji
        Bundle bundle = new Bundle();

        if(getArguments() != null){
            try {
                boolean receivedIsAddingToPlan = getArguments().getBoolean("isAddingToPlan");
                bundle.putBoolean("isAddingToPlan",receivedIsAddingToPlan);
                Log.d(TAG, "onClick: Passed boolean" +receivedIsAddingToPlan);

                //tryb edycji istniejacego planu
                receivedIsEditingExistingPlan = getArguments().getBoolean("isEditingExistingPlan");
                if(receivedIsEditingExistingPlan){
                    String receivedPlanId = getArguments().getString("receivedPlanId");
                    String receivedPlanName = getArguments().getString("receivedPlanName");
                    ArrayList<ExerciseModel> exerciseModels = (ArrayList<ExerciseModel>) getArguments().getSerializable("exerlist");
                    Log.d(TAG, String.valueOf(exerciseModels));
                    bundle.putSerializable("exerlist", exerciseModels);
                    bundle.putBoolean("isEditingExistingPlan", true);
                    bundle.putString("receivedPlanId", receivedPlanId);
                    bundle.putString("receivedPlanName", receivedPlanName);
                }
            }
            catch (Exception e){
                Log.e(TAG, e.getMessage());
            }
        }

        bundle.putString("selectedCategory", selectedButton);
        bundle.putString("categoryID", categoryID);
        fragment.setArguments(bundle);

        //przelaczenie widoku na ten fragment
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment).addToBackStack("addExercise");
        fragmentTransaction.commit();
    }
}