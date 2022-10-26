
package com.example.mojfizjo.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mojfizjo.Models.ExerciseModel;
import com.example.mojfizjo.R;

import java.util.ArrayList;

public class BrowsePlanExercisesRecyclerViewAdapter extends RecyclerView.Adapter<BrowsePlanExercisesRecyclerViewAdapter.MyBrowsePlansExerViewHolder> {

    private static final String TAG = "Bam";
    Context context;
    ArrayList<ExerciseModel> exerciseModels;
    Activity activity;

    public BrowsePlanExercisesRecyclerViewAdapter(Context context,  ArrayList<ExerciseModel> exerciseModels, Activity activity) {
        this.context = context;
        this.exerciseModels = exerciseModels;
        this.activity = activity;
    }

    @NonNull
    @Override
    public BrowsePlanExercisesRecyclerViewAdapter.MyBrowsePlansExerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.browse_plans_exercises_recycler_view, parent, false);
        return new BrowsePlanExercisesRecyclerViewAdapter.MyBrowsePlansExerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BrowsePlanExercisesRecyclerViewAdapter.MyBrowsePlansExerViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: "+exerciseModels.get(position).getExerciseName());
        holder.exerciseName.setText(exerciseModels.get(position).getExerciseName());
        holder.sets.setText(Integer.toString(exerciseModels.get(position).getSets()));
        holder.time.setText(exerciseModels.get(position).getTime());
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

    public static class MyBrowsePlansExerViewHolder extends RecyclerView.ViewHolder {

        TextView exerciseName,sets,time;

        public MyBrowsePlansExerViewHolder(@NonNull View itemView) {
            super(itemView);

            exerciseName = itemView.findViewById(R.id.exercise_name);
            sets = itemView.findViewById(R.id.sets);
            time = itemView.findViewById(R.id.time);
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
