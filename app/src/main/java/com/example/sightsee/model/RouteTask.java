package com.example.sightsee.model;


import android.os.AsyncTask;
import android.os.Environment;

import com.example.sightsee.utils.Rest;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by aryo on 28/1/16.
 */
public class RouteTask extends AsyncTask<Void, String, ArrayList<LatLng>> {

    private final IMaps IMaps;
    private final LatLng start;
    private final LatLng end;

    public RouteTask(IMaps IMaps, LatLng start, LatLng end) {
        this.IMaps = IMaps;
        this.start = start;
        this.end = end;
    }

    @Override
    protected ArrayList<LatLng> doInBackground(Void... params) {
        String origin = this.start.latitude+","+this.end.longitude;
        String destination = this.end.latitude+","+this.end.longitude;
        String url = "http://maps.googleapis.com/maps/api/directions/json?origin="+origin+"&destination="+destination;
        String json = Rest.get(url).getString();

        publishProgress(json);

        ArrayList<LatLng> routes = RoutingHelper.parse(json);
        return routes;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if(this.IMaps != null)
            this.IMaps.routeJsonObtained(values[0]);
    }

    @Override
    protected void onPostExecute(ArrayList<LatLng> routes) {
        if (this.IMaps != null)
            try {
                this.IMaps.onJSONRouteLoaded(routes);
            } catch (IOException e) {

            }
    }

}
