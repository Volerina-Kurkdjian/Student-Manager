package com.example.studentmanager.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.studentmanager.MainActivity;
import com.example.studentmanager.R;
import com.example.studentmanager.async.AsyncTaskRunner;
import com.example.studentmanager.async.Callback;
import com.example.studentmanager.database.models.Student;
import com.example.studentmanager.database.models.Subject;
import com.example.studentmanager.database.repositories.StudentRepository;
import com.example.studentmanager.database.repositories.SubjectRepository;
import com.example.studentmanager.profile.adapters.SpinnerAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;


public class StudentProfileFragment extends Fragment {

    private final String SHARED_PREF_FILE_NAME="loginSharedPref";
    private SharedPreferences sharedPreferences;

    private String email;

    private StudentRepository studentRepository;
    private SubjectRepository subjectRepository;
    private final AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView registrationNumberTextView;
    private TextView groupNumberTextView;
    private RadioGroup financialRadioGroup;
    private RadioButton budgetRadioButton;
    private RadioButton taxRadioButton;
    private Spinner subjectsSpinner;
    private Button editButton;
    private Button deleteButton;
    private Button logoutButton;
    private Button reportButton;

    private Subject selectedSubject;

    public StudentProfileFragment() {
        // Required empty public constructor
    }

    public static StudentProfileFragment newInstance() {
        return new StudentProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get extra email of current user from login fragment
        email = getActivity().getIntent().getExtras().getString("email", "");
        studentRepository = new StudentRepository(getContext());
        subjectRepository = new SubjectRepository(getContext());
        sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // bind variables to Views
        initComponents(view);

        // Display profile data from DB
        displayDataFromDB();

        logoutButton.setOnClickListener(v -> {
            performLogout();
        });

        deleteButton.setOnClickListener(v -> {
            performDelete();
        });

        editButton.setOnClickListener(v -> {
            performNavigationToUpdateFragment(view);
        });

        reportButton.setOnClickListener(v -> {
            performNavigationToGradeSheetFragment(view);
        });

        subjectsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSubject = (Subject)parent.getItemAtPosition(position);
                if(!selectedSubject.getSubjectName().equals("select a subject"))
                {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("subject", selectedSubject);
                    bundle.putString("email", email);
                    Navigation.findNavController(view).navigate(R.id.studentSubjectFragment, bundle);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void displayDataFromDB() {
        Callable<Student> callable = () -> studentRepository.getStudent(email);
        Callback<Student> callback = (Student student) -> {
            nameTextView.setText(student.getNume());
            emailTextView.setText((student.getEmailStudent()));
            registrationNumberTextView.setText(Integer.valueOf(student.getNumarMatricol()).toString());
            groupNumberTextView.setText(Integer.valueOf(student.getGroup()).toString());

            String frminv = student.getFrminvat();
            financialRadioGroup.clearCheck();
            if(frminv.equals("Budget")) {
                budgetRadioButton.toggle();
            }
            else {
                taxRadioButton.toggle();
            }
        };
        asyncTaskRunner.executeAsync(callable, callback);

        // Get the data for the spinner
        displaySpinnerData();
    }

    private void displaySpinnerData() {
        Callable<List<Subject>> callable = () -> subjectRepository.getAll();
        Callback<List<Subject>> callback = (List<Subject> subjects) -> {
            Subject nousub=new Subject("select a subject",new Date(),0);
            ArrayList<Subject> arraysub=new ArrayList<>();
            arraysub.add(nousub);
            for(int i=0;i<subjects.size();i++)
            {
                arraysub.add(subjects.get(i));
            }
            SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getContext(), R.layout.custom_spinner_subjects, arraysub);
            subjectsSpinner.setAdapter(spinnerAdapter);
        };
        asyncTaskRunner.executeAsync(callable, callback);
    }

    private void deleteSharedPreferences() {
        sharedPreferences.edit().clear().apply();
    }

    private void performLogout() {
        // The logout action means we clear saved credentials and then redirect to main activity
        deleteSharedPreferences();

        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void performDelete() {
        // First we retrieve the student
        Callable<Student> retrieveCallable = () -> studentRepository.getStudent(email);
        Callback<Student> retrieveCallback = (Student student) -> {
            // Now we delete the student
            Callable<Integer> deleteCallable = () -> studentRepository.delete(student);
            Callback<Integer> deleteCallback = (Integer nr) -> {
                System.out.println("deleted student");
                // Now we perform a normal logout
                performLogout();
            };

            asyncTaskRunner.executeAsync(deleteCallable, deleteCallback);
        };

        asyncTaskRunner.executeAsync(retrieveCallable, retrieveCallback);
    }

    private void performNavigationToUpdateFragment(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        Navigation.findNavController(view).navigate(R.id.updateStudentFragment, bundle);
    }

    private void performNavigationToGradeSheetFragment(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        Navigation.findNavController(view).navigate(R.id.studentGradeSheetFragment, bundle);
    }

    private void initComponents(View view) {
        nameTextView = view.findViewById(R.id.student_profile_name_tv);
        emailTextView = view.findViewById(R.id.student_profile_email_tv);
        registrationNumberTextView = view.findViewById(R.id.student_register_number_tv);
        groupNumberTextView = view.findViewById(R.id.student_group_number_tv);
        financialRadioGroup = view.findViewById(R.id.student_frminv_rg);
        budgetRadioButton = view.findViewById(R.id.student_budget_profile);
        taxRadioButton = view.findViewById(R.id.student_tax_profile);
        subjectsSpinner = view.findViewById(R.id.student_profile_spinner);
        editButton = view.findViewById(R.id.student_edit_profile);
        deleteButton = view.findViewById(R.id.student_delete_profile);
        logoutButton = view.findViewById(R.id.student_logout_profile);
        reportButton = view.findViewById(R.id.student_profile_report_btn);
    }
}