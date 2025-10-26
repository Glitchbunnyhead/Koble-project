package com.koble.koble.model;
public class Idea {

    //Declaring class attributes
    private long id;
    private String proposer;
    private String targetAudience;
    private String justification;
    private String title;
    private String aim;
    private  String subtitle;
    private String area;
    private String description;
    private String type;
    private Teacher teacher;
    private Student student;

    //Constructor of the class
    public Idea(){}

    public Idea(String proposer, String targetAudience, String justification, String title, String aim,
            String subtitle, String area, String description, String type, Teacher teacher, Student student) {
        this.proposer = proposer;
        this.targetAudience = targetAudience;
        this.justification = justification;
        this.title = title;
        this.aim = aim;
        this.subtitle = subtitle;
        this.area = area;
        this.description = description;
        this.type = type;
        this.teacher = teacher;
        this.student = student;
    }

    //Getters and Setters of the class
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProposer() {
        return proposer;
    }

    public void setProposer(String proposer) {
        this.proposer = proposer;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAim() {
        return aim;
    }

    public void setAim(String aim) {
        this.aim = aim;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    
    
}
