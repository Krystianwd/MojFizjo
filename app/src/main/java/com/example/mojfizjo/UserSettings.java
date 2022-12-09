package com.example.mojfizjo;

import java.util.Date;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class UserSettings {

    @Getter @Setter private String userID;
    @Getter @Setter private boolean notifyAboutWorkout;
    @Getter @Setter private int notificationHours;
    @Getter @Setter private boolean notifyAboutSteps;
    @Getter @Setter private boolean notifyAboutWater;
    @Getter @Setter private int stepsNumber;
    @Getter @Setter private int stepsAmount;
    @Getter @Setter private int waterDaysAmount;
    @Getter @Setter private int workoutDaysAmount;
    @Getter @Setter private int waterLiters;
    @Getter @Setter private String uID;
    @Getter @Setter private boolean stepsDone;
    @Getter @Setter private boolean waterDone;
    @Getter @Setter private boolean workoutDone;
    @Getter @Setter private boolean workoutInc;
    @Getter @Setter private Date lastUpdate;


}
