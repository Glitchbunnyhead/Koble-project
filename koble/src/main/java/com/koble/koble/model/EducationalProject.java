package com.koble.koble.model;

public class EducationalProject extends Project {

    private int slots;
    private String justification;
    private String course;

    public EducationalProject(){super();}

    public EducationalProject(int slots, String justification, String course) {
        super();
        this.setType("Educational");
        this.slots = slots;
        this.justification = justification;
        this.course = course;
    }
    
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