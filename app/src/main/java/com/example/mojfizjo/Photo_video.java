package com.example.mojfizjo;

public class Photo_video implements Comparable<Photo_video>{

    private String link;
    private String exerciseID;
    private int order;
    private String uID;

    public Photo_video(){
        // Required empty public constructor
    }

    public Photo_video(String link, String exerciseID, int order, String ID) {
        this.link = link;
        this.exerciseID = exerciseID;
        this.order = order;
        this.uID = ID;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getExerciseID() {
        return exerciseID;
    }

    public void setExerciseID(String exerciseID) {
        this.exerciseID = exerciseID;
    }

    public int getOrder() {
        return order;
    }

    public String getID() {
        return uID;
    }

    public void setID(String ID) {
        this.uID = ID;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    //sortowanie wzgledem kolejnosci
    @Override
    public int compareTo(Photo_video other_photo_video) {
        return this.getOrder() - other_photo_video.getOrder();
    }
}
