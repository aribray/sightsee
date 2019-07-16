package com.example.sightsee.ui;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.app.NotificationManagerCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

//import com.afollestad.materialdialogs.MaterialDialog;
import com.example.sightsee.R;
import com.example.sightsee.model.DouglasPeucker;
import com.example.sightsee.model.IMaps;
import com.example.sightsee.model.MapRoute;
import com.example.sightsee.model.RouteBoxerTask;
import com.example.sightsee.model.RouteTask;
import com.example.sightsee.utils.SearchDirection;
import com.example.sightsee.utils.SearchDirectionListener;
import com.example.sightsee.utils.TestingDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

//import ap.mobile.routeboxer.helper.FileHelper;
import com.example.sightsee.model.RouteBoxer;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, IMaps, RouteBoxerTask.IRouteBoxerTask,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, SearchDirectionListener {

//    private static final int NOTIFICATION_ID = 99;
//    private static final int WEAR_REQUEST_CODE = 77;
//    private static final int WEAR_REQUEST_CODE_2 = 88;

    private final static int ALL_PERMISSIONS_RESULT = 101;
    public static final int PLACE_AUTOCOMPLETE_FROM_PLACE_REQUEST_CODE=1;
    public static final int PLACE_AUTOCOMPLETE_TO_PLACE_REQUEST_CODE=2;
    private long UPDATE_INTERVAL = 15000;  /* 15 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    //    private Toolbar myToolbar;
    private GoogleMap mMap;
    Location mLocation;
    private LocationRequest mLocationRequest;

    private int distance = 200; // meter

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private LatLngBounds bounds;
    private Polyline routePolyline, simplifiedPolyline;
    private GoogleApiClient mGoogleApiClient;
    private Marker originMarker;
    private Marker destinationMarker;
    private String origin;
    private String destination;
    private ArrayList<RouteBoxer.Box> boxes;
    private ArrayList<Polygon> boxPolygons;
    private ArrayList<Polygon> gridBoxes;
    //    private DistanceDialog dialog;
    private float defaultZoom = 13;
    private TestingDialog testDialog;
    //    private MaterialDialog myTestDialog;
//    private MaterialDialog routeBoxProcessDialog;
    private String json;
    //    private WearActionReceiver wearActionReceiver;
    private Context mContext;
    private AutocompleteSupportFragment etOrigin;
    private AutocompleteSupportFragment etDestination;
    private Button btnFindPath;

    private int toleranceDistance = 200;
    private Polyline line;
    private LatLng start, end;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create an instance of GoogleAPIClient.
        if (this.mGoogleApiClient == null) {
            this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        setContentView(R.layout.activity_maps);
//        ActionBar actionBar = this.getSupportActionBar();
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.show();
        btnFindPath = (Button) findViewById(R.id.btnFindPath);

        etOrigin = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.etOrigin);

        etDestination = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.etDestination);
//        this.getSupportActionBar().setHomeButtonEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.mContext = this;


//         Initialize the Origin AutocompleteSupportFragment.


        etOrigin.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        etOrigin.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                                                            @Override
                                                            public void onPlaceSelected(Place place) {
                                                                // TODO: Get info about the selected place.
                                                                etOrigin.setText(place.getName());
                                                                origin = place.getId();
                                                                Log.i("Success", "Place: " + place.getAddress() + ", " + place.getLatLng());
                                                                Log.i("Success", origin);
                                                            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("error", "An error occurred: " + status);
            }
        });



        etDestination.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        etDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                etDestination.setText(place.getAddress());
                destination = place.getId();
                Log.i("Success", "Place: " + place.getName() + ", " + place.getId());
                Log.i("Success", destination);
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("error", "An error occurred: " + status);
            }
        });

        String res = getString(R.string.google_api_key);
        Places.initialize(getApplicationContext(), res);

        PlacesClient placesClient = Places.createClient(this);

        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
    }

    protected void onStart() {
        this.mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        this.mGoogleApiClient.disconnect();
        super.onStop();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 99);
            return;
        }
        this.mMap.setMyLocationEnabled(true);
//        this.mMap.setOnMapLongClickListener(this);
//        this.mMap.setOnInfoWindowClickListener(this);

        UiSettings uiSettings = this.mMap.getUiSettings();

        uiSettings.setCompassEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMapToolbarEnabled(true);

//        if (this.origin != null)
//            this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(this.origin, this.defaultZoom));


    }

    public static class Route implements Serializable {
        public ArrayList<LatLng> points;

        public Route(ArrayList<LatLng> points) {
            this.points = points;
        }
    }

    @Override
    public void onJSONRouteLoaded(ArrayList<LatLng> route) throws IOException {
//        Toast.makeText(this, "I'm here", Toast.LENGTH_SHORT).show();
//
//        PolylineOptions polylineOptions = new PolylineOptions()
//                .color(Color.RED)
//                .width(8);
//        for (LatLng point : route)
//            polylineOptions.add(point);
//        if (this.routePolyline != null)
//            this.routePolyline.remove();
//        this.routePolyline = this.mMap.addPolyline(polylineOptions);
//
//        ArrayList<DouglasPeucker.Point> points = new ArrayList<>();
//        for(LatLng point: route)
//            points.add(new DouglasPeucker.Point(point.latitude, point.longitude));
//
//        DouglasPeucker douglasPeucker = new DouglasPeucker();
//        this.toleranceDistance = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_key_tolerance_distance", "200"));
//        ArrayList<DouglasPeucker.Point> simplifiedRoute = douglasPeucker.simplify(points, this.toleranceDistance);
//
//        PolylineOptions simplifiedPolylineOptions = new PolylineOptions()
//                .color(Color.BLUE)
//                .width(8);
//        for(DouglasPeucker.Point latLng : simplifiedRoute)
//            simplifiedPolylineOptions.add(new LatLng(latLng.latitude, latLng.longitude));
//
//        if(this.simplifiedPolyline != null)
//            this.simplifiedPolyline.remove();
//
//        this.simplifiedPolyline = this.mMap.addPolyline(simplifiedPolylineOptions);
//
//        if (this.boxPolygons == null)
//            this.boxPolygons = new ArrayList<>();
//        else {
//            for (Polygon polygon : this.boxPolygons) {
//                polygon.remove();
//            }
//        }
//
//        ArrayList<LatLng> sRoute = new ArrayList<>();
//        for(DouglasPeucker.Point point: simplifiedRoute)
//            sRoute.add(new LatLng(point.latitude, point.longitude));
//
//        RouteBoxerTask routeBoxerTask = new RouteBoxerTask(route, this.distance, this);
//        routeBoxerTask.execute();
    }

    @Override
    public void routeJsonObtained(String json) {

        this.json = json;
        if (isStoragePermissionGranted())
            this.writeJsonToFile(json);

    }


    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("RouteBoxer", "Permission is granted");
                return true;
            } else {

                Log.v("RouteBoxer", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("RouteBoxer", "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            this.writeJsonToFile(this.json);
    }

    private void writeJsonToFile(String json) {
        if (isExternalStorageAvailable() && !isExternalStorageReadOnly()) {
            //saveButton.setEnabled(false);

            String filepath = "RouteBoxer";
            String indexFilename = "idx.txt";
            int index = 0;

            File indexFile = new File(getExternalFilesDir(filepath), indexFilename);
            String myData = "";

            try {
                FileInputStream fis = new FileInputStream(indexFile);
                DataInputStream in = new DataInputStream(fis);
                BufferedReader br =
                        new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    myData = myData + strLine;
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (myData == "") index = 0;
            else index = Integer.valueOf(myData);

            try {
                FileOutputStream fos = new FileOutputStream(new File(getExternalFilesDir(filepath), "route-" + index + ".txt"));
                fos.write(json.getBytes());
                fos.close();

                index++;

                fos = new FileOutputStream(indexFile);
                fos.write(String.valueOf(index).getBytes());
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onRouteBoxerTaskComplete(ArrayList<RouteBoxer.Box> boxes) {
        this.draw(boxes, Color.argb(128, 255, 0, 0), Color.argb(15, 255, 0, 0));
//        if(this.routeBoxProcessDialog != null && this.routeBoxProcessDialog.isShowing())
//            this.routeBoxProcessDialog.dismiss();
//        this.showNotification();
    }

//    private void showNotification() {
//        Intent dismissIntent = new Intent(WearActionReceiver.WEAR_ACTION);
//        dismissIntent.putExtra(WearActionReceiver.WEAR_ACTION_CODE, WearActionReceiver.DISMISS_NOTIFICATION);
//
//        PendingIntent pendingIntentDismiss = PendingIntent.getBroadcast(mContext, WEAR_REQUEST_CODE,
//                dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Intent recalculateIntent = new Intent(WearActionReceiver.WEAR_ACTION);
//        recalculateIntent.putExtra(WearActionReceiver.WEAR_ACTION_CODE, WearActionReceiver.RECALCULATE);
//
//        PendingIntent pendingIntentRecalculate = PendingIntent.getBroadcast(mContext, WEAR_REQUEST_CODE_2,
//                recalculateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.ic_action_call_split)
//                .setContentTitle("RouteBoxer")
//                .setContentText("RouteBoxer has completed the computation process")
//                .addAction(R.mipmap.ic_launcher_round, "Dismiss", pendingIntentDismiss)
//                .addAction(R.mipmap.ic_launcher_round, "Recalculate", pendingIntentRecalculate);
//
//        NotificationManagerCompat notificationManagerCompat =
//                NotificationManagerCompat.from(this);
//        notificationManagerCompat.notify(NOTIFICATION_ID, mBuilder.build());
//    }

    @Override
    public void onRouteBoxerMessage(String message) {
//        if(this.routeBoxProcessDialog != null && this.routeBoxProcessDialog.isShowing())
//            this.routeBoxProcessDialog.setContent(message);
    }

    @Override
    public void onRouteBoxerGrid(ArrayList<RouteBoxer.Box> boxes, int boxBorderColor, int markedColor, int simpleMarkedColor) {
        if (this.gridBoxes == null)
            this.gridBoxes = new ArrayList<>();
        else this.gridBoxes.clear();

        for (RouteBoxer.Box box : boxes) {
            LatLng nw = new LatLng(box.ne.latitude, box.sw.longitude);
            LatLng se = new LatLng(box.sw.latitude, box.ne.longitude);
            LatLng sw = new LatLng(box.sw.latitude, box.sw.longitude);
            LatLng ne = new LatLng(box.ne.latitude, box.ne.longitude);
            PolygonOptions polygonOptions = new PolygonOptions()
                    .add(sw, nw, ne, se, sw)
                    .strokeColor(boxBorderColor)
                    .strokeWidth(3);
            if (box.simpleMarked) {
                polygonOptions.strokeColor(boxBorderColor)
                        .fillColor(simpleMarkedColor);
            } else if (box.marked) {
                polygonOptions.strokeColor(boxBorderColor)
                        .fillColor(markedColor);
            } else
                polygonOptions.fillColor(Color.TRANSPARENT);
            Polygon boxPolygon = mMap.addPolygon(polygonOptions);
            this.gridBoxes.add(boxPolygon);
        }
    }

    @Override
    public void onRouteBoxerBoxes(ArrayList<RouteBoxer.Box> boxes, int boxBorderColor, int boxFillColor) {
    }

    @Override
    public void onRouteBoxerSimplifiedRoute(ArrayList<LatLng> simplifiedRoute, int lineColor) {
        boolean simplify = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_simplify", true);
        if (!simplify) return;

        PolylineOptions polylineOptions = new PolylineOptions()
                .color(lineColor)
                .width(8);
        for (LatLng point : simplifiedRoute)
            polylineOptions.add(point);
        if (this.simplifiedPolyline != null)
            this.simplifiedPolyline.remove();
        this.simplifiedPolyline = this.mMap.addPolyline(polylineOptions);
        List<PatternItem> pattern = Arrays.asList(
                new Dash(30), new Gap(10));
        this.simplifiedPolyline.setPattern(pattern);
    }

    private void draw(ArrayList<RouteBoxer.Box> boxes, int color, int fillColor) {

        if (this.boxPolygons == null)
            this.boxPolygons = new ArrayList<>();
        else this.boxPolygons.clear();

        for (RouteBoxer.Box box : boxes) {
            LatLng nw = new LatLng(box.ne.latitude, box.sw.longitude);
            LatLng se = new LatLng(box.sw.latitude, box.ne.longitude);
            LatLng sw = new LatLng(box.sw.latitude, box.sw.longitude);
            LatLng ne = new LatLng(box.ne.latitude, box.ne.longitude);
            PolygonOptions polygonOptions = new PolygonOptions()
                    .add(sw, nw, ne, se, sw)
                    .strokeColor(color)
                    .strokeWidth(5);
            if (box.marked) {
                polygonOptions.strokeColor(Color.DKGRAY)
                        .fillColor(Color.argb(96, 0, 0, 0));
            } else if (box.expandMarked) {
                polygonOptions.strokeColor(Color.DKGRAY)
                        .fillColor(Color.argb(72, 0, 0, 0));
            } else
                polygonOptions.fillColor(fillColor);
            Polygon boxPolygon = mMap.addPolygon(polygonOptions);
            this.boxPolygons.add(boxPolygon);
        }

        return;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 99);
            return;
        }

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12);
            mMap.animateCamera(cameraUpdate);

            startLocationUpdates();

//        Location location = LocationServices.FusedLocationApi.getLastLocation(this.mGoogleApiClient);
//        this.start = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        //origin = new LatLng(38.595900, -89.985198);
//        start = new LatLng(38.506380, -89.968063);

        if (this.start == null)
            new AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setMessage("Unable to obtain your last known location. Please enable Location on Settings.")
                    .show();
        if (this.mMap != null && this.start != null) {
            this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(this.start, this.defaultZoom));
            MarkerOptions originMarkerOptions = new MarkerOptions()
                    .title("Your location")
                    .snippet("Your last known location")
                    .position(this.start)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            if (this.originMarker != null)
                this.originMarker.remove();
            this.originMarker = this.mMap.addMarker(originMarkerOptions);
        }
        this.mGoogleApiClient.disconnect();
    }

    private void sendRequest() {
//        Toast.makeText(this, "I'm here", Toast.LENGTH_SHORT).show();
//        String origin = etOrigin.toString();
//        String destination = etDestination.toString();
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new SearchDirection( this, origin, destination).execute();
//            infoLayout.setVisibility(View.VISIBLE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Enable Permissions", Toast.LENGTH_LONG).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, (LocationListener) this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    public void onLocationChanged(Location location) {


    }

//    @Override
//    public void onMapLongClick(LatLng destination) {
//
//        this.destination = destination;
//
//        //this.destination = new LatLng(-7.953037137608645,112.63877917081118);
//        //this.origin = new LatLng(-7.9545055,112.6148412);
//
//        //this.destination = new LatLng(-7.982594952681266,112.63102859258652);
//        //this.origin = new LatLng(-7.9520931,112.6126944);
//
//        MarkerOptions destinationMarkerOptions = new MarkerOptions()
//                .title("Destination")
//                .position(this.destination)
//                .snippet("Tap to RouteBox");
//
//        if (this.destinationMarker != null)
//            this.destinationMarker.remove();
//        this.destinationMarker = this.mMap.addMarker(destinationMarkerOptions);
//        this.destinationMarker.showInfoWindow();
//
//
//    }

//    @Override
//    public void onInfoWindowClick(Marker marker) {
//        this.recalculateBox();
//    }

    private void recalculateBox() {
        if (this.start != null && this.end != null) {
            //origin = new LatLng(38.595900, -89.985198);
            //destination = new LatLng(38.506360, -89.984318);
            //destination = new LatLng(38.506380, -89.968063);
            //origin = new LatLng(38.504700, -89.851810);

            //this.origin = new LatLng(-7.9544773,112.6148372);
            //this.destination = new LatLng(-7.953271897865304,112.63915132731199);

            RouteTask routeTask = new RouteTask(this, this.start, this.end);
            if (routeTask.getStatus() == AsyncTask.Status.PENDING)
                routeTask.execute();
            /*
            this.routeBoxProcessDialog = new MaterialDialog.Builder(this)
                    .cancelable(false)
                    .content("Obtaining boxes...")
                    .progress(true, 0)
                    .progressIndeterminateStyle(true)
                    .show();
            */
        }
    }

    int bpNum = 3;
    android.os.Handler handler = new Handler();

    Runnable waitRunnable = new Runnable() {
        @Override
        public void run() {
            if (bpNum == 0) {
                if (MapsActivity.this.testDialog != null)
                    MapsActivity.this.testDialog.dismiss();
            } else handler.postDelayed(this, 500);
        }
    };

    @Override
    public void onDirectionFinderStart() {

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<MapRoute> mapRoutes) {
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();


        for (MapRoute mapRoute : mapRoutes) {
//            Log.d("checking", "onDirectionFinderSuccess: " + mapRoute.points);

            PolylineOptions polylineOptions = new PolylineOptions()
                    .color(Color.RED)
                    .width(8);
            for (LatLng point : mapRoute.points)
                polylineOptions.add(point);
            if (this.routePolyline != null)
                this.routePolyline.remove();
            this.routePolyline = this.mMap.addPolyline(polylineOptions);

            ArrayList<DouglasPeucker.Point> points = new ArrayList<>();
            for (LatLng point : mapRoute.points)
                points.add(new DouglasPeucker.Point(point.latitude, point.longitude));

            DouglasPeucker douglasPeucker = new DouglasPeucker();
            this.toleranceDistance = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_key_tolerance_distance", "200"));
            ArrayList<DouglasPeucker.Point> simplifiedRoute = douglasPeucker.simplify(points, this.toleranceDistance);

            PolylineOptions simplifiedPolylineOptions = new PolylineOptions()
                    .color(Color.BLUE)
                    .width(8);
            for (DouglasPeucker.Point latLng : simplifiedRoute)
                simplifiedPolylineOptions.add(new LatLng(latLng.latitude, latLng.longitude));

            if (this.simplifiedPolyline != null)
                this.simplifiedPolyline.remove();

            this.simplifiedPolyline = this.mMap.addPolyline(simplifiedPolylineOptions);

            if (this.boxPolygons == null)
                this.boxPolygons = new ArrayList<>();
            else {
                for (Polygon polygon : this.boxPolygons) {
                    polygon.remove();
                }
            }

            ArrayList<LatLng> sRoute = new ArrayList<>();
            for (DouglasPeucker.Point point : simplifiedRoute)
                sRoute.add(new LatLng(point.latitude, point.longitude));

            RouteBoxerTask routeBoxerTask = new RouteBoxerTask(mapRoute.points, this.distance, this);
            routeBoxerTask.execute();

            ((TextView) findViewById(R.id.tvDuration)).setText(mapRoute.mapDuration.txtDuration);
            ((TextView) findViewById(R.id.tvDistance)).setText(mapRoute.mapDistance.txtDistance);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
//                   use for default marker image on map
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//                    use for show custom image on map
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(mapRoute.startAddress)
                    .position(mapRoute.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title(mapRoute.endAddress)
                    .position(mapRoute.endLocation)));

//            PolylineOptions polylineOptions = new PolylineOptions().
//                    geodesic(true).
//                    color(Color.rgb(82, 219, 255)).
//                    width(10);

            for (int i = 0; i < mapRoute.points.size(); i++)
                polylineOptions.add(mapRoute.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));


            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : originMarkers) {
                builder.include(marker.getPosition());
            }

            for (Marker marker : destinationMarkers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();

            int padding = 20; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            mMap.moveCamera(cu);
        }
    }



//    public void connectClient()
//    {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//    }
//
//    private ArrayList findUnAskedPermissions(ArrayList wanted) {
//        ArrayList result = new ArrayList();
//
//        for (Object perm : wanted) {
//            if (!hasPermission((String) perm)) {
//                result.add(perm);
//            }
//        }
//
//        return result;
//    }
}

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//
//                this.dialog = new DistanceDialog();
//                this.dialog.setDistance(this.distance);
//                this.dialog.show(this.getSupportFragmentManager(), "distanceDialog");
//
//                return true;
//
//            case R.id.action_test:
//
//                /*
//                this.testDialog = new TestingDialog();
//                this.testDialog.show(this.getSupportFragmentManager(), "testingDialog");
//                this.testDialog.text("Getting path...");
//                handler.postDelayed(waitRunnable, 1);
//                bpNum = 0;
//                IMaps vIMaps = new IMaps() {
//                    @Override
//                    public void onJSONRouteLoaded(ArrayList<LatLng> route) throws IOException {
//                        StringBuilder sb = new StringBuilder();
//                        for(LatLng pos: route)
//                            sb.append(pos.latitude + ";" + pos.longitude + "\n");
//                        FileHelper.write(MapsActivity.this, "path-v.txt", sb.toString(), true);
//                        bpNum--;
//                    }
//                };
//                IMaps hIMaps = new IMaps() {
//                    @Override
//                    public void onJSONRouteLoaded(ArrayList<LatLng> route) throws IOException {
//                        StringBuilder sb = new StringBuilder();
//                        for(LatLng pos: route)
//                            sb.append(pos.latitude + ";" + pos.longitude + "\n");
//                        FileHelper.write(MapsActivity.this, "path-h.txt", sb.toString(), true);
//                        bpNum--;
//                    }
//                };
//                IMaps dIMaps = new IMaps() {
//                    @Override
//                    public void onJSONRouteLoaded(ArrayList<LatLng> route) throws IOException {
//                        StringBuilder sb = new StringBuilder();
//                        for(LatLng pos: route)
//                            sb.append(pos.latitude + ";" + pos.longitude + "\n");
//                        FileHelper.write(MapsActivity.this, "path-d.txt", sb.toString(), true);
//                        bpNum--;
//                    }
//                };
//                try {
//                    boolean reroute = false;
//                    if(bpNum > 0) reroute = true;
//                    if (!FileHelper.exists(this, "path-v.txt") || reroute) {
//                        LatLng vStart = new LatLng(38.595900, -89.985198);
//                        LatLng vEnd = new LatLng(38.506360, -89.984318);
//                        RouteTask vRouteTask = new RouteTask(vIMaps, vStart, vEnd);
//                        vRouteTask.execute();
//                    }
//                    if (!FileHelper.exists(this, "path-h.txt") || reroute) {
//                        LatLng hStart = new LatLng(38.506380, -89.968063);
//                        LatLng hEnd = new LatLng(38.504700, -89.851810);
//                        RouteTask hRouteTask = new RouteTask(hIMaps, hStart, hEnd);
//                        hRouteTask.execute();
//                    }
//                    if (!FileHelper.exists(this, "path-d.txt") || reroute) {
//                        LatLng dStart = new LatLng(38.621889, -90.153827);
//                        LatLng dEnd = new LatLng(38.555006, -90.077097);
//                        RouteTask dRouteTask = new RouteTask(dIMaps, dStart, dEnd);
//                        dRouteTask.execute();
//                    }
//                    String vRaw = FileHelper.read(this, "path-v.txt").trim();
//                    String[] pairs = vRaw.split("\n");
//                    ArrayList<LatLng> points = new ArrayList<>();
//                    for(String data: pairs) {
//                        String[] cols = data.split(";");
//                        LatLng point = new LatLng(Double.parseDouble(cols[0]), Double.parseDouble(cols[1]));
//                        points.add(point);
//                    }
//                    while(points.size() > 100)
//                        points.remove((new Random()).nextInt((points.size()-2))+1);
//                    String hRaw = FileHelper.read(this, "path-h.txt").trim();
//                    String[] hPairs = hRaw.split("\n");
//                    ArrayList<LatLng> hPoints = new ArrayList<>();
//                    for(String data: hPairs) {
//                        String[] cols = data.split(";");
//                        LatLng point = new LatLng(Double.parseDouble(cols[0]), Double.parseDouble(cols[1]));
//                        hPoints.add(point);
//                    }
//                    while(hPoints.size() > 100)
//                        hPoints.remove((new Random()).nextInt((hPoints.size()-2))+1);
//                    String dRaw = FileHelper.read(this, "path-d.txt").trim();
//                    String[] vPairs = dRaw.split("\n");
//                    ArrayList<LatLng> dPoints = new ArrayList<>();
//                    for(String data: vPairs) {
//                        String[] cols = data.split(";");
//                        LatLng point = new LatLng(Double.parseDouble(cols[0]), Double.parseDouble(cols[1]));
//                        dPoints.add(point);
//                    }
//                    while(dPoints.size() > 100)
//                        dPoints.remove((new Random()).nextInt((dPoints.size()-2))+1);
//                    //TestTask testTask = new TestTask(this, points, dPoints, hPoints);
//                    //testTask.setStatusInterface(this);
//                    //testTask.execute();
//                } catch (Exception ex) {
//                    this.testDialog.text(ex.getMessage());
//                }
//                //this.testDialog.dismiss();
//                */
//
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    @Override
//    public void onClick(DialogInterface dialog, int which) {
//        switch (which) {
//            case DialogInterface.BUTTON_POSITIVE:
//                this.distance = (int) this.dialog.distance;
//                break;
//            case DialogInterface.BUTTON_NEGATIVE:
//                dialog.dismiss();
//                break;
//        }
//    }
//
//    @Override
//    public void start() {
//        this.myTestDialog = new MaterialDialog.Builder(this).content("Loading...")
//                .cancelable(false)
//                .show();
//    }
//
//    @Override
//    public void showStatus(String status) {
//        this.myTestDialog.setContent(status);
//    }
//
//    @Override
//    public void showError(String error) {
//        this.myTestDialog.setContent(error);
//    }
//
//    @Override
//    public void done() {
//        this.myTestDialog.dismiss();
//    }
//}

//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.fragment.app.FragmentActivity;
//
//import android.annotation.TargetApi;
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.location.Location;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.sightsee.R;
//import com.example.sightsee.model.MapRoute;
//import com.example.sightsee.utils.SearchDirection;
//import com.example.sightsee.utils.SearchDirectionListener;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;
//import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
//import com.google.android.gms.common.GooglePlayServicesRepairableException;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.LocationListener;
//
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdate;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.LatLngBounds;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.Polyline;
//import com.google.android.gms.maps.model.PolylineOptions;
//
//import com.google.android.libraries.places.api.Places;
//import com.google.android.libraries.places.api.model.Place;
//import com.google.android.libraries.places.api.net.PlacesClient;
//import com.google.android.libraries.places.widget.Autocomplete;
//import com.google.android.libraries.places.widget.AutocompleteActivity;
//import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
//import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
//import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
//
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
//import static android.Manifest.permission.ACCESS_FINE_LOCATION;
//
//public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener, LocationListener, SearchDirectionListener {
//
//    private GoogleMap mMap;
//    Location mLocation;
//    GoogleApiClient mGoogleApiClient;
//    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
//    private LocationRequest mLocationRequest;
//    private long UPDATE_INTERVAL = 15000;  /* 15 secs */
//    private long FASTEST_INTERVAL = 5000; /* 5 secs */
//
//    public boolean etOriginSelected =false ;
//    public boolean etDestinationSelected =false;
//
//    private Button btnFindPath;
////    private EditText etOrigin;
////    private EditText etDestination;
//    private List<Marker> originMarkers = new ArrayList<>();
//    private List<Marker> destinationMarkers = new ArrayList<>();
//    private List<Polyline> polylinePaths = new ArrayList<>();
//    private ProgressDialog progressDialog;
//    private LinearLayout infoLayout;
//    private ArrayList<String> permissionsToRequest;
//    private ArrayList permissionsRejected = new ArrayList();
//    private ArrayList permissions = new ArrayList();
//
//    private AutocompleteSupportFragment etOrigin;
//    private AutocompleteSupportFragment etDestination;
//    private String origin;
//    private String destination;
//
//    private final static int ALL_PERMISSIONS_RESULT = 101;
//    public static final int PLACE_AUTOCOMPLETE_FROM_PLACE_REQUEST_CODE=1;
//    public static final int PLACE_AUTOCOMPLETE_TO_PLACE_REQUEST_CODE=2;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//
//        btnFindPath = (Button) findViewById(R.id.btnFindPath);
////        etOrigin = (EditText) findViewById(R.id.etOrigin);
////        etDestination = (EditText) findViewById(R.id.etDestination);
//
//        etOrigin = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.etOrigin);
//
//        etDestination = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.etDestination);
//        infoLayout = findViewById(R.id.infoLayout);
//
//
//        // Initialize the Origin AutocompleteSupportFragment.
//
//
//        etOrigin.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
//
//        etOrigin.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//                                                            @Override
//                                                            public void onPlaceSelected(Place place) {
//                                                                // TODO: Get info about the selected place.
//                                                                etOrigin.setText(place.getName());
//                                                                origin = place.getId();
//                                                                Log.i("Success", "Place: " + place.getAddress() + ", " + place.getLatLng());
//                                                                Log.i("Success", origin);
//                                                            }
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i("error", "An error occurred: " + status);
//            }
//        });
//
//
//
//        etDestination.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
//
//        etDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                etDestination.setText(place.getAddress());
//                destination = place.getId();
//                Log.i("Success", "Place: " + place.getName() + ", " + place.getId());
//                Log.i("Success", destination);
//            }
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i("error", "An error occurred: " + status);
//            }
//        });
//
//
////        etOrigin.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                findPlace();
////            }
////        });
////        etDestination.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                findPlace2();
////            }
////        });
//
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//        String res = getString(R.string.google_api_key);
//        Places.initialize(getApplicationContext(), res);
//
//        PlacesClient placesClient = Places.createClient(this);
//
//
//
//        btnFindPath.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendRequest();
//            }
//        });
//
//
//
//        permissions.add(ACCESS_FINE_LOCATION);
//        permissions.add(ACCESS_COARSE_LOCATION);
//
//        permissionsToRequest = findUnAskedPermissions(permissions);
//        //get the permissions we have asked for before but are not granted..
//        //we will store this in a global list to access later.
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//
//            if (permissionsToRequest.size() > 0) {
//                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
//            }
//        }
//
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//
//
//        connectClient();
//    }
//
////    private void findPlace2() {
//////        Toast.makeText(this, "I'm here too", Toast.LENGTH_SHORT).show();
////        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
////            Intent intent = new Autocomplete
////                    .IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
////                    .build(MapsActivity.this);
////            startActivityForResult(intent, PLACE_AUTOCOMPLETE_TO_PLACE_REQUEST_CODE);
////
////    }
////
////    private void findPlace() {
//////        Toast.makeText(this, "I'm here", Toast.LENGTH_SHORT).show();
////        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
////
////            Intent intent = new Autocomplete
////                    .IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
////                    .build(MapsActivity.this);
////            startActivityForResult(intent, PLACE_AUTOCOMPLETE_FROM_PLACE_REQUEST_CODE);
////    }
//
////    @Override
////    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//////        Toast.makeText(this, "I'm here", Toast.LENGTH_SHORT).show();
////        if (requestCode == PLACE_AUTOCOMPLETE_FROM_PLACE_REQUEST_CODE) {
////            if (resultCode == RESULT_OK) {
////                Toast.makeText(this, "I'm here", Toast.LENGTH_SHORT).show();
////                Place place = Autocomplete.getPlaceFromIntent(data);
////                String address = (String) place.getAddress();
////                etOrigin.setText(address, TextView.BufferType.EDITABLE);
////                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
////                // TODO: Handle the error.
////                Status status = Autocomplete.getStatusFromIntent(data);
////                Log.d("error", status.getStatusMessage());
////            } else if (resultCode == RESULT_CANCELED) {
////                // The user canceled the operation.
////            }
////            } else if (requestCode == PLACE_AUTOCOMPLETE_TO_PLACE_REQUEST_CODE) {
////            if (resultCode == RESULT_OK) {
////                Place place = Autocomplete.getPlaceFromIntent(data);
////                String address = (String) place.getAddress();
////                etDestination.setText(address, TextView.BufferType.EDITABLE);
////            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
////                // TODO: Handle the error.
////                Status status = Autocomplete.getStatusFromIntent(data);
////                Log.d("error", status.getStatusMessage());
////            } else if (resultCode == RESULT_CANCELED) {
////                // The user canceled the operation.
////            }
////
////        }
////    }
//
//    private void sendRequest() {
//        Toast.makeText(this, "I'm here", Toast.LENGTH_SHORT).show();
////        String origin = etOrigin.toString();
////        String destination = etDestination.toString();
//        if (origin.isEmpty()) {
//            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (destination.isEmpty()) {
//            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        try {
//            new SearchDirection( this, origin, destination).execute();
//            infoLayout.setVisibility(View.VISIBLE);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        mMap.setMyLocationEnabled(true);
//    }
//
//
//
//
//    @Override
//    public void onDirectionFinderStart() {
//
//        if (originMarkers != null) {
//            for (Marker marker : originMarkers) {
//                marker.remove();
//            }
//        }
//
//        if (destinationMarkers != null) {
//            for (Marker marker : destinationMarkers) {
//                marker.remove();
//            }
//        }
//
//        if (polylinePaths != null) {
//            for (Polyline polyline : polylinePaths) {
//                polyline.remove();
//            }
//        }
//    }
//
//    @Override
//    public void onDirectionFinderSuccess(List<MapRoute> mapRoutes) {
//        polylinePaths = new ArrayList<>();
//        originMarkers = new ArrayList<>();
//        destinationMarkers = new ArrayList<>();
//
//
//        for (MapRoute mapRoute : mapRoutes) {
//            ((TextView) findViewById(R.id.tvDuration)).setText(mapRoute.mapDuration.txtDuration);
//            ((TextView) findViewById(R.id.tvDistance)).setText(mapRoute.mapDistance.txtDistance);
//
//            originMarkers.add(mMap.addMarker(new MarkerOptions()
////                   use for default marker image on map
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
////                    use for show custom image on map
////                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
//                    .title(mapRoute.startAddress)
//                    .position(mapRoute.startLocation)));
//            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//                    .title(mapRoute.endAddress)
//                    .position(mapRoute.endLocation)));
//
//            PolylineOptions polylineOptions = new PolylineOptions().
//                    geodesic(true).
//                    color(Color.rgb(82, 219, 255)).
//                    width(10);
//
//            for (int i = 0; i < mapRoute.points.size(); i++)
//                polylineOptions.add(mapRoute.points.get(i));
//
//            polylinePaths.add(mMap.addPolyline(polylineOptions));
//        }
//
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        for (Marker marker : originMarkers) {
//            builder.include(marker.getPosition());
//        }
//
//        for (Marker marker : destinationMarkers) {
//            builder.include(marker.getPosition());
//        }
//        LatLngBounds bounds = builder.build();
//
//        int padding = 20; // offset from edges of the map in pixels
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//
//        mMap.moveCamera(cu);
//    }
//
//
//    public void connectClient()
//    {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//    }
//
//    private ArrayList findUnAskedPermissions(ArrayList wanted) {
//        ArrayList result = new ArrayList();
//
//        for (Object perm : wanted) {
//            if (!hasPermission((String) perm)) {
//                result.add(perm);
//            }
//        }
//
//        return result;
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.connect();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        if (!checkPlayServices()) {
//            Toast.makeText(getApplicationContext(),"Please install google play services",Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//
//
//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//
//            LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12);
//            mMap.animateCamera(cameraUpdate);
//
//            startLocationUpdates();
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//
//
//    }
//
//    private boolean checkPlayServices() {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (apiAvailability.isUserResolvableError(resultCode)) {
//                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
//                        .show();
//            } else
//                finish();
//
//            return false;
//        }
//        return true;
//    }
//
//    protected void startLocationUpdates() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(UPDATE_INTERVAL);
//        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
//        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(getApplicationContext(), "Enable Permissions", Toast.LENGTH_LONG).show();
//        }
//
//        LocationServices.FusedLocationApi.requestLocationUpdates(
//                mGoogleApiClient, mLocationRequest, this);
//
//
//    }
//
//    private boolean hasPermission(String permission) {
//        if (canMakeSmores()) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
//            }
//        }
//        return true;
//    }
//
//    private boolean canMakeSmores() {
//        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
//    }
//
//
//    @TargetApi(Build.VERSION_CODES.M)
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//
//        switch (requestCode) {
//
//            case ALL_PERMISSIONS_RESULT:
//                for (String perms : permissionsToRequest) {
//                    if (!hasPermission(perms)) {
//                        permissionsRejected.add(perms);
//                    }
//                }
//
//                if (permissionsRejected.size() > 0) {
//
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
//                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
//                                            }
//                                        }
//                                    });
//                            return;
//                        }
//                    }
//
//                }
//
//                break;
//        }
//
//    }
//
//    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
//        new AlertDialog.Builder(MapsActivity.this)
//                .setMessage(message)
//                .setPositiveButton("OK", okListener)
//                .setNegativeButton("Cancel", null)
//                .create()
//                .show();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        stopLocationUpdates();
//    }
//
//
//    public void stopLocationUpdates()
//    {
//        if (mGoogleApiClient.isConnected()) {
//            LocationServices.FusedLocationApi
//                    .removeLocationUpdates(mGoogleApiClient, this);
//            mGoogleApiClient.disconnect();
//        }
//    }
//}
