package com.example.sightsee.utils;

import java.util.List;

import noman.googleplaces.Place;
import noman.googleplaces.PlacesException;

public interface PlacesListener {
    void onPlacesFailure(PlacesException e);

    void onPlacesStart();

    void onPlacesSuccess(List<Place> places);

    void onPlacesFinished();
}
