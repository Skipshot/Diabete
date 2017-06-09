package com.example.fdelahaye.myapplication.Objects;

import android.content.Context;
import android.text.TextUtils;

import com.example.fdelahaye.myapplication.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
    //endregion

    //region Methods
    public static Settings getJsonSettings(Context context, String filename) {

        Gson gson = new Gson();
        Settings settings = new Settings();

        if(JsonUtil.fileExists(context, filename)) {
            //read json file
            String jsonFile = JsonUtil.readFromFile(context, filename);
            //convert string jsonFile to List<Glycaemia>
            settings = gson.fromJson(jsonFile, new TypeToken<Settings>(){}.getType());
        }
        return settings;
    }

    public static void update(Settings settings, Context context, String filename) {
        settings.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        JsonUtil.writeToFile(new Gson().toJson(settings), context, filename);
    }

    public boolean isComplete() {
        return this.breakfastIndex > 0 && this.breakfastRatio > 0 &&
                this.lunchIndex > 0 && this.lunchRatio > 0 &&
                this.dinnerIndex > 0 && this.dinnerRatio > 0 &&
                this.goalBeforeMeal > 0 && this.goalOutMeal > 0 &&
                !TextUtils.isEmpty(this.hourBreakfast) && !TextUtils.isEmpty(this.hourLunch) && !TextUtils.isEmpty(this.hourDinner);
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
