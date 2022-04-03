package com.example.studentmanager.authentication.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.SharedPreferencesKt;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.studentmanager.ProfesorProfileActivity;
import com.example.studentmanager.R;
import com.example.studentmanager.StudentProfileActivity;
import com.example.studentmanager.async.AsyncTaskRunner;
import com.example.studentmanager.async.Callback;
import com.example.studentmanager.database.models.Profesor;
import com.example.studentmanager.database.models.Student;
import com.example.studentmanager.database.repositories.ProfesorRepository;
import com.example.studentmanager.database.repositories.StudentRepository;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;


public class LoginFragment extends Fragment {

    public static  final String SHARED_PREF_FILE_NAME="loginSharedPref";
    public static final String EMAIL="email";
    public static final String PASSWORD="password";
    public static final String REMEMBER="remember";
    private SharedPreferences preferences;
    private static final String ROLE = "role";
    private String role;
    private EditText emaillogin;
    private EditText passwordlogin;
    private CheckBox checkBox;
    private Button loginbtn;
    private final AsyncTaskRunner taskRunner=new AsyncTaskRunner();

    ProfesorRepository profesorRepository;
    private StudentRepository studrepository;

    public LoginFragment() {
        // Required empty public constructor
    }


    public static LoginFragment newInstance(String role) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ROLE, role);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emaillogin=view.findViewById(R.id.login_email);
        passwordlogin=view.findViewById(R.id.login_password);
        preferences=getActivity().getSharedPreferences(SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
        checkBox=view.findViewById(R.id.login_remember_check);
        loginbtn=view.findViewById(R.id.save_login_button);


        loginbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email= emaillogin.getText().toString();
                String pass=passwordlogin.getText().toString();
                boolean checked;
                checked = checkBox.isChecked();
                MessageDigest md = null;
                try {
                    md = MessageDigest.getInstance("SHA-256");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                md.update(pass.getBytes(StandardCharsets.UTF_8));
                byte[] digest=md.digest();//will create the hash and put it into a byte array
                String hashedPassword=String.format("%064x",new BigInteger(1,digest));
                if(role.equals("student")) {
                    Callable<Student> callable = new Callable<Student>() {
                        @Override
                        public Student call() throws Exception {

                            return studrepository.getStudent(email);

                        }
                    };
                    Callback<Student> callback = new Callback<Student>() {
                        @Override
                        public void runResultOnUIThread(Student result) {
                           if(result==null)
                               Toast.makeText(getContext(),"This user doesn't exist",Toast.LENGTH_LONG).show();
                           else
                               if(result.getPasswordStudent().equals(hashedPassword))
                               {

                                   if(checked==true)
                                   {
                                       saveLoginCredentialsToSharedPref(email,hashedPassword);
                                   }
                                   Intent intent=new Intent(getContext(), StudentProfileActivity.class);
                                   Bundle bundle=new Bundle();
                                   bundle.putString("email",email);
                                   intent.putExtras(bundle);
                                   startActivity(intent);
                                   getActivity().finish();
                               }
                               else
                               {
                                   Toast.makeText(getContext(),"Authentication not successful",Toast.LENGTH_LONG).show();
                               }

                        }
                    };
                    taskRunner.executeAsync(callable, callback);
                }

                else
                {

                    Callable<Profesor> callable = new Callable<Profesor>() {
                        @Override
                        public Profesor call() throws Exception {

                            return profesorRepository.getProfesor(email);

                        }
                    };
                    Callback<Profesor> callback = new Callback<Profesor>() {
                        @Override
                        public void runResultOnUIThread(Profesor result) {
                            if(result==null)
                                Toast.makeText(getContext(),"This user doesn't exist",Toast.LENGTH_LONG).show();
                            else
                            if(result.getPassword().equals(hashedPassword))
                            {

                                if(checked)
                                {
                                    saveLoginCredentialsToSharedPref(email,hashedPassword);
                                }
                                Intent intent=new Intent(getContext(), ProfesorProfileActivity.class);
                                Bundle bundle=new Bundle();
                                bundle.putString("email",email);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                getActivity().finish();
                            }
                            else
                            {
                                Toast.makeText(getContext(),"Authentication not successful",Toast.LENGTH_LONG).show();
                            }

                        }
                    };
                    taskRunner.executeAsync(callable, callback);

                }
            }
        });
    }


private void saveLoginCredentialsToSharedPref(String email, String parola)
{
    SharedPreferences.Editor editor=preferences.edit();
    editor.putString(EMAIL,email);
    editor.putString(PASSWORD,parola);
    editor.putString("role",role);
    editor.apply();

}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profesorRepository=new ProfesorRepository(getContext());
        studrepository=new StudentRepository(getContext());

        if(getArguments()!=null)
            this.role=getArguments().getString(ROLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_login, container, false);
    }
}