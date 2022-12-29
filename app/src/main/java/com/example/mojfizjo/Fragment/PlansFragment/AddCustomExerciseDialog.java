package com.example.mojfizjo.Fragment.PlansFragment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

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
import androidx.fragment.app.DialogFragment;

import com.example.mojfizjo.R;

public class AddCustomExerciseDialog extends DialogFragment {
    boolean isCustomExercise;
    private EditText editTextSets;
    private EditText editTextTime;
    private EditText editTextName;

    private DialogListener listener;

    public interface DialogListener {
        void addCustomExercise(String exerciseName, String sets, String time);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        isCustomExercise = false;

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
                        String exerciseName = editTextName.getText().toString();
                        String sets = editTextSets.getText().toString();
                        String time = editTextTime.getText().toString();
                        if(sets.isEmpty() || exerciseName.isEmpty() || time.isEmpty())
                        {
                            Toast.makeText(getContext(),"Musisz wypełnić wszystkie pola",Toast.LENGTH_SHORT);
                        }
                        else {
                            listener.addCustomExercise(exerciseName,sets, time);
                        }

                    }
                })
                .setNegativeButton(getResources().getString(R.string.anuluj), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        builder.create().dismiss();
                    }
                });
        ;
        editTextSets = (EditText) dialogLayout.findViewById(R.id.dialog_sets);
        editTextTime = (EditText) dialogLayout.findViewById(R.id.dialog_time);
        editTextName = (EditText) dialogLayout.findViewById(R.id.dialog_name);
        try {
            isCustomExercise = getArguments().getBoolean("isCustomExercise");
            if(isCustomExercise){
                Log.d(TAG, "onCreateDialog: BAM");
//                editTextName.setEnabled(true);
            }
            else {
                editTextName.setText(getArguments().getString("exerciseName"));
            }
        }catch (Exception e){
            Log.d(TAG, "onCreateDialog: "+e);
        }
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddCustomExerciseDialog.DialogListener) getParentFragmentManager().findFragmentByTag("AddPlanFragment");
            Log.d(TAG, "onAttach: " + listener);
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
        }
    }


}
