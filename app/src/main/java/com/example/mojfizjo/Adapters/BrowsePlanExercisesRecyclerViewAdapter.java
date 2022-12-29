
package com.example.mojfizjo.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mojfizjo.Fragment.PlansFragment.BrowsePlanExercisesFragment;
import com.example.mojfizjo.Fragment.PlansFragment.EditExerciseDialog;
import com.example.mojfizjo.MainActivity;
import com.example.mojfizjo.Models.ExerciseModel;
import com.example.mojfizjo.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BrowsePlanExercisesRecyclerViewAdapter extends RecyclerView.Adapter<BrowsePlanExercisesRecyclerViewAdapter.MyBrowsePlansExerViewHolder> {

    private static final String TAG = "Bam";
    public int position;
    Context context;
    ArrayList<ExerciseModel> exerciseModels;
    Activity activity;
    String planId;

    public BrowsePlanExercisesRecyclerViewAdapter(Context context, String planId, ArrayList<ExerciseModel> exerciseModels, Activity activity) {
        this.context = context;
        this.exerciseModels = exerciseModels;
        this.activity = activity;
        this.planId = planId;

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
        Log.d(TAG, "onBindViewHolder: " + exerciseModels.get(position).getExerciseName());
        String text = exerciseModels.get(position).getExerciseName();
        if(exerciseModels.get(position).getExercise() == null){
            holder.isCustomExercise.setText("(Własne ćwiczenie)");
        }
        holder.exerciseName.setText(text);
        holder.sets.setText(Integer.toString(exerciseModels.get(position).getSets()));
        holder.time.setText(exerciseModels.get(position).getTime());
        holder.parentLayout.setOnLongClickListener(view -> {
            Log.d(TAG, String.valueOf(view.getId()));

            //wyswietlenie wyskakujacego menu
            PopupMenu popupMenu = new PopupMenu(context, holder.parentLayout);
            popupMenu.getMenuInflater().inflate(R.menu.plan_crud_popup_menu, popupMenu.getMenu());
            popupMenu.setOnDismissListener(popupMenu1 -> Log.d(TAG, "dismiss"));
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int id = menuItem.getItemId();
                this.position = holder.getBindingAdapterPosition();
                int getPosition = holder.getBindingAdapterPosition();
                switch (id) {

                    //przycisk edycji planu
                    case R.id.menu_edit:
                        Log.d(TAG, "edit");
                        EditExerciseDialog editExerciseDialog = new EditExerciseDialog();
                        Bundle bundle = new Bundle();
                        bundle.putInt("position",position);
                        bundle.putString("planId",planId);
                        bundle.putString("exerciseName", exerciseModels.get(getPosition).getExerciseName());
                        bundle.putString("time",exerciseModels.get(getPosition).getTime());
                        bundle.putString("sets",Integer.toString(exerciseModels.get(getPosition).getSets()));
//                        bundle.putSerializable("exercises",exerciseModels);
                        if(exerciseModels.get(position).getExercise() == null){
                            bundle.putBoolean("isCustomExercise",true);
                        }
                        editExerciseDialog.setArguments(bundle);
                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                        editExerciseDialog.show(activity.getSupportFragmentManager(), "editExerciseDialog");
                        break;

                    //przycisk usuwania planu
                    case R.id.menu_delete:
                        Log.d(TAG, "delete");
                        String exerciseName = exerciseModels.get(getPosition).getExerciseName();
                        ArrayList<ExerciseModel> newExerciseModels = exerciseModels;
                        newExerciseModels.remove(getPosition);
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("plans").document(planId).update("exercises", newExerciseModels).addOnSuccessListener(task -> {
                            Toast.makeText(context, "Usunięto ćwiczenie o nazwie: " + exerciseName, Toast.LENGTH_LONG).show();
                            ((MainActivity) context).setUpPLanModels();
                            newExerciseModels.clear();
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

        try {
            return exerciseModels.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public static class MyBrowsePlansExerViewHolder extends RecyclerView.ViewHolder {

        TextView exerciseName, sets, time,isCustomExercise;
        CardView parentLayout;

        public MyBrowsePlansExerViewHolder(@NonNull View itemView) {
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
