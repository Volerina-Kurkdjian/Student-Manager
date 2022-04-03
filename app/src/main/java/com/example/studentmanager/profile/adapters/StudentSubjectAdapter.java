package com.example.studentmanager.profile.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanager.R;
import com.example.studentmanager.async.AsyncTaskRunner;
import com.example.studentmanager.async.Callback;
import com.example.studentmanager.database.models.Student;
import com.example.studentmanager.database.models.StudentSubjectCrossRef;
import com.example.studentmanager.database.repositories.StudentRepository;
import com.example.studentmanager.database.repositories.StudentSubjectCrossrefRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class StudentSubjectAdapter extends RecyclerView.Adapter<StudentSubjectAdapter.StudentSubjectViewHolder> {

    private final List<StudentSubjectCrossRef> list;
    private final LayoutInflater layoutInflater;
    private final StudentRepository studentRepository;
    private final StudentSubjectCrossrefRepository studentSubjectCrossrefRepository;
    private final AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();
    private final Context context;

    public StudentSubjectAdapter(Context context, List<StudentSubjectCrossRef> list) {
        this.list = list;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        studentRepository = new StudentRepository(context);
        studentSubjectCrossrefRepository = new StudentSubjectCrossrefRepository(context);
    }

    @NonNull
    @Override
    public StudentSubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.subject_item_layout, parent, false);
        return new StudentSubjectViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentSubjectViewHolder holder, int position) {
        StudentSubjectCrossRef crossRef = list.get(position);
        initializeItemFromDB(holder, crossRef);

        holder.studentSubjectGradeUpdateButton.setOnClickListener(v -> {
            if(!holder.studentSubjectGrade.getText().toString().equals("")) {
                double newGrade = Double.parseDouble(holder.studentSubjectGrade.getText().toString());
                if (newGrade >= 0 && newGrade < 11) {
                    updateStudentGrade(newGrade, crossRef);
                }
                else {
                    Toast.makeText(context, "Grade must be between 0 and 10", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(context, "Grade must have a value", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void initializeItemFromDB(@NonNull StudentSubjectViewHolder holder, StudentSubjectCrossRef crossRef) {
        int studentID = crossRef.getIdStud();
        Callable<Student> callable = () -> studentRepository.getStudentByID(studentID);
        Callback<Student> callback = (Student student) -> {
            holder.studentName.setText(student.getNumeStudent());
            holder.studentSubjectGrade.setText(Double.valueOf(crossRef.getGrade()).toString());
        };
        asyncTaskRunner.executeAsync(callable, callback);
    }

    private void updateStudentGrade(double newGrade, StudentSubjectCrossRef crossRef) {
        crossRef.setGrade(newGrade);
        Callable<Integer> callable = () -> studentSubjectCrossrefRepository.update(crossRef);
        Callback<Integer> callback = (Integer nr) -> {
            Toast.makeText(context, "New student grade saved!", Toast.LENGTH_SHORT).show();
        };
        asyncTaskRunner.executeAsync(callable, callback);
    }

    class StudentSubjectViewHolder extends RecyclerView.ViewHolder {

        private final StudentSubjectAdapter studentSubjectAdapter;
        public final TextView studentName;
        public final EditText studentSubjectGrade;
        public final Button studentSubjectGradeUpdateButton;

        public StudentSubjectViewHolder(View itemView, StudentSubjectAdapter studentSubjectAdapter) {
            super(itemView);
            this.studentSubjectAdapter = studentSubjectAdapter;
            this.studentName = itemView.findViewById(R.id.prof_subject_student_name_tv);
            this.studentSubjectGrade = itemView.findViewById(R.id.prof_subject_student_grade_et);
            this.studentSubjectGradeUpdateButton = itemView.findViewById(R.id.prof_subject_student_update_btn);
        }
    }
}
