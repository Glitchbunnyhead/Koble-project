package com.koble.koble.model;

public class ResearchProject extends Project {
    private String objective;
    private String justification;
    private String discipline;


    public ResearchProject(){super();
        this.setType("Research");
    }

    public ResearchProject(String objective, String justification, String discipline) {
        super();
        this.setType("Research");
        this.objective = objective;
        this.justification = justification;
        this.discipline = discipline;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification =  justification;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    

    

}
