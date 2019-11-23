package com.example.sightsee.utils;

import com.example.sightsee.model.MapRoute;

import java.util.List;

public interface SearchDirectionListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<MapRoute> mapRoute);
}
