package com.koble.koble.model;

import java.util.Objects;

public class StudentIdea {

    private long ideaId;
    private long studentId;

    public StudentIdea() {}

    public StudentIdea(long ideaId, long studentId) {
        this.ideaId = ideaId;
        this.studentId = studentId;
    }

    public long getIdeaId() {
        return ideaId;
    }

    public void setIdeaId(long ideaId) {
        this.ideaId = ideaId;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }



    @Override
    public String toString() {
        return "StudentIdea{" +
                "ideaId=" + ideaId +
                ", studentId=" + studentId +
                '}';
    }
}
