package com.example.studentmanager.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"idStud", "idSubject"}, tableName = "studentSubjectCross")
public class StudentSubjectCrossRef {

    @ColumnInfo(name = "idStud")
    private int idStud;
    @ColumnInfo(name = "idSubject")
    private int idSubject;
    @ColumnInfo(name = "grade")
    private double grade;


    public StudentSubjectCrossRef(int idStud, int idSubject, double grade) {
        this.idStud = idStud;
        this.idSubject = idSubject;
        this.grade = grade;
    }

    public int getIdStud() {
        return idStud;
    }

    public void setIdStud(int idStud) {
        this.idStud = idStud;
    }

    public int getIdSubject() {
        return idSubject;
    }

    public void setIdSubject(int idSubject) {
        this.idSubject = idSubject;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }
}
