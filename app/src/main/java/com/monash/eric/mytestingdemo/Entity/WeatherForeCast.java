package com.monash.eric.mytestingdemo.Entity;
/**
 * Created by IBM on 1/09/2016.
 */
public class WeatherForeCast {

    private String date;
    private String weather;
    private String temperatue_max;
    private String temperatue_min;


    public WeatherForeCast() {
    }

    public WeatherForeCast(String date, String weather, String temperatue_max, String temperatue_min) {
        this.date = date;
        this.weather = weather;
        this.temperatue_max = temperatue_max;
        this.temperatue_min = temperatue_min;
    }



    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemperatue_max() {
        return temperatue_max;
    }

    public void setTemperatue_max(String temperatue_max) {
        this.temperatue_max = temperatue_max;
    }

    public String getTemperatue_min() {
        return temperatue_min;
    }

    public void setTemperatue_min(String temperatue_min) {
        this.temperatue_min = temperatue_min;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
