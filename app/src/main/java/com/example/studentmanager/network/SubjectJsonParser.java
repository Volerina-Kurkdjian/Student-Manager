package com.example.studentmanager.network;

import android.os.Build;

import com.example.studentmanager.database.models.Diploma;
import com.example.studentmanager.database.models.Profesor;
import com.example.studentmanager.database.models.Subject;
import com.example.studentmanager.database.utils.DateConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SubjectJsonParser {

    public static final String SUBJECT_NAME="name";
    public static final String  EXAM_DATE="exam_date";
    public static final String NUMBER_OF_TESTS="number_of_tests";
    public static final String PROFESSOR="professor";
    public static final String PROFESSOR_NAME="name";
    public static final String EMAIL="email";
    public static final String PASSWORD="password";

    public static List<Subject> SubjectsfromJson(String json)
    {
        try {
            JSONArray array=new JSONArray(json);
            return readSubjects(array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();

    }

    private static List<Subject> readSubjects(JSONArray array) throws JSONException {
        List<Subject> subjects=new ArrayList<>();
        for(int i=0;i<array.length();i++)
        {
            Subject subject=readSubject(array.getJSONObject(i));
            subjects.add(subject);
        }
        return  subjects;
    }


    private static Subject readSubject(JSONObject object) throws JSONException {
        String subjectName=object.getString(SUBJECT_NAME);
        Date  subjectDateExam= DateConverter.fromString(object.getString(EXAM_DATE));
        int nbtest=Integer.parseInt(object.getString(NUMBER_OF_TESTS));

        return new Subject(subjectName,subjectDateExam,nbtest);
    }

    public static List<Profesor> ProfesorsFromJson(String json)
    {
        try {
            JSONArray array=new JSONArray(json);
            return readProfessors(array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();

    }


    private static  List<Profesor> readProfessors(JSONArray array) throws JSONException {
        List<Profesor> profesors=new ArrayList<>();
        for(int i=0;i<array.length();i++)
        {
            Profesor prof=readProfesor(array.getJSONObject(i).getJSONObject(PROFESSOR));
            profesors.add(prof);
        }
        return profesors;
    }



    private static Profesor readProfesor(JSONObject object) throws JSONException
    {
        String nameProfesor =object.getString(PROFESSOR_NAME);
        String emailProfesor=object.getString(EMAIL);
        String pass=object.getString(PASSWORD);
        JSONObject diplomaobject=object.getJSONObject("diploma");
        Diploma d=new Diploma(diplomaobject.getString("name"),diplomaobject.getInt("grade"));
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            md.update(pass.getBytes(StandardCharsets.UTF_8));
        }
        byte[] digest=md.digest();//will create the hash and put it into a byte array
        String hashedPassword=String.format("%064x",new BigInteger(1,digest));


        return new Profesor(emailProfesor,nameProfesor,hashedPassword,d);
    }



}
