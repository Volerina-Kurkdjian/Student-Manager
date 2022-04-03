package com.example.studentmanager.database.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.studentmanager.database.utils.DateConverter;

import java.util.Date;

@Entity(tableName = "subjects",indices = {@Index(value = {"subjectName"},unique = true)})
public class Subject implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idSubject")
    private  int idSubject;
    @ColumnInfo(name = "subjectName")
    private String subjectName;
    @ColumnInfo(name = "subjectDateExam")
    private Date subjectDateExam;
    @ColumnInfo(name = "idProfesorSubject")
    private int idProfesorSubject;
    @ColumnInfo(name = "numberOfTests")
    private int numberOfTests;


    @Ignore
    public Subject(String subjectName, Date subjectDateExam, int numberOfTests) {
        this.subjectName = subjectName;
        this.subjectDateExam = subjectDateExam;
        this.numberOfTests = numberOfTests;
    }

    public Subject(int idSubject, String subjectName, Date subjectDateExam, int idProfesorSubject, int numberOfTests) {
        this.idSubject = idSubject;
        this.subjectName = subjectName;
        this.subjectDateExam = subjectDateExam;
        this.idProfesorSubject = idProfesorSubject;
        this.numberOfTests = numberOfTests;
    }

    @Ignore
    public Subject(String subjectName, Date subjectDateExam, int idProfesorSubject, int numberOfTests) {
        this.subjectName = subjectName;
        this.subjectDateExam = subjectDateExam;
        this.idProfesorSubject = idProfesorSubject;
        this.numberOfTests = numberOfTests;
    }

    private Subject(Parcel source)
    {
        idSubject=source.readInt();
        subjectName=source.readString();
        subjectDateExam=DateConverter.fromString(source.readString());
        idProfesorSubject=source.readInt();
        numberOfTests=source.readInt();

    }

    public int getNumberOfTests() {
        return numberOfTests;
    }

    public void setNumberOfTests(int numberOfTests) {
        this.numberOfTests = numberOfTests;
    }

    public int getIdProfesorSubject() {
        return idProfesorSubject;
    }

    public void setIdProfesorSubject(int idProfesorSubject) {
        this.idProfesorSubject = idProfesorSubject;
    }

    public static  Creator<Subject> CREATOR=new Creator<Subject>() {
        @Override
        public Subject createFromParcel(Parcel source) {
            return new Subject(source);
        }

        @Override
        public Subject[] newArray(int size) {
            return new Subject[size];
        }
    };

    public Date getSubjectDateExam() {
        return subjectDateExam;
    }

    public void setSubjectDateExam(Date subjectDateExam) {
        this.subjectDateExam = subjectDateExam;
    }

    public int getIdSubject() {
        return idSubject;
    }

    public void setIdSubject(int idSubject) {
        idSubject = idSubject;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "idSubject=" + idSubject +
                ", subjectName='" + subjectName + '\'' +
                ", subjectDateExam=" + subjectDateExam +
                ", idProfesorSubject=" + idProfesorSubject +
                ", numberOfTests=" + numberOfTests +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(idSubject);
        dest.writeString(subjectName);
        dest.writeString(DateConverter.fromDate(subjectDateExam));
        dest.writeInt(idProfesorSubject);
        dest.writeInt(numberOfTests);


    }
}
