package com.koble.koble.model;

public abstract class Project {

    //Declaring class attributes
    private long id;
    private String timeline;
    private String linkExtension; // CORREÇÃO: era 'linlExtension'
    private String duration;
    private String image;
    private String complementHours;
    private boolean fellowship;
    private double fellowValue;
    private String requirements;
    private String title;
    private String subtitle;
    private String description;
    private String coordenator;
    private String type;
    private String typeId;

    //Constructor of the class

    public Project() {
    }

    //Getters and Setters of the class
    public Project(String complementHours, String coordenator, String description, String duration, double fellowValue, String image, String linkExtension, String requirements, boolean fellowship, String subtitle, String timeline, String title, String type, String typeId) {
        this.complementHours = complementHours;
        this.coordenator = coordenator;
        this.description = description;
        this.duration = duration;
        this.fellowValue = fellowValue;
        this.image = image;
        this.linkExtension = linkExtension; // Usando o nome corrigido
        this.requirements = requirements;
        this.fellowship = fellowship;
        this.subtitle = subtitle;
        this.timeline = timeline;
        this.title = title;
        this.type = type;
        this.typeId = typeId;
    }

    //Getters and Setters of the class

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

    public String getLinkExtension() { // Usando o nome corrigido
        return linkExtension;
    }

    public void setLinkExtension(String linkExtension) { // Usando o nome corrigido
        this.linkExtension = linkExtension;
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

    public boolean isfellowship() {
        return fellowship;
    }

    public void setfellowship(boolean fellowship) {
        this.fellowship = fellowship;
    }

    public double getfellowValue() {
        return fellowValue;
    }

    public void setfellowValue(double fellowValue) {
        this.fellowValue = fellowValue;
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

    public String getCoordenator() {
        return coordenator;
    }

    public void setCoordenator(String coordenator) {
        this.coordenator = coordenator;
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

    // Métodos Padrão (Recomendados para POJOs)
    // ----------------------------------------------------

    /**
     * Compara objetos Project. Dois projetos são considerados iguais se tiverem o mesmo ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return id == project.id;
    }

    /**
     * Gera um código hash consistente baseado no ID.
     */
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    /**
     * Retorna uma representação em String do objeto para facilitar o debugging.
     */
    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", timeline='" + timeline + '\'' +
                ", linkExtension='" + linkExtension + '\'' +
                ", duration='" + duration + '\'' +
                ", image='" + image + '\'' +
                ", complementHours='" + complementHours + '\'' +
                ", fellowship=" + fellowship +
                ", fellowValue=" + fellowValue +
                ", requirements='" + requirements + '\'' +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", description='" + description + '\'' +
                ", coordenator='" + coordenator + '\'' +
                ", type='" + type + '\'' +
                ", typeId='" + typeId + '\'' +
                '}';
    }
}