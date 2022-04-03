package com.example.studentmanager.database.utils;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {
    public static final String FORMAT_DATE="dd/MM/yyyy";
    public static final SimpleDateFormat formatter=new SimpleDateFormat(FORMAT_DATE, Locale.US);

    @TypeConverter
    public static Date fromString(String value)
    {
        try {
            return formatter.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @TypeConverter
    public static String fromDate(Date value)
    {
        if(value==null)
        {
            return null;
        }
        return formatter.format(value);
    }


}
