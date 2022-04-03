package com.example.studentmanager.database.repositories;

import android.content.Context;

import com.example.studentmanager.async.AsyncTaskRunner;
import com.example.studentmanager.database.DatabaseManager;
import com.example.studentmanager.database.daos.StudentDao;
import com.example.studentmanager.database.daos.StudentSubjectCrossrefDao;
import com.example.studentmanager.database.daos.SubjectDao;
import com.example.studentmanager.database.models.Student;
import com.example.studentmanager.database.models.Subject;
import com.example.studentmanager.database.utils.ProfesorWithSubjects;
import com.example.studentmanager.database.utils.StudentWithSubjects;
import com.example.studentmanager.database.utils.SubjectWithStudents;

import java.util.ArrayList;
import java.util.List;

public class SubjectRepository {

    private SubjectDao subjectDao;
    private List<Subject> subjectList=new ArrayList<>();
    private final AsyncTaskRunner taskRunner;
    private  List<SubjectWithStudents> subjectsWithStudents =new ArrayList<>();
    private StudentSubjectCrossrefDao studentSubjectCrossrefDao;

    public SubjectRepository(Context context)
    {
        DatabaseManager databaseManager=DatabaseManager.getInstance(context);
        subjectDao =databaseManager.getSubjectDao();
        studentSubjectCrossrefDao =databaseManager.getStudentSubjectCrossrefDao();
        this.taskRunner = new AsyncTaskRunner();
    }


    public List<Subject> getAll() {
        return subjectDao.getAll();
    }

    public Long insert(Subject pi)
    {
        return subjectDao.insert(pi);
    }


    public Integer update(Subject pu)
    {
        return subjectDao.update(pu);
    }

    public Integer delete(Subject pd)
    {
        return subjectDao.delete(pd);
    }


    public List<StudentWithSubjects> getAllStudentsWithSubjects()
    {
        return studentSubjectCrossrefDao.getStudentWithSubjects();
    }

    public List<Subject> getProfesorSubjects(int id)
    {
        return subjectDao.getProfesorSubjects(id);
    }

    public Subject getSubjectByID(int id) {
        return subjectDao.getSubjectByID(id);
    }
}
