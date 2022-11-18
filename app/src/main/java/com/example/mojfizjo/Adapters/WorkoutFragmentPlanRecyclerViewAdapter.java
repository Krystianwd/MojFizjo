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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mojfizjo.Fragment.WorkoutPlanViewFragment;
import com.example.mojfizjo.Models.PlanModel;
import com.example.mojfizjo.R;

import java.util.ArrayList;

public class WorkoutFragmentPlanRecyclerViewAdapter extends RecyclerView.Adapter<WorkoutFragmentPlanRecyclerViewAdapter.MyMainPlanViewHolder> {

    private static final String TAG = "Bam";
    Context context;
    ArrayList<PlanModel> planModels;
    Activity activity;

    public WorkoutFragmentPlanRecyclerViewAdapter(Context context, ArrayList<PlanModel> planModels, Activity activity) {
        this.context = context;
        this.planModels = planModels;
        this.activity = activity;
    }

    @NonNull
    @Override
    public WorkoutFragmentPlanRecyclerViewAdapter.MyMainPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.main_plan_recyclerview,parent,false);
        return new WorkoutFragmentPlanRecyclerViewAdapter.MyMainPlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutFragmentPlanRecyclerViewAdapter.MyMainPlanViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.planName.setText(planModels.get(position).getPlanName());
        for(int i=0;i<planModels.get(position).getExerciseModels().size();i++) {
            TextView textView = new TextView(context);
            textView.setText(planModels.get(position).getExerciseModels().get(i).getExerciseName());
            holder.exerciseName.addView(textView);
        }
        holder.parentLayout.setOnClickListener(view -> {

            int getPosition = holder.getAdapterPosition();
            PlanModel planModel = planModels.get(getPosition);
            Log.d(TAG, String.valueOf(view.getId()));
            if(planModel.hasExercises()){
                Bundle bundle = new Bundle();
                bundle.putString("planName", planModel.getPlanName());
                bundle.putInt("position",getPosition);
                bundle.putSerializable("exerlist",planModel.getExerciseModels());
                bundle.putStringArrayList("exercisesFinished", new ArrayList<>());
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment fragment = new WorkoutPlanViewFragment();
                fragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commit();
            }
            else {
                String text = context.getResources().getString(R.string.pustyplan);
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
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
}