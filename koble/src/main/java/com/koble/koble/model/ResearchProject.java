package com.koble.koble.model;

// Make sure to import the Project class if it's in another package
// import your.package.Project;

public class ResearchProject extends Project {

    //Declaring class attributes
    private String aim;
    private String justification;
    private String course;

    //Constructor of the class
    public ResearchProject(){super();}

    public ResearchProject(String aim, String course, String justification) {
        super();
        this.aim = aim;
        this.course = course;
        this.justification = justification;
    }

    //Getters and Setters of the class

    public String getAim() {
        return aim;
    }

    public void setAim(String aim) {
        this.aim = aim;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getCourses() {
        return course;
    }

    public void setCourses(String course) {
        this.course = course;
    }





}
