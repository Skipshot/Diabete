package com.example.fdelahaye.myapplication.Objects;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fdelahaye.myapplication.Objects.Glycaemia;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fdelahaye on 30/06/2017.
 */

public class Utils {

    public static TextView AddCell(Context context, String text) {
        TextView tv = new TextView(context);
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    public static Date parseTime(String date) {
        try {
            return new SimpleDateFormat("HH:mm").parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    public static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    public static Date parseDateTime(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    public static boolean isMeal(String timeOfTheDay) {
        if(!TextUtils.isEmpty(timeOfTheDay))
            return timeOfTheDay.equals(Glycaemia.timeOfTheDayEnum.BREAKFAST.name()) ||
                    timeOfTheDay.equals(Glycaemia.timeOfTheDayEnum.LUNCH.name()) ||
                    timeOfTheDay.equals(Glycaemia.timeOfTheDayEnum.DINNER.name());
        else
            return false;
    }

    public static boolean isFieldsValid(EditText glucoseCheck, EditText glucoseFood, String timeOfTheDay) {
        if(isMeal(timeOfTheDay))
            return !TextUtils.isEmpty(glucoseCheck.getText().toString()) &&
                    !TextUtils.isEmpty(glucoseFood.getText().toString());
        else
            return !TextUtils.isEmpty(glucoseCheck.getText().toString());
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
