package com.example.mojfizjo.Fragment.PlansFragment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.mojfizjo.R;

import java.util.ArrayList;

public class SetDateToRemindDialog extends DialogFragment {
    String selectedDay;
    String selectedHour;
    private DialogListener listener;
    public interface DialogListener{
        void setDate(ArrayList<String> selectedDays,String selectedHour);
        void skipSetDate();
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ArrayList<String> selectedDays = new ArrayList<String>();
        ArrayList<Integer> checkedInts = new ArrayList<Integer>();
        String[] days = getResources().getStringArray(R.array.Days);
        String[] hours = getResources().getStringArray(R.array.Hours);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_set_time_to_remind, null);


        ArrayAdapter listAdapter = new ArrayAdapter<String>(requireActivity(), R.layout.list_day,R.id.textviewDaysList, days);
        ListView listView = (ListView) dialogLayout.findViewById(R.id.remind_day_list);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageView imageView = view.findViewById(R.id.daysListCheckBox);
                if(!checkedInts.contains(i)){
                    imageView.setImageResource(R.drawable.ic_baseline_check_box_24);
                    checkedInts.add(i);
                }
                else {
                    imageView.setImageResource(R.drawable.ic_baseline_check_box_outline_blank_24);
                    checkedInts.remove(checkedInts.indexOf(i));
                }
            }
        });
        Spinner spinnerHours = dialogLayout.findViewById(R.id.remind_hour);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterHours = ArrayAdapter.createFromResource(requireActivity(),
                R.array.Hours, R.layout.spinner_select_day);

        // Specify the layout to use when the list of choices appears
        adapterHours.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerHours.setAdapter(adapterHours);
        // Set the dialog title

        builder.setView(dialogLayout)
                // Set the action buttons
                .setPositiveButton(getResources().getString(R.string.zatwierdz), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the selectedItems results somewhere
                        // or return them to the component that opened the dialog
//                        selectedDay = spinnerDays.getSelectedItem().toString();
                        for (int number: checkedInts) {
                            selectedDays.add(days[number]);
                        }
                        selectedHour = spinnerHours.getSelectedItem().toString();
                        listener.setDate(selectedDays,selectedHour);
                    }
                })
                .setNeutralButton(getResources().getString(R.string.pomin), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.skipSetDate();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.anuluj), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        builder.create().dismiss();
                    }
                })
               ;

        return builder.create();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DialogListener) getParentFragmentManager().findFragmentByTag("AddPlanFragment");;
            Log.d(TAG, "onAttach: "+listener);
        }
        catch (ClassCastException  e) {
            throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
        }
    }
}
