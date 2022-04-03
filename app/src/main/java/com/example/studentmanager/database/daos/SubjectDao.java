package com.example.studentmanager.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentmanager.database.models.Subject;

import java.util.List;
@Dao
public interface SubjectDao {

    @Query("select * from subjects")
    List<Subject> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Subject subject);

    @Update
    int update(Subject subject);

    @Delete
    int delete(Subject subject);

    @Query("select * from subjects where idProfesorSubject=:id")
    List<Subject> getProfesorSubjects(int id);

    @Query("select * from subjects where idSubject=:id")
    Subject getSubjectByID(int id);
}
