package com.example.weatherapp.model.weather;

import com.google.gson.annotations.SerializedName;

public class Rain {
    @SerializedName("1h")
    public double _1h;

    public double get_1h() {
        return _1h;
    }
}
