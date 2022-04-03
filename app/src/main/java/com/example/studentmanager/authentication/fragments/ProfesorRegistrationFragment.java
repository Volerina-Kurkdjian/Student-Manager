package com.example.studentmanager.authentication.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.studentmanager.R;
import com.example.studentmanager.async.AsyncTaskRunner;
import com.example.studentmanager.async.Callback;
import com.example.studentmanager.database.models.Diploma;
import com.example.studentmanager.database.models.Profesor;
import com.example.studentmanager.database.repositories.ProfesorRepository;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;


public class ProfesorRegistrationFragment extends Fragment {

    ProfesorRepository profesorRepository;
    private final AsyncTaskRunner taskRunner=new AsyncTaskRunner();
    Diploma diploma=new Diploma("Informatics",10);

        private EditText profesorName;
        private EditText profesorEmail;
        private EditText profesorPassword;
        private Button profesorSignup;

    public ProfesorRegistrationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profesorName=view.findViewById(R.id.profesor_registration_name);
        profesorEmail=view.findViewById(R.id.professor_registration_email);
        profesorPassword=view.findViewById(R.id.profesor_registration_password);
        profesorSignup=view.findViewById(R.id.btn_professor_registration);

        profesorSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(validate()==true)
                        insertProfessorintoDatabase(v);

            }
        });
    }

    private boolean validate()
    {
        if(profesorName.getText()==null || profesorName.getText().toString().trim().length()<3)
        {
            Toast.makeText(getContext(),"Invalid name",Toast.LENGTH_LONG).show();
            return false;
        }
        if(profesorEmail.getText()==null || profesorEmail.getText().toString().trim().length()<3)
        {
            Toast.makeText(getContext(),"Invalid email",Toast.LENGTH_LONG).show();
            return false;
        }
        if(profesorPassword.getText()==null || profesorPassword.getText().toString().trim().length()<8)
        {
            Toast.makeText(getContext(),"Invalid password, it must have at least 8 characters!",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void insertProfessorintoDatabase(View v)
    {
        String name=profesorName.getText().toString();
        String email=profesorEmail.getText().toString();
        String password=profesorPassword.getText().toString();

        try {
            MessageDigest md=MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes(StandardCharsets.UTF_8));
            byte[] digest=md.digest();//will create the hash and put it into a byte array
            String hashedPassword=String.format("%064x",new BigInteger(1,digest));
            Profesor newprofesor=new Profesor(email,name,hashedPassword,diploma);

            Callable<Long> callable=new Callable<Long>() {
                @Override
                public Long call() throws Exception {
                    return profesorRepository.insert(newprofesor);
                }
            };
            Callback<Long> callback=new Callback<Long>() {
                @Override
                public void runResultOnUIThread(Long result) {
                    Bundle bundle=new Bundle();
                    bundle.putString("role","professor");
                    Navigation.findNavController(v).navigate(R.id.loginFragment,bundle);

                }
            };
            taskRunner.executeAsync(callable,callback);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static ProfesorRegistrationFragment newInstance() {
        ProfesorRegistrationFragment fragment = new ProfesorRegistrationFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       profesorRepository=new ProfesorRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profesor_registration, container, false);
    }
}