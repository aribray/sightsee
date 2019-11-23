package com.example.sightsee.utils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.sightsee.DistanceActivity;
import com.example.sightsee.R;
import com.example.sightsee.WelcomeActivity;
import com.example.sightsee.ui.MapsActivity;

public class PathActivity extends AppCompatActivity {

    private Button wholeTrip;
    private Button nearby;
    private Button btnHelp;
//    private Boolean wantsNearby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);

        wholeTrip = findViewById(R.id.whole_trip);
        nearby = findViewById(R.id.nearby);
        btnHelp = findViewById(R.id.help);


        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonShowPopupWindowClick(view);
            }
        });

        wholeTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(), DistanceActivity.class);
//                wantsNearby = false;
                i.putExtra("wantsNearby", false);
                startActivity(i);
            }
        });

        nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onButtonShowPopupWindowClick(view);
//                wantsNearby = true;
                Intent i=new Intent(getApplicationContext(), DistanceActivity.class);
//                wantsNearby = true;
                i.putExtra("wantsNearby", true);
                startActivity(i);
            }
        });

    }

    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
//        popupView.setBackgroundColor(Color.parseColor("#8062def8"));

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }
}
