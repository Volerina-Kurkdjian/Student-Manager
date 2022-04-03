package com.example.studentmanager.profile.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanager.R;
import com.example.studentmanager.async.AsyncTaskRunner;
import com.example.studentmanager.async.Callback;
import com.example.studentmanager.database.models.StudentSubjectCrossRef;
import com.example.studentmanager.database.models.Subject;
import com.example.studentmanager.database.repositories.SubjectRepository;

import java.util.List;
import java.util.concurrent.Callable;

public class StudentGradesAdapter extends RecyclerView.Adapter<StudentGradesAdapter.StudentGradesViewHolder> {

    private final List<StudentSubjectCrossRef> crossRefList;
    private final LayoutInflater layoutInflater;
    private final SubjectRepository subjectRepository;
    private final AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();
    private final Context context;

    public StudentGradesAdapter(Context context, List<StudentSubjectCrossRef> crossRefList) {
        this.context = context;
        this.crossRefList = crossRefList;
        this.layoutInflater = LayoutInflater.from(context);
        subjectRepository = new SubjectRepository(context);
    }

    @NonNull
    @Override
    public StudentGradesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.grade_item_layout, parent, false);
        return new StudentGradesViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentGradesViewHolder holder, int position) {
        StudentSubjectCrossRef crossRef = crossRefList.get(position);

        // Get the subject name from DB
        displaySubjectName(holder, crossRef);

        if (crossRef.getGrade() == 0) {
            holder.grade.setText("Not Graded");
            holder.grade.setTextColor(Color.parseColor("#D32F2F"));
        }
        else {
            holder.grade.setText(Double.valueOf(crossRef.getGrade()).toString());

            if (crossRef.getGrade() < 5) {
                holder.grade.setTextColor(Color.parseColor("#D32F2F"));
            }
            else {
                holder.grade.setTextColor(Color.parseColor("#388E3C"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return crossRefList.size();
    }

    private void displaySubjectName(@NonNull StudentGradesViewHolder holder, StudentSubjectCrossRef crossRef) {
        Callable<Subject> callable = () -> subjectRepository.getSubjectByID(crossRef.getIdSubject());
        Callback<Subject> callback = (Subject subject) -> {
            holder.subjectName.setText(subject.getSubjectName());
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }

    static class StudentGradesViewHolder extends RecyclerView.ViewHolder {
        private final StudentGradesAdapter studentGradesAdapter;
        public final TextView subjectName;
        public final TextView grade;

        public StudentGradesViewHolder(View itemView, StudentGradesAdapter studentGradesAdapter) {
            super(itemView);
            this.studentGradesAdapter = studentGradesAdapter;
            this.subjectName = itemView.findViewById(R.id.grade_item_subject_name);
            this.grade = itemView.findViewById(R.id.grade_item_grade);
        }
    }
}
