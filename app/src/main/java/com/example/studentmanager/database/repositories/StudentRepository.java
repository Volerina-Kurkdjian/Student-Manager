package com.example.studentmanager.database.repositories;

import android.content.Context;

import com.example.studentmanager.async.AsyncTaskRunner;
import com.example.studentmanager.async.Callback;
import com.example.studentmanager.database.DatabaseManager;
import com.example.studentmanager.database.daos.StudentDao;
import com.example.studentmanager.database.daos.StudentSubjectCrossrefDao;
import com.example.studentmanager.database.models.Profesor;
import com.example.studentmanager.database.models.Student;
import com.example.studentmanager.database.utils.ProfesorWithSubjects;
import com.example.studentmanager.database.utils.StudentWithSubjects;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class StudentRepository {

    private StudentDao studentDao;
    private List<Student> studentList=new ArrayList<>();
    private final AsyncTaskRunner taskRunner;
    private  List<StudentWithSubjects> studentsWithSubjects =new ArrayList<>();
    private StudentSubjectCrossrefDao studentSubjectCrossrefDao;

    public StudentRepository(Context context)
    {
        DatabaseManager databaseManager=DatabaseManager.getInstance(context);
        studentDao =databaseManager.getStudentDao();
        studentSubjectCrossrefDao =databaseManager.getStudentSubjectCrossrefDao();
        this.taskRunner = new AsyncTaskRunner();
    }


    public Student getStudent(String email)//extracts a single student from db
    {
        return studentDao.getStudent(email);
    }

    public Student getStudentByID(int id) {
        return studentDao.getStudentByID(id);
    }


//    public void  getAll() {
//        Callable<List<Student>> callable=new Callable<List<Student>>() {
//            @Override
//            public List<Student> call() throws Exception {
//                return studentDao.getAll();
//            }
//        };
//
//        Callback<List<Student>> callback=new Callback<List<Student>>() {
//            @Override
//            public void runResultOnUIThread(List<Student> result) {
//               studentList=result;
//            }
//        };
//        taskRunner.executeAsync(callable,callback);
//
//    }

    public List<Student> getAll() {
        return studentDao.getAll();
    }


//    public void insert(final Student student) throws Exception {
//        Callable<Long> callable = new Callable<Long>() {
//            @Override
//            public Long call() throws Exception {
//                return studentDao.insert(student);
//            }
//
//        };
//
//        Callback<Long> callback=new Callback<Long>() {
//            @Override
//            public void runResultOnUIThread(Long result) {
//                getAll();
//            }
//        };
//        taskRunner.executeAsync(callable, callback);
//    }


    public Long insert(Student pi)
    {
        return studentDao.insert(pi);
    }


//    public void update(final Student student)
//    {
//        Callable<Integer> callable = new Callable<Integer>() {
//            @Override
//            public Integer call() throws Exception {
//                return studentDao.update(student);
//            }
//
//        };
//
//        Callback<Integer> callback=new Callback<Integer>() {
//            @Override
//            public void runResultOnUIThread(Integer result) {
//                getAll();
//            }
//        };
//        taskRunner.executeAsync(callable, callback);
//
//    }


    public Integer update(Student pu)
    {
        return studentDao.update(pu);
    }


//  public void delete(final Student student)
//    {
//        Callable<Integer> callable = new Callable<Integer>() {
//            @Override
//            public Integer call() throws Exception {
//                return studentDao.delete(student);
//            }
//
//        };
//
//        Callback<Integer> callback=new Callback<Integer>() {
//            @Override
//            public void runResultOnUIThread(Integer result) {
//                getAll();
//            }
//        };
//        taskRunner.executeAsync(callable, callback);
//    }
//

    public Integer delete(Student pd)
    {
        return studentDao.delete(pd);
    }


    public List<StudentWithSubjects> getAllStudentsWithSubjects()
    {
        return studentSubjectCrossrefDao.getStudentWithSubjects();
    }








}
