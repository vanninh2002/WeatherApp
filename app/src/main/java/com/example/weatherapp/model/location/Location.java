package com.example.weatherapp.model.location;

import java.util.List;

public class    Location {
    public String name;
    public double lat;
    public double lon;
    public String country;
    private String state;

    public String getState() {
        return state;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getCountry() {
        return country;
    }
}
