package com.example.studentmanager.database.utils;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.studentmanager.database.models.Student;
import com.example.studentmanager.database.models.StudentSubjectCrossRef;
import com.example.studentmanager.database.models.Subject;

import java.util.List;

public class StudentWithSubjects {
    @Embedded
    public Student student;
    @Relation(
            parentColumn = "idStud",
            entityColumn = "idSubject",
            associateBy = @Junction(StudentSubjectCrossRef.class)
    )
    public List<Subject> subjects;
}
