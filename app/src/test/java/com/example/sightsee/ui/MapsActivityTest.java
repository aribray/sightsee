package com.example.sightsee.ui;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static java.util.Collections.emptyList;
import static org.junit.Assert.*;

public class MapsActivityTest {

    @Test
    public void onJSONRouteLoaded() throws IOException {
        MapsActivity mapsActivity = new MapsActivity();
        ArrayList<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(12.34, 1.4));
        latLngs.add(new LatLng(32.12, 392.22));
        mapsActivity.onJSONRouteLoaded(latLngs);
    }
}