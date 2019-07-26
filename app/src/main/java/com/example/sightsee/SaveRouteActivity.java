package com.example.sightsee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sightsee.ui.MapsActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveRouteActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private EditText routeName;
    private Button btnSave;
    private ArrayList<LatLng> origin;
    private ArrayList<LatLng> destination;
    private ArrayList<LatLng> waypoints;
    private FirebaseFirestore database;
    private TextView routes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_route);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            origin = getIntent().getParcelableArrayListExtra("origin");
            destination = getIntent().getParcelableArrayListExtra("destination");
            waypoints = getIntent().getParcelableArrayListExtra("waypoints");
        }

        routes = (TextView) findViewById(R.id.routes);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();

        routeName = (EditText) findViewById(R.id.routeName);

        routeName.setInputType(InputType.TYPE_CLASS_TEXT);

        btnSave = (Button) findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRoute();
            }
        });

        database = FirebaseFirestore.getInstance();
    }

    private void saveRoute() {

        final Map<String, Object> route = new HashMap<>();
        route.put("origin", origin);
        route.put("destination", destination);
        route.put("waypoints", waypoints);
        route.put("routeName", routeName.getText().toString());

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Add a new document with a generated ID
//        List routes = (List)
                database.collection("users")
                .document(uid)
                        .collection("routes").add(route)

                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                          @Override
                                          public void onSuccess(DocumentReference documentReference) {
                                              Toast.makeText(SaveRouteActivity.this, "Successfully added " + routeName.getText().toString() + " to route", Toast.LENGTH_SHORT).show();
                                              Log.d("success", "DocumentSnapshot added with ID: " + documentReference.getId());
                                          }})


                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("error", "Error adding document", e);
                    }
                });

    }
}

