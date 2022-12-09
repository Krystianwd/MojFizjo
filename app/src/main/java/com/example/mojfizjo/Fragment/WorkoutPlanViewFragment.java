package com.example.mojfizjo.Fragment;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mojfizjo.Adapters.WorkoutPlanViewFragmentRecyclerViewAdapter;
import com.example.mojfizjo.Fragment.HomeFragment.HomeFragment;
import com.example.mojfizjo.MainActivity;
import com.example.mojfizjo.Models.ExerciseModel;
import com.example.mojfizjo.Models.PlanModel;
import com.example.mojfizjo.R;
import com.example.mojfizjo.UserSettings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class WorkoutPlanViewFragment extends Fragment {

    MainActivity mainActivity;
    UserSettings userSettings;
    public ArrayList<PlanModel> planModels;

    public WorkoutPlanViewFragment() {
        // Required empty public constructor
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getContext();
        assert mainActivity != null;
        planModels = mainActivity.planModels;
        userSettings = mainActivity.userSettings;

        view = inflater.inflate(R.layout.fragment_workout_plan_view, container, false);
        assert getArguments() != null;

        TextView planNameText =  view.findViewById(R.id.planNameText);
        String planName = getArguments().getString("planName");
        planNameText.setText(planName);

        Button endSessionButton = view.findViewById(R.id.endSessionButton);
        endSessionButton.setOnClickListener(view -> {

            //odznaczenie planu
            String dayOfTheWeek = new SimpleDateFormat("EEEE").format(new Date());
            for (int i = 0; i < planModels.size(); i++) {
                Map<String, Boolean> remindDay = planModels.get(i).getRemindDay();
                if (remindDay.containsKey(dayOfTheWeek) && Boolean.FALSE.equals(remindDay.get(dayOfTheWeek))) {
                    PlanModel planModel = planModels.get(i);
                    if(Objects.equals(planModel.getPlanName(), planName)){
                        Log.d(TAG, "plan done:" + planModel.getPlanName());
                        remindDay.put(dayOfTheWeek, true);
                        planModel.setRemindDay(remindDay);
                        planModels.set(i,planModel);
                    }
                }
            }

            //przekierowanie na fragment domowy
            HomeFragment fragment = new HomeFragment();
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commit();
        });

        ArrayList<ExerciseModel> exerciseModels = (ArrayList<ExerciseModel>) getArguments().getSerializable("exerlist");
        Log.d(TAG, String.valueOf(exerciseModels.size()));
        ArrayList<String> exercisesFinished = getArguments().getStringArrayList("exercisesFinished");
        RecyclerView recyclerView = view.findViewById(R.id.workout_plan_view_recyclerview);
        WorkoutPlanViewFragmentRecyclerViewAdapter adapter = new WorkoutPlanViewFragmentRecyclerViewAdapter(view.getContext(), exerciseModels, exercisesFinished, planName, requireActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }
}