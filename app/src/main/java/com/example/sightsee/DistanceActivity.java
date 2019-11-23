package com.example.sightsee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.sightsee.ui.MapsActivity;
import com.example.sightsee.ui.SelectionActivity;

public class DistanceActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView textView;
    private Button btnNext;
    private int num;
    private TextView miles;
    private Boolean wantsNearby;
    private Boolean wants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);

        Bundle extras = getIntent().getExtras();


        Boolean wants = extras.getBoolean("wantsNearby");

        wantsNearby = wants;

        btnNext = (Button) findViewById(R.id.next);
        miles = (TextView) findViewById(R.id.miles);
        miles.setVisibility(View.GONE);


        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView = (TextView)findViewById(R.id.max_distance);
                textView.setText(String.valueOf(i));
                miles.setVisibility(View.VISIBLE);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                num = Integer.valueOf(textView.getText().toString());
                btnNext.setVisibility(View.VISIBLE);

            }

        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SelectionActivity.class);
                intent.putExtra("max_distance", num);
                if (wantsNearby != null && wantsNearby == true) {
                    intent.putExtra("wantsNearby", true);
                } else {
                    intent.putExtra("wantsNearby", false);
                }
                startActivity(intent);
            }
        });
    }
}
