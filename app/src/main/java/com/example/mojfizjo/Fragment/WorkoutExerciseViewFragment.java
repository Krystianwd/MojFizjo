package com.example.mojfizjo.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mojfizjo.R;

import java.util.Locale;


public class WorkoutExerciseViewFragment extends Fragment {

    String exerciseName;
    String time;
    int sets;

    int countdownSeconds;
    boolean isCountdownRunning;
    boolean isCountdownRunningBackup;
    int currentSet;

    TextView exerciseNameText;
    TextView setCounterText;
    TextView countdownText;
    ImageButton pauseButton;
    ImageButton stopButton;
    ImageButton resetButton;

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

        //uchwyty do elementow widoku
        exerciseNameText = view.findViewById(R.id.exerciseNameText);
        setCounterText = view.findViewById(R.id.setCounterText);
        countdownText = view.findViewById(R.id.countdownText);
        pauseButton = view.findViewById(R.id.pauseButton);
        stopButton = view.findViewById(R.id.stopButton);
        resetButton = view.findViewById(R.id.resetButton);

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
        currentSet++;
        String textToDisplay = getResources().getString(R.string.seria) + " " + currentSet + "/" + sets;
        setCounterText.setText(textToDisplay);
        resetTimer();
    }

    void togglePauseButtonVisual(){
        if(isCountdownRunning) pauseButton.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
        else pauseButton.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
    }
}