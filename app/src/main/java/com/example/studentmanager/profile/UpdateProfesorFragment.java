package com.example.studentmanager.profile;

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


public class UpdateProfesorFragment extends Fragment {

    private EditText name;
    private EditText email;
    private EditText password;
    private EditText grade;
    private EditText diploma;
    Button btnsave;
    private ProfesorRepository profesorRepository;
    private String emailfound;
    AsyncTaskRunner asyncTaskRunner;

    public UpdateProfesorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        name=view.findViewById(R.id.profesor_name);
        email=view.findViewById(R.id.professor_email);
        password=view.findViewById(R.id.profesor_password);
        diploma=view.findViewById(R.id.etdiploma);
        grade=view.findViewById(R.id.etgradeprofesor);
        btnsave=view.findViewById(R.id.btn_update_professor);
        profesorRepository=new ProfesorRepository(getContext());
        asyncTaskRunner=new AsyncTaskRunner();
        Callable<Profesor> callable=new Callable<Profesor>() {
            @Override
            public Profesor call() throws Exception {
                return profesorRepository.getProfesor(emailfound);

            }
        };

        Callback<Profesor> callback=new Callback<Profesor>() {
            @Override
            public void runResultOnUIThread(Profesor result) {
                name.setText(result.getName());
                email.setText(result.getEmail());
                password.setText(result.getPassword());
                diploma.setText(result.getDiploma().getDiplomaName());
                grade.setText(Integer.valueOf(result.getDiploma().getGrade()).toString());

            }
        };

        asyncTaskRunner.executeAsync(callable,callback);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Callable<Profesor> callable = () -> profesorRepository.getProfesor(emailfound);
                Callback<Profesor> callback = (Profesor p) -> {
                    // Set new data
                    p.setName(name.getText().toString());
                    p.setEmail(email.getText().toString());
                    p.setDiploma(new Diploma(diploma.getText().toString(), Integer.parseInt(grade.getText().toString())));

                    MessageDigest md = null;
                    String hashedPassword;

                    try {
                        md = MessageDigest.getInstance("SHA-256");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                    // Hash Password and change the professor password if it's different than the original
                    if (md != null && !password.getText().toString().equals(p.getPassword())) {
                        md.update(password.getText().toString().getBytes(StandardCharsets.UTF_8));
                        byte[] digest=md.digest();//will create the hash and put it into a byte array
                        hashedPassword=String.format("%064x",new BigInteger(1,digest));
                        p.setPassword(hashedPassword);
                    }

                    Callable<Integer> updateCallable = () -> profesorRepository.update(p);
                    Callback<Integer> updateCallback = (Integer id) -> Navigation.findNavController(view).navigate(R.id.profesorProfileFragment);

                    asyncTaskRunner.executeAsync(updateCallable, updateCallback);
                };

                asyncTaskRunner.executeAsync(callable, callback);
            }
        });

    }

    public static UpdateProfesorFragment newInstance(String param1, String param2) {
        UpdateProfesorFragment fragment = new UpdateProfesorFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments()!=null)
            this.emailfound=getArguments().getString("email");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_profesor, container, false);
    }
}