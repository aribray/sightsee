package com.example.sightsee.model;


import java.util.ArrayList;

import com.example.sightsee.model.RouteBoxer;

/**
 * Created by Aryo on 8/13/2017.
 */

public class Route {

    ArrayList<Point> points = new ArrayList<>();

    public class Point {
        public double latitude;
        public double longitude;

        public Point(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

}
