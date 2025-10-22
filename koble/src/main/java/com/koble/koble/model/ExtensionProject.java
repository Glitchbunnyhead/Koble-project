package com.koble.koble.model;

public class ExtensionProject extends Project{
    //Declaring class attributes
    private String targetAudience;
    private int slots;
    private String selectionProcess;

    //Constructor of the class
    public ExtensionProject(){super();}

    //Getters and Setters of the class

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public String getSelectionProcess() {
        return selectionProcess;
    }

    public void setSelectionProcess(String selectionProcess) {
        this.selectionProcess = selectionProcess;
    }






} 

