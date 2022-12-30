package com.example.mojfizjo;

public class Exercise {

    private String name;
    private String category;
    private String content;
    private int duration;
    private boolean viewedInCategory;
    private String uID;

    public Exercise(){
        // Required empty public constructor
    }

    public Exercise(String category, String name, boolean viewedInCategory, int duration, String content, String uID) {
        this.uID = uID;
        this.name = name;
        this.category = category;
        this.content = content;
        this.duration = duration;
        this.viewedInCategory = viewedInCategory;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isViewedInCategory() {
        return viewedInCategory;
    }
    public void setViewedInCategory(boolean viewedInCategory) {
        this.viewedInCategory = viewedInCategory;
    }

    public String getID() {
        return uID;
    }
    public void setID(String uID) {
        this.uID = uID;
    }
}
