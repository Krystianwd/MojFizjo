package com.example.mojfizjo.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mojfizjo.Models.ExerciseModel;
import com.example.mojfizjo.R;
import com.example.mojfizjo.Fragment.WorkoutExerciseViewFragment;

import java.util.ArrayList;

public class WorkoutPlanViewFragmentRecyclerViewAdapter extends RecyclerView.Adapter<WorkoutPlanViewFragmentRecyclerViewAdapter.MyWorkoutPlanViewFragmentViewHolder> {

    private static final String TAG = "Bam";
    Context context;
    ArrayList<ExerciseModel> exerciseModels;
    Activity activity;

    public WorkoutPlanViewFragmentRecyclerViewAdapter(Context context,  ArrayList<ExerciseModel> exerciseModels, Activity activity) {
        this.context = context;
        this.exerciseModels = exerciseModels;
        this.activity = activity;
    }

    @NonNull
    @Override
    public WorkoutPlanViewFragmentRecyclerViewAdapter.MyWorkoutPlanViewFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.workout_plan_view_recyclerview, parent, false);
        return new WorkoutPlanViewFragmentRecyclerViewAdapter.MyWorkoutPlanViewFragmentViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WorkoutPlanViewFragmentRecyclerViewAdapter.MyWorkoutPlanViewFragmentViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: "+exerciseModels.get(position).getExerciseName());
        holder.exerciseName.setText(exerciseModels.get(position).getExerciseName());

        holder.parentLayout.setOnClickListener(view -> {

            int getPosition = holder.getAdapterPosition();
            ExerciseModel exerciseModel = exerciseModels.get(getPosition);
            Log.d(TAG, String.valueOf(view.getId()));
            Bundle bundle = new Bundle();
            bundle.putString("exerciseName", exerciseModel.getExerciseName());
            bundle.putInt("position",getPosition);
            bundle.putInt("sets",exerciseModel.getSets());
            bundle.putString("time",exerciseModel.getTime());
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            Fragment fragment = new WorkoutExerciseViewFragment();
            fragment.setArguments(bundle);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commit();
        });
    }

    @Override
    public int getItemCount() {

        try {
            return exerciseModels.size();
        }
        catch(Exception e){
            return 0;
        }
    }

    public static class MyWorkoutPlanViewFragmentViewHolder extends RecyclerView.ViewHolder {

        TextView exerciseName;
        CardView parentLayout;
        ImageView exerciseFinishedCheckbox;

        public MyWorkoutPlanViewFragmentViewHolder(@NonNull View itemView) {
            super(itemView);

            exerciseName = itemView.findViewById(R.id.exercise_name);
            parentLayout = itemView.findViewById(R.id.parent_view);
            exerciseFinishedCheckbox = itemView.findViewById(R.id.exercise_finished);

        }
    }
}
