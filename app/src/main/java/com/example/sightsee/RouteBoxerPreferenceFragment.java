//package com.example.sightsee;
//
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.preference.EditTextPreference;
//import android.preference.PreferenceFragment;
//
//import androidx.annotation.Nullable;
//
//public class RouteBoxerPreferenceFragment extends PreferenceFragment
//        implements SharedPreferences.OnSharedPreferenceChangeListener {
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        addPreferencesFromResource(R.xml.preference_main);
//
//        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
//        EditTextPreference toleranceDistancePreference = (EditTextPreference) findPreference("pref_key_tolerance_distance");
//        toleranceDistancePreference.setSummary(sp.getString("pref_key_tolerance_distance", "200") + " meter perpendicular distance tolerance");
//
//        sp.registerOnSharedPreferenceChangeListener(this);
//
//    }
//
//
//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if(key.equals("pref_key_tolerance_distance")) {
//            String s = sharedPreferences.getString("pref_key_tolerance_distance", "200") + " meter perpendicular distance tolerance";
//            this.findPreference("pref_key_tolerance_distance").setSummary(s);
//        }
//    }
//}
