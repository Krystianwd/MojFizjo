package com.example.mojfizjo.Models;

import java.util.ArrayList;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@AllArgsConstructor
public class PlanModelDB {

    @Getter @Setter
    String planId;
    @Getter @Setter
    String planName;
    @Getter @Setter
    ArrayList<ExerciseModel> exercises;
    @Getter @Setter
    Map<String,Boolean> remindDay;
    @Getter @Setter
    String remindHour;
    @Getter @Setter
    String userID;

}
