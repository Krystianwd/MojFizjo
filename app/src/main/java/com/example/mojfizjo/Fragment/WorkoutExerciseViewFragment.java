package com.example.mojfizjo.Fragment;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mojfizjo.Models.ExerciseModel;
import com.example.mojfizjo.R;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;


public class WorkoutExerciseViewFragment extends Fragment {

    String exerciseName;
    String time;
    int sets;
    ArrayList<String> exercisesFinished;
    String planName;
    ArrayList<ExerciseModel> exerciseModels;

    int countdownSeconds;
    boolean isCountdownRunning;
    boolean isCountdownRunningBackup;
    int currentSet;
    TextView isCustomExercise;
    TextView exerciseNameText;
    TextView setCounterText;
    TextView countdownText;
    ImageButton pauseButton;
    ImageButton stopButton;
    ImageButton resetButton;
    Button confirmExerciseFinishedButton;
    Button previewExerciseButton;

    public WorkoutExerciseViewFragment() {
        // Required empty public constructor
    }

    View view;

    @Override
    public void onPause()
    {
        super.onPause();
        isCountdownRunningBackup = isCountdownRunning;
        isCountdownRunning = false;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (isCountdownRunningBackup) isCountdownRunning = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_workout_exercise_view, container, false);

        assert getArguments() != null;

        //pobranie argumentow
        exerciseName = getArguments().getString("exerciseName");
        time = getArguments().getString("time");
        sets = getArguments().getInt("sets");
        exercisesFinished = getArguments().getStringArrayList("exercisesFinished");
        planName = getArguments().getString("planName");
        exerciseModels = (ArrayList<ExerciseModel>)getArguments().getSerializable("exerlist");
        Log.d(TAG, String.valueOf(exerciseModels.size()));

        //uchwyty do elementow widoku
        exerciseNameText = view.findViewById(R.id.exerciseNameText);
        setCounterText = view.findViewById(R.id.setCounterText);
        countdownText = view.findViewById(R.id.countdownText);
        pauseButton = view.findViewById(R.id.pauseButton);
        stopButton = view.findViewById(R.id.stopButton);
        resetButton = view.findViewById(R.id.resetButton);
        previewExerciseButton = view.findViewById(R.id.previewExerciseButton);
        confirmExerciseFinishedButton = view.findViewById(R.id.confirmExerciseFinishedButton);
        isCustomExercise = view.findViewById(R.id.workout_isCustomExercise);
        Log.d(TAG, "onCreateView: "+getArguments().getBoolean("exerciseRef"));
        if(getArguments().getBoolean("exerciseRef")){
            previewExerciseButton.setEnabled(false);
            isCustomExercise.setText("(Własne ćwiczenie)");
        }
        //dodanie sluchaczy przyciskow
        pauseButton.setOnClickListener(view -> {
            isCountdownRunning = !isCountdownRunning;
            togglePauseButtonVisual();
        });
        stopButton.setOnClickListener(view -> {
            isCountdownRunning = false;
            nextSet();
            togglePauseButtonVisual();
        });
        resetButton.setOnClickListener(view -> {
            isCountdownRunning = false;
            resetTimer();
            togglePauseButtonVisual();
        });
        confirmExerciseFinishedButton.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("exercisesFinished", exercisesFinished);
            bundle.putString("planName", planName);
            bundle.putSerializable("exerlist", exerciseModels);
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            Fragment fragment = new WorkoutPlanViewFragment();
            fragment.setArguments(bundle);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commit();
        });
        previewExerciseButton.setOnClickListener(view -> {

            //znalezienie ID tego cwiczenia
            String exerciseID = "";
            for(ExerciseModel e:exerciseModels){
                if(Objects.equals(e.getExerciseName(), exerciseName)){
                    exerciseID = e.getExercise().getId();
                }
            }
            if(exerciseID.equals("")){
                Toast.makeText(getContext(), getResources().getString(R.string.brak_podgladu), Toast.LENGTH_SHORT).show();
            }else{
                //zatrzymanie stopera
                isCountdownRunning = false;
                togglePauseButtonVisual();

                //ustawienie argumentow do widoku cwiczenia
                Fragment fragment = new ExerciseViewFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isShowingPreview", true);
                bundle.putString("selectedExercise", exerciseName);
                bundle.putString("exerciseID", exerciseID);

                //przelaczenie widoku na ten fragment
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.frame_layout, fragment).addToBackStack("showPreview");
                fragmentTransaction.commit();
            }
        });

        //ustawienia poczatkowe
        exerciseNameText.setText(exerciseName);
        currentSet = 0;
        nextSet();

        //minutnik
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override

            public void run()
            {
                if (isCountdownRunning) countdownSeconds--;
                if(countdownSeconds == 0) nextSet();
                handler.postDelayed(this, 1000);
                refreshTimer();
            }
        });

        return view;
    }

    void refreshTimer(){
        int h = countdownSeconds / 3600;
        int m = (countdownSeconds % 3600) / 60;
        int s = countdownSeconds % 60;
        String displayedTime = String.format(Locale.getDefault(), "%d:%02d:%02d", h,m,s);
        countdownText.setText(displayedTime);
    }

    void resetTimer(){
        countdownSeconds = Integer.parseInt(time);
        refreshTimer();
    }

    void nextSet(){
        if(currentSet < sets){
            currentSet++;
            String textToDisplay = getResources().getString(R.string.seria) + " " + currentSet + "/" + sets;
            setCounterText.setText(textToDisplay);
            resetTimer();
        }else{
            confirmExerciseFinishedButton.setVisibility(View.VISIBLE);
            isCountdownRunning = false;
            exercisesFinished.add(exerciseName);
        }
    }

    void togglePauseButtonVisual(){
        if(isCountdownRunning) pauseButton.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
        else pauseButton.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
    }
}