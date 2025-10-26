package com.koble.koble.model;
public class Teacher {

    //Declaring class attributes
    private long id;
    private String siape;
    private String email;
    private String name;
    private String password;
    private String phoneNumber;

    //Constructor of the class
    public Teacher(){

    }

    public Teacher( String siape, String email, String name, String password, String phoneNumber) {
        this.siape = siape;
        this.email = email;
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    //Getters and Setters of the class

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSiape() {
        return siape;
    }

    public void setSiape(String siape) {
        this.siape = siape;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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







}
