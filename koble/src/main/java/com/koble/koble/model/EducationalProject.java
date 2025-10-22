package com.koble.koble.model;

public class EducationalProject extends Project {

    //Declaring class attributes
    private int slots;
    private String justification;
    private String course;

    //Constructor of the class
    public EducationalProject(){super();}

    //Getters and Setters of the class

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
