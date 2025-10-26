package com.koble.koble.model;

import java.util.Date;

public class Student {

    //Declaring class attributes
    private long id;
    private String registration;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private Date birthDate;

    //Constructor of the class
    public Student(){}

    public Student(String email, String name, String password, String phoneNumber, String registration, Date birthDate) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.registration = registration;
        this.birthDate = birthDate;
    }

    //Getters and Setters of the class

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

      public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

}
