package com.example.studentmanager.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import com.example.studentmanager.database.models.StudentSubjectCrossRef;
import com.example.studentmanager.database.utils.StudentWithSubjects;
import com.example.studentmanager.database.utils.SubjectWithStudents;

import java.util.List;

@Dao
public interface StudentSubjectCrossrefDao {

    @Transaction//nu l am folosit
    @Query("select * from subjects")
    List<SubjectWithStudents> getSubjectsWithStudents();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(StudentSubjectCrossRef studentSubjectCrossRef);

    @Update
    int update(StudentSubjectCrossRef studentSubjectCrossRef);

    @Delete
    int delete(StudentSubjectCrossRef studentSubjectCrossRef);

    @Transaction
    @Query("select * from students")
    List<StudentWithSubjects> getStudentWithSubjects();

    @Query("select * from studentSubjectCross where idSubject=:id")
    List<StudentSubjectCrossRef> getStudentsRefInSubject(int id);

    @Query("select * from studentSubjectCross where idStud=:id")
    List<StudentSubjectCrossRef> getSubjectsRefForStudent(int id);

    @Query("select * from studentSubjectCross where idStud=:idStud and idSubject=:idSubject")
    StudentSubjectCrossRef getRef(int idStud, int idSubject);
}
