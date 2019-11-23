package com.example.sightsee.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MapRoute {

    public MapDistance mapDistance;
    public MapDuration mapDuration;

    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public ArrayList<LatLng> points;

}
