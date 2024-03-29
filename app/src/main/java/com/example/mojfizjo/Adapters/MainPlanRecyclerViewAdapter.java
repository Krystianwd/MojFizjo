package com.example.mojfizjo.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mojfizjo.Fragment.PlansFragment.AddNewPlanFragment;
import com.example.mojfizjo.Fragment.PlansFragment.BrowsePlanExercisesFragment;
import com.example.mojfizjo.MainActivity;
import com.example.mojfizjo.Models.PlanModel;
import com.example.mojfizjo.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

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

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBindViewHolder(@NonNull MainPlanRecyclerViewAdapter.MyMainPlanViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.planName.setText(planModels.get(position).getPlanName());
        for(int i=0;i<planModels.get(position).getExerciseModels().size();i++) {
            TextView textView = new TextView(context);
            String text = planModels.get(position).getExerciseModels().get(i).getExerciseName();
            textView.setText(text);
            holder.exerciseName.addView(textView);
        }
        Map<String, Boolean> remindDay = planModels.get(position).getRemindDay();
        StringBuilder remindDayString = new StringBuilder();
        for (Map.Entry<String, Boolean> entry : remindDay.entrySet()) {
            remindDayString.append(entry.getKey());
            remindDayString.append(", ");
        }
        int length = remindDayString.length();
        remindDayString = remindDayString.replace(length - 2, length - 1, "");
            holder.dayOfTheWeek.setText(remindDayString);
            holder.parentLayout.setOnClickListener(view -> {
            Log.d(TAG, String.valueOf(view.getId()));
            Bundle bundle = new Bundle();
            int getPosition = holder.getBindingAdapterPosition();
            bundle.putString("planId",planModels.get(getPosition).getPlanId());
            bundle.putInt("position",getPosition);
            bundle.putSerializable("exerlist",planModels.get(getPosition).getExerciseModels());
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            Fragment fragment = new BrowsePlanExercisesFragment();
            fragment.setArguments(bundle);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment,"BrowsePlanFragment").addToBackStack("Browse").commit();
        });
        holder.parentLayout.setOnLongClickListener(view -> {
            Log.d(TAG, String.valueOf(view.getId()));

            //wyswietlenie wyskakujacego menu
            PopupMenu popupMenu = new PopupMenu(context, holder.parentLayout);
            popupMenu.getMenuInflater().inflate(R.menu.plan_crud_popup_menu, popupMenu.getMenu());
            popupMenu.setOnDismissListener(popupMenu1 -> Log.d(TAG, "dismiss"));
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int id =  menuItem.getItemId();
                int getPosition = holder.getBindingAdapterPosition();
                switch(id){

                        //przycisk edycji planu
                    case R.id.menu_edit:
                        Log.d(TAG, "edit");
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isEditingExistingPlan",true);
                        bundle.putInt("position",getPosition);
                        bundle.putSerializable("exerlist",planModels.get(getPosition).getExerciseModels());
                        bundle.putString("receivedPlanId", planModels.get(getPosition).getPlanId());
                        bundle.putString("receivedPlanName", planModels.get(getPosition).getPlanName());
                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                        Fragment fragment = new AddNewPlanFragment();
                        fragment.setArguments(bundle);
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment, "AddPlanFragment").addToBackStack("Browse").commit();
                        break;

                        //przycisk usuwania planu
                    case R.id.menu_delete:
                        Log.d(TAG, "delete");
                        String planId = planModels.get(getPosition).getPlanId();
                        String planName = planModels.get(getPosition).getPlanName();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference planToDelete = db.collection("plans").document(planId);
                        planToDelete.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                planToDelete.delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                            Context context = view.getContext();
                                            Toast.makeText(context,context.getResources().getString(R.string.usunieto_plan) + " " + planName, Toast.LENGTH_SHORT).show();
                                            ((MainActivity) context).setUpPLanModels();
                                        })
                                        .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
                            } else {
                                Log.e(TAG, "get failed with ", task.getException());
                            }
                        });
                        break;

                    default:
                        Log.e(TAG, "invalid item ID");
                        return false;
                }
                return true;
            });
            popupMenu.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return planModels.size();
    }
    public static class MyMainPlanViewHolder extends RecyclerView.ViewHolder{

        TextView planName,dayOfTheWeek;
        LinearLayout exerciseName;
        CardView parentLayout;
        public MyMainPlanViewHolder(@NonNull View itemView) {
            super(itemView);
            dayOfTheWeek = itemView.findViewById(R.id.main_plan_day_of_the_week);
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
