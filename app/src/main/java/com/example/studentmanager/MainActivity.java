package com.example.studentmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.studentmanager.async.AsyncTaskRunner;
import com.example.studentmanager.async.Callback;
import com.example.studentmanager.authentication.fragments.RoleChoserFragment;
import com.example.studentmanager.database.models.Profesor;
import com.example.studentmanager.database.models.Subject;
import com.example.studentmanager.database.repositories.ProfesorRepository;
import com.example.studentmanager.database.repositories.SubjectRepository;
import com.example.studentmanager.network.HttpManager;
import com.example.studentmanager.network.SubjectJsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MainActivity extends FragmentActivity {

    ProfesorRepository profesorRepository;
    private final AsyncTaskRunner taskRunner=new AsyncTaskRunner();
    Fragment fragmentRoleChoser= RoleChoserFragment.newInstance();
    private List<Subject> subjects=new ArrayList<>();
    private List<Profesor> professors=new ArrayList<>();
    private SubjectRepository subjectRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        subjectRepository=new SubjectRepository(getApplicationContext());
        profesorRepository = new ProfesorRepository(getApplicationContext());


//        profesorRepository=new ProfesorRepository(this);
//        Profesor profesor = new Profesor("prof@prof.com", "Prof Test", 1234);
//        insert(profesor);

        //getSupportFragmentManager().beginTransaction().replace(R.id.placeholder_fragment,fragmentRoleChoser).commit();
      getSubjectsFromHttp();
    }

    private void getSubjectsFromHttp()
    {
        Callable<String> asyncOperation=new HttpManager("https://jsonkeeper.com/b/4ZW3");
        Callback<String> mainThreadOperation=new Callback<String>() {
            @Override
            public void runResultOnUIThread(String result) {
                subjects= SubjectJsonParser.SubjectsfromJson(result);
                professors=SubjectJsonParser.ProfesorsFromJson(result);

                jsonInsertionIntoDatabase(subjects, professors);
            }
        };

        taskRunner.executeAsync(asyncOperation, mainThreadOperation);
    }

    private void jsonInsertionIntoDatabase(List<Subject> s,List<Profesor> p)
    {
        for(int i=0;i<s.size();i++)
        {
            insert(p.get(i),s.get(i));
        }
    }

    public void  insert(Profesor p,Subject s) {
        Callable<Long> callable=new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return profesorRepository.insert(p);
            }
        };
        Callback<Long> callback=new Callback<Long>() {
            @Override
            public void runResultOnUIThread(Long result) {

               Callable<Long> callable1=new Callable<Long>() {
                   @Override
                   public Long call() throws Exception {

                       if(result.intValue()<0)
                       {
                           insertSubjectWithProfesorFromDatabase(s,p.getEmail());
                           return new Long(-1);
                       }
                       else {
                           s.setIdProfesorSubject(result.intValue());//eu l am pus aici
                           return subjectRepository.insert(s);
                       }


                   }
               };
               Callback<Long> callback1=new Callback<Long>() {
                   @Override
                   public void runResultOnUIThread(Long result) {

                       System.out.println(result);
                   }
               };
               taskRunner.executeAsync(callable1,callback1);
            }
        };
        taskRunner.executeAsync(callable,callback);

    }

    public void insertSubjectWithProfesorFromDatabase(Subject s,String email)
    {

        Callable<Profesor> callable=new Callable<Profesor>() {
            @Override
            public Profesor call() throws Exception {
                return profesorRepository.getProfesor(email);
            }
        };
        Callback<Profesor> callback=new Callback<Profesor>() {
            @Override
            public void runResultOnUIThread(Profesor result) {
                s.setIdProfesorSubject(result.getIdProfesor());

                Callable<Long> callable1=new Callable<Long>() {
                    @Override
                    public Long call() throws Exception {

                        return subjectRepository.insert(s);
                    }
                };
                Callback<Long> callback1=new Callback<Long>() {
                    @Override
                    public void runResultOnUIThread(Long result) {

                        System.out.println(result);
                    }
                };
                taskRunner.executeAsync(callable1,callback1);

            }

        };
        taskRunner.executeAsync(callable,callback);

    }
}