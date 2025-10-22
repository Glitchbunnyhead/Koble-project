package com.koble.koble.model;

public class CompanyProject {

    //Declaring class attributes
    private Company company;
    private  Project project;

    //Constructor of the class
    public CompanyProject(){}

    public CompanyProject(Company company, Project project) {
        this.company = company;
        this.project = project;
    }

    //Getters and Setters of the class
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

   
}
