package com.example.mojfizjo.Fragment;

import static android.content.ContentValues.TAG;

import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mojfizjo.Exercise;
import com.example.mojfizjo.Fragment.PlansFragment.AddNewExerciseDialog;
import com.example.mojfizjo.Models.ExerciseModel;
import com.example.mojfizjo.R;
import com.example.mojfizjo.Photo_video;

import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Vector;

public class ExerciseViewFragment extends Fragment implements AddNewExerciseDialog.DialogListener {

    public String name,ID,category,content;
    int duration;
    DocumentReference reference;
    public ExerciseViewFragment() {
        // Required empty public constructor
    }

    Boolean isEditingExistingPlan = false;
    String receivedPlanId ="";
    String receivedPlanName="";

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_exercise_view, container, false);

        //uchwyt do bazy danych z dokumentami Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //uchwyt do bazy danych z grafika Firebase Storage
        StorageReference mStorage = FirebaseStorage.getInstance().getReference();
        //uchwyt do ukladu glownego
        LinearLayout exerciseViewLayout = view.findViewById(R.id.exercise_view_layout);

        //argumenty pobrane z poprzedniego widoku
        assert getArguments() != null;
        String receivedExerciseName = getArguments().getString("selectedExercise");
        String receivedExerciseID = getArguments().getString("exerciseID");


        // ----- ALTERNATYWNE TRYBY WIDOKU -----

        //dodawanie cwiczenia do planu
        boolean receivedIsAddingToPlan = getArguments().getBoolean("isAddingToPlan");
        if(receivedIsAddingToPlan){
            Button submitAddingToPlan = view.findViewById(R.id.submitAddingExerciseToPlan);
            submitAddingToPlan.setEnabled(true);
            submitAddingToPlan.setVisibility(View.VISIBLE);
            submitAddingToPlan.setOnClickListener(view -> {
                AddNewExerciseDialog addNewExerciseDialog = new AddNewExerciseDialog();
                addNewExerciseDialog.show(getParentFragmentManager(),"addNewExerciseDialog");
            });

            //tryb edycji planu
            isEditingExistingPlan = getArguments().getBoolean("isEditingExistingPlan");
            if(isEditingExistingPlan){
                receivedPlanId = getArguments().getString("receivedPlanId");
                receivedPlanName = getArguments().getString("receivedPlanName");
                Log.d(TAG, receivedPlanName);
            }
        }

        //wyswietlanie podgladu cwiczenia
        boolean receivedIsShowingPreview = getArguments().getBoolean("isShowingPreview");
        if(receivedIsShowingPreview){
            Button submitAddingToPlan = view.findViewById(R.id.submitAddingExerciseToPlan);
            submitAddingToPlan.setEnabled(true);
            submitAddingToPlan.setVisibility(View.VISIBLE);
            submitAddingToPlan.setText(getResources().getString(R.string.powrot_do_treningu));
            Drawable replacementImage = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_change_circle_24);
            submitAddingToPlan.setCompoundDrawablesRelativeWithIntrinsicBounds(null, replacementImage, null, null);
            submitAddingToPlan.setOnClickListener(view -> {
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                fm.popBackStack ("showPreview", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            });
        }

        // ----- -----


        //wyswietlana nazwa cwiczenia
        TextView displayedExerciseName = view.findViewById(R.id.registration_header);
        displayedExerciseName.setText(receivedExerciseName);

        //pobranie z bd cwiczenia o tym ID
        db.collection("exercises").document(receivedExerciseID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            //pobranie danych cwiczenia
                            Exercise exercise = document.toObject(Exercise.class);
                            assert exercise != null;
                            exercise.setID(document.getId());
                            ID = exercise.getID();
                            name = exercise.getName();
                            category = exercise.getCategory();
                            content = exercise.getContent();
                            duration = exercise.getDuration();
                            reference = document.getReference();
                            Log.d(TAG, ID + name + category + content + duration);

                            //tymczasowy pusty podglad cwiczenia (obrazek bedzie zaladowany asynchronicznie)
                            ImageView generated_image_preview = new ImageView(view.getContext());
                            int previewImageID = View.generateViewId();
                            generated_image_preview.setId(previewImageID);
                            generated_image_preview.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                            exerciseViewLayout.addView(generated_image_preview);

                            //dodanie tekstu z czasem trwania ćwiczenia
                            TextView generated_exercise_time = new TextView(view.getContext());
                            int newExerciseTimeID = View.generateViewId();
                            generated_exercise_time.setId(newExerciseTimeID);
                            generated_exercise_time.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
                            String displayedExerciseDuration = getResources().getString(R.string.czas_trwania) + duration + getResources().getString(R.string.sekund);
                            generated_exercise_time.setText(displayedExerciseDuration);
                            generated_exercise_time.setTextColor(Color.parseColor("#A1887F"));
                            generated_exercise_time.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                            exerciseViewLayout.addView(generated_exercise_time);

                            //dodanie naglowka opisu
                            TextView generated_exercise_descriptionHeader = new TextView(view.getContext());
                            int newExerciseDescriptionHeaderID = View.generateViewId();
                            generated_exercise_descriptionHeader.setId(newExerciseDescriptionHeaderID);
                            generated_exercise_descriptionHeader.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
                            generated_exercise_descriptionHeader.setText(getResources().getString(R.string.opis));
                            generated_exercise_descriptionHeader.setTextColor(Color.parseColor("#A1887F"));
                            generated_exercise_descriptionHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                            exerciseViewLayout.addView(generated_exercise_descriptionHeader);

                            //dodanie opisu
                            TextView generated_exercise_description = new TextView(view.getContext());
                            int newExerciseDescriptionID = View.generateViewId();
                            generated_exercise_description.setId(newExerciseDescriptionID);
                            generated_exercise_description.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
                            generated_exercise_description.setText(content);
                            generated_exercise_description.setTextColor(Color.parseColor("#000000"));
                            generated_exercise_description.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                            exerciseViewLayout.addView(generated_exercise_description);

                            //dodanie naglowka zdjec i filmow
                            TextView generated_exercise_imagesHeader = new TextView(view.getContext());
                            int newExerciseImagesHeaderID = View.generateViewId();
                            generated_exercise_imagesHeader.setId(newExerciseImagesHeaderID);
                            generated_exercise_imagesHeader.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
                            generated_exercise_imagesHeader.setText(getResources().getString(R.string.zdjecia_filmy));
                            generated_exercise_imagesHeader.setTextColor(Color.parseColor("#A1887F"));
                            generated_exercise_imagesHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                            exerciseViewLayout.addView(generated_exercise_imagesHeader);


                            //-----CZESC ASYNCHRONICZNA - MUSI BYC W KODZIE JAKO OSTATNIA, ZASTEPUJEMY TYMCZASOWE GRAFIKI Z CZESCI SYNCHRONICZNEJ-----

                            //pobranie wszystkich obrazkow z bd
                            db.collection("photos_videos")
                                    .get()
                                    .addOnCompleteListener(task_internal -> {
                                        if (task_internal.isSuccessful()) {

                                            //stworzenie wektora na zdjecia i filmy cwiczenia
                                            Vector<Photo_video> photosVideosVector = new Vector<>();

                                            for (QueryDocumentSnapshot document_internal : task_internal.getResult()) {
                                                Log.d(TAG, document_internal.getId() + " => " + document_internal.getData());

                                                //pobranie danych zdjecia
                                                Photo_video photo_video = document_internal.toObject(Photo_video.class);
                                                photo_video.setID(document_internal.getId());
                                                String photo_ID = photo_video.getID();
                                                String photo_link = photo_video.getLink();
                                                String photo_ExerciseID = photo_video.getExerciseID();
                                                int photo_order = photo_video.getOrder();

                                                //jezeli ID cwiczenia w zdjeciu odpowiada ID naszego cwiczenia, dodajemy do wektora
                                                if(Objects.equals(photo_ExerciseID, ID)) {
                                                    Log.d(TAG, photo_ID + photo_link + photo_order + photo_ExerciseID);
                                                    photosVideosVector.add(photo_video);
                                                }
                                            }

                                            //jezeli exercise posiada liste zdjec
                                            if (!(photosVideosVector.isEmpty())){

                                                //sortowanie wzgledem kolejnosci
                                                Collections.sort(photosVideosVector);

                                                //pobranie obrazka z serwera na podstawie linku
                                                Task<Uri> downloadUrlTask = mStorage.child(photosVideosVector.firstElement().getLink()).getDownloadUrl();
                                                downloadUrlTask.addOnSuccessListener(result -> {

                                                    //przygotowanie obrazka na podglad cwiczenia
                                                    generated_image_preview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                                    generated_image_preview.setImageResource(R.drawable.blank_exercise_photo);

                                                    //zaladowanie grafiki z serwera (w orginalnej skali!)
                                                    Picasso.get().load(result).into(generated_image_preview);
                                                });
                                                downloadUrlTask.addOnFailureListener(e -> Log.w(TAG, "downloadUrlTask:failure", e));

                                                //dodanie zdjec
                                                for(int i=0; i< photosVideosVector.size(); i++){

                                                    //tymczasowe puste miejsce
                                                    ImageView new_generated_image = new ImageView(view.getContext());
                                                    int newImageID = View.generateViewId();
                                                    new_generated_image.setId(newImageID);
                                                    new_generated_image.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                                                    exerciseViewLayout.addView(new_generated_image);

                                                    //dodanie grafiki
                                                    Log.d(TAG, photosVideosVector.get(i).getLink());
                                                    Task<Uri> downloadUrlTask2 = mStorage.child(photosVideosVector.get(i).getLink()).getDownloadUrl();
                                                    downloadUrlTask2.addOnSuccessListener(result -> {

                                                        //przygotowanie obrazka na podglad cwiczenia
                                                        new_generated_image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                                        new_generated_image.setImageResource(R.drawable.blank_exercise_photo);

                                                        //zaladowanie grafiki z serwera (w orginalnej skali!)
                                                        Picasso.get().load(result).into(new_generated_image);
                                                    });
                                                    downloadUrlTask2.addOnFailureListener(e -> Log.w(TAG, "downloadUrlTask:failure", e));
                                                }
                                            }
                                            else{
                                                //jezeli nie ma zdjec, zmiana naglowka
                                                generated_exercise_imagesHeader.setText(getResources().getString(R.string.brak_zdjec));
                                            }
                                        }
                                        else {
                                            Log.e(TAG, "Error getting documents: ", task_internal.getException());
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.w(TAG, "downloadDBCollectionTask:failure", e));

                        }
                        else {
                            Log.e(TAG, "Error: No exercise with this ID");
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
    public void applyText(String sets, String time) {
        //stworzenie instancji fragmentu planow
        Fragment fragment = getParentFragmentManager().findFragmentByTag("AddPlanFragment");
        if(fragment != null){
            //przekazanie parametrow do tej instancji
            Bundle bundle = new Bundle();
            ExerciseModel exerciseModel = new ExerciseModel(name,reference,Integer.parseInt(sets),time);
            bundle.putBoolean("isAddingToPlan",true);
            bundle.putSerializable("exercise",exerciseModel);
            Log.d(TAG, "applyText: "+bundle);

            //tryb edycji planu
            if(isEditingExistingPlan){
                ArrayList<ExerciseModel> exerciseModels = (ArrayList<ExerciseModel>) getArguments().getSerializable("exerlist");
                bundle.putSerializable("exerlist", exerciseModels);
                bundle.putBoolean("isEditingExistingPlan", true);
                bundle.putString("receivedPlanId", receivedPlanId);
                bundle.putString("receivedPlanName", receivedPlanName);
                Log.e(TAG, receivedPlanName + receivedPlanId + exerciseModels);
            }

            fragment.setArguments(bundle);

            //przelaczenie widoku na ten fragment
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            requireActivity().getSupportFragmentManager().popBackStack("addExercise",POP_BACK_STACK_INCLUSIVE);
            fragmentTransaction.replace(R.id.frame_layout, fragment, "AddPlanFragment");
            fragmentTransaction.commit();
        } else{
            Log.e(TAG,"???");
        }
    }
}