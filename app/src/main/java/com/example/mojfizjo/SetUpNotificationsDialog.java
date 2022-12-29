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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.mojfizjo.Fragment.ExerciseViewFragment;
import com.example.mojfizjo.R;

public class SetUpNotificationsDialog extends DialogFragment {
    private EditText editTextSets;
    private EditText editTextTime;
    private SetUpNotificationDialogListener listener;
    public interface SetUpNotificationDialogListener{
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
        Spinner spinnerHours = dialogLayout.findViewById(R.id.remind_hour_before_training);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterHours = ArrayAdapter.createFromResource(requireActivity(),
                R.array.Hours, R.layout.spinner_select_day);

        // Specify the layout to use when the list of choices appears
        adapterHours.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerHours.setAdapter(adapterHours);
        // Set the dialog title
        builder.setView(dialogLayout)
                // Add action buttons
                .setPositiveButton(getResources().getString(R.string.zatwierdz), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
//                        String sets = editTextSets.getText().toString();
//                        String time = editTextTime.getText().toString();
//                        listener.applyText(sets,time);
                        builder.create().dismiss();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.anuluj), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        builder.create().dismiss();
                    }
                });
        ;
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (SetUpNotificationDialogListener) getParentFragmentManager().findFragmentByTag("ExerciseViewFragment");;
            Log.d(TAG, "onAttach: "+listener);
        }
        catch (ClassCastException  e) {
            throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
        }
    }


}
