package com.example.mojfizjo;

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

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.mojfizjo.Fragment.ExerciseViewFragment;
import com.example.mojfizjo.R;

public class ShowWeeklyPlanDialog extends DialogFragment {
    private EditText editTextSets;
    private EditText editTextTime;

    private com.example.mojfizjo.Fragment.PlansFragment.AddNewExerciseDialog.DialogListener listener;
    public interface DialogListener{
        void applyText(String sets,String time);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_set_reminders, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogLayout)
                // Add action buttons
                .setNeutralButton("Zamknij", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (com.example.mojfizjo.Fragment.PlansFragment.AddNewExerciseDialog.DialogListener) getParentFragmentManager().findFragmentByTag("ExerciseViewFragment");;
            Log.d(TAG, "onAttach: "+listener);
        }
        catch (ClassCastException  e) {
            throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
        }
    }


}
