package com.koble.koble.model;
import java.util.Date;

public class Participant {
    //Declaring class attributes
    private long id;
    private String name;
    private String cpf;
    private String phoneNumber;
    private Date birthDate;
    private Student isStudent;
    private Teacher isTeacher;
    private ExternalPerson isExternalPerson;
    private Project project;


    //Constructor of the class
    public Participant(){}

    public Participant(String name, String cpf, String phoneNumber, Date birthDate, Project project) {
        this.name = name;
        this.cpf = cpf;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.project = project;
        this.isStudent = null;
        this.isTeacher = null;
        this.isExternalPerson = null;

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

    public Date getBirthDate(){
        return birthDate;
    }

    public void setBirthDate(Date birthDate){
        this.birthDate = birthDate;
    }

    public Student getIsStudent(){
        return isStudent;
    }
    
    public void setIsStudent(Student isStudent){
        this.isStudent = isStudent;
    }

    public Teacher getIsTeacher(){
        return isTeacher;
    }
    
    public void setIsTeacher(Teacher isTeacher){
        this.isTeacher = isTeacher;
    }

    public ExternalPerson getIsExternalPerson(){
        return isExternalPerson;
    }

    public void setIsExternalPerson(ExternalPerson isExternalPerson){
        this.isExternalPerson = isExternalPerson;

    }

    public Project getProject(){
        return project;
    }

    public void setProject(Project project){
        this.project = project;
    }
}
