package com.example.weatherapp.api;

import com.example.weatherapp.model.weather.Root;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface API_Weather {
    // https://api.openweathermap.org/data/2.5/forecast?lat=44.34&lon=10.99&appid=80e60e8cf01923c43459ae13ecb12c54

    @GET("/data/2.5/onecall")
    Call<Root> API_Weather_Call(@Query("lat") double latitude,
                                @Query("lon") double longitude,
                                @Query("appid") String appID);
}
