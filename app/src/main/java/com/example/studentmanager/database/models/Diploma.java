package com.example.studentmanager.database.models;

public class Diploma {
    private String diplomaName;
    private int grade;


    public Diploma(String diplomaName, int grade) {
        this.diplomaName = diplomaName;
        this.grade = grade;
    }

    public String getDiplomaName() {
        return diplomaName;
    }

    public void setDiplomaName(String diplomaName) {
        this.diplomaName = diplomaName;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "Diploma{" +
                "diplomaName='" + diplomaName + '\'' +
                ", grade=" + grade +
                '}';
    }
}
