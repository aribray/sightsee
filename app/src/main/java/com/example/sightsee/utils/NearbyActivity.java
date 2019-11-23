package com.example.sightsee.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sightsee.R;
import com.example.sightsee.model.MapDistance;
import com.example.sightsee.model.MapDuration;
import com.example.sightsee.model.MapRoute;
import com.example.sightsee.ui.MapsActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.google.maps.internal.StringJoin.join;

public class NearbyActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mDatabase;
    private Context mContext;
    private PlacesClient placesClient;
    private GoogleMap mMap;
    private int toleranceDistance;
    private int distance; // meter
    HashMap<Marker, ArrayList<String>> hashMap = new HashMap<>();
    private ArrayList<Marker> waypointMarkers = new ArrayList<>();
    private Marker marker;
    Location mLocation;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 15000;  /* 15 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private static final int DEFAULT_ZOOM = 15;
    private LatLng coordinates;
    private Button btnRoute;
    private String waypoints;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();


    private ArrayList<LatLng> Latlng = new ArrayList<>();
    private List<LatLng> path = new ArrayList();

    private List<Polyline> polylinePaths = new ArrayList<>();

    private Button btnSeeNearby;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            distance = extras.getInt("max_distance");
        }


        toleranceDistance = distance;

        setContentView(R.layout.activity_nearby);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Create an instance of GoogleAPIClient.
        if (this.mGoogleApiClient == null) {
            this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nearbyMap);
        mapFragment.getMapAsync(this);

        this.mContext = this;

        String res = getString(R.string.google_api_key);
        Places.initialize(getApplicationContext(), res);

        placesClient = Places.createClient(this);

        btnSeeNearby = findViewById(R.id.routeNearby);
        btnSeeNearby.setVisibility(View.GONE);

        btnRoute = findViewById(R.id.btnRoute);
        btnRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waypointParser(waypointMarkers);
                redrawRoute();
                btnRoute.setVisibility(View.GONE);
            }
        });
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


        getDeviceLocation();


        UiSettings uiSettings = this.mMap.getUiSettings();

        uiSettings.setCompassEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                           mLastKnownLocation = (Location) task.getResult();
                           coordinates = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                            loadNearByPlaces(coordinates.latitude, coordinates.longitude);

                        } else {
                            Log.d("error", "Current location is null. Using defaults.");
                            Log.e("exception", "Exception: %s", task.getException());
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });

        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
//                                    Log.i("success", "onResponse: "  + photo);

                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title(name)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

                                    hashMap.put(marker, list);
                                    mMap.setOnInfoWindowClickListener(marker -> {
//                                            Toast.makeText(MapsActivity.this, "We're here", Toast.LENGTH_SHORT).show();
                                        marker.setTag(result);
                                        showMyDialog(NearbyActivity.this, marker);
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

        }

    }

    private void showMyDialog(final Context context, final Marker marker) {

//        RequestQueue queue = Volley.newRequestQueue(this);

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        String photo = hashMap.get(marker).get(1);
        String stringPlace = hashMap.get(marker).get(0);
//        LinearLayout ll = (LinearLayout) this.findViewById(R.id.dialog);
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
//        TextView ratingText = (TextView) dialog.findViewById(R.id.rating);

        markerTitle.setText(marker.getTitle());
//        ratingText.setText(marker);
//        ListView listView = (ListView) dialog.findViewById(R.id.listView);
//
//        listView.addView(imageView);

//
//        Button btnBtmLeft = (Button) dialog.findViewById(R.id.btnBtmLeft);
        Button btnBtmRight = (Button) dialog.findViewById(R.id.btnBtmRight);

//        btnBtmLeft.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });

        btnBtmRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (waypointMarkers.contains(marker)) {
                    waypointMarkers.remove(marker);
                    Latlng.remove(marker.getPosition());
                    btnBtmRight.setText("Add to Route");
                    Toast.makeText(NearbyActivity.this, "Removing waypoint from route", Toast.LENGTH_SHORT).show();
                } else {
                    waypointMarkers.add(marker);
                    Latlng.add(marker.getPosition());
                    btnBtmRight.setText("Remove from Route");
                    Toast.makeText(NearbyActivity.this, "Adding waypoint to route", Toast.LENGTH_SHORT).show();
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

    public void redrawRoute() {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String res = getString(R.string.google_maps_key);
        final String url = "https://maps.googleapis.com/maps/api/directions/json?&origin=" + coordinates.latitude + "," + coordinates.longitude + "&destination=" + waypointMarkers.get(waypointMarkers.size() -1).getPosition().latitude + "," + waypointMarkers.get(waypointMarkers.size() -1).getPosition().longitude + "&waypoints=" + waypoints + "&key=" + res;

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
//                                mapRoutes.add(mapRoute);
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
                            destinationMarkers = new ArrayList<>();

                            double destinationLat = waypointMarkers.get(waypointMarkers.size() -1).getPosition().latitude;
                            double destinationLon = waypointMarkers.get(waypointMarkers.size()-1).getPosition().longitude;

                            LatLng destinationCoords = new LatLng(destinationLat, destinationLon);

                            originMarkers.add(mMap.addMarker(new MarkerOptions()
//                   use for default marker image on map
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//                    use for show custom image on map
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                                    .title("Starting your trip!")
                                    .position(coordinates)));
                            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                    .title(waypointMarkers.get(waypointMarkers.size()-1).getTitle())
                                    .position(destinationCoords)));

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

        btnSeeNearby.setVisibility(View.VISIBLE);

        btnSeeNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "https://www.google.com/maps/dir/?api=1&origin=" + coordinates.latitude + "," + coordinates.longitude + "&destination=" + destinationMarkers.get(0).getPosition().latitude + "," + destinationMarkers.get(0).getPosition().longitude + "&waypoints=" + waypoints + "&travelmode=driving&dir_action=navigate";
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
