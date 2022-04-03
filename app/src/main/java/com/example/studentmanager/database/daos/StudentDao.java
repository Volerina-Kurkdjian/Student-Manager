package com.example.studentmanager.database.daos;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentmanager.database.models.Student;

import java.util.List;

@Dao
public interface StudentDao {

    @Query("select * from students")
    List<Student> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Student student);

    @Update
    int update(Student student);

    @Delete
    int delete(Student student);

    @Query("select * from students where emailStudent=:email")
    Student getStudent(String email);

    @Query("select * from students where idStud=:id")
    Student getStudentByID(int id);
}
