package com.example.weatherapp.adapter.location;

import android.view.View;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherapp.MainActivity;
import com.example.weatherapp.R;
import com.example.weatherapp.iterface.IChooseLocation;
import com.example.weatherapp.model.location.Location;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class LocationViewHolder extends RecyclerView.ViewHolder {
    private TextView tv_CityName;
    private CardView cv_item_location;
    private IChooseLocation iChooseLocation;
    private LocationAdapter locationAdapter;
    private RecyclerView rv_CityName;
    private SearchView sv_location;
    private MainActivity mainActivity;

    public LocationViewHolder(@NonNull View itemView, IChooseLocation iChooseLocation, LocationAdapter locationAdapter, SearchView msv_location, RecyclerView mrv_CityName, MainActivity mainActivity) {
        super(itemView);
        tv_CityName = itemView.findViewById(R.id.tv_CityName);
        cv_item_location = itemView.findViewById(R.id.cv_item_location);
        sv_location = itemView.findViewById(R.id.sv_location);
        rv_CityName = itemView.findViewById(R.id.rv_CityName);
        this.iChooseLocation = iChooseLocation;
        this.locationAdapter = locationAdapter;
        this.sv_location = msv_location;
        this.rv_CityName = mrv_CityName;
        this.mainActivity = mainActivity;
    }

    public void bind(Location locationModel, IChooseLocation iChooseLocation) {
        tv_CityName.setText(locationModel.getName() + ", " + locationModel.getState());
        cv_item_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double lat = locationModel.getLat();
                double lon = locationModel.getLon();
                iChooseLocation.onChooseLocation(lat, lon);
                mainActivity.getWeather();
                mainActivity.setLocationName(locationModel.getName());
                Toast.makeText(view.getContext(), tv_CityName.getText().toString(), Toast.LENGTH_SHORT).show();
                sv_location.onActionViewCollapsed();
            }
        });
    }
}