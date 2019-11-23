package com.example.sightsee.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sightsee.R;
import com.example.sightsee.WelcomeActivity;
import com.example.sightsee.utils.MyAdapter;
import com.example.sightsee.utils.NearbyActivity;
import com.example.sightsee.utils.StateVO;

import java.util.ArrayList;

public class SelectionActivity extends AppCompatActivity {

    private Button submitButton;
    private CheckBox checkBox;
    private int distance; // meter
    private TextView helperText;
    private Boolean wantsNearby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            distance = extras.getInt("max_distance") * 1610;
            wantsNearby = extras.getBoolean("wantsNearby");
        }


        submitButton = findViewById(R.id.submit);

        checkBox = (CheckBox)findViewById(R.id.checkbox);

        helperText = (TextView) findViewById(R.id.helper_selection);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wantsNearby == true) {
                    Intent i = new Intent(getApplicationContext(), NearbyActivity.class);
                    i.putExtra("max_distance", distance);
                    startActivity(i);
                } else {
                    Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                    i.putExtra("max_distance", distance);
                    startActivity(i);
                }
            }
        });

        final String[] places_of_worship = {
                "Places of Worship", "church", "hindu_temple ", "mosque", "synagogue"};
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayList<StateVO> listVOs = new ArrayList<>();

        for (int i = 0; i < places_of_worship.length; i++) {
            StateVO stateVO = new StateVO();
            stateVO.setTitle(places_of_worship[i]);
            stateVO.setSelected(false);
            listVOs.add(stateVO);
        }
        MyAdapter myAdapter = new MyAdapter(SelectionActivity.this, 0,
                listVOs);
        spinner.setAdapter(myAdapter);

        //shopping spinner

        final String[] shopping = {
                "Shopping", "art_gallery", "book_store", "shopping_mall"};
        Spinner shoppingSpinner = (Spinner) findViewById(R.id.shopping);

        ArrayList<StateVO> listShopping = new ArrayList<>();

        for (int i = 0; i < shopping.length; i++) {
            StateVO shoppingVO = new StateVO();
            shoppingVO.setTitle(shopping[i]);
            shoppingVO.setSelected(false);
            listShopping.add(shoppingVO);
        }
        MyAdapter shoppingAdapter = new MyAdapter(SelectionActivity.this, 0,
                listShopping);
        shoppingSpinner.setAdapter(shoppingAdapter);

        //entertainment spinner

        final String[] entertainment = {
                "Entertainment", "amusement_park", "aquarium", "bowling_alley", "casino", "movie theater", "zoo"};
        Spinner entertainmentSpinner = (Spinner) findViewById(R.id.entertainment);

        ArrayList<StateVO> listEntertainment = new ArrayList<>();

        for (int i = 0; i < entertainment.length; i++) {
            StateVO entertainmentVO = new StateVO();
            entertainmentVO.setTitle(entertainment[i]);
            entertainmentVO.setSelected(false);
            listEntertainment.add(entertainmentVO);
        }
        MyAdapter entertainmentAdapter = new MyAdapter(SelectionActivity.this, 0,
                listEntertainment);
        entertainmentSpinner.setAdapter(entertainmentAdapter);

        // educational spinner

        final String[] education = {
                "Educational", "city_hall", "embassy", "library", "museum"};
        Spinner educationalSpinner = (Spinner) findViewById(R.id.educational);

        ArrayList<StateVO> listEducational = new ArrayList<>();

        for (int i = 0; i < education.length; i++) {
            StateVO educationVO = new StateVO();
            educationVO.setTitle(education[i]);
            educationVO.setSelected(false);
            listEducational.add(educationVO);
        }
        MyAdapter educationalAdapter = new MyAdapter(SelectionActivity.this, 0,
                listEducational);
        educationalSpinner.setAdapter(educationalAdapter);

        //food spinner

        final String[] food = {
                "Food", "cafe", "restaurant", "bar"};
        Spinner foodSpinner = (Spinner) findViewById(R.id.food);

        ArrayList<StateVO> listFood = new ArrayList<>();

        for (int i = 0; i < food.length; i++) {
            StateVO foodVO = new StateVO();
            foodVO.setTitle(food[i]);
            foodVO.setSelected(false);
            listFood.add(foodVO);
        }
        MyAdapter foodAdapter = new MyAdapter(SelectionActivity.this, 0,
                listFood);
        foodSpinner.setAdapter(foodAdapter);

        //lodging spinner

        final String[] lodging = {
                "Places to Stay", "campground", "lodging"};
        Spinner lodgingSpinner = (Spinner) findViewById(R.id.lodging);

        ArrayList<StateVO> listLodging = new ArrayList<>();

        for (int i = 0; i < lodging.length; i++) {
            StateVO lodgingVO = new StateVO();
            lodgingVO.setTitle(lodging[i]);
            lodgingVO.setSelected(false);
            listLodging.add(lodgingVO);
        }
        MyAdapter lodgingAdapter = new MyAdapter(SelectionActivity.this, 0,
                listLodging);
        lodgingSpinner.setAdapter(lodgingAdapter);

        //wellness spinner

        final String[] wellness = {
                "Wellness", "gym", "spa"};
        Spinner wellnessSpinner = (Spinner) findViewById(R.id.wellness);

        ArrayList<StateVO> listWellness = new ArrayList<>();

        for (int i = 0; i < wellness.length; i++) {
            StateVO wellnessVO = new StateVO();
            wellnessVO.setTitle(wellness[i]);
            wellnessVO.setSelected(false);
            listWellness.add(wellnessVO);
        }
        MyAdapter wellnessAdapter = new MyAdapter(SelectionActivity.this, 0,
                listWellness);
        wellnessSpinner.setAdapter(wellnessAdapter);

        //nightlife spinner

        final String[] nightlife = {
                "Nightlife", "bar", "night_club"};
        Spinner nightlifeSpinner = (Spinner) findViewById(R.id.nightlife);

        ArrayList<StateVO> listNightlife = new ArrayList<>();

        for (int i = 0; i < nightlife.length; i++) {
            StateVO nightlifeVO = new StateVO();
            nightlifeVO.setTitle(nightlife[i]);
            nightlifeVO.setSelected(false);
            listNightlife.add(nightlifeVO);
        }
        MyAdapter nightlifeAdapter = new MyAdapter(SelectionActivity.this, 0,
                listNightlife);
        nightlifeSpinner.setAdapter(nightlifeAdapter);
    }
}
