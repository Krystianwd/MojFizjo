package com.example.mojfizjo.Models;

import java.util.ArrayList;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@AllArgsConstructor
public class PlanModel {

    @Getter @Setter
    String planName;
    @Getter @Setter
    ArrayList<ExerciseModel> exerciseModels;
    @Getter @Setter
    Map<String,Boolean> remindDay;
    @Getter @Setter
    String remindHour;


    public boolean hasExercises(){ return !exerciseModels.isEmpty(); }

}
