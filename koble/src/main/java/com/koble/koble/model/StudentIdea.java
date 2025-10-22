package com.koble.koble.model;
public class StudentIdea {

    //Declaring class attributes
    private Idea idea;
    private Student student;

    //Constructor of the class
    public StudentIdea(){}

    public StudentIdea(Idea idea, Student student) {
        this.idea = idea;
        this.student = student;
    }

    //Getters and Setters of the class
    public Idea getIdea() {
        return idea;
    }

    public void setIdea(Idea idea) {
        this.idea = idea;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
