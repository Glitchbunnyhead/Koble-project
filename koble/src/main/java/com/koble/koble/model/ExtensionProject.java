package com.koble.koble.model;

public class ExtensionProject extends Project{
    private String targetAudience ;
    private int slots;
    private String selectionProcess;

    public ExtensionProject(){super();
        this.setType("Extension");

    }

    public ExtensionProject(String targetAudience, int slots, String selectionProcess) {
        super();
        this.setType("Extension");
        this.targetAudience = targetAudience;
        this.slots = slots;
        this.selectionProcess = selectionProcess;
    }


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

