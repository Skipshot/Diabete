package com.example.fdelahaye.myapplication.Objects;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by fdelahaye on 04/07/2017.
 */

public class HbA1c {
    private String date;
    private float control;

    //region Constructor
    public HbA1c() {
    }

    public HbA1c(String date, float control) {
        this.date = date;
        this.control = control;
    }
    //endregion

    //region Methods
    public static ArrayList<HbA1c> get(Context context, String filename) {

        Gson gson = new Gson();
        ArrayList<HbA1c> HbA1cList = new ArrayList<HbA1c>();

        if(JsonUtil.fileExists(context, filename)) {
            //read json file
            String jsonFile = JsonUtil.readFromFile(context, filename);
            //convert string jsonFile to List<HbA1c>
            HbA1cList = gson.fromJson(jsonFile, new TypeToken<List<HbA1c>>(){}.getType());
        }
        return HbA1cList;
    }

    public void set(Context context, String filename) {
        //get all HbA1c control
        ArrayList<HbA1c> hbA1cList = HbA1c.get(context, filename);
        //add this new one
        hbA1cList.add(this);
        //re-write file
        JsonUtil.writeToFile(new Gson().toJson(hbA1cList), context, filename);
    }

    public static ArrayList<HbA1c> getDateStartStopHbA1cList(Context context, String filename, Date dStart, Date dStop) {
        ArrayList<HbA1c> hbA1cList = HbA1c.get(context, filename);

        //switch start and stop if start is after stop
        if(dStart.after(dStop)) {
            Date temp = dStart;
            dStart = dStop;
            dStop = temp;
        }

        Calendar calStart = Calendar.getInstance();
        calStart.setTime(dStart);
        Calendar calStop = Calendar.getInstance();
        calStop.setTime(dStop);

        for (Iterator<HbA1c> iterator = hbA1cList.iterator(); iterator.hasNext();) {
            HbA1c h = iterator.next();
            Date currentDate = Utils.parseDate(h.getDate());
            if (currentDate.before(dStart) || currentDate.after(dStop)) {
                iterator.remove();
            }
        }

        //sort objects by descending
        Collections.sort(hbA1cList, new Comparator<HbA1c>() {
            @Override
            public int compare(HbA1c o1, HbA1c o2) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(sdf.parse(o1.getDate()));

                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(sdf.parse(o2.getDate()));

                    return c2.compareTo(c1);        //descending
                    // return c1.compareTo(c2);     //ascending
                    //return o1.getDate().compareTo(o2.getDate());      //origin
                }
                catch (ParseException ex) {
                    ex.printStackTrace();
                }
                return 0;
            }
        });

        return hbA1cList;
    }
    //endregion

    //region Getters & Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getControl() {
        return control;
    }

    public void setControl(float control) {
        this.control = control;
    }
    //endregion
}
