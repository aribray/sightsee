package com.example.sightsee.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public interface IMaps {
    void onJSONRouteLoaded(ArrayList<LatLng> route) throws IOException;
    void routeJsonObtained(String json);
}
