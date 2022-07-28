package com.example.weather.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class Forecast {

    @SerializedName("day")
    @Expose
    private String day;
    @SerializedName("temperature")
    @Expose
    private String temperature;
    @SerializedName("wind")
    @Expose
    private String wind;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

}