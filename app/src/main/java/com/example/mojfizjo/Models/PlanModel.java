package com.example.mojfizjo.Models;

import java.util.ArrayList;

public class PlanModel {

    String planName;
    ArrayList<ExerciseModel> exerciseModels;

    public PlanModel() {
        //required empty public constructor
    }

    public PlanModel(String planName, ArrayList<ExerciseModel> exerciseModels) {
        this.planName = planName;
        this.exerciseModels = exerciseModels;
    }

    public String getPlanName() {
        return planName;
    }

    public boolean hasExercises(){ return !exerciseModels.isEmpty(); }

    public ArrayList<ExerciseModel> getExerciseModel() {
        return exerciseModels;
    }
    public void addExercise(ExerciseModel exerciseModel){
        this.exerciseModels.add(exerciseModel);
    }
}
