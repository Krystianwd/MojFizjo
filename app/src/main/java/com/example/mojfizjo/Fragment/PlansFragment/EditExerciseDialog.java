package com.example.mojfizjo.Fragment.PlansFragment;


import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.fragment.app.DialogFragment;

import com.example.mojfizjo.Fragment.ExerciseViewFragment;
import com.example.mojfizjo.MainActivity;
import com.example.mojfizjo.Models.ExerciseModel;
import com.example.mojfizjo.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EditExerciseDialog extends DialogFragment {
    boolean isCustomExercise;
    private EditText editTextSets;
    private EditText editTextTime;
    private EditText editTextName;
    ArrayList<ExerciseModel> exerciseModels;
    String planId;
    int position;
    private DialogListener listener;

    public interface DialogListener {
        void editExercise(String name, String sets, String time,int position,String planId);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_add_exercise_to_plan, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogLayout)
                // Add action buttons
                .setPositiveButton(getResources().getString(R.string.zatwierdz), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String sets = editTextSets.getText().toString();
                        String time = editTextTime.getText().toString();
                        String name = editTextName.getText().toString();
                        if(sets.isEmpty() || name.isEmpty() || time.isEmpty())
                        {
                            Toast.makeText(getContext(),"Musisz wypełnić wszystkie pola",Toast.LENGTH_SHORT);
                        }
                        else {
                            listener.editExercise(name,sets, time,position,planId);
                        }
                    }
                })
                .setNegativeButton(getResources().getString(R.string.anuluj), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        builder.create().dismiss();
                    }
                });
        editTextSets = (EditText) dialogLayout.findViewById(R.id.dialog_sets);
        editTextTime = (EditText) dialogLayout.findViewById(R.id.dialog_time);
        editTextName = (EditText) dialogLayout.findViewById(R.id.dialog_name);

        editTextName.setText(getArguments().getString("exerciseName"));
        editTextTime.setText(getArguments().getString("time"));
        editTextSets.setText(getArguments().getString("sets"));
        position = getArguments().getInt("position");
        planId = getArguments().getString("planId");
        try {
            isCustomExercise = getArguments().getBoolean("isCustomExercise");
            if(isCustomExercise){
                Log.d(MotionEffect.TAG, "onCreateDialog: BAM");
            }
            else {
                editTextName.setEnabled(false);
            }
        }catch (Exception e){
            Log.d(MotionEffect.TAG, "onCreateDialog: "+e);
        }        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
                listener = (DialogListener) getParentFragmentManager().findFragmentByTag("BrowsePlanFragment");
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
        }
    }


}
