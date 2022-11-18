package com.example.mojfizjo.Fragment;


import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.mojfizjo.MainActivity;
import com.example.mojfizjo.Models.PlanModel;
import com.example.mojfizjo.R;
import com.example.mojfizjo.UserSettings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    TextView textViewWorkoutDaysAmount;
    TextView textViewWaterDaysAmount;
    TextView textViewStepsAmount;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mainActivity = (MainActivity) getContext();
        planModels = mainActivity.planModels;
        userSettings = mainActivity.userSettings;
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        textViewPlans = view.findViewById(R.id.home_textView_plans);
        buttonPlans = view.findViewById(R.id.home_button_plans);

        textViewWater = view.findViewById(R.id.home_textView_water);
        buttonWater = view.findViewById(R.id.button_water);

        textViewSteps = view.findViewById(R.id.home_textView_steps);

        textViewWorkoutDaysAmount = view.findViewById(R.id.home_textView_workouts);

        textViewWaterDaysAmount = view.findViewById(R.id.home_textView_water_days_amount);

        textViewStepsAmount = view.findViewById(R.id.home_textView_steps_amount);
        buttonPlans.setOnClickListener(view1 -> {
            mainActivity.boottomNavigationview.setSelectedItemId(R.id.workout);
        });
        buttonWater.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Potwierdź operacje");
// Add the buttons
            builder.setPositiveButton("Zatwierdż", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    db.collection("user_settings").whereEqualTo("userID", mainActivity.currentUser.getUid())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            db.collection("user_settings").document(document.getId())
                                                    .update("waterDone", true,"waterDaysAmount",userSettings.getWaterDaysAmount()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.d(TAG, "onSuccess: Udalo sie");
                                                            userSettings.setWaterDone(true);
                                                            userSettings.setWaterDaysAmount(userSettings.getWorkoutDaysAmount()+1);
                                                            setTextViewWater();
                                                            setTextViewWaterDaysAmount();
                                                        }
                                                    });
                                        }
                                    }
                                }
                            });
                }
            });
            builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        setTextViewPlans();
        setTextViewWater();
        setTextViewSteps();
        setTextViewWorkoutDaysAmount();
        setTextViewWaterDaysAmount();
        setTextViewStepsAmount();
        return view;
    }

    void setTextViewPlans() {
        boolean hasPlansToday = false;
        StringBuilder days = new StringBuilder();
        String dayOfTheWeek = new SimpleDateFormat("EEEE").format(new Date());
        for (int i = 0; i < planModels.size(); i++) {
            Map<String, Boolean> remindDay = planModels.get(i).getRemindDay();
            if (remindDay.containsKey(dayOfTheWeek) && remindDay.get(dayOfTheWeek) == false) {
                days.append(planModels.get(i).getPlanName());
                days.append(", ");
                hasPlansToday = true;
            }
        }
        if (hasPlansToday) {
            int length = days.length();
            days = days.replace(length - 2, length - 1, "");
            textViewPlans.setText("Masz dzisiaj do wykonania plan: " + days);
        } else {
            textViewPlans.setText("Nie masz dzisiaj żadnych treningów do wykonania");
        }

    }

    void setTextViewWater() {
        if (userSettings.isNotifyAboutWater()) {
            if (userSettings.isWaterDone()) {
                textViewWater.setText("Picie wody na dzisiaj zaliczone!");
                buttonWater.setVisibility(View.GONE);
                buttonWater.setEnabled(false);
            } else {
                if (userSettings.getWaterLiters() >= 5) {
                    textViewWater.setText("Masz dzisiaj do wypicia " + userSettings.getWaterLiters() + " litrów wody!");
                } else if (userSettings.getWaterLiters() > 2) {
                    textViewWater.setText("Masz dzisiaj do wypicia " + userSettings.getWaterLiters() + " litry wody!");
                } else if (userSettings.getWaterLiters() == 1) {
                    textViewWater.setText("Masz dzisiaj do wypicia " + userSettings.getWaterLiters() + " litr wody!");
                }
            }

        } else {
            textViewWater.setText("");
            textViewWater.setVisibility(View.GONE);
            buttonWater.setVisibility(View.GONE);
            buttonWater.setEnabled(false);
        }

    }

    void setTextViewSteps() {
        if (userSettings.isNotifyAboutSteps()) {
            if (userSettings.isStepsDone()) {
                textViewSteps.setText("Kroki wody na dzisiaj zaliczone");
            } else {
                textViewSteps.setText("Masz dzisiaj do zrobienia " + userSettings.getStepsNumber() + " kroków!");
            }
        } else {
            textViewSteps.setText("");
            textViewSteps.setVisibility(View.GONE);

        }
    }

    void setTextViewWorkoutDaysAmount() {
        if (userSettings.isNotifyAboutWorkout()) {
            if (userSettings.getWorkoutDaysAmount() > 1) {
                textViewWorkoutDaysAmount.setText("Trenujesz " + userSettings.getWorkoutDaysAmount() + " dni pod rząd!");
            } else {
                textViewWorkoutDaysAmount.setText("");
                textViewWorkoutDaysAmount.setVisibility(View.GONE);
            }
        }

    }

    void setTextViewWaterDaysAmount() {
        if (userSettings.isNotifyAboutWater()) {
            if (userSettings.getWaterDaysAmount() > 1) {
                textViewWaterDaysAmount.setText("Pamiętałeś o piciu wody " + userSettings.getWaterDaysAmount() + " dni pod rząd!");
            } else {
                textViewWaterDaysAmount.setText("");
                textViewWaterDaysAmount.setVisibility(View.GONE);
            }
        }

    }

    void setTextViewStepsAmount() {
        if (userSettings.isNotifyAboutSteps()) {
            if (userSettings.getStepsAmount() > 0) {
                textViewStepsAmount.setText("Wykonałeś w sumie " + userSettings.getStepsAmount() + " kroków!");
            } else {
                textViewStepsAmount.setText("");
                textViewStepsAmount.setVisibility(View.GONE);
            }
        }

    }
}