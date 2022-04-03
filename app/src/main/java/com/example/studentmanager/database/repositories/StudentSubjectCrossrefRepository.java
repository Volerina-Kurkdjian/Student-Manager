package com.example.studentmanager.database.repositories;

import android.content.Context;

import com.example.studentmanager.async.AsyncTaskRunner;
import com.example.studentmanager.database.DatabaseManager;
import com.example.studentmanager.database.daos.StudentSubjectCrossrefDao;
import com.example.studentmanager.database.daos.SubjectDao;
import com.example.studentmanager.database.models.StudentSubjectCrossRef;
import com.example.studentmanager.database.models.Subject;
import com.example.studentmanager.database.utils.StudentWithSubjects;
import com.example.studentmanager.database.utils.SubjectWithStudents;

import java.util.ArrayList;
import java.util.List;

public class StudentSubjectCrossrefRepository {


    private List<Subject> subjectList=new ArrayList<>();
    private final AsyncTaskRunner taskRunner;
    private  List<SubjectWithStudents> subjectsWithStudents =new ArrayList<>();
    private StudentSubjectCrossrefDao studentSubjectCrossrefDao;

    public StudentSubjectCrossrefRepository(Context context)
    {
        DatabaseManager databaseManager=DatabaseManager.getInstance(context);
        studentSubjectCrossrefDao =databaseManager.getStudentSubjectCrossrefDao();
        this.taskRunner = new AsyncTaskRunner();
    }


    public Long insert(StudentSubjectCrossRef pi)
    {
        return studentSubjectCrossrefDao.insert(pi);
    }


    public Integer update(StudentSubjectCrossRef pu)
    {
        return studentSubjectCrossrefDao.update(pu);
    }

    public Integer delete(StudentSubjectCrossRef pd)
    {
        return studentSubjectCrossrefDao.delete(pd);
    }

    public List<StudentSubjectCrossRef> getStudentsRefInSubject(int id) {
        return studentSubjectCrossrefDao.getStudentsRefInSubject(id);
    }

    public List<StudentSubjectCrossRef> getSubjectsRefForStudent(int id) {
        return studentSubjectCrossrefDao.getSubjectsRefForStudent(id);
    }

    public StudentSubjectCrossRef getRef(int idStud, int idSubject) {
        return studentSubjectCrossrefDao.getRef(idStud, idSubject);
    }
}
