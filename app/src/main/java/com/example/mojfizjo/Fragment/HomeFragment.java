package com.example.mojfizjo.Fragment;



import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mojfizjo.MainActivity;
import com.example.mojfizjo.Models.PlanModel;
import com.example.mojfizjo.R;
import com.example.mojfizjo.UserSettings;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class HomeFragment extends Fragment {
    private static final String TAG = "cyk";
    public ArrayList<PlanModel> planModels;
    UserSettings userSettings;
    public View view;
    MainActivity mainActivity;
    TextView textViewPlans;
    Button buttonPlans;
    TextView textViewWater;
    Button buttonWater;
    TextView textViewSteps;
    Button buttonSteps;
    TextView textViewWorkoutDaysAmount;
    TextView textViewWaterDaysAmount;
    TextView textViewStepsAmount;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mainActivity = (MainActivity) getContext();
        assert mainActivity != null;
        planModels = mainActivity.planModels;
        userSettings = mainActivity.userSettings;

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        textViewPlans = view.findViewById(R.id.home_textView_plans);
        buttonPlans = view.findViewById(R.id.home_button_plans);

        textViewWater = view.findViewById(R.id.home_textView_water);
        buttonWater = view.findViewById(R.id.button_water);

        textViewSteps = view.findViewById(R.id.home_textView_steps);
        buttonSteps = view.findViewById(R.id.button_steps);

        textViewWorkoutDaysAmount = view.findViewById(R.id.home_textView_workouts);
        textViewWaterDaysAmount = view.findViewById(R.id.home_textView_water_days_amount);
        textViewStepsAmount = view.findViewById(R.id.home_textView_steps_amount);

        buttonPlans.setOnClickListener(view1 -> mainActivity.boottomNavigationview.setSelectedItemId(R.id.workout));

        buttonWater.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getResources().getString(R.string.potwierdz_operacje));
// Add the buttons
            builder.setPositiveButton(getResources().getString(R.string.zatwierdz), (dialog, id) -> db.collection("user_settings").whereEqualTo("userID", mainActivity.currentUser.getUid())
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("user_settings").document(document.getId())
                                        .update("waterDone", true,"waterDaysAmount",userSettings.getWaterDaysAmount()+1).addOnSuccessListener(unused -> {
                                            Log.d(TAG, "onSuccess: Udalo sie");
                                            userSettings.setWaterDone(true);
                                            userSettings.setWaterDaysAmount(userSettings.getWorkoutDaysAmount()+1);
                                            try{
                                                setTextViewWater();
                                                setTextViewWaterDaysAmount();
                                            }catch(NullPointerException e){
                                               Log.e(TAG, "User has invalid settings. Details: " + e.getLocalizedMessage());
                                            }
                                        });
                            }
                        }
                    }));
            builder.setNegativeButton(getResources().getString(R.string.anuluj), (dialog, id) -> {
                // User cancelled the dialog
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        try{
            setTextViewWater();
            setTextViewSteps();
            setTextViewWaterDaysAmount();
            setTextViewStepsAmount();
            setTextViewPlans();
            setTextViewWorkoutDaysAmount();
        }catch(NullPointerException e){
            Log.e(TAG, "User has invalid settings. Details: " + e.getLocalizedMessage());
        }
        return view;
    }

    void setTextViewPlans() {
        boolean hasPlansToday = false;
        StringBuilder days = new StringBuilder();
        String dayOfTheWeek = new SimpleDateFormat("EEEE").format(new Date());
        for (int i = 0; i < planModels.size(); i++) {
            Map<String, Boolean> remindDay = planModels.get(i).getRemindDay();
            if (remindDay.containsKey(dayOfTheWeek) && Boolean.FALSE.equals(remindDay.get(dayOfTheWeek))) {
                days.append(planModels.get(i).getPlanName());
                days.append(", ");
                hasPlansToday = true;
            }
        }
        if (hasPlansToday) {
            int length = days.length();
            days = days.replace(length - 2, length - 1, "");
            String temp = getResources().getString(R.string.plan_do_wykonania) + " " + days;
            textViewPlans.setText(temp);
        } else {
            textViewPlans.setText(getResources().getString(R.string.brak_planow_do_wykonania));
        }

    }

    void setTextViewWater () {
        if (userSettings.isNotifyAboutWater()) {
            if (userSettings.isWaterDone()) {
                textViewWater.setText(getResources().getString(R.string.picie_wody_zaliczone));
                buttonWater.setVisibility(View.GONE);
                buttonWater.setEnabled(false);
            } else {
                //dobranie odpowiedniego sufiksu
                String hydrationMessageSuffix;
                if (userSettings.getWaterLiters() % 10 >= 5 || userSettings.getWaterLiters() % 10 == 1 || (userSettings.getWaterLiters() <= 21 && userSettings.getWaterLiters() > 4)) {
                    hydrationMessageSuffix = getResources().getString(R.string.litrow_wody);
                } else {
                    hydrationMessageSuffix = getResources().getString(R.string.litry_wody);
                }
                //nadpisanie w wyjatkowym przypadku 1 litra
                if (userSettings.getWaterLiters() == 1) {
                    hydrationMessageSuffix = getResources().getString(R.string.litr_wody);
                }
                String hydrationMessageText = getResources().getString(R.string.do_wypicia) + " " + userSettings.getWaterLiters() + " " + hydrationMessageSuffix;
                textViewWater.setText(hydrationMessageText);
            }

        } else {
            textViewWater.setText("");
            textViewWater.setVisibility(View.GONE);
            buttonWater.setVisibility(View.GONE);
            buttonWater.setEnabled(false);
        }

    }

    void setTextViewSteps () {
        if (userSettings.isNotifyAboutSteps()) {
            if (userSettings.isStepsDone()) {
                textViewSteps.setText(getResources().getString(R.string.kroki_zaliczone));
            } else {
                //dobranie odpowiedniego sufiksu
                String stepsMessageSuffix;
                if (userSettings.getStepsNumber() % 10 >= 5 || userSettings.getStepsNumber() % 10 == 1 || (userSettings.getStepsNumber() <= 21 && userSettings.getStepsNumber() > 4)) {
                    stepsMessageSuffix = getResources().getString(R.string.krokow);
                } else {
                    stepsMessageSuffix = getResources().getString(R.string.kroki);
                }
                String stepsMessageText = getResources().getString(R.string.do_zrobienia) + " " + userSettings.getStepsNumber() + " " + stepsMessageSuffix;
                textViewSteps.setText(stepsMessageText);
            }
        } else {
            textViewSteps.setText("");
            textViewSteps.setVisibility(View.GONE);
            buttonSteps.setVisibility(View.GONE);
            buttonSteps.setEnabled(false);
        }
    }

    void setTextViewWorkoutDaysAmount () {
        if (userSettings.isNotifyAboutWorkout() && userSettings.getWorkoutDaysAmount() > 1) {
            String daysMessage = getResources().getString(R.string.trenujesz) + " " + userSettings.getWorkoutDaysAmount() + " " + getResources().getString(R.string.dni_pod_rzad);
            textViewWorkoutDaysAmount.setText(daysMessage);
        } else {
            textViewWorkoutDaysAmount.setText("");
            textViewWorkoutDaysAmount.setVisibility(View.GONE);
        }

    }

        void setTextViewWaterDaysAmount () {
        if (userSettings.isNotifyAboutWater() && userSettings.getWaterDaysAmount() > 1) {
            String daysMessage = getResources().getString(R.string.piles_wode) + " " + userSettings.getWaterDaysAmount() + " " + getResources().getString(R.string.dni_pod_rzad);
            textViewWaterDaysAmount.setText(daysMessage);
        } else {
            textViewWaterDaysAmount.setText("");
            textViewWaterDaysAmount.setVisibility(View.GONE);
        }

    }

        void setTextViewStepsAmount () {
        if (userSettings.isNotifyAboutSteps() && userSettings.getStepsAmount() > 0) {
            //dobranie odpowiedniego sufiksu
            String stepsMessageSuffix;
            if (userSettings.getStepsAmount() % 10 >= 5 || userSettings.getStepsAmount() % 10 == 1 || (userSettings.getStepsAmount() <= 21 && userSettings.getStepsAmount() > 4)) {
                stepsMessageSuffix = getResources().getString(R.string.krokow);
            } else {
                stepsMessageSuffix = getResources().getString(R.string.kroki);
            }
            String stepsTotalMessage = getResources().getString(R.string.wykonales_w_sumie) + " " + userSettings.getStepsAmount() + " " + stepsMessageSuffix;
            textViewStepsAmount.setText(stepsTotalMessage);
        } else {
            textViewStepsAmount.setText("");
            textViewStepsAmount.setVisibility(View.GONE);
        }

    }
}