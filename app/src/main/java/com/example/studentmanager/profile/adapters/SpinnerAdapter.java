package com.example.studentmanager.profile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.studentmanager.R;
import com.example.studentmanager.database.models.Subject;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<Subject> {


    LayoutInflater layoutInflater;

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Subject> subjects) {
        super(context, resource, subjects);

        layoutInflater=LayoutInflater.from(context);



    }


    private int getImageId(String subjectName)
    {
        switch (subjectName)
        {
            case "Android":
                    return R.drawable.android;
            case "Data structures":
                return R.drawable.laptop;
            case "Windows application programming":
                return R.drawable.programming;
            case "Evolutive programming":
                return R.drawable.motherboard;
            case "Multiparadigm programming Java":
                return R.drawable.hack;
        }
        return  R.color.white;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView=layoutInflater.inflate(R.layout.custom_spinner_subjects,null, true);
        Subject subject=getItem(position);
        TextView textView=rowView.findViewById(R.id.subjecttext);
        ImageView imageView=rowView.findViewById(R.id.subjectimage);
        textView.setText(subject.getSubjectName());
        imageView.setImageResource(getImageId(subject.getSubjectName()));

        return rowView;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView==null)
             convertView=layoutInflater.inflate(R.layout.custom_spinner_subjects,parent, false);

        Subject subject=getItem(position);
        TextView textView=convertView.findViewById(R.id.subjecttext);
        ImageView imageView=convertView.findViewById(R.id.subjectimage);
        textView.setText(subject.getSubjectName());
        imageView.setImageResource(getImageId(subject.getSubjectName()));

        return convertView;
    }
}
