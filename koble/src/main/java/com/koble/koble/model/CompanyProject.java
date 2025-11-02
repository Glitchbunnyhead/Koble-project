package com.koble.koble.model;

public class CompanyProject {

    //Declaring class attributes
    private long companyId;
    private long projectId;

    //Constructor of the class
    public CompanyProject(){}

    public CompanyProject(long companyId, long projectId) {
        this.companyId = companyId;
        this.projectId = projectId;
    }

    public long getCompanyId() {
        return companyId;
    }   
    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }
    public long getProjectId() {
        return projectId;
    }
    public void setProjectId(long projectId) {
        this.projectId = projectId;}
   
}
