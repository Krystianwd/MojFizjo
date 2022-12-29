package com.example.mojfizjo.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mojfizjo.Models.ExerciseModel;
import com.example.mojfizjo.R;

import java.util.ArrayList;

public class AddNewPlanRecyclerViewAdapter extends RecyclerView.Adapter<AddNewPlanRecyclerViewAdapter.MyAddNewPlanViewHolder>{

    private static final String TAG = "Bam";
    Context context;
    ArrayList<ExerciseModel> exerciseModels;
    Activity activity;
    String planId;
    public AddNewPlanRecyclerViewAdapter(Context context,  String planId,ArrayList<ExerciseModel> exerciseModels, Activity activity) {
        this.context = context;
        this.exerciseModels = exerciseModels;
        this.activity = activity;
        this.planId = planId;
    }

    @NonNull
    @Override
    public AddNewPlanRecyclerViewAdapter.MyAddNewPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.browse_plans_exercises_recycler_view, parent, false);
        return new AddNewPlanRecyclerViewAdapter.MyAddNewPlanViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AddNewPlanRecyclerViewAdapter.MyAddNewPlanViewHolder holder, int position) {
        String text = exerciseModels.get(position).getExerciseName();
        if(exerciseModels.get(position).getExercise() == null){
            text+="(Własne ćwiczenie)";
        }
        holder.exerciseName.setText(text);
        holder.sets.setText(Integer.toString(exerciseModels.get(position).getSets()));
        holder.time.setText(exerciseModels.get(position).getTime());
        if(exerciseModels.get(position).getExercise() == null){
            holder.isCustomExercise.setText("(Własne ćwiczenie)");
        }
//        holder.parentLayout.setOnLongClickListener(view -> {
//            Log.d(TAG, String.valueOf(view.getId()));
//
//            //wyswietlenie wyskakujacego menu
//            PopupMenu popupMenu = new PopupMenu(context, holder.parentLayout);
//            popupMenu.getMenuInflater().inflate(R.menu.plan_crud_popup_menu, popupMenu.getMenu());
//            popupMenu.setOnDismissListener(popupMenu1 -> Log.d(TAG, "dismiss"));
//            popupMenu.setOnMenuItemClickListener(menuItem -> {
//                int id =  menuItem.getItemId();
//                int getPosition = holder.getBindingAdapterPosition();
//                switch(id){
//
//                    //przycisk edycji planu
//                    case R.id.menu_edit:
////                        Log.d(TAG, "edit");
//                        EditExerciseWhenAddPlanDialog editExerciseWhenAddPlanDialog = new EditExerciseWhenAddPlanDialog();
//                        Bundle bundle = new Bundle();
//                        bundle.putString("exerciseName",tempExerciseModels.get(getPosition).getExerciseName());
//                        editExerciseWhenAddPlanDialog.setArguments(bundle);
//                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
//                        editExerciseWhenAddPlanDialog.show(activity.getSupportFragmentManager(), "editExerciseWhenAddPlanDialog");
//                        break;
//
//                    //przycisk usuwania planu
//                    case R.id.menu_delete:
//                        Log.d(TAG, "delete");
//                        tempExerciseModels.remove(getPosition);
//                        notifyDataSetChanged();
//                        break;
//
//                    default:
//                        Log.e(TAG, "invalid item ID");
//                        return false;
//                }
//                return true;
//            });
//            popupMenu.show();
//            return true;
//        });
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

    public static class MyAddNewPlanViewHolder extends RecyclerView.ViewHolder {

        TextView exerciseName,sets,time,isCustomExercise;
        CardView parentLayout;

        public MyAddNewPlanViewHolder(@NonNull View itemView) {
            super(itemView);
            isCustomExercise = itemView.findViewById(R.id.isCustomExercise);
            exerciseName = itemView.findViewById(R.id.exercise_name);
            sets = itemView.findViewById(R.id.sets);
            time = itemView.findViewById(R.id.time);
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
