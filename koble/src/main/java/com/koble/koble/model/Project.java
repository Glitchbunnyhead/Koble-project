package com.koble.koble.model;


public abstract class Project {

    private long id;
    private String title;
    private String subtitle;
    private String coordinator;
    private String description;
    private String timeline;
    private String externalLink; 
    private String duration;
    private String image;
    private String complementHours;
    private boolean scholarshipAvailable;
    private String scholarshipType;
    private double salary;
    private String requirements;
    private int scholarshipQuantity;
    private String type;
    private String typeId;


    public Project() {
    }

    public Project(String complementHours, String coordinator, String description, String duration, double salary, String image, String externalLink, String requirements, boolean scholarship, String subtitle, String timeline, String title, String type, String typeId,String scholarshipType, int scholarshipQuantity) {
        this.complementHours = complementHours;
        this.coordinator = coordinator;
        this.description = description;
        this.duration = duration;
        this.salary = salary;
        this.image = image;
        this.externalLink = externalLink; 
        this.requirements = requirements;
        this.scholarshipAvailable = scholarship;
        this.subtitle = subtitle;
        this.timeline = timeline;
        this.title = title;
        this.type = type;
        this.typeId = typeId;
        this.scholarshipType = scholarshipType;
        this.scholarshipQuantity = scholarshipQuantity;
    }

 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return id == project.id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public String getExternalLink() {
        return externalLink;
    }

    public void setExternalLink(String linkExtension) {
        this.externalLink = linkExtension;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getComplementHours() {
        return complementHours;
    }

    public void setComplementHours(String complementHours) {
        this.complementHours = complementHours;
    }

   
    public boolean isScholarshipAvailable() {
        return scholarshipAvailable;
    }

    public void setScholarshipAvailable(boolean scholarshipAvailable) {
        this.scholarshipAvailable = scholarshipAvailable;
    }

    public String getScholarshipType() {
        return scholarshipType;
    }

    public void setScholarshipType(String scholarshipType) {
        this.scholarshipType = scholarshipType;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public int getScholarshipQuantity() {
        return scholarshipQuantity;
    }

    public void setScholarshipQuantity(int scholarshipQuantity) {
        this.scholarshipQuantity = scholarshipQuantity;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(String coordenator) {
        this.coordinator = coordenator;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }


    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }


    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", timeline='" + timeline + '\'' +
                ", externalLink='" + externalLink + '\'' +
                ", duration='" + duration + '\'' +
                ", image='" + image + '\'' +
                ", complementHours='" + complementHours + '\'' +
                ", scholarshipAvailble=" + scholarshipAvailable +
                ", salary=" + salary +
                ", requirements='" + requirements + '\'' +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", description='" + description + '\'' +
                ", coordinator='" + coordinator + '\'' +
                ", type='" + type + '\'' +
                ", typeId='" + typeId + '\'' +
                '}';
    }

   
}