package com.example.studentmanager.database.repositories;

import android.content.Context;

import com.example.studentmanager.async.AsyncTaskRunner;
import com.example.studentmanager.async.Callback;
import com.example.studentmanager.database.DatabaseManager;
import com.example.studentmanager.database.daos.ProfesorDao;
import com.example.studentmanager.database.models.Profesor;
import com.example.studentmanager.database.utils.ProfesorWithSubjects;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ProfesorRepository {
    private ProfesorDao profesorDao;
    private List<Profesor> profesorList=new ArrayList<>();
    private final AsyncTaskRunner taskRunner;
    private  List<ProfesorWithSubjects> profesorsWithSubjects=new ArrayList<>();

    public ProfesorRepository(Context context)
    {
        DatabaseManager databaseManager=DatabaseManager.getInstance(context);
        profesorDao=databaseManager.getProfesorDao();
        this.taskRunner = new AsyncTaskRunner();
    }

    public Profesor getProfesor(String email)
    {
        return profesorDao.getProfesor(email);
    }

    public Profesor getProfessorByID(int id) {
        return profesorDao.getProfessorByID(id);
    }

//    public void  getAll() {
//        Callable<List<Profesor>> callable=new Callable<List<Profesor>>() {
//            @Override
//            public List<Profesor> call() throws Exception {
//                return profesorDao.getAll();
//            }
//        };
//        Callback<List<Profesor>> callback=new Callback<List<Profesor>>() {
//            @Override
//            public void runResultOnUIThread(List<Profesor> result) {
//                profesorList=result;
//            }
//        };
//        taskRunner.executeAsync(callable,callback);
//
//    }

    public List<Profesor> getAll() {
        return profesorDao.getAll();
    }

//    public void insert(final Profesor profesor) throws Exception {
//        Callable<Long> callable = new Callable<Long>() {
//            @Override
//            public Long call() throws Exception {
//                return profesorDao.insert(profesor);
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

    public Long insert(Profesor pi)
    {
        return profesorDao.insert(pi);
    }


//    public void update(final Profesor profesor)
//    {
//        Callable<Integer> callable = new Callable<Integer>() {
//            @Override
//            public Integer call() throws Exception {
//                return profesorDao.update(profesor);
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

    public Integer update(Profesor pu)
    {
        return profesorDao.update(pu);
    }


//    public void delete(final Profesor profesor)
//    {
//        Callable<Integer> callable = new Callable<Integer>() {
//            @Override
//            public Integer call() throws Exception {
//                return profesorDao.delete(profesor);
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

    public Integer delete(Profesor pd)
    {
        return profesorDao.delete(pd);
    }

//    public void  getAllProfessorWithSubjects() {
//        Callable<List<ProfesorWithSubjects>> callable=new Callable<List<ProfesorWithSubjects>>() {
//            @Override
//            public List<ProfesorWithSubjects> call() throws Exception {
//                return profesorDao.getAllProfesorWithSubjects();
//            }
//        };
//        Callback<List<ProfesorWithSubjects>> callback=new Callback<List<ProfesorWithSubjects>>() {
//            @Override
//            public void runResultOnUIThread(List<ProfesorWithSubjects> result) {
//                profesorsWithSubjects=result;
//            }
//        };
//        taskRunner.executeAsync(callable,callback);
//
//
//    }

    public List<ProfesorWithSubjects> getAllProfesorWithSubjects()
    {
        return profesorDao.getAllProfesorWithSubjects();
    }

}
