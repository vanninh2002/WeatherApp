package com.example.weatherapp.adapter.location;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.MainActivity;
import com.example.weatherapp.R;
import com.example.weatherapp.iterface.IChooseLocation;
import com.example.weatherapp.model.location.Location;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationViewHolder> {
//    private TextView tv_CityName;
    private List<Location> mLocationList;
    private IChooseLocation mIChooseLocation;
    private SearchView mSV_Location;
    private RecyclerView mRV_City;
    private MainActivity mainActivity;

    public LocationAdapter(List<Location> mLocationList, IChooseLocation mIChooseLocation, SearchView mSearchViewLocation, RecyclerView mRV_City, MainActivity mainActivity) {
        this.mLocationList = mLocationList;
        this.mIChooseLocation = mIChooseLocation;
        this.mSV_Location = mSearchViewLocation;
        this.mRV_City = mRV_City;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view, mIChooseLocation, this, mSV_Location, mRV_City, mainActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        holder.bind(mLocationList.get(position), mIChooseLocation);
    }

    @Override
    public int getItemCount() {
        if (mLocationList != null) {
            return mLocationList.size();
        }
        return 0;
    }

    public void setData(List<Location> locationList) {
        this.mLocationList = locationList;
        notifyDataSetChanged();
    }
}

