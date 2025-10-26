package com.koble.koble.model;

public class TeacherProject {

    //Declaring class attributes
    private Teacher teacher;
    private Project project;

    //Constructor of the class
    public TeacherProject(){}

    public TeacherProject(Teacher teacher, Project project) {
        this.teacher = teacher;
        this.project = project;
    }

    //Getters and Setters of the class
    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
