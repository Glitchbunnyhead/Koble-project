package com.koble.koble.model;
public class TeacherIdea {

    //Declaring class attributes
    private Idea idea;
    private Teacher teacher;

    //Constructor of the class
    public TeacherIdea(){}

    public TeacherIdea(Idea idea, Teacher teacher) {
        this.idea = idea;
        this.teacher = teacher;
    }

    //Getters and Setters of the class
    public Idea getIdea() {
        return idea;
    }

    public void setIdea(Idea idea) {
        this.idea = idea;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    
    
    
}
