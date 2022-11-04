package com.example.mojfizjo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class UserSettings {

    @Getter @Setter private String userID;
    @Getter @Setter private boolean enableNotifications;
    @Getter @Setter private String notificationTime;
    @Getter @Setter private boolean notifyAboutSteps;
    @Getter @Setter private boolean notifyAboutWater;
    @Getter @Setter private int stepsNumber;
    @Getter @Setter private int waterLiters;
    @Getter @Setter private String uID;

}
