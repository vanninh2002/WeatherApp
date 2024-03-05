package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weatherapp.adapter.location.LocationAdapter;
import com.example.weatherapp.api.API_Client;
import com.example.weatherapp.api.API_Location;
import com.example.weatherapp.api.API_Weather;
import com.example.weatherapp.iterface.IChooseLocation;
import com.example.weatherapp.model.location.Location;
import com.example.weatherapp.model.weather.Current;
import com.example.weatherapp.model.weather.Daily;
import com.example.weatherapp.model.weather.Hourly;
import com.example.weatherapp.model.weather.Root;
import com.example.weatherapp.model.weather.Weather;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, IChooseLocation {
    private TextView tv_number_degrees_celsius, tv_location, tv_num_max, tv_num_min, tv_inf_weather;
    private ImageView img_background;
    private TextView tv_today, tv_bf1day, tv_bf2day, tv_bf3day, tv_bf4day;
    private TextView tv_time_now, tv_time_after_1h, tv_time_after_2h, tv_time_after_3h, tv_time_after_4h, tv_time_after_5h, tv_time_after_6h, tv_time_after_7h;
    private TextView tv_temp_now, tv_temp_after_1h, tv_temp_after_2h, tv_temp_after_3h, tv_temp_after_4h, tv_temp_after_5h, tv_temp_after_6h, tv_temp_after_7h;
    private ImageView img_weather_now, img_weather_after_1h, img_weather_after_2h, img_weather_after_3h, img_weather_after_4h, img_weather_after_5h, img_weather_after_6h, img_weather_after_7h;
    private TextView tv_temp_day_today, tv_temp_night_today, tv_temp_day_after1d, tv_temp_night_after1d, tv_temp_day_after2d, tv_temp_night_after2d, tv_temp_day_after3d, tv_temp_night_after3d, tv_temp_day_after4d, tv_temp_night_after4d;
    private SearchView sv_location;
    private RecyclerView rv_CityName;
    private API_Location API_DIRECT_GEOCODING;
    private API_Weather API_WEATHER;

    // Location
    private final String COUNTRY = ",vn";
    private final int LIMIT = 5;
    // Weather
    private double num_lat, num_lon;
    //    private final String APP_ID = "bd5e378503939ddaee76f12ad7a97608";
    private final String APP_ID = "e7704bc895b4a8d2dfd4a29d404285b6";

    private String head_url = "https://openweathermap.org/img/wn/";
    private String end_url = "@2x.png";
    private String hour_converted;
    private String date_converted;
    private int REQUEST_LOCATION = 88;
    private FusedLocationProviderClient fusedLocationProviderClient;
    //    private final static int REQUEST_CODE = 100;
//
    private LocationRequest locationRequest;
//    private final static int REQUEST_CHECK_SETTING = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setStatusBarColor(this.getResources().getColor(R.color.black));
//        getSupportActionBar().hide();
        // Location
        sv_location = findViewById(R.id.sv_location); // SearchView
        rv_CityName = findViewById(R.id.rv_CityName); // RecycleView

        API_DIRECT_GEOCODING = API_Client.getRetrofit().create(API_Location.class);
        API_WEATHER = API_Client.getRetrofit().create(API_Weather.class);
        sv_location.setOnQueryTextListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_CityName.setLayoutManager(linearLayoutManager);

        // Weather
        tv_num_max = findViewById(R.id.tv_nummax);
        tv_num_min = findViewById(R.id.tv_nummin);
        tv_number_degrees_celsius = findViewById(R.id.tv_number_degrees_celsius);
        tv_location = findViewById(R.id.tv_location);
        tv_inf_weather = findViewById(R.id.tv_inf_weather);
        img_background = findViewById(R.id.img_background);

        // du doan
        tv_time_now = findViewById(R.id.tv_time_now);
        tv_time_after_1h = findViewById(R.id.tv_time_after_1h);
        tv_time_after_2h = findViewById(R.id.tv_time_after_2h);
        tv_time_after_3h = findViewById(R.id.tv_time_after_3h);
        tv_time_after_4h = findViewById(R.id.tv_time_after_4h);
        tv_time_after_5h = findViewById(R.id.tv_time_after_5h);
        tv_time_after_6h = findViewById(R.id.tv_time_after_6h);
        tv_time_after_7h = findViewById(R.id.tv_time_after_7h);
        tv_temp_now = findViewById(R.id.tv_temp_now);
        tv_temp_after_1h = findViewById(R.id.tv_temp_after_1h);
        tv_temp_after_2h = findViewById(R.id.tv_temp_after_2h);
        tv_temp_after_3h = findViewById(R.id.tv_temp_after_3h);
        tv_temp_after_4h = findViewById(R.id.tv_temp_after_4h);
        tv_temp_after_5h = findViewById(R.id.tv_temp_after_5h);
        tv_temp_after_6h = findViewById(R.id.tv_temp_after_6h);
        tv_temp_after_7h = findViewById(R.id.tv_temp_after_7h);
        img_weather_now = findViewById(R.id.img_weather_now);
        img_weather_after_1h = findViewById(R.id.img_weather_after_1h);
        img_weather_after_2h = findViewById(R.id.img_weather_after_2h);
        img_weather_after_3h = findViewById(R.id.img_weather_after_3h);
        img_weather_after_4h = findViewById(R.id.img_weather_after_4h);
        img_weather_after_5h = findViewById(R.id.img_weather_after_5h);
        img_weather_after_6h = findViewById(R.id.img_weather_after_6h);
        img_weather_after_7h = findViewById(R.id.img_weather_after_7h);
        tv_temp_day_today = findViewById(R.id.tv_temp_day_today);
        tv_temp_day_after1d = findViewById(R.id.tv_temp_day_after1d);
        tv_temp_day_after2d = findViewById(R.id.tv_temp_day_after2d);
        tv_temp_day_after3d = findViewById(R.id.tv_temp_day_after3d);
        tv_temp_day_after4d = findViewById(R.id.tv_temp_day_after4d);
        tv_temp_night_today = findViewById(R.id.tv_temp_night_today);
        tv_temp_night_after1d = findViewById(R.id.tv_temp_night_after1d);
        tv_temp_night_after2d = findViewById(R.id.tv_temp_night_after2d);
        tv_temp_night_after3d = findViewById(R.id.tv_temp_night_after3d);
        tv_temp_night_after4d = findViewById(R.id.tv_temp_night_after4d);
        tv_today = findViewById(R.id.tv_today);
        tv_bf1day = findViewById(R.id.tv_bf1day);
        tv_bf2day = findViewById(R.id.tv_bf2day);
        tv_bf3day = findViewById(R.id.tv_bf3day);
        tv_bf4day = findViewById(R.id.tv_bf4day);
        checkPermissionLocation();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        getCurrentLocation();
        tv_number_degrees_celsius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {
                    getCurrentLocation();
                } else {
                    turnOnGPS();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {

                getCurrentLocation();
            }
        }
    }

    private void getCurrentLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() > 0) {

                                        int index = locationResult.getLocations().size() - 1;
                                        num_lat = locationResult.getLocations().get(index).getLatitude();
                                        num_lon = locationResult.getLocations().get(index).getLongitude();

                                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                        try {
                                            List<Address> addresses = geocoder.getFromLocation(num_lat, num_lon, 1);
                                            if (addresses != null && !addresses.isEmpty()) {
                                                // Get the first address and its address line
                                                Address address = addresses.get(0);
                                                String addressLine = address.getAddressLine(0);
                                                // Display the address line and the coordinates in the TextView
                                                tv_location.setText(addressLine);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        getWeather();
                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void turnOnGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(MainActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(MainActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }

    private void checkPermissionLocation() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        getListLocation(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        getListLocation(newText);
        return false;
    }

    private void getListLocation(String query) {
        Call<List<Location>> listCall = API_DIRECT_GEOCODING.API_DIRECT_GEOCODING_CALL(query, LIMIT, APP_ID);
        listCall.enqueue(new Callback<List<Location>>() {
            @Override
            public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                if (response.isSuccessful()) {
                    List<Location> locationList = response.body();
                    rv_CityName.setAdapter(new LocationAdapter(locationList, MainActivity.this, sv_location, rv_CityName, MainActivity.this));
                } else
                    rv_CityName.setAdapter(new LocationAdapter(null, null, sv_location, rv_CityName, MainActivity.this));
            }

            @Override
            public void onFailure(Call<List<Location>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getWeather() {
        Call<Root> listCall = API_WEATHER.API_Weather_Call(num_lat, num_lon, APP_ID);
        listCall.enqueue(new Callback<Root>() {
            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {
                if (response.isSuccessful()) {
                    Root root = response.body();
                    Current current = root.getCurrent();
                    ArrayList<Daily> dailyList = root.getDaily();
                    ArrayList<Hourly> hourly = root.getHourly();
                    Daily today = dailyList.get(0);
                    ArrayList<Weather> weather = current.getWeather();
                    String weather_icon = weather.get(0).getIcon();
                    String weather_type = weather.get(0).getMain();
                    String timeZone = root.getTimezone();

                    switch (weather_type) {
                        case "Thunderstorm":
                            if (weather_icon.contains("d")) {
                                img_background.setImageResource(R.drawable.img_thunderstorm_day);
                            } else
                                img_background.setImageResource(R.drawable.img_thunderstorm_night);
                            break;

                        case "Clouds":
                            if (weather_icon.contains("d")) {
                                img_background.setImageResource(R.drawable.img_clouds_day);
                            } else
                                img_background.setImageResource(R.drawable.img_clouds_night);
                            break;

                        case "Drizzle":
                            if (weather_icon.contains("d")) {
                                img_background.setImageResource(R.drawable.img_drizzle_day);
                            } else
                                img_background.setImageResource(R.drawable.img_drizzle_night);
                            break;

                        case "Rain":
                            if (weather_icon.contains("d")) {
                                img_background.setImageResource(R.drawable.img_rain_day);
                            } else
                                img_background.setImageResource(R.drawable.img_rain_night);
                            break;

                        case "Snow":
                            if (weather_icon.contains("d")) {
                                img_background.setImageResource(R.drawable.img_snow_day);
                            } else
                                img_background.setImageResource(R.drawable.img_snow_night);
                            break;

                        case "Atmosphere":
                            img_background.setImageResource(R.drawable.img_atmosphere);
                            break;

                        case "Clear":
                            if (weather_icon.contains("d")) {
                                img_background.setImageResource(R.drawable.img_clear_day);
                            } else
                                img_background.setImageResource(R.drawable.img_clear_night);
                            break;
                    }

                    tv_number_degrees_celsius.setText(convertToCelsius(current.getTemp()));
                    tv_num_max.setText(getResources().getString(R.string.two_dots) + " " + convertToCelsius(today.getTemp().getMax()));
                    tv_num_min.setText(getResources().getString(R.string.two_dots) + " " + convertToCelsius(today.getTemp().getMin()));
                    tv_time_now.setText(convertToHour(root.getTimezone(), hourly.get(0).getDt()));
                    tv_time_after_1h.setText(convertToHour(timeZone, hourly.get(1).getDt()));
                    tv_time_after_2h.setText(convertToHour(timeZone, hourly.get(2).getDt()));
                    tv_time_after_3h.setText(convertToHour(timeZone, hourly.get(2).getDt()));
                    tv_time_after_4h.setText(convertToHour(timeZone, hourly.get(4).getDt()));
                    tv_time_after_5h.setText(convertToHour(timeZone, hourly.get(5).getDt()));
                    tv_time_after_6h.setText(convertToHour(timeZone, hourly.get(6).getDt()));
                    tv_time_after_7h.setText(convertToHour(timeZone, hourly.get(7).getDt()));

                    Glide.with(MainActivity.this).load(head_url + hourly.get(0).getWeather().get(0).getIcon() + end_url).into(img_weather_now);
                    Glide.with(MainActivity.this).load(head_url + hourly.get(1).getWeather().get(0).getIcon() + end_url).into(img_weather_after_1h);
                    Glide.with(MainActivity.this).load(head_url + hourly.get(2).getWeather().get(0).getIcon() + end_url).into(img_weather_after_2h);
                    Glide.with(MainActivity.this).load(head_url + hourly.get(3).getWeather().get(0).getIcon() + end_url).into(img_weather_after_3h);
                    Glide.with(MainActivity.this).load(head_url + hourly.get(4).getWeather().get(0).getIcon() + end_url).into(img_weather_after_4h);
                    Glide.with(MainActivity.this).load(head_url + hourly.get(5).getWeather().get(0).getIcon() + end_url).into(img_weather_after_5h);
                    Glide.with(MainActivity.this).load(head_url + hourly.get(6).getWeather().get(0).getIcon() + end_url).into(img_weather_after_6h);
                    Glide.with(MainActivity.this).load(head_url + hourly.get(7).getWeather().get(0).getIcon() + end_url).into(img_weather_after_7h);

                    tv_temp_now.setText(convertToCelsius((hourly.get(0).getTemp())));
                    tv_temp_after_1h.setText(convertToCelsius((hourly.get(1).getTemp())));
                    tv_temp_after_2h.setText(convertToCelsius((hourly.get(2).getTemp())));
                    tv_temp_after_3h.setText(convertToCelsius((hourly.get(3).getTemp())));
                    tv_temp_after_4h.setText(convertToCelsius((hourly.get(4).getTemp())));
                    tv_temp_after_5h.setText(convertToCelsius((hourly.get(5).getTemp())));
                    tv_temp_after_6h.setText(convertToCelsius((hourly.get(6).getTemp())));
                    tv_temp_after_7h.setText(convertToCelsius((hourly.get(7).getTemp())));
                    tv_inf_weather.setText("Feels like " + convertToCelsius(hourly.get(0).getFeels_like()) + ", " + hourly.get(0).getWeather().get(0).getDescription() + ".");
                    tv_temp_day_today.setText(convertToCelsius(dailyList.get(0).getTemp().getDay()));
                    tv_temp_day_after1d.setText(convertToCelsius(dailyList.get(1).getTemp().getDay()));
                    tv_temp_day_after2d.setText(convertToCelsius(dailyList.get(2).getTemp().getDay()));
                    tv_temp_day_after3d.setText(convertToCelsius(dailyList.get(3).getTemp().getDay()));
                    tv_temp_day_after4d.setText(convertToCelsius(dailyList.get(4).getTemp().getDay()));
                    tv_temp_night_today.setText(convertToCelsius(dailyList.get(0).getTemp().getNight()));
                    tv_temp_night_after1d.setText(convertToCelsius(dailyList.get(1).getTemp().getNight()));
                    tv_temp_night_after2d.setText(convertToCelsius(dailyList.get(2).getTemp().getNight()));
                    tv_temp_night_after3d.setText(convertToCelsius(dailyList.get(3).getTemp().getNight()));
                    tv_temp_night_after4d.setText(convertToCelsius(dailyList.get(4).getTemp().getNight()));

                    // Convert the unix time to milliseconds
                    tv_today.setText(convertToDate(timeZone, dailyList.get(1).getDt()));
                    tv_bf1day.setText(convertToDate(timeZone, dailyList.get(2).getDt()));
                    tv_bf2day.setText(convertToDate(timeZone, dailyList.get(3).getDt()));
                    tv_bf3day.setText(convertToDate(timeZone, dailyList.get(4).getDt()));
                    tv_bf4day.setText(convertToDate(timeZone, dailyList.get(5).getDt()));
                } else
                    Toast.makeText(MainActivity.this, "Failureeeee", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Root> call, Throwable t) {
                Toast.makeText(MainActivity.this, "API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onChooseLocation(double lat, double lon) {
        num_lat = lat;
        num_lon = lon;
    }

    @Override
    public void onBackPressed() {
        if (!sv_location.isIconified()) {
            sv_location.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    public void setLocationName(String name) {
        tv_location.setText(name);
    }

    public String convertToHour(String timeZone, long dateUnix) {
        Date date = new Date(dateUnix * 1000); // Date object
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm"); // Format object
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        String hourFormatted = sdf.format(date); // Formatted date string
        hour_converted = hourFormatted;
        return hour_converted;
    }

    public String convertToDate(String timeZone, long dateUnix) {
        Date date = new Date(dateUnix * 1000); // Date object
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.ENGLISH); // Format object
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        String dateFormatted = sdf.format(date); // Formatted date string
        date_converted = dateFormatted;
        return date_converted;
    }

    public String convertToCelsius(double kelvin) {
        return Math.round(kelvin - 273.15) + "°";
    }
}