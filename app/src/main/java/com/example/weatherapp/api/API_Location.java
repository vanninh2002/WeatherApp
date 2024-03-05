package com.example.weatherapp.api;

import com.example.weatherapp.model.location.Location;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface API_Location {
    // http://api.openweathermap.org/geo/1.0/direct?q=phu%yen,vn&limit=5&appid=80e60e8cf01923c43459ae13ecb12c54

    @GET("/geo/1.0/direct")
    Call<List<Location>> API_DIRECT_GEOCODING_CALL(@Query("q") String q,
                                                   @Query("limit") int limit,
                                                   @Query("appid") String appid);
}
