package com.example.studentmanager.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.studentmanager.R;
import com.example.studentmanager.async.AsyncTaskRunner;
import com.example.studentmanager.async.Callback;
import com.example.studentmanager.database.models.Profesor;
import com.example.studentmanager.database.models.Student;
import com.example.studentmanager.database.models.StudentSubjectCrossRef;
import com.example.studentmanager.database.models.Subject;
import com.example.studentmanager.database.repositories.ProfesorRepository;
import com.example.studentmanager.database.repositories.StudentRepository;
import com.example.studentmanager.database.repositories.StudentSubjectCrossrefRepository;
import com.example.studentmanager.database.repositories.SubjectRepository;
import com.example.studentmanager.database.utils.DateConverter;

import java.util.concurrent.Callable;

public class StudentSubjectFragment extends Fragment {

    private StudentSubjectCrossrefRepository studentSubjectCrossrefRepository;
    private SubjectRepository subjectRepository;
    private ProfesorRepository profesorRepository;
    private StudentRepository studentRepository;
    private final AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();

    private Subject currentSubject;
    private String studentEmail;

    private TextView subjectName;
    private TextView professorName;
    private TextView professorEmail;
    private TextView subjectExamDate;
    private TextView subjectGrade;

    public StudentSubjectFragment() {
        // Required empty public constructor
    }

    public static StudentSubjectFragment newInstance() {
        return new StudentSubjectFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        currentSubject = getArguments().getParcelable("subject");
        studentEmail = getArguments().getString("email", "");

        studentSubjectCrossrefRepository = new StudentSubjectCrossrefRepository(getContext());
        subjectRepository = new SubjectRepository(getContext());
        studentRepository = new StudentRepository(getContext());
        profesorRepository = new ProfesorRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_subject, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize components
        initializeComponents(view);

        // Populate fragment view with data from DB
        displayData();
    }

    private void initializeComponents(View view) {
        subjectName = view.findViewById(R.id.student_subject_name_tv);
        professorName = view.findViewById(R.id.student_subject_prof_name_tv);
        professorEmail = view.findViewById(R.id.student_subject_prof_email_tv);
        subjectExamDate = view.findViewById(R.id.student_subject_exam_date_tv);
        subjectGrade = view.findViewById(R.id.student_subject_grade_tv);
    }

    private void displayData() {
        // Get data about the professor
        displayProfessorData();

        // Get data about the subject
        displaySubjectData();

        // Get data about grade
        displayRefData();
    }

    private void displayProfessorData() {
        int professorID = currentSubject.getIdProfesorSubject();
        Callable<Profesor> callable = () -> profesorRepository.getProfessorByID(professorID);
        Callback<Profesor> callback = (Profesor p) -> {
            professorName.setText(p.getNameProfesor());
            professorEmail.setText(p.getEmailProfesor());
        };
        asyncTaskRunner.executeAsync(callable, callback);
    }

    private void displaySubjectData() {
        subjectExamDate.setText(DateConverter.fromDate(currentSubject.getSubjectDateExam()));
        subjectName.setText(currentSubject.getSubjectName());
    }

    private void displayRefData() {
        // Get the student by email to obtain his ID
        Callable<Student> studentCallable = () -> studentRepository.getStudent(studentEmail);
        Callback<Student> studentCallback = (Student student) -> {
            Callable<StudentSubjectCrossRef> refCallable = () -> studentSubjectCrossrefRepository.getRef(student.getIdStud(), currentSubject.getIdSubject());
            Callback<StudentSubjectCrossRef> refCallback = (StudentSubjectCrossRef ref) -> {
                subjectGrade.setText("Grade: " + ref.getGrade());
            };
            asyncTaskRunner.executeAsync(refCallable, refCallback);
        };
        asyncTaskRunner.executeAsync(studentCallable, studentCallback);
    }
}