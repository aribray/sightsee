package com.example.sightsee.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.example.sightsee.model.MapDistance;
import com.example.sightsee.model.MapDuration;
import com.example.sightsee.model.MapRoute;
import com.example.sightsee.ui.MapsActivity;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchDirection {
    private static final String URL_DIRECTION_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyAsUdxhIyV6VxuKuMRWNnA-AOjLZWaf4V0";
    private SearchDirectionListener listener;
    private String origin;
    private String destination;
    private ArrayList waypoints;

    public SearchDirection(SearchDirectionListener listener, String origin, String destination) {
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
    }

    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        new RawDataDownload().execute(createUrl());
    }

    private String createUrl() throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(origin, "utf-8");
        String urlDestination = URLEncoder.encode(destination, "utf-8");

        Log.d("URL", "createUrl: " + URL_DIRECTION_API + "origin=place_id:" + urlOrigin + "&destination=place_id:" + urlDestination + "&key=" + GOOGLE_API_KEY);
        return URL_DIRECTION_API + "&origin=place_id:" + urlOrigin + "&destination=place_id:" + urlDestination + "&key=" + GOOGLE_API_KEY;
    }

    private class RawDataDownload extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;
        Log.i("JSON", "parseJSon: " + data);
        List<MapRoute> mapRoutes = new ArrayList<MapRoute>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");

            JSONObject jsonRoute = jsonRoutes.getJSONObject(0);
            MapRoute mapRoute = new MapRoute();

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
//            String encodedString = overview_polylineJson.getString("points");
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
            JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
            JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

            mapRoute.mapDistance = new MapDistance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
            mapRoute.mapDuration = new MapDuration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
            mapRoute.endAddress = jsonLeg.getString("end_address");
            mapRoute.startAddress = jsonLeg.getString("start_address");
            mapRoute.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
            mapRoute.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
            mapRoute.points = decodePolyLine(overview_polylineJson.getString("points"));

            mapRoutes.add(mapRoute);


        listener.onDirectionFinderSuccess(mapRoutes);
    }

    private ArrayList<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        ArrayList<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
}
