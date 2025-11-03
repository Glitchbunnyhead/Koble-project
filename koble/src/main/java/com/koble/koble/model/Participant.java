package com.koble.koble.model;
import java.util.Date;

public class Participant {
    //Declaring class attributes
    private long id;
    private String name;
    private String cpf;
    private String phoneNumber;
    private String role;
    private long projectId;


    //Constructor of the class
    public Participant(){}

    public Participant(String name, String cpf, String phoneNumber, long projectId, String role) {
        this.name = name;
        this.cpf = cpf;
        this.phoneNumber = phoneNumber;
        this.projectId = projectId;
        this.role = role;

    }

    //Getters and Setters of the class
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }
}
