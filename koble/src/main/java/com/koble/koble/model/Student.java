package com.koble.koble.model;

import java.util.Date;

public class Student {

    //Declaring class attributes
    private long id;
    private String registration;
    private String name;
    private String emial;
    private String passsword;
    private String phoneNumber;
    private Date birthDate;

    //Constructor of the class
    public Student(){}

    public Student(String emial, String name, String passsword, String phoneNumber, String registration, Date birthDate) {
        this.emial = emial;
        this.name = name;
        this.passsword = passsword;
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

    public String getEmial() {
        return emial;
    }

    public void setEmial(String emial) {
        this.emial = emial;
    }

    public String getPasssword() {
        return passsword;
    }

    public void setPasssword(String passsword) {
        this.passsword = passsword;
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
