package com.example.studentmanager.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.studentmanager.MainActivity;
import com.example.studentmanager.R;
import com.example.studentmanager.async.AsyncTaskRunner;
import com.example.studentmanager.async.Callback;
import com.example.studentmanager.database.models.Profesor;
import com.example.studentmanager.database.models.Subject;
import com.example.studentmanager.database.repositories.ProfesorRepository;
import com.example.studentmanager.database.repositories.SubjectRepository;
import com.example.studentmanager.profile.adapters.SpinnerAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;


public class ProfesorProfileFragment extends Fragment {

    public static  final String SHARED_PREF_FILE_NAME="loginSharedPref";
    private Button logoutteacher;
    private SharedPreferences sharedPreferences;
    private TextView teachername;
    private TextView emailteacher;
    private TextView diplomateacher;
    private Spinner spinnerteacher;
    private SpinnerAdapter spinnerAdapter;
    private AsyncTaskRunner asyncTaskRunner=new AsyncTaskRunner();
    private String email;
    private Button deleteteacherbtn;
    private Button updateprofile;

    private Subject selectedSubject;

    public ProfesorProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logoutteacher=view.findViewById(R.id.btnlogoutteacher);
        sharedPreferences=getActivity().getSharedPreferences(SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
        teachername=view.findViewById(R.id.tvnumeteacher);
        emailteacher=view.findViewById(R.id.tvemailteacher);
        diplomateacher=view.findViewById(R.id.tvdiploma);
        spinnerteacher=view.findViewById(R.id.spinnerteacher1);
        deleteteacherbtn=view.findViewById(R.id.btndeleteteacher);
        updateprofile=view.findViewById(R.id.btnupdate);

        logoutteacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sharedPreferences.edit().clear().apply();
                Intent intent=new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        getProfesorFromDatabase();
        populateSpinner();

        spinnerteacher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSubject = (Subject)parent.getItemAtPosition(position);
                if(!selectedSubject.getSubjectName().equals("select a subject"))
                {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("subject", selectedSubject);
                    Navigation.findNavController(view).navigate(R.id.profesorSubjectFragment, bundle);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        deleteteacherbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProfile();
            }
        });

        updateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("email",email);
                Navigation.findNavController(v).navigate(R.id.updateProfesorFragment,bundle);
            }
        });

    }

    private void deleteProfile()
    {
        ProfesorRepository profesorRepository=new ProfesorRepository(getContext());

        Callable<Profesor> profesorCallable = new Callable<Profesor>() {
            @Override
            public Profesor call() throws Exception {
                return profesorRepository.getProfesor(email);
            }
        };
        Callback<Profesor> profesorCallback = new Callback<Profesor>() {
            @Override
            public void runResultOnUIThread(Profesor result) {
                Callable<Integer> callable=new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        return profesorRepository.delete(result);
                    }
                };
                Callback<Integer> callback=new Callback<Integer>() {
                    @Override
                    public void runResultOnUIThread(Integer result) {

                        sharedPreferences.edit().clear().apply();
                        Intent intent=new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                };
                asyncTaskRunner.executeAsync(callable,callback);
            }
        };
        asyncTaskRunner.executeAsync(profesorCallable, profesorCallback);

    }




    private void getProfesorFromDatabase()
    {


        if(!email.equals(""))
        {
            ProfesorRepository profesorRepository=new ProfesorRepository(getContext());
            Callable<Profesor> callable=new Callable<Profesor>() {
                @Override
                public Profesor call() throws Exception {
                    return profesorRepository.getProfesor(email);
                }
            };
            Callback<Profesor> callback=new Callback<Profesor>() {
                @Override
                public void runResultOnUIThread(Profesor result) {
                    teachername.setText(result.getName());
                    emailteacher.setText(result.getEmail());
                    diplomateacher.setText(result.getDiploma().getDiplomaName());
                }
            };
            asyncTaskRunner.executeAsync(callable,callback);
        }
    }

    public static ProfesorProfileFragment newInstance() {
        ProfesorProfileFragment fragment = new ProfesorProfileFragment();
        return fragment;
    }

    public void populateSpinner()
    {
        SubjectRepository subjectRepository=new SubjectRepository(getContext());
        ProfesorRepository profesorRepository = new ProfesorRepository(getContext());

        Callable<Profesor> profesorCallable = new Callable<Profesor>() {
            @Override
            public Profesor call() throws Exception {
                return profesorRepository.getProfesor(email);
            }
        };
        Callback<Profesor> profesorCallback = new Callback<Profesor>() {
            @Override
            public void runResultOnUIThread(Profesor result) {
                Callable<List<Subject>> callable=new Callable<List<Subject>>() {
                    @Override
                    public List<Subject> call() throws Exception {
                        return subjectRepository.getProfesorSubjects(result.getIdProfesor());
                    }
                };
                Callback<List<Subject>> callback=new Callback<List<Subject>>() {
                    @Override
                    public void runResultOnUIThread(List<Subject> result) {
                        Subject nousub=new Subject("select a subject",new Date(),0);
                        ArrayList<Subject> arraysub=new ArrayList<>();
                        arraysub.add(nousub);
                        for(int i=0;i<result.size();i++)
                        {
                            arraysub.add(result.get(i));
                        }
                        spinnerAdapter=new SpinnerAdapter(getContext(),R.layout.custom_spinner_subjects,arraysub);
                        spinnerteacher.setAdapter(spinnerAdapter);
                    }
                };
                asyncTaskRunner.executeAsync(callable,callback);
            }
        };
        asyncTaskRunner.executeAsync(profesorCallable, profesorCallback);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getActivity().getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle!=null)
        {
            email=bundle.getString("email");
        }
        else
        {
            email="";
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profesor_profile, container, false);
    }
}