package com.example.sightsee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API;
import static com.google.maps.internal.StringJoin.join;

public class ProfileActivity extends AppCompatActivity {
    private Button btnLogOut;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;
    private ArrayList<LatLng> waypointsList = new ArrayList<>();
    private String waypointString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(GOOGLE_SIGN_IN_API)
                .build();


        mGoogleApiClient.connect();

        Toolbar setupToolbar = findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        TextView profileFullName = findViewById(R.id.profileFullName);
        TextView profileUserName = findViewById(R.id.profileUserName);

        final TextView routes = findViewById(R.id.routes);


        // set to current user

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            profileFullName.setText(((FirebaseUser) user).getDisplayName());
            profileUserName.setText(user.getEmail());
        }

        btnLogOut = (Button) findViewById(R.id.btnLogOut);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                // ...
                                Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                                Intent i=new Intent(getApplicationContext(),WelcomeActivity.class);
                                startActivity(i);
                            }
                        });
            }
        });

        database = FirebaseFirestore.getInstance();

        database.collection("users").document(user.getUid()).collection("routes").get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                final QuerySnapshot[] document = {task.getResult()};

                routes.setText("User has " + document[0].size() + " routes");

                LinearLayout ll = (LinearLayout)findViewById(R.id.buttonLayout);

                List<DocumentSnapshot> routes = document[0].getDocuments();

                for (int i = 0; i < document[0].size(); i ++) {
                    LinearLayout row = new LinearLayout(ProfileActivity.this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(10, 10, 10 ,10);

                    row.setGravity(Gravity.CENTER);

                    DocumentSnapshot route = routes.get(i);

                    Map<String, Object> routeData = route.getData();
                    String routeName = routeData.get("routeName").toString();

                    Button routeButton = new Button(ProfileActivity.this);
                    final Button deleteButton = new Button(ProfileActivity.this);
                    deleteButton.setTag(route.getId());

                    routeButton.getBackground().setColorFilter(routeButton.getContext().getResources().getColor(R.color.aquamarine), PorterDuff.Mode.MULTIPLY);
                    deleteButton.getBackground().setColorFilter(deleteButton.getContext().getResources().getColor(R.color.crimson), PorterDuff.Mode.MULTIPLY);


                    routeButton.setText(routeName);
                    deleteButton.setText(R.string.delete_route);

                    row.addView(routeButton);
                    row.addView(deleteButton);

                    List<HashMap> origins = (List<HashMap>) route.get("origin");
                    List<HashMap> destinations = (List<HashMap>) route.get("destination");
                    List<HashMap> waypoints = (List<HashMap>) route.get("waypoints");

                    for (int k = 0; k < waypoints.size(); k ++) {
                        String waypointLat =  waypoints.get(k).get("latitude").toString();
                        String waypointLon = waypoints.get(k).get("longitude").toString();

                        double waypoint1 = Double.valueOf(waypointLat);
                        double waypoint2 = Double.valueOf(waypointLon);

                        LatLng waypointPoint = new LatLng(waypoint1, waypoint2);

                        waypointsList.add(waypointPoint);

                    }

                    waypointParser(waypointsList);

                   String originLat =  origins.get(0).get("latitude").toString();
                   String originLon = origins.get(0).get("longitude").toString();

                   String destinationLat =  destinations.get(0).get("latitude").toString();
                   String destinationLon = destinations.get(0).get("longitude").toString();

                   double origin1 = Double.valueOf(originLat);
                   double origin2 = Double.valueOf(originLon);

                   double destination1 = Double.valueOf(destinationLat);
                   double destination2 = Double.valueOf(destinationLon);

                    final LatLng originPoint = new LatLng(origin1, origin2);
                    final LatLng destinationPoint = new LatLng(destination1, destination2);

                    Log.d("success", "onComplete: " + waypointsList);

                    routeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String uri = "https://www.google.com/maps/dir/?api=1&origin=" + originPoint.latitude + "," + originPoint.longitude + "&destination=" + destinationPoint.latitude + "," + destinationPoint.longitude + "&waypoints=" + waypointString + "&travelmode=driving&dir_action=navigate";
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            startActivity(intent);
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DocumentReference document = database.collection("users").document(user.getUid()).collection("routes").document(deleteButton.getTag().toString());

                            document.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("success", "DocumentSnapshot successfully deleted!");
                                            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                                            startActivity(i);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("error", "Error deleting document", e);
                                        }
                                    });
                        }
                    });

                    ll.addView(row);
                }

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void waypointParser(ArrayList waypointsList) {
        if (waypointsList == null || waypointsList.size() == 0) {
            waypointString = "";
        } else {
            String[] waypointStrs = new String[waypointsList.size()];

            for (int i = 0; i < waypointsList.size(); i++) {
                LatLng waypoint = (LatLng) waypointsList.get(i);
                waypointStrs[i] = waypoint.latitude + "," + waypoint.longitude;
            }
            waypointString = join('|', waypointStrs);
            waypointString.replace("|", "via:");
        }
    }
}
