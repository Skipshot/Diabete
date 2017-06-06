package com.example.fdelahaye.myapplication.Objects;

import android.content.Context;

import com.example.fdelahaye.myapplication.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fdelahaye on 29/05/2017.
 */

public class Settings {
    private String date;
    private float breakfastRatio;
    private float breakfastIndex;
    private float lunchRatio;
    private float lunchIndex;
    private float dinnerRatio;
    private float dinnerIndex;
    private short goalBeforeMeal;
    private short goalOutMeal;
    private String hourBreakfast;
    private String hourLunch;
    private String hourDinner;
    private boolean notificationHbA1c;
    private String delaisNotifHbA1c;

    //region Contructor
    public Settings() {
    }

    //Contructor from settings.json file
    public Settings(Context context, String filename) {
        try {
            if(JsonUtil.fileExists(context, filename)) {
                //read json file
                String jsonFile = JsonUtil.readFromFile(context, filename);
                //convert to json object
                JSONObject jObj = new JSONObject(jsonFile);

                //add values of Settings object with Json
                this.date = jObj.getString("date");
                this.breakfastRatio = (float)jObj.optDouble("breakfast ratio");
                this.breakfastIndex = (float)jObj.optDouble("breakfast index");
                this.lunchRatio = (float)jObj.optDouble("lunch ratio");
                this.lunchIndex = (float)jObj.optDouble("lunch index");
                this.dinnerRatio = (float)jObj.optDouble("dinner ratio");
                this.dinnerIndex = (float)jObj.optDouble("dinner index");
                this.goalBeforeMeal = (short)jObj.optInt("goal before meal");
                this.goalOutMeal = (short)jObj.optInt("goal out meal");
                this.hourBreakfast = jObj.optString("hour breakfast");
                this.hourLunch = jObj.optString("hour lunch");
                this.hourDinner = jObj.optString("hour dinner");
                this.notificationHbA1c = jObj.optBoolean("notifications HbA1c");
                this.delaisNotifHbA1c = jObj.optString("delais notifications HbA1c");
            }
        }
        catch(JSONException ex) {
            ex.printStackTrace();
        }
    }

    public Settings(String date, short breakfastRatio, float breakfastIndex, short lunchRatio, float lunchIndex, short dinnerRatio, float dinnerIndex, short goalBeforeMeal, short goalOutMeal, String hourBreakfast, String hourLunch, String hourDinner, boolean notificationHbA1c, String delaisNotifHbA1c) {
        this.date = date;
        this.breakfastRatio = breakfastRatio;
        this.breakfastIndex = breakfastIndex;
        this.lunchRatio = lunchRatio;
        this.lunchIndex = lunchIndex;
        this.dinnerRatio = dinnerRatio;
        this.dinnerIndex = dinnerIndex;
        this.goalBeforeMeal = goalBeforeMeal;
        this.goalOutMeal = goalOutMeal;
        this.hourBreakfast = hourBreakfast;
        this.hourLunch = hourLunch;
        this.hourDinner = hourDinner;
        this.notificationHbA1c = notificationHbA1c;
        this.delaisNotifHbA1c = delaisNotifHbA1c;
    }
    //endregion


    //region Methods
    public static Settings getJsonSettings(Context context, String filename) {
        try {
            Settings s = new Settings();
            if(JsonUtil.fileExists(context, filename)) {
                //read json file
                String jsonFile = JsonUtil.readFromFile(context, filename);
                //convert to json object
                JSONObject jObj = new JSONObject(jsonFile);

                //add values of Settings object with Json
                s.setDate(jObj.getString("date"));
                s.setBreakfastRatio((float)jObj.optDouble("breakfast ratio"));
                s.setBreakfastIndex((float)jObj.optDouble("breakfast index"));
                s.setLunchRatio((float)jObj.optDouble("lunch ratio"));
                s.setLunchIndex((float)jObj.optDouble("lunch index"));
                s.setDinnerRatio((float)jObj.optDouble("dinner ratio"));
                s.setDinnerIndex((float)jObj.optDouble("dinner index"));
                s.setGoalBeforeMeal((short)jObj.optInt("goal before meal"));
                s.setGoalOutMeal((short)jObj.optInt("goal out meal"));
                s.setHourBreakfast(jObj.optString("hour breakfast"));
                s.setHourLunch(jObj.optString("hour lunch"));
                s.setHourDinner(jObj.optString("hour dinner"));
                s.setNotificationHbA1c(jObj.optBoolean("notifications HbA1c"));
                s.setDelaisNotifHbA1c(jObj.optString("delais notifications HbA1c"));
            }

            return s;
        }
        catch(JSONException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static JSONObject setJsonSettings(Settings settings, Context context, String filename) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("date", settings.getDate());
            jsonObj.put("breakfast ratio", settings.getBreakfastRatio());
            jsonObj.put("breakfast index", settings.getBreakfastIndex());
            jsonObj.put("lunch ratio", settings.getLunchRatio());
            jsonObj.put("lunch index", settings.getLunchIndex());
            jsonObj.put("dinner ratio", settings.getDinnerRatio());
            jsonObj.put("dinner index", settings.getDinnerIndex());
            jsonObj.put("goal before meal", settings.getGoalBeforeMeal());
            jsonObj.put("goal out meal", settings.getGoalOutMeal());
            jsonObj.put("hour breakfast", settings.getHourBreakfast());
            jsonObj.put("hour lunch", settings.getHourLunch());
            jsonObj.put("hour dinner", settings.getHourDinner());
            jsonObj.put("notifications HbA1c", settings.isNotificationHbA1c());
            jsonObj.put("delais notifications HbA1c", settings.getDelaisNotifHbA1c());

            JsonUtil.writeToFile(jsonObj.toString(), context, filename);

            return jsonObj;
        }
        catch(JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    //endregion


    //region Getters & Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getBreakfastRatio() {
        return breakfastRatio;
    }

    public void setBreakfastRatio(float breakfastRatio) {
        this.breakfastRatio = breakfastRatio;
    }

    public float getBreakfastIndex() {
        return breakfastIndex;
    }

    public void setBreakfastIndex(float breakfastIndex) {
        this.breakfastIndex = breakfastIndex;
    }

    public float getLunchRatio() {
        return lunchRatio;
    }

    public void setLunchRatio(float lunchRatio) {
        this.lunchRatio = lunchRatio;
    }

    public float getLunchIndex() {
        return lunchIndex;
    }

    public void setLunchIndex(float lunchIndex) {
        this.lunchIndex = lunchIndex;
    }

    public float getDinnerRatio() {
        return dinnerRatio;
    }

    public void setDinnerRatio(float dinnerRatio) {
        this.dinnerRatio = dinnerRatio;
    }

    public float getDinnerIndex() {
        return dinnerIndex;
    }

    public void setDinnerIndex(float dinnerIndex) {
        this.dinnerIndex = dinnerIndex;
    }

    public short getGoalBeforeMeal() {
        return goalBeforeMeal;
    }

    public void setGoalBeforeMeal(short goalBeforeMeal) {
        this.goalBeforeMeal = goalBeforeMeal;
    }

    public short getGoalOutMeal() {
        return goalOutMeal;
    }

    public void setGoalOutMeal(short goalOutMeal) {
        this.goalOutMeal = goalOutMeal;
    }

    public String getHourBreakfast() {
        return hourBreakfast;
    }

    public void setHourBreakfast(String hourBreakfast) {
        this.hourBreakfast = hourBreakfast;
    }

    public String getHourLunch() {
        return hourLunch;
    }

    public void setHourLunch(String hourLunch) {
        this.hourLunch = hourLunch;
    }

    public String getHourDinner() {
        return hourDinner;
    }

    public void setHourDinner(String hourDinner) {
        this.hourDinner = hourDinner;
    }

    public boolean isNotificationHbA1c() {
        return notificationHbA1c;
    }

    public void setNotificationHbA1c(boolean notificationHbA1c) {
        this.notificationHbA1c = notificationHbA1c;
    }

    public String getDelaisNotifHbA1c() {
        return delaisNotifHbA1c;
    }

    public void setDelaisNotifHbA1c(String delaisNotifHbA1c) {
        this.delaisNotifHbA1c = delaisNotifHbA1c;
    }
    //endregion
}
