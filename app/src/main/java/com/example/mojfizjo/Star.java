package com.example.mojfizjo;

//todo implement lombok instead of getters, setters and constructors
public class Star {

    private String exercise;
    private String userID;
    private String uID;

    public Star() {
        // Required empty public constructor
    }

    public Star(String exercise, String userID, String ID) {
        this.exercise = exercise;
        this.userID = userID;
        this.uID = ID;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getID() {
        return uID;
    }

    public void setID(String ID) {
        this.uID = ID;
    }
}
