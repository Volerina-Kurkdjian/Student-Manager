package com.example.studentmanager.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.studentmanager.database.models.Profesor;
import com.example.studentmanager.database.models.Student;
import com.example.studentmanager.database.utils.ProfesorWithSubjects;

import java.util.List;

@Dao
public interface ProfesorDao {

    @Query("select * from profesors")
    List<Profesor> getAll();

    @Insert(onConflict= OnConflictStrategy.IGNORE)
    long insert(Profesor profesor);

    @Update
    int update(Profesor profesor);

    @Delete
    int delete(Profesor profesor);

    @Transaction
    @Query("select * from profesors")
    List<ProfesorWithSubjects> getAllProfesorWithSubjects();

    @Query("select * from profesors where emailProfesor=:email")
    Profesor getProfesor(String email);

    @Query("select * from profesors where idProfesor=:id")
    Profesor getProfessorByID(int id);
}
