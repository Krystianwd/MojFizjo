package com.example.mojfizjo;

public class Category {

    private String name;
    private String iconLink;
    private String uID;

    public Category(){
        // Required empty public constructor
    }

    public Category(String ID, String iconLink, String name){
        this.uID = ID;
        this.iconLink = iconLink;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconLink() {
        return iconLink;
    }

    public void setIconLink(String iconLink) {
        this.iconLink = iconLink;
    }

    public String getID() {
        return uID;
    }

    public void setID(String ID) {
        this.uID = ID;
    }
}
