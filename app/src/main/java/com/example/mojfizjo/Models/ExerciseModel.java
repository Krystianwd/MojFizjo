package com.example.mojfizjo.Models;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;

public class ExerciseModel implements Serializable {
    String exerciseName;
    DocumentReference exercise;
    int sets;
    String time;

    public ExerciseModel() {
    }

    public ExerciseModel(String exerciseName, DocumentReference exercise, int sets, String time) {
        this.exerciseName = exerciseName;
        this.exercise = exercise;
        this.sets = sets;
        this.time = time;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public DocumentReference getExercise() {
        return exercise;
    }

    public int getSets() {
        return sets;
    }

    public String getTime() {
        return time;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public void setExercise(DocumentReference exercise) {
        this.exercise = exercise;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
