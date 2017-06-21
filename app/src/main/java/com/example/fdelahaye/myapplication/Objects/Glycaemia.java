package com.example.fdelahaye.myapplication.Objects;

import android.content.Context;

import com.example.fdelahaye.myapplication.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
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
 * Created by fdelahaye on 29/05/2017.
 */

public class Glycaemia {
    private String dateCreate;
    private String dateUpdate;
    private String timeOfTheDay;
    private short glucoseCheck;
    private short glucoseFood;
    private float insulineMeal;
    private float insulineTreat;
    private float bolus;
    private float ratio;
    private float indexTreat;
    private float objective;
    private Float acetone;
    private String injectionBody;
    private String comment;

    public enum timeOfTheDayEnum {
        BREAKFAST(0),
        MORNING(1),
        LUNCH(2),
        AFTERNOON(3),
        DINNER(4),
        SLEEP(5),
        NIGHT(6);

        private final int position;

        timeOfTheDayEnum(int position) {
            this.position = position;
        }
        public int getPosition() {
            return position;
        }
    }

    //region Constructor
    public Glycaemia() {}

    public Glycaemia(String dateCreate, String dateUpdate, String timeOfTheDay, short glucoseCheck, short glucoseFood, float insulineMeal, float insulineTreat, float bolus, float ratio, float indexTreat, float objective, Float acetone, String injectionBody, String comment) {
        this.dateCreate = dateCreate;
        this.dateUpdate = dateUpdate;
        this.timeOfTheDay = timeOfTheDay;
        this.glucoseCheck = glucoseCheck;
        this.glucoseFood = glucoseFood;
        this.insulineMeal = insulineMeal;
        this.insulineTreat = insulineTreat;
        this.bolus = bolus;
        this.ratio = ratio;
        this.indexTreat = indexTreat;
        this.objective = objective;
        this.acetone = acetone;
        this.injectionBody = injectionBody;
        this.comment = comment;
    }
    //endregion

    //region Methods
    public static ArrayList<Glycaemia> getGlycaemiaList(Context context, String filename) {
        Gson gson = new Gson();
        ArrayList<Glycaemia> glycaemiaList = new ArrayList<Glycaemia>();

        if(JsonUtil.fileExists(context, filename)) {
            //read json file
            String jsonFile = JsonUtil.readFromFile(context, filename);
            //convert string jsonFile to List<Glycaemia>
            glycaemiaList = gson.fromJson(jsonFile, new TypeToken<List<Glycaemia>>(){}.getType());
        }
        return glycaemiaList;
    }

    public static ArrayList<Glycaemia> getTodayGlycaemiaList(ArrayList<Glycaemia> glycaemiaList) {
        ArrayList<Glycaemia> todayList = new ArrayList<Glycaemia>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            for (Glycaemia glycaemia : glycaemiaList) {
                Calendar calGlycaemia = Calendar.getInstance();
                Calendar calToday = Calendar.getInstance();
                calGlycaemia.setTime(sdf.parse(glycaemia.getDateCreate()));

                if (calGlycaemia.get(Calendar.YEAR) == calToday.get(Calendar.YEAR) &&
                    calGlycaemia.get(Calendar.DAY_OF_YEAR) == calToday.get(Calendar.DAY_OF_YEAR)) {

                        todayList.add(glycaemia);
                }
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return todayList;
    }

    public static ArrayList<Glycaemia> getRemindGlycaemiaList(ArrayList<Glycaemia> glycaemiaList, String timeOfTheDay) {
        if(glycaemiaList.size() <= 0) return null;

        ArrayList<Glycaemia> remindList = new ArrayList<Glycaemia>();

        int i = 0;

        //sort objects by descending
        Collections.sort(glycaemiaList, new Comparator<Glycaemia>() {
            @Override
            public int compare(Glycaemia o1, Glycaemia o2) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(sdf.parse(o1.getDateCreate()));

                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(sdf.parse(o2.getDateCreate()));

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

        for (Glycaemia glycaemia : glycaemiaList) {
            if (timeOfTheDay.equals(glycaemia.getTimeOfTheDay())) {
                remindList.add(glycaemia);
                i++;
            }
            if(i >= 7) break;
        }

        return remindList;
    }

    public static ArrayList<Glycaemia> getDateStartStopGlycaemiaList(Context context, String baseFilename, Date dStart, Date dStop) {
        ArrayList<Glycaemia> glycaemiaList = new ArrayList<>();

        if(dStart.after(dStop)) {
            Date temp = dStart;
            dStart = dStop;
            dStop = temp;
        }

        Calendar calStart = Calendar.getInstance();
        calStart.setTime(dStart);
        Calendar calStop = Calendar.getInstance();
        calStop.setTime(dStop);

        int monthStart = calStart.get(Calendar.MONTH) + 1 ;
        int yearStart = calStart.get(Calendar.YEAR);

        int monthStop = calStop.get(Calendar.MONTH) + 1;
        int yearStop = calStop.get(Calendar.YEAR);

        for (int year = yearStart; year <= yearStop; year++) {   //for each year between start and stop
            for (int month = monthStart; month <= monthStop; month++) {  //for each month between start and stop
                glycaemiaList.addAll( getGlycaemiaList(context, String.format("%s%s-%s.json", baseFilename, year, month) ) );
            }
        }

        //TODO : foreach with iterator, need to transform iterator to arraylist
        for (Iterator<Glycaemia> iterator = glycaemiaList.iterator(); iterator.hasNext();) {
            Glycaemia g = iterator.next();
            Date currentDate = parseDate(g.getDateCreate());
            if (currentDate.before(dStart) || currentDate.after(dStop)) {
                iterator.remove();
            }
        }

        //sort objects by descending
        Collections.sort(glycaemiaList, new Comparator<Glycaemia>() {
            @Override
            public int compare(Glycaemia o1, Glycaemia o2) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(sdf.parse(o1.getDateCreate()));

                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(sdf.parse(o2.getDateCreate()));

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

        return glycaemiaList;
    }

    public static void WriteGlycaemiaFile(List<Glycaemia> glycaemiaList, Context context, String filename) {
        JsonUtil.writeToFile(new Gson().toJson(glycaemiaList), context, filename);
    }

    private static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    //endregion

    //region Getters & Setters
    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String date) {
        this.dateCreate = date;
    }

    public String getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(String dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public short getGlucoseCheck() {
        return glucoseCheck;
    }

    public void setGlucoseCheck(short glucoseCheck) {
        this.glucoseCheck = glucoseCheck;
    }

    public short getGlucoseFood() {
        return glucoseFood;
    }

    public void setGlucoseFood(short glucoseFood) {
        this.glucoseFood = glucoseFood;
    }

    public float getInsulineMeal() {
        return insulineMeal;
    }

    public void setInsulineMeal(float insulineMeal) {
        this.insulineMeal = insulineMeal;
    }

    public float getInsulineTreat() {
        return insulineTreat;
    }

    public void setInsulineTreat(float insulineTreat) {
        this.insulineTreat = insulineTreat;
    }

    public float getBolus() {
        return bolus;
    }

    public void setBolus(float bolus) {
        this.bolus = bolus;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public float getIndexTreat() {
        return indexTreat;
    }

    public void setIndexTreat(float indexTreat) {
        this.indexTreat = indexTreat;
    }

    public float getObjective() {
        return objective;
    }

    public void setObjective(float objective) {
        this.objective = objective;
    }

    public Float getAcetone() {
        return acetone;
    }

    public void setAcetone(Float acetone) {
        this.acetone = acetone;
    }

    public String getInjectionBody() {
        return injectionBody;
    }

    public void setInjectionBody(String injectionBody) {
        this.injectionBody = injectionBody;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimeOfTheDay() {
        return timeOfTheDay;
    }

    public void setTimeOfTheDay(String timeOfTheDay) {
        this.timeOfTheDay = timeOfTheDay;
    }
    //endregion
}
