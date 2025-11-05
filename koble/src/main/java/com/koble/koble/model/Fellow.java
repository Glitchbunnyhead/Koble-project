package com.koble.koble.model;

import java.util.Date;

public class Fellow {

    //Declaring class attributes
    private long studentId;
    private long projectId;
    private String cpf;
    private String lattesCurriculum;

    //Constructor of the class
    public Fellow(){}

    public Fellow(long studentId,long projectId, String cpf, String lattesCurriculum) {
        this.studentId = studentId;
        this.projectId = projectId;
        this.cpf = cpf;
        this.lattesCurriculum = lattesCurriculum;
    }

    //Getters and Setters of the class
    public long getStudentId() {
        return studentId;
    }
    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }
    public long getProjectId() {
        return projectId;
    }
    public void setProjectId(long projectId) {             
        this.projectId = projectId;
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

}