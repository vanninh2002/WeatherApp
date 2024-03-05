package com.example.weatherapp.model.weather;

import java.util.ArrayList;

public class Root {
    public double lat;
    public double lon;
    public String timezone;
    public int timezone_offset;
    public Current current;
    public ArrayList<Minutely> minutely;
    public ArrayList<Hourly> hourly;
    public ArrayList<Daily> daily;

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getTimezone() {
        return timezone;
    }

    public int getTimezone_offset() {
        return timezone_offset;
    }

    public Current getCurrent() {
        return current;
    }

    public ArrayList<Minutely> getMinutely() {
        return minutely;
    }

    public ArrayList<Hourly> getHourly() {
        return hourly;
    }

    public ArrayList<Daily> getDaily() {
        return daily;
    }
}
