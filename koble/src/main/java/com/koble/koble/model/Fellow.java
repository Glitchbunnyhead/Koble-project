package com.koble.koble.model;

import java.util.Date;

public class Fellow {

    //Declaring class attributes
    private Student student;
    private Project project;
    private String cpf;
    private String lattesCurriculum;
    private Date birthDate;

    //Constructor of the class
    public Fellow(){}

    public Fellow(Student student, Project project, String cpf, String lattesCurriculum, Date birthDate) {
        this.student = student;
        this.project = project;
        this.cpf = cpf;
        this.lattesCurriculum = lattesCurriculum;
        this.birthDate = birthDate;
    }

    //Getters and Setters of the class
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getLattesCurriculum() {
        return lattesCurriculum;
    }

    public void setLattesCurriculum(String lattesCurriculum) {
        this.lattesCurriculum = lattesCurriculum;
    }

  public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    
}
