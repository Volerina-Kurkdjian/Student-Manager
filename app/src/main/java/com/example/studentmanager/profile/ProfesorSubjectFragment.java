package com.example.studentmanager.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
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
import com.example.studentmanager.profile.adapters.StudentSubjectAdapter;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;


public class ProfesorSubjectFragment extends Fragment {

    private Subject currentSubject;

    private StudentSubjectCrossrefRepository studentSubjectCrossrefRepository;
    private SubjectRepository subjectRepository;
    private StudentRepository studentRepository;
    private final AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();

    private CalendarView examCalendarView;
    private RecyclerView studentsRecyclerView;
    private Button chartButton;
    private Button exportreport;

    public ProfesorSubjectFragment() {
        // Required empty public constructor
    }

    public static ProfesorSubjectFragment newInstance() {
        return new ProfesorSubjectFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        currentSubject = getArguments().getParcelable("subject");

        studentRepository=new StudentRepository(getContext());
        studentSubjectCrossrefRepository = new StudentSubjectCrossrefRepository(getContext());
        subjectRepository = new SubjectRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profesor_subject, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        examCalendarView = view.findViewById(R.id.profesor_subject_date_picker);
        studentsRecyclerView = view.findViewById(R.id.profesor_subject_recycler);
        chartButton = view.findViewById(R.id.chart_btn);
        exportreport=view.findViewById(R.id.export_teacher);
        // Add data to recyclerView
        populateRecyclerView();

        // Set the right Exam Date in the calendar view
        examCalendarView.setDate(currentSubject.getSubjectDateExam().getTime());

        examCalendarView.setOnDateChangeListener((CalendarView v, int year, int month, int dayOfMonth) -> {
            // Change the exam date for this subject
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            Date date = calendar.getTime();
            currentSubject.setSubjectDateExam(date);
            changeExamDateForSubject(currentSubject);
        });

        chartButton.setOnClickListener(v -> {
            performNavigationToChartFragment(view);
        });

        exportreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                exportReport();
            }
        });

    }

    private void exportReport()
    {
        Callable<List<StudentSubjectCrossRef>> callable=new Callable<List<StudentSubjectCrossRef>>() {
            @Override
            public List<StudentSubjectCrossRef> call() throws Exception {
                return studentSubjectCrossrefRepository.getStudentsRefInSubject(currentSubject.getIdSubject());
            }
        };

        Callback<List<StudentSubjectCrossRef>> callback=new Callback<List<StudentSubjectCrossRef>>() {
            @Override
            public void runResultOnUIThread(List<StudentSubjectCrossRef> result) {

                File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }

                File file = new File(exportDir, "studentReport.csv");
                try {
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                for(StudentSubjectCrossRef ref : result) {
                    Callable<Student> studentCallable = () -> studentRepository.getStudentByID(ref.getIdStud());
                    Callback<Student> studentCallback = (Student student) -> {

                        FileWriter fileWriter = null;
                        try {
                            fileWriter = new FileWriter(file, true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // 4. Write to CSV file
                        CSVWriter csvWriter = new CSVWriter(fileWriter);
                        String[] data = new String[2];
                        data[0] = student.getNumeStudent();
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
                    asyncTaskRunner.executeAsync(studentCallable, studentCallback);
                }
            }
        };

        asyncTaskRunner.executeAsync(callable,callback);

    }


    private void populateRecyclerView() {
        Callable<List<StudentSubjectCrossRef>> callable = () -> studentSubjectCrossrefRepository.getStudentsRefInSubject(currentSubject.getIdSubject());
        Callback<List<StudentSubjectCrossRef>> callback = (List<StudentSubjectCrossRef> crossRefList) -> {
            StudentSubjectAdapter studentSubjectAdapter = new StudentSubjectAdapter(getContext(), crossRefList);
            studentsRecyclerView.setAdapter(studentSubjectAdapter);
            studentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        };
        asyncTaskRunner.executeAsync(callable, callback);
    }

    private void changeExamDateForSubject(Subject subject) {
        Callable<Integer> callable = () -> subjectRepository.update(subject);
        Callback<Integer> callback = (Integer nr) -> {
            System.out.println("Exam Date changed");
            Toast.makeText(getContext(), "Exam Date Saved", Toast.LENGTH_SHORT).show();
        };
        asyncTaskRunner.executeAsync(callable, callback);
    }

    private void performNavigationToChartFragment(View view) {
        // Get the grade data
        Callable<List<StudentSubjectCrossRef>> callable = () -> studentSubjectCrossrefRepository.getStudentsRefInSubject(currentSubject.getIdSubject());
        Callback<List<StudentSubjectCrossRef>> callback = (List<StudentSubjectCrossRef> crossRefList) -> {
            // sort the data
            int passed = 0;
            int failed = 0;
            int ungraded = 0;
            for (StudentSubjectCrossRef ref : crossRefList) {
                if(ref.getGrade() == 0) {
                    ungraded ++;
                }
                else if (ref.getGrade() < 5) {
                    failed++;
                } else {
                    passed++;
                }
            }

            Bundle bundle = new Bundle();
            bundle.putInt("passed", passed);
            bundle.putInt("failed", failed);
            bundle.putInt("ungraded", ungraded);
            Navigation.findNavController(view).navigate(R.id.chartFragment, bundle);
        };
        asyncTaskRunner.executeAsync(callable, callback);
    }
}