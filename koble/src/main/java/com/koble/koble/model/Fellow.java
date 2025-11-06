package com.koble.koble.model;


public class Fellow {

    private long studentId;
    private long projectId;
    private String cpf;
    private String lattesCurriculum;

    public Fellow(){}

    public Fellow(long studentId,long projectId, String cpf, String lattesCurriculum) {
        this.studentId = studentId;
        this.projectId = projectId;
        this.cpf = cpf;
        this.lattesCurriculum = lattesCurriculum;
    }

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