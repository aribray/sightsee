package com.example.sightsee.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.Volley;
import com.example.sightsee.R;
import com.example.sightsee.model.DouglasPreucker;
import com.example.sightsee.model.IMaps;
import com.example.sightsee.model.MapDistance;
import com.example.sightsee.model.MapDuration;
import com.example.sightsee.model.MapRoute;
import com.example.sightsee.model.RouteBoxerTask;
import com.example.sightsee.model.RouteTask;
import com.example.sightsee.utils.MyAdapter;
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

//import ap.mobile.routeboxer.helper.FileHelper;
import com.example.sightsee.model.RouteBoxer;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.google.maps.internal.StringJoin.join;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, IMaps, RouteBoxerTask.IRouteBoxerTask,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, SearchDirectionListener {


    private final static int ALL_PERMISSIONS_RESULT = 101;
    public static final int PLACE_AUTOCOMPLETE_FROM_PLACE_REQUEST_CODE = 1;
    public static final int PLACE_AUTOCOMPLETE_TO_PLACE_REQUEST_CODE = 2;
    private long UPDATE_INTERVAL = 15000;  /* 15 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    private Toolbar myToolbar;
    private GoogleMap mMap;
    Location mLocation;
    private LocationRequest mLocationRequest;

    private int distance; // meter

    private String waypoints;
    private Marker marker;
    private ArrayList<LatLng> Latlng = new ArrayList<>();
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private ArrayList<Marker> waypointMarkers = new ArrayList<>();
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
    private Button btnSightsee;
    private Button btnRoute;

    private int toleranceDistance;
    private Polyline line;
    private LatLng start, end;
    public List<LatLng> centerBoxes;
    boolean runBoth = true;
    boolean simplify = false;
    private List<MapRoute> mapRoutes = new ArrayList<MapRoute>();
    private List<LatLng> path = new ArrayList();

    public MapsActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            distance = extras.getInt("max_distance");
        }

        toleranceDistance = distance;

        setContentView(R.layout.activity_maps);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);



        // Create an instance of GoogleAPIClient.
        if (this.mGoogleApiClient == null) {
            this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }



        btnFindPath = (Button) findViewById(R.id.btnFindPath);

        btnSightsee = (Button) findViewById(R.id.sightsee);

        btnRoute = (Button) findViewById(R.id.route);

        etOrigin = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.etOrigin);
        etOrigin.setHint("Origin");

        etDestination = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.etDestination);

        etDestination.setHint("Destination");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.mContext = this;

//         Initialize the Origin and Destination AutocompleteSupportFragments.

        etOrigin.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        etOrigin.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
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
                btnSightsee.setVisibility(View.VISIBLE);
                sendRequest();
            }
        });

        btnSightsee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waypointParser(waypointMarkers);
                redrawRoute();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.account:
//                getAccount();
                return true;
            case R.id.save:
//                saveRoute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
//        uiSettings.setMapToolbarEnabled(true);
    }

    public static class Route implements Serializable {
        public ArrayList<LatLng> points;

        public Route(ArrayList<LatLng> points) {
            this.points = points;
        }
    }

    @Override
    public void onJSONRouteLoaded(ArrayList<LatLng> route) {

//        Route r = new Route(image1_6);
//        Log.d("RouteBoxer", r.toString());
//
//        boolean simplify = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_simplify", true);
//        boolean runBoth = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_runboth", false);
//
//        RouteBoxerTask routeBoxerTask = new RouteBoxerTask(image1_6, this.distance, simplify, runBoth, this);
//        routeBoxerTask.execute();
//
//        PolylineOptions polylineOptions = new PolylineOptions()
//                .color(Color.RED)
//                .width(8);
//        for (LatLng point : image1_6)
//            polylineOptions.add(point);
//        if (this.routePolyline != null)
//            this.routePolyline.remove();
//        this.routePolyline = this.mMap.addPolyline(polylineOptions);
//        //this.routePolyline.setPattern(Arrays.asList(new Dash(30), new Gap(10)));
//        if (this.boxPolygons == null)
//            this.boxPolygons = new ArrayList<>();
//        else {
//            for (Polygon polygon : this.boxPolygons) {
//                polygon.remove();
//            }
//        }
//
//        if(this.gridBoxes != null) {
//            for(Polygon polygon: this.gridBoxes)
//                polygon.remove();
//        }
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
                FileOutputStream fos = new FileOutputStream(new File(getExternalFilesDir(filepath), "image1_6-" + index + ".txt"));
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
        Toast.makeText(this, "I'm here", Toast.LENGTH_SHORT).show();
        this.draw(boxes, Color.argb(128, 255, 0, 0), Color.argb(15, 255, 0, 0));
    }


    @Override
    public void onRouteBoxerMessage(String message) {
    }

    @Override
    public void onRouteBoxerGrid(ArrayList<RouteBoxer.Box> boxes, int boxBorderColor, int markedColor, int simpleMarkedColor) {
        if (this.gridBoxes == null)
            this.gridBoxes = new ArrayList<>();
        else this.gridBoxes.clear();

        centerBoxes = new ArrayList<LatLng>();
        for (RouteBoxer.Box box : boxes) {
            LatLng nw = new LatLng(box.ne.latitude, box.sw.longitude);
            LatLng se = new LatLng(box.sw.latitude, box.ne.longitude);
            LatLng sw = new LatLng(box.sw.latitude, box.sw.longitude);
            LatLng ne = new LatLng(box.ne.latitude, box.ne.longitude);
              if (box.marked) {
                LatLng center = new LatLng((box.ne.latitude + box.sw.latitude)/2, (box.ne.longitude + box.sw.longitude)/2);
                loadNearByPlaces(center.latitude, center.longitude);
            }
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
            }
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
    }

    private void sendRequest() {
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new SearchDirection(this, origin, destination).execute();
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


    private void recalculateBox() {
        if (this.start != null && this.end != null) {

            RouteTask routeTask = new RouteTask(this, this.start, this.end);
            if (routeTask.getStatus() == AsyncTask.Status.PENDING)
                routeTask.execute();
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
        waypointMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();


        for (MapRoute mapRoute : mapRoutes) {

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.rgb(82, 219, 255)).
                    width(10);

            ArrayList<DouglasPreucker.Point> points = new ArrayList<>();
            for (LatLng point : mapRoute.points)
                points.add(new DouglasPreucker.Point(point.latitude, point.longitude));

            DouglasPreucker douglasPreucker = new DouglasPreucker();
            this.toleranceDistance = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_key_tolerance_distance", "20000"));
//            ArrayList<DouglasPreucker.Point> simplifiedRoute = douglasPreucker.simplify(points, this.toleranceDistance);

            if (this.boxPolygons == null)
                this.boxPolygons = new ArrayList<>();
            else {
                for (Polygon polygon : this.boxPolygons) {
                    polygon.remove();
                }
            }

//            ArrayList<LatLng> sRoute = new ArrayList<>();
//            for (DouglasPreucker.Point point : simplifiedRoute)
//                sRoute.add(new LatLng(point.latitude, point.longitude));

            ((TextView) findViewById(R.id.tvDuration)).setText(mapRoute.mapDuration.txtDuration);
            ((TextView) findViewById(R.id.tvDistance)).setText(mapRoute.mapDistance.txtDistance);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
//                   use for default marker image on map
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//                    use for show custom image on map
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(mapRoute.startAddress)
                    .position(mapRoute.startLocation)));
                    start = mapRoute.startLocation;
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title(mapRoute.endAddress)
                    .position(mapRoute.endLocation)));
                    end = mapRoute.endLocation;


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

            for (Marker marker : waypointMarkers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();

            RouteBoxerTask routeBoxerTask = new RouteBoxerTask(mapRoute.points, this.distance,this);

            routeBoxerTask.execute();

            int padding = 20; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);


            mMap.moveCamera(cu);

        }
    }


    private void loadNearByPlaces(double latitude, double longitude) {
        ArrayList<String> listPlaces = MyAdapter.getSelectedString();

        for (int i = 0; i < listPlaces.size(); i++) {
            String type = listPlaces.get(i);
            StringBuilder googlePlacesUrl =
                    new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
            googlePlacesUrl.append("&radius=").append(distance);
            googlePlacesUrl.append("&type=").append(type);
            googlePlacesUrl.append("&key=" + getString(R.string.google_maps_key));

            Log.d("loadnearby", "loadNearByPlaces: " + googlePlacesUrl);

            RequestQueue queue = Volley.newRequestQueue(this);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, googlePlacesUrl.toString(), null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                JSONArray results = response.getJSONArray("results");

                                for (int i = 0; i < results.length(); i++) {


                                    JSONObject result = results.getJSONObject(i);
                                    ;
                                    JSONObject geometry = result.getJSONObject("geometry");
                                    String name = result.getString("name");
                                    JSONObject location = geometry.getJSONObject("location");
                                    double latitude = location.getDouble("lat");
                                    double longitude = location.getDouble("lng");

                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                        @Override
                                        public void onInfoWindowClick(Marker marker) {
//                                            Toast.makeText(MapsActivity.this, "We're here", Toast.LENGTH_SHORT).show();

                                            if (waypointMarkers.contains(marker)) {
                                                waypointMarkers.remove(marker);
                                                Latlng.remove(marker.getPosition());
                                                Toast.makeText(MapsActivity.this, "Removing waypoint from image1_6", Toast.LENGTH_SHORT).show();
                                            } else {
                                                waypointMarkers.add(marker);
                                                Latlng.add(marker.getPosition());
                                                Toast.makeText(MapsActivity.this, "Adding waypoint to image1_6", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

//                                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                                        @Override
//                                        public boolean onMarkerClick(Marker marker) {
//                                            waypointMarkers.add(marker);
//                                            return true;
//                                        }
//                                    });
//                                    Log.i("parsing", "onResponse: " + image1_2);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.i("Response", "onResponse: " + response) ;
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error

                        }
                    });
            mMap.getUiSettings().setMapToolbarEnabled(false);
            queue.add(jsonObjectRequest);

        }

    }
//
    public void waypointParser(ArrayList waypointMarkers) {
        if (waypointMarkers == null || waypointMarkers.size() == 0) {
            waypoints = "";
        } else {
            String[] waypointStrs = new String[waypointMarkers.size()];

            for (int i = 0; i < waypointMarkers.size(); i++) {
                Marker waypoint = (Marker) waypointMarkers.get(i);
                waypointStrs[i] = waypoint.getPosition().latitude + "," + waypoint.getPosition().longitude;
            }
            waypoints = join('|', waypointStrs);
            waypoints.replace("|", "via:");
        }
    }
//
    public void redrawRoute() {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String res = getString(R.string.google_maps_key);
        final String url = "https://maps.googleapis.com/maps/api/directions/json?&origin=place_id:" + origin + "&destination=place_id:" + destination + "&waypoints=" + waypoints + "&key=" + res;

        Log.d("redraw", "redrawRoute: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        MapRoute mapRoute = new MapRoute();

                        try {
                            JSONArray jsonRoutes = response.getJSONArray("routes");
                            JSONObject jsonRoute = jsonRoutes.getJSONObject(0);


                            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
                            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");

                            for (int i = 0; i < jsonLegs.length(); i++) {


                                JSONObject jsonLeg = jsonLegs.getJSONObject(i);
                                JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
                                JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
                                JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
                                JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

                                mapRoute.mapDistance = new MapDistance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
                                mapRoute.mapDuration = new MapDuration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
                                mapRoute.endAddress = jsonLeg.getString("end_address");
                                mapRoute.startAddress = jsonLeg.getString("start_address");
                                mapRoute.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
                                mapRoute.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
                                mapRoute.points = decodePolyLine(overview_polylineJson.getString("points"));
                                path = PolyUtil.decode(overview_polylineJson.getString("points"));
                                mapRoutes.add(mapRoute);
                            }

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

                            polylinePaths = new ArrayList<>();
                            originMarkers = new ArrayList<>();
                            waypointMarkers = new ArrayList<>();
                            destinationMarkers = new ArrayList<>();

                                originMarkers.add(mMap.addMarker(new MarkerOptions()
//                   use for default marker image on map
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//                    use for show custom image on map
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                                        .title("Starting your trip!")
                                        .position(start)));
                                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                        .title(mapRoutes.get(mapRoutes.size()-1).endAddress)
                                        .position(mapRoutes.get(mapRoutes.size()-1).endLocation)));

                            mMap.addPolyline(new PolylineOptions()
                                    .geodesic(true)
                                    .color(Color.rgb(82, 219, 255))
                                    .width(10)
                                    .addAll(path));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                            }
                }, new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            // TODO: Handle error

        }
    });

// Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        btnRoute.setVisibility(View.VISIBLE);

        btnRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "https://www.google.com/maps/dir/?api=1&origin=" + start.latitude + "," + start.longitude + "&destination=" + end.latitude + "," + end.longitude + "&waypoints=" + waypoints + "&travelmode=driving&dir_action=navigate";
//                String new_url = url.replace("www", "");
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });

    }

    private ArrayList<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        ArrayList<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
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
//                    public void onJSONRouteLoaded(ArrayList<LatLng> image1_6) throws IOException {
//                        StringBuilder sb = new StringBuilder();
//                        for(LatLng pos: image1_6)
//                            sb.append(pos.latitude + ";" + pos.longitude + "\n");
//                        FileHelper.write(MapsActivity.this, "path-v.txt", sb.toString(), true);
//                        bpNum--;
//                    }
//                };
//                IMaps hIMaps = new IMaps() {
//                    @Override
//                    public void onJSONRouteLoaded(ArrayList<LatLng> image1_6) throws IOException {
//                        StringBuilder sb = new StringBuilder();
//                        for(LatLng pos: image1_6)
//                            sb.append(pos.latitude + ";" + pos.longitude + "\n");
//                        FileHelper.write(MapsActivity.this, "path-h.txt", sb.toString(), true);
//                        bpNum--;
//                    }
//                };
//                IMaps dIMaps = new IMaps() {
//                    @Override
//                    public void onJSONRouteLoaded(ArrayList<LatLng> image1_6) throws IOException {
//                        StringBuilder sb = new StringBuilder();
//                        for(LatLng pos: image1_6)
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
//import android.image1_2.Location;
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
//import com.google.android.gms.image1_2.LocationListener;
//
//import com.google.android.gms.image1_2.LocationRequest;
//import com.google.android.gms.image1_2.LocationServices;
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
//                                                            public void onPlaceSelected(Place image2_1) {
//                                                                // TODO: Get info about the selected image2_1.
//                                                                etOrigin.setText(image2_1.getName());
//                                                                origin = image2_1.getId();
//                                                                Log.i("Success", "Place: " + image2_1.getAddress() + ", " + image2_1.getLatLng());
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
//            public void onPlaceSelected(Place image2_1) {
//                // TODO: Get info about the selected image2_1.
//                etDestination.setText(image2_1.getAddress());
//                destination = image2_1.getId();
//                Log.i("Success", "Place: " + image2_1.getName() + ", " + image2_1.getId());
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
////                Place image2_1 = Autocomplete.getPlaceFromIntent(data);
////                String address = (String) image2_1.getAddress();
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
////                Place image2_1 = Autocomplete.getPlaceFromIntent(data);
////                String address = (String) image2_1.getAddress();
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
//    public void onLocationChanged(Location image1_2) {
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
