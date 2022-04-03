package com.example.studentmanager.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.studentmanager.R;
import com.example.studentmanager.async.AsyncTaskRunner;
import com.example.studentmanager.async.Callback;
import com.example.studentmanager.database.models.Student;
import com.example.studentmanager.database.repositories.StudentRepository;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.concurrent.Callable;

public class UpdateStudentFragment extends Fragment {

    private StudentRepository studentRepository;
    private final AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();

    private final String SHARED_PREF_FILE_NAME="loginSharedPref";
    private SharedPreferences sharedPreferences;

    private String emailString;

    private EditText name;
    private EditText email;
    private EditText password;
    private EditText registrationNumber;
    private DatePicker enrolmentDate;
    private EditText groupNumber;
    private Button saveButton;

    public UpdateStudentFragment() {
        // Required empty public constructor
    }

    public static UpdateStudentFragment newInstance(String param1, String param2) {
        return new UpdateStudentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        emailString = getArguments().getString("email", "");
        studentRepository = new StudentRepository(getContext());
        sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize components
        initComponents(view);

        // Display data from DB
        displayDataFromDB();

        saveButton.setOnClickListener(v -> {
            performUpdate(view);
        });
    }

    private void displayDataFromDB() {
        Callable<Student> callable = () -> studentRepository.getStudent(emailString);
        Callback<Student> callback = (Student student) -> {
            name.setText(student.getNume());
            email.setText(student.getEmailStudent());
            password.setText(student.getPasswordStudent());
            registrationNumber.setText(Integer.valueOf(student.getNumarMatricol()).toString());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(student.getDataInmatriculare());
            enrolmentDate.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            groupNumber.setText(Integer.valueOf(student.getGroup()).toString());
        };
        asyncTaskRunner.executeAsync(callable, callback);
    }

    private void performUpdate(View view) {
        // First, we get the student from the database that we must update
        Callable<Student> retrieveCallable = () -> studentRepository.getStudent(emailString);
        Callback<Student> retrieveCallback = (Student student) -> {
            // We check to see if the password is changed and re-hash it
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                noSuchAlgorithmException.printStackTrace();
            }

            if(!student.getPasswordStudent().equals(password.getText().toString())) {
                md.update(password.getText().toString().getBytes(StandardCharsets.UTF_8));
                byte[] digest=md.digest();//will create the hash and put it into a byte array
                String hashedPassword=String.format("%064x",new BigInteger(1,digest));
                student.setPasswordStudent(hashedPassword);
            }

            emailString = email.getText().toString();

            student.setNumeStudent(name.getText().toString());
            student.setEmailStudent(email.getText().toString());
            student.setNumarMatricol(Integer.parseInt(registrationNumber.getText().toString()));
            student.setGroup(Integer.parseInt(groupNumber.getText().toString()));

            Calendar calendar = Calendar.getInstance();
            calendar.set(enrolmentDate.getYear(), enrolmentDate.getMonth(), enrolmentDate.getDayOfMonth());
            student.setDataInmatriculare(calendar.getTime());

            Callable<Integer> updateCallable = () -> studentRepository.update(student);
            Callback<Integer> updateCallback = (Integer nr) -> {
                // After update we go back to profile
                System.out.println("updated student");
                deleteSharedPreferences();
                Bundle bundle = new Bundle();
                bundle.putString("email", emailString);
                Navigation.findNavController(view).navigate(R.id.studentProfileFragment, bundle);
            };

            asyncTaskRunner.executeAsync(updateCallable, updateCallback);
        };

        asyncTaskRunner.executeAsync(retrieveCallable, retrieveCallback);
    }

    private void initComponents(View view) {
        name = view.findViewById(R.id.update_student_name);
        email = view.findViewById(R.id.update_student_email);
        password = view.findViewById(R.id.update_student_password);
        registrationNumber = view.findViewById(R.id.update_student_reg_number);
        enrolmentDate = view.findViewById(R.id.update_student_date_picker);
        groupNumber = view.findViewById(R.id.update_student_groupnumber);
        saveButton = view.findViewById(R.id.btn_update_student);
    }

    private void deleteSharedPreferences() {
        sharedPreferences.edit().clear().apply();
    }
}