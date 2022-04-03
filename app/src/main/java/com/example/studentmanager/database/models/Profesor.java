package com.example.studentmanager.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "profesors",indices={@Index(value = {"emailProfesor"},unique = true)})
public class Profesor {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idProfesor")
    private int idProfesor;
    @ColumnInfo(name = "emailProfesor")
    private String emailProfesor;
    @ColumnInfo(name = "nameProfesor")
    private String nameProfesor;
    @ColumnInfo(name = "password")
    private String password;
    @Embedded
    private Diploma diploma;

    public Diploma getDiploma() {
        return diploma;
    }

    public void setDiploma(Diploma diploma) {
        this.diploma = diploma;
    }

    public Profesor(int idProfesor, String emailProfesor, String nameProfesor, String password, Diploma diploma) {
        this.idProfesor = idProfesor;
        this.emailProfesor = emailProfesor;
        this.nameProfesor = nameProfesor;
        this.password = password;
        this.diploma = diploma;
    }

    @Ignore
    public Profesor(String emailProfesor, String nameProfesor, String password, Diploma diploma) {
        this.emailProfesor = emailProfesor;
        this.nameProfesor = nameProfesor;
        this.password = password;
        this.diploma = diploma;
    }


    public int getIdProfesor() {
        return idProfesor;
    }

    public void setIdProfesor(int idProfesor) {
        this.idProfesor = idProfesor;
    }

    public String getEmailProfesor() {
        return emailProfesor;
    }

    public void setEmailProfesor(String emailProfesor) {
        this.emailProfesor = emailProfesor;
    }

    public String getNameProfesor() {
        return nameProfesor;
    }

    public void setNameProfesor(String nameProfesor) {
        this.nameProfesor = nameProfesor;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return nameProfesor;
    }

    public String getEmail() {
        return emailProfesor;
    }


    public void setName(String name) {
        this.nameProfesor = name;
    }

    public void setEmail(String email) {
        this.emailProfesor = email;
    }


    @Override
    public String toString() {
        return "Profesor{" +
                "idProfesor=" + idProfesor +
                ", emailProfesor='" + emailProfesor + '\'' +
                ", nameProfesor='" + nameProfesor + '\'' +
                ", accessCode=" + password +
                '}';
    }
}
