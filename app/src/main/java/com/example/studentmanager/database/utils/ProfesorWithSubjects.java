package com.example.studentmanager.database.utils;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.studentmanager.database.models.Profesor;
import com.example.studentmanager.database.models.Subject;

import java.util.List;

public class ProfesorWithSubjects {

    @Embedded
    public Profesor profesor;

    @Relation(
            parentColumn = "idProfesor",
            entityColumn = "idProfesorSubject")
    public List<Subject> subjects;

}
