package com.example.sightsee.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.akexorcist.googledirection.model.Line;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.Volley;
import com.example.sightsee.ProfileActivity;
import com.example.sightsee.R;
import com.example.sightsee.SaveRouteActivity;
import com.example.sightsee.model.DouglasPeucker;
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
import com.google.android.gms.common.api.ApiException;
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
import java.util.HashMap;
import java.util.List;

//import ap.mobile.routeboxer.helper.FileHelper;
import com.example.sightsee.model.RouteBoxer;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
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

    private DatabaseReference mDatabase;

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
    private float defaultZoom = 13;
    private TestingDialog testDialog;
    private String json;
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
    private PlacesClient placesClient;
    HashMap<Marker, ArrayList<String>> hashMap = new HashMap<>();
    private UiSettings uiSettings;

    private List<Marker> markerKeys;


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


        mDatabase = FirebaseDatabase.getInstance().getReference();


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

        placesClient = Places.createClient(this);

        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (origin != null && destination != null) {
                    btnSightsee.setVisibility(View.VISIBLE);
                    btnFindPath.setVisibility(View.GONE);
                    sendRequest();
                } else
                    Toast.makeText(MapsActivity.this, "Please enter origin and destination address!", Toast.LENGTH_SHORT).show();
            }
        });

        btnSightsee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSightsee.setVisibility(View.GONE);
                waypointParser(waypointMarkers);
                redrawRoute();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu, menu);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(true);
        } else {
            // No user is signed in
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.account:
                //direct to user profile activity
                Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.save:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    if (path.size() != 0) {
                        ArrayList<LatLng> arrayPath = new ArrayList<>();
                        ArrayList<LatLng> originPoint = new ArrayList<>();
                        ArrayList<LatLng> destinationPoint = new ArrayList<>();
                        ArrayList<LatLng> waypointsPoint = new ArrayList<>();

                        arrayPath.addAll(path);

                        originPoint.add(originMarkers.get(0).getPosition());
                        destinationPoint.add(destinationMarkers.get(0).getPosition());

                        for (int i = 0; i < waypointMarkers.size(); i ++) {
                            waypointsPoint.add(waypointMarkers.get(i).getPosition());
                        }
                        Intent saveIntent = new Intent(MapsActivity.this, SaveRouteActivity.class);
                        saveIntent.putParcelableArrayListExtra("origin", originPoint);
                        saveIntent.putParcelableArrayListExtra("destination", destinationPoint);
                        saveIntent.putParcelableArrayListExtra("waypoints", waypointsPoint);
                        startActivity(saveIntent);
                    } else {
                        Toast.makeText(this, "You must create a route before saving a route", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "You must be logged in to save a route", Toast.LENGTH_SHORT).show();
                }
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

        UiSettings uiSettings = this.mMap.getUiSettings();

        uiSettings.setCompassEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
    }

    public static class Route implements Serializable {
        public ArrayList<LatLng> points;

        public Route(ArrayList<LatLng> points) {
            this.points = points;
        }
    }

    @Override
    public void onJSONRouteLoaded(ArrayList<LatLng> route) {
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

            ArrayList<DouglasPeucker.Point> points = new ArrayList<>();
            for (LatLng point : mapRoute.points)
                points.add(new DouglasPeucker.Point(point.latitude, point.longitude));

//            DouglasPeucker douglasPeucker = new DouglasPeucker();
            this.toleranceDistance = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_key_tolerance_distance", "20000"));

            if (this.boxPolygons == null)
                this.boxPolygons = new ArrayList<>();
            else {
                for (Polygon polygon : this.boxPolygons) {
                    polygon.remove();
                }
            }

            LinearLayout linearLayout = findViewById(R.id.infoLayout);
            linearLayout.setVisibility(View.VISIBLE);

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

                                for (int k = 0; k < results.length(); k++) {


                                    final JSONObject result = results.getJSONObject(k);

                                    JSONObject geometry = result.getJSONObject("geometry");
                                    String name = result.getString("name");
                                    JSONObject location = geometry.getJSONObject("location");
                                    double latitude = location.getDouble("lat");
                                    double longitude = location.getDouble("lng");
                                    final String placeString = result.getString("place_id");
                                    JSONArray photos = result.getJSONArray("photos");
                                    JSONObject photoObject = photos.getJSONObject(0);
                                    final String photo = photoObject.getString("photo_reference");

                                    ArrayList<String> list = new ArrayList<>();
                                    list.add(placeString);
                                    list.add(photo);

                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title(name)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

                                    hashMap.put(marker, list);
                                    mMap.setOnInfoWindowClickListener(marker -> {
                                            marker.setTag(result);
                                            showMyDialog(MapsActivity.this, marker);
                                    });

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

           markerKeys = new ArrayList<>(hashMap.keySet());

        }

    }

    private void showMyDialog(final Context context, final Marker marker) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        String photo = hashMap.get(marker).get(1);
        String stringPlace = hashMap.get(marker).get(0);
//
        //Place Photos API call
       final String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photo + "&key=" + getString(R.string.google_maps_key);


       List<Place.Field> placeFields = Arrays.asList(Place.Field.PHOTO_METADATAS, Place.Field.RATING, Place.Field.NAME, Place.Field.WEBSITE_URI, Place.Field.OPENING_HOURS);

        FetchPlaceRequest request = FetchPlaceRequest.builder(stringPlace, placeFields).build();

        ImageView imageView = (ImageView) new ImageView(context);
        final TextView ratingView = new TextView(this);
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {

            Place requestedPlace = response.getPlace();


            ratingView.setText(getString(R.string.rating) + " " + requestedPlace.getRating().toString());


            // Get the photo metadata.
            PhotoMetadata photoMetadata = requestedPlace.getPhotoMetadatas().get(0);

            // Get the attribution text.
            String attributions = photoMetadata.getAttributions();


            // Create a FetchPhotoRequest.
            FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500) // Optional.
                    .setMaxHeight(300) // Optional.
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                imageView.setImageBitmap(bitmap);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    // Handle error with given status code.
                    Log.e("error", "Place not found: " + exception.getMessage());
                }
            });
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.e("error", "Place not found: " + exception.getMessage());
            }
        });

        TextView markerTitle = (TextView) dialog.findViewById(R.id.txtTitle);

        markerTitle.setText(marker.getTitle());

        Button btnBtmRight = (Button) dialog.findViewById(R.id.btnBtmRight);

        btnBtmRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (waypointMarkers.contains(marker)) {
                    waypointMarkers.remove(marker);
                    Latlng.remove(marker.getPosition());
                    btnBtmRight.setText("Add to Route");
                    Toast.makeText(MapsActivity.this, "Removing waypoint from route", Toast.LENGTH_SHORT).show();
                } else {
                    waypointMarkers.add(marker);
                    Latlng.add(marker.getPosition());
                    btnBtmRight.setText("Remove from Route");
                    Toast.makeText(MapsActivity.this, "Adding waypoint to route", Toast.LENGTH_SHORT).show();
                }
            }
        });

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dialogWidth = (int)(displayMetrics.widthPixels * 0.60);
        int dialogHeight = (int)(displayMetrics.heightPixels * 0.40);
        dialog.getWindow().setLayout(dialogWidth, dialogHeight);


        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 125 ,0, 0);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(50,430,0,10);
        dialog.addContentView(imageView,params);
        dialog.addContentView(ratingView, layoutParams);
        dialog.show();
    }

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
//                           waypointMarkers = new ArrayList<>();
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
