package com.example.studentmanager.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentmanager.R;
import com.example.studentmanager.async.AsyncTaskRunner;
import com.example.studentmanager.async.Callback;
import com.example.studentmanager.database.models.Student;
import com.example.studentmanager.database.models.StudentSubjectCrossRef;
import com.example.studentmanager.database.models.Subject;
import com.example.studentmanager.database.repositories.StudentRepository;
import com.example.studentmanager.database.repositories.StudentSubjectCrossrefRepository;
import com.example.studentmanager.database.repositories.SubjectRepository;
import com.example.studentmanager.profile.adapters.StudentGradesAdapter;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class StudentGradeSheetFragment extends Fragment {

    private String studentEmail;

    private StudentRepository studentRepository;
    private StudentSubjectCrossrefRepository studentSubjectCrossrefRepository;
    private SubjectRepository subjectRepository;
    private final AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();

    private TextView studentNameTextView;
    private Button exportButton;
    private RecyclerView gradesRecyclerView;

    public StudentGradeSheetFragment() {
        // Required empty public constructor
    }

    public static StudentGradeSheetFragment newInstance() {
        return new StudentGradeSheetFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentEmail = getArguments().getString("email", "");
        studentRepository = new StudentRepository(getContext());
        studentSubjectCrossrefRepository = new StudentSubjectCrossrefRepository(getContext());
        subjectRepository = new SubjectRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_grade_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initComponents(view);

        displayDataFromDB();

        exportButton.setOnClickListener(v -> {
            performExport();
        });
    }

    private void initComponents(View view) {
        studentNameTextView = view.findViewById(R.id.grade_student_name_tv);
        exportButton = view.findViewById(R.id.grade_export_btn);
        gradesRecyclerView = view.findViewById(R.id.grade_recycler);

        // Initializing the recycler view
        initRecycler();
    }

    private void initRecycler() {
        // Find the student by email first
        Callable<Student> studentCallable = () -> studentRepository.getStudent(studentEmail);
        Callback<Student> studentCallback = (Student student) -> {
            Callable<List<StudentSubjectCrossRef>> callable = () -> studentSubjectCrossrefRepository.getSubjectsRefForStudent(student.getIdStud());
            Callback<List<StudentSubjectCrossRef>> callback = (List<StudentSubjectCrossRef> list) -> {
                StudentGradesAdapter studentGradesAdapter = new StudentGradesAdapter(getContext(), list);
                gradesRecyclerView.setAdapter(studentGradesAdapter);
                gradesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            };
            asyncTaskRunner.executeAsync(callable, callback);
        };

        asyncTaskRunner.executeAsync(studentCallable, studentCallback);
    }

    private void displayDataFromDB() {
        Callable<Student> studentCallable = () -> studentRepository.getStudent(studentEmail);
        Callback<Student> studentCallback = (Student student) -> {
            studentNameTextView.setText(student.getNumeStudent());
        };
        asyncTaskRunner.executeAsync(studentCallable, studentCallback);
    }

    private void performExport() {
        // Get data

        // 1. Get the student
        Callable<Student> studentCallable = () -> studentRepository.getStudent(studentEmail);
        Callback<Student> studentCallback = (Student student) -> {
            // 2. Get his subject data
            Callable<List<StudentSubjectCrossRef>> listCallable = () -> studentSubjectCrossrefRepository.getSubjectsRefForStudent(student.getIdStud());
            Callback<List<StudentSubjectCrossRef>> listCallback = (List<StudentSubjectCrossRef> list) -> {
                // 2.5. Declare the file object where data should be written
                File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }

                File file = new File(exportDir, "gradeSheet3.csv");
                try {
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                // 3. Get subject info
                for(StudentSubjectCrossRef ref : list) {
                    Callable<Subject> subjectCallable = () -> subjectRepository.getSubjectByID(ref.getIdSubject());
                    Callback<Subject> subjectCallback = (Subject subject) -> {

                        FileWriter fileWriter = null;
                        try {
                            fileWriter = new FileWriter(file, true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // 4. Write to CSV file
                        CSVWriter csvWriter = new CSVWriter(fileWriter);
                        String[] data = new String[2];
                        data[0] = subject.getSubjectName();
                        data[1] = Double.valueOf(ref.getGrade()).toString();
                        synchronized (csvWriter) {
                            csvWriter.writeNext(data, true);
                        }

                        try {
                            csvWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    };
                    asyncTaskRunner.executeAsync(subjectCallable, subjectCallback);
                }

                Toast.makeText(getContext(), "File saved at " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            };
            asyncTaskRunner.executeAsync(listCallable, listCallback);
        };

        asyncTaskRunner.executeAsync(studentCallable, studentCallback);
    }
}