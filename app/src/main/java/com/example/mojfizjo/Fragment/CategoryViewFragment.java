package com.example.mojfizjo.Fragment;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mojfizjo.Exercise;
import com.example.mojfizjo.R;
import com.example.mojfizjo.Photo_video;

import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.squareup.picasso.Picasso;

import java.util.Objects;

public class CategoryViewFragment extends Fragment implements View.OnClickListener{

    public CategoryViewFragment() {
        // Required empty public constructor
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_category_view, container, false);

        //uchwyt do bazy danych z dokumentami Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //uchwyt do bazy danych z grafika Firebase Storage
        StorageReference mStorage = FirebaseStorage.getInstance().getReference();
        //uchwyt do ukladu glownego
        LinearLayout exercisesListLayout = view.findViewById(R.id.All_exercises_layout);

        //wyswietlana nazwa kategorii
        assert getArguments() != null;
        String receivedCategoryName = getArguments().getString("selectedCategory");
        TextView displayedCategoryName = view.findViewById(R.id.category_name);
        displayedCategoryName.setText(receivedCategoryName);

        //pobranie ID kategorii
        String receivedCategoryID = getArguments().getString("categoryID");

        //pobranie cwiczen z bd
        db.collection("exercises")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());

                            //pobranie danych cwiczenia
                            Exercise exercise = document.toObject(Exercise.class);
                            exercise.setID(document.getId());
                            String ID = exercise.getID();
                            String name = exercise.getName();
                            String category = exercise.getCategory();
                            boolean viewedInCategory = exercise.isViewedInCategory();
                            Log.d(TAG, ID + name + category + viewedInCategory);

                            //jezeli cwiczenie nalezy do obecnej kategorii i ma byc w niej wyswietlane
                            if(Objects.equals(category, receivedCategoryID) && viewedInCategory){

                                //stworzenie nowego pojemnika na cwiczenie
                                LinearLayout new_generated_exercise_layout = new LinearLayout(view.getContext());
                                int newExerciseLayoutID = View.generateViewId();
                                new_generated_exercise_layout.setId(newExerciseLayoutID);
                                new_generated_exercise_layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                                new_generated_exercise_layout.setBackgroundResource(R.drawable.exercise_selector);
                                new_generated_exercise_layout.setOrientation(LinearLayout.VERTICAL);

                                //dodanie do cwiczenia przycisku z obrazkiem
                                ImageButton new_generated_exercise_button = new ImageButton(view.getContext());
                                int newExerciseButtonID = View.generateViewId();
                                new_generated_exercise_button.setId(newExerciseButtonID);
                                new_generated_exercise_button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                new_generated_exercise_button.setContentDescription(name);
                                new_generated_exercise_button.setTag(ID);
                                new_generated_exercise_button.setImageResource(R.drawable.blank_exercise_photo);
                                new_generated_exercise_button.setOnClickListener(this);
                                new_generated_exercise_layout.addView(new_generated_exercise_button);

                                //dodanie dolnego paska z nazwa i iloscia gwiazdek
                                LinearLayout new_generated_exercise_layout_internal = new LinearLayout(view.getContext());
                                int newExerciseLayoutInternalID = View.generateViewId();
                                new_generated_exercise_layout_internal.setId(newExerciseLayoutInternalID);
                                new_generated_exercise_layout_internal.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                new_generated_exercise_layout_internal.setOrientation(LinearLayout.HORIZONTAL);

                                //dodanie tekstu z nazwa cwiczenia
                                TextView new_generated_exercise_name = new TextView(view.getContext());
                                int newExerciseNameID = View.generateViewId();
                                new_generated_exercise_name.setId(newExerciseNameID);
                                new_generated_exercise_name.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
                                new_generated_exercise_name.setText(name);
                                new_generated_exercise_name.setTextColor(Color.parseColor("#A1887F"));
                                new_generated_exercise_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 34);
                                new_generated_exercise_layout_internal.addView(new_generated_exercise_name);

                                //dodanie pustego miejsca
                                Space new_generated_exercise_layout_internal_space = new Space(view.getContext());
                                new_generated_exercise_layout_internal_space.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2.0f));
                                new_generated_exercise_layout_internal.addView(new_generated_exercise_layout_internal_space);

                                //dodanie przycisku z gwiazdka
                                ImageButton new_generated_exercise_star = new ImageButton(view.getContext());
                                int newExerciseStarID = View.generateViewId();
                                new_generated_exercise_star.setId(newExerciseStarID);
                                int starButtonHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
                                new_generated_exercise_star.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, starButtonHeight, 1.0f));
                                new_generated_exercise_star.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                new_generated_exercise_star.setContentDescription(getResources().getString(R.string.gwiazdki));
                                new_generated_exercise_star.setTag(ID);
                                new_generated_exercise_star.setImageResource(R.drawable.ic_baseline_stars_24);
                                new_generated_exercise_star.setOnClickListener(this);
                                new_generated_exercise_layout_internal.addView(new_generated_exercise_star);

                                //dodanie tekstu wyswietlajacego ilosc gwiazdek
                                TextView new_generated_exercise_starCounter = new TextView(view.getContext());
                                int newExerciseStarCounterID = View.generateViewId();
                                new_generated_exercise_starCounter.setId(newExerciseStarCounterID);
                                new_generated_exercise_starCounter.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
                                new_generated_exercise_starCounter.setText("0");
                                new_generated_exercise_starCounter.setTextColor(Color.parseColor("#A1887F"));
                                new_generated_exercise_starCounter.setTextSize(TypedValue.COMPLEX_UNIT_SP, 34);
                                new_generated_exercise_layout_internal.addView(new_generated_exercise_starCounter);

                                new_generated_exercise_layout.addView(new_generated_exercise_layout_internal);
                                exercisesListLayout.addView(new_generated_exercise_layout);


                                //aktualizacja obrazka, jezeli cwiczenie posiada wlasny
                                //pobranie wszystkich obrazkow z bd
                                db.collection("photos_videos")
                                        .get()
                                        .addOnCompleteListener(task_internal -> {
                                            if (task_internal.isSuccessful()) {
                                                for (QueryDocumentSnapshot document_internal : task_internal.getResult()) {
                                                    Log.d(TAG, document_internal.getId() + " => " + document_internal.getData());

                                                    //pobranie danych zdjecia
                                                    Photo_video photo_video = document_internal.toObject(Photo_video.class);
                                                    photo_video.setID(document_internal.getId());
                                                    String photo_ID = photo_video.getID();
                                                    String photo_link = photo_video.getLink();
                                                    String photo_exerciseID = photo_video.getExerciseID();
                                                    int photo_order = photo_video.getOrder();
                                                    Log.d(TAG, photo_ID + photo_link + photo_order + photo_exerciseID);

                                                    //jezeli ID cwiczenia w zdjeciu odpowiada ID naszego cwiczenia i jest 1 w kolejnosci
                                                    if(Objects.equals(photo_exerciseID, ID) && photo_order == 1){
                                                        //pobranie obrazka z serwera na podstawie linku pobranego z bd
                                                        Task<Uri> downloadUrlTask = mStorage.child(photo_link).getDownloadUrl();
                                                        //ustawienie obrazka na przycisk
                                                        downloadUrlTask.addOnSuccessListener(result -> Picasso.get().load(result).fit().into(new_generated_exercise_button));
                                                        downloadUrlTask.addOnFailureListener(e -> {
                                                            Log.w(TAG, "downloadUrlTask:failure", e);
                                                            new_generated_exercise_button.setImageResource(R.drawable.ic_error_outline);
                                                        });
                                                    }
                                                }
                                            }
                                            else {
                                                Log.e(TAG, "Error getting documents: ", task_internal.getException());
                                            }
                                        })
                                        .addOnFailureListener(e -> Log.w(TAG, "downloadDBCollectionTask:failure", e));
                            }
                        }
                    }
                    else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
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
        String exerciseID = (String) view.getTag();

        //jezeli wcisniety przycisk to gwiazdka
        if(Objects.equals(selectedButton, getResources().getString(R.string.gwiazdki))){
            Log.e(TAG, "star");
        }
        //w przeciwnym wypadku jest to podglad cwiczenia
        else{
            //stworzenie instancji fragmentu widoku kategorii
            Fragment fragment = new ExerciseViewFragment();
            //przekazanie parametru z nazwa przycisku i ID kategorii do tej instancji
            Bundle bundle = new Bundle();
            Boolean receivedIsAddingToPlan = getArguments().getBoolean("isAddingToPlan");
            if(receivedIsAddingToPlan){
                bundle.putBoolean("isAddingToPlan",receivedIsAddingToPlan);
                Log.d(TAG, "onClick: Passed boolean" +receivedIsAddingToPlan);
            }
            bundle.putString("selectedExercise", selectedButton);
            bundle.putString("exerciseID", exerciseID);
            fragment.setArguments(bundle);

            //przelaczenie widoku na ten fragment
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment, "ExerciseViewFragment");
            fragmentTransaction.commit();

        }
    }
}