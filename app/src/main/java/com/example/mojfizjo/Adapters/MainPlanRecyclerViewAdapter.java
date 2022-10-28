package com.example.mojfizjo.Adapters;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mojfizjo.Fragment.PlansFragment.BrowsePlanExercisesFragment;
import com.example.mojfizjo.Models.PlanModel;
import com.example.mojfizjo.R;

import java.util.ArrayList;

public class MainPlanRecyclerViewAdapter extends RecyclerView.Adapter<MainPlanRecyclerViewAdapter.MyMainPlanViewHolder> {

    private static final String TAG = "Bam";
    Context context;
    ArrayList<PlanModel> planModels;
    Activity activity;

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    public MainPlanRecyclerViewAdapter(Context context, ArrayList<PlanModel> planModels, Activity activity) {
//        setHasStableIds(true);
        this.context = context;
        this.planModels = planModels;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MainPlanRecyclerViewAdapter.MyMainPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.main_plan_recyclerview,parent,false);
        return new MainPlanRecyclerViewAdapter.MyMainPlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainPlanRecyclerViewAdapter.MyMainPlanViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.planName.setText(planModels.get(position).getPlanName());
        for(int i=0;i<planModels.get(position).getExerciseModel().size();i++) {
            TextView textView = new TextView(context);
            textView.setText(planModels.get(position).getExerciseModel().get(i).getExerciseName());
            holder.exerciseName.addView(textView);
        }
        holder.parentLayout.setOnClickListener(view -> {
            Log.d(TAG, String.valueOf(view.getId()));
            Bundle bundle = new Bundle();
            int getPosition = holder.getAdapterPosition();
            bundle.putInt("position",getPosition);
            bundle.putSerializable("exerlist",planModels.get(getPosition).getExerciseModel());
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            Fragment fragment = new BrowsePlanExercisesFragment();
            fragment.setArguments(bundle);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack("Browse").commit();
        });
    }

    @Override
    public int getItemCount() {
        return planModels.size();
    }
    public static class MyMainPlanViewHolder extends RecyclerView.ViewHolder{

        TextView planName;
        LinearLayout exerciseName;
        CardView parentLayout;
        public MyMainPlanViewHolder(@NonNull View itemView) {
            super(itemView);

            planName = itemView.findViewById(R.id.mainPlanName);
            exerciseName = itemView.findViewById(R.id.LinearLayoutPlansMain);
            parentLayout = itemView.findViewById(R.id.parent_view);

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
