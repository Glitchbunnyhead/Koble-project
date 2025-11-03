package com.koble.koble.model;
public class StudentIdea {

    //Declaring class attributes
    private long ideaId;
    private long studentId;

    //Constructor of the class
    public StudentIdea(){}

    public StudentIdea(long idea, long student) {
        this.ideaId = idea;
        this.studentId = student;
    }

    public long getIdeaId() {
        return ideaId;
    }

    public void setIdeaId(long ideaId) {
        this.ideaId = ideaId;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    

}
