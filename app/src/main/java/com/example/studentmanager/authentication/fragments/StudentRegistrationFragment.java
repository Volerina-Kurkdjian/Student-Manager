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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.studentmanager.R;
import com.example.studentmanager.async.AsyncTaskRunner;
import com.example.studentmanager.async.Callback;
import com.example.studentmanager.database.models.Profesor;
import com.example.studentmanager.database.models.Student;
import com.example.studentmanager.database.models.StudentSubjectCrossRef;
import com.example.studentmanager.database.models.Subject;
import com.example.studentmanager.database.repositories.StudentRepository;
import com.example.studentmanager.database.repositories.StudentSubjectCrossrefRepository;
import com.example.studentmanager.database.repositories.SubjectRepository;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class StudentRegistrationFragment extends Fragment {

    private StudentRepository studrepository;
    private SubjectRepository subjectRepository;
    private StudentSubjectCrossrefRepository studentSubjectCrossrefRepository;

    private final AsyncTaskRunner taskRunner=new AsyncTaskRunner();
    private EditText studentnName;
    private EditText studentEmail;
    private EditText studentPassword;
    private EditText studentRegistrationNumber;
    private EditText studentGroupNumber;
    private RadioGroup studentRadiogroup;
    private Button  studentSignupButton;
    private DatePicker studentRegistrationDate;
    private Calendar studcalendar;

    public StudentRegistrationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        studentnName=view.findViewById(R.id.registration_student_name);
        studentEmail=view.findViewById(R.id.registration_student_email);
        studentPassword=view.findViewById(R.id.registration_student_password);
        studentRegistrationNumber=view.findViewById(R.id.registration_student_reg_number);
        studentGroupNumber=view.findViewById(R.id.registration_student_groupnumber);
        studentRadiogroup=view.findViewById(R.id.radio_group_registration_student);
        studentRegistrationDate=view.findViewById(R.id.registration_student_date_picker);
        studentSignupButton=view.findViewById(R.id.btn_registration_student_signup);

        studentSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateStudent()==true)

                    insertStudentIntoDatabase(v);
            }
        });


    }

    public static StudentRegistrationFragment newInstance() {
        StudentRegistrationFragment fragment = new StudentRegistrationFragment();
        return fragment;
    }

    private boolean validateStudent()
    {
        if(studentnName.getText().toString()==null ||studentnName.getText().toString().trim().length()<3) {
            Toast.makeText(getContext(), "Invalid student name", Toast.LENGTH_LONG).show();
            return false;
        }
        if(studentEmail.getText().toString()==null ||studentEmail.getText().toString().trim().length()<3) {
            Toast.makeText(getContext(), "Invalid student email", Toast.LENGTH_LONG).show();
            return false;
        }
        if(studentPassword.getText().toString()==null ||studentPassword.getText().toString().trim().length()<8) {
            Toast.makeText(getContext(), "Invalid student password,must have at least 8 characters", Toast.LENGTH_LONG).show();
            return false;
        }
        if(studentRegistrationNumber.getText().toString()==null ||studentRegistrationNumber.getText().toString().trim().length()<6) {
            Toast.makeText(getContext(), "Invalid student registration number, it must have at least 6 characters", Toast.LENGTH_LONG).show();
            return false;
        }
        if(studentGroupNumber.getText().toString()==null || Integer.parseInt(studentGroupNumber.getText().toString())>1095 ||Integer.parseInt(studentGroupNumber.getText().toString())<1090) {
            Toast.makeText(getContext(), "Invalid student group, it must start from 1091 and end at 1094", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }


    private void insertStudentIntoDatabase(View v)
    {
        String studName=studentnName.getText().toString();
        String studEmail=studentEmail.getText().toString();
        String studPassword=studentPassword.getText().toString();
        int studnrMatricol=Integer.parseInt(studentRegistrationNumber.getText().toString());
        int studGroupnumber=Integer.parseInt(studentGroupNumber.getText().toString());
        studcalendar=Calendar.getInstance();
        studcalendar.set(studentRegistrationDate.getYear(),studentRegistrationDate.getMonth(),studentRegistrationDate.getDayOfMonth());
        Date registrationdatestud=studcalendar.getTime();
        String frminvatamant;
        if(studentRadiogroup.getCheckedRadioButtonId()==R.id.rb_reg_student_budget)
            frminvatamant="Budget";
        else
            frminvatamant="Tax";

        try {
            MessageDigest md=MessageDigest.getInstance("SHA-256");
            md.update(studPassword.getBytes(StandardCharsets.UTF_8));
            byte[] digest=md.digest();//will create the hash and put it into a byte array
            String hashedPassword=String.format("%064x",new BigInteger(1,digest));
            Student student=new Student(studName,registrationdatestud,frminvatamant,studGroupnumber,studnrMatricol,studEmail,hashedPassword);

            Callable<Long> callable=new Callable<Long>() {
                @Override
                public Long call() throws Exception {
                    return studrepository.insert(student);
                }
            };
            Callback<Long> callback=new Callback<Long>() {
                @Override
                public void runResultOnUIThread(Long result) {
                    // Add this student as participant to all available subjects
                    student.setIdStud(result.intValue());
                    addStudentToSubjects(student);

                    Bundle bundle=new Bundle();
                    bundle.putString("role","student");
                    Navigation.findNavController(v).navigate(R.id.loginFragment,bundle);
                }
            };
            taskRunner.executeAsync(callable,callback);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studrepository=new StudentRepository(getContext());
        studentSubjectCrossrefRepository = new StudentSubjectCrossrefRepository(getContext());
        subjectRepository = new SubjectRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_registration, container, false);
    }

    private void addStudentToSubjects(Student student) {
        // Get all subjects from DB
        Callable<List<Subject>> retrieveSubjectsCallable = () -> subjectRepository.getAll();
        Callback<List<Subject>> retrieveSubjectsCallback = (List<Subject> subjects) -> {
            // Bind student to all subjects
            for (Subject subject : subjects) {
                StudentSubjectCrossRef crossRef = new StudentSubjectCrossRef(student.getIdStud(), subject.getIdSubject(), 0);
                Callable<Long> bindCallable = () -> studentSubjectCrossrefRepository.insert(crossRef);
                Callback<Long> bindCallback = (Long id) -> System.out.println("Bound student to subject");
                taskRunner.executeAsync(bindCallable, bindCallback);
            }
        };

        taskRunner.executeAsync(retrieveSubjectsCallable, retrieveSubjectsCallback);
    }
}