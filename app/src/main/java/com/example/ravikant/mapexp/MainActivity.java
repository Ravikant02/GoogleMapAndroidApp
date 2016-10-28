package com.example.ravikant.mapexp;

import android.*;
import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private GoogleMap mGoogleMap;
    private ImageView mMarkerImageView;
    private GpsTrackerNew gps;
    private Geocoder geocoder;
    private LatLng curentpoint, center;
    private double latitude, longitude;
    private List<Address> addresses;
    private LinearLayout markerLayout;
    private LocationRequest mLocationRequest;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 11;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        setContentView(R.layout.activity_tmp);
        markerLayout = (LinearLayout) findViewById(R.id.locationMarker);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        mLocationRequest = LocationRequest.create();
        gps = new GpsTrackerNew(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // return;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // mGoogleApiClient.connect();
        setupMap();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        /*mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(17.447412, 78.376230))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_markericon))
                .draggable(true));
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.getUiSettings().isMyLocationButtonEnabled();
        mGoogleMap.getUiSettings().isZoomControlsEnabled();
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);*/
    }

    private void setUpDragListners() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        gps.canGetLocation();
        LatLng curentpoint = new LatLng(gps.getLatitude(), gps.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(curentpoint).zoom(15f).tilt(30).build();
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition arg0) {
                // TODO Auto-generated method stub
                LatLng center = mGoogleMap.getCameraPosition().target;
                try {
                    getLocation(center.latitude, center.longitude);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void stupMap() {
        try {

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapFragment);
            mapFragment.getMapAsync(this);

            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMap.getUiSettings().setCompassEnabled(true);
            mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
            mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);

            PendingResult<Status> result = LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                            new LocationListener() {

                                @Override
                                public void onLocationChanged(Location location) {

                                }
                            });


            result.setResultCallback(new ResultCallback<Status>() {

                @Override
                public void onResult(Status status) {

                    if (status.isSuccess()) {

                        Log.e("Result", "success");

                    } else if (status.hasResolution()) {
                        try {
                            status.startResolutionForResult(MainActivity.this,
                                    100);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            gps = new GpsTrackerNew(this);

            gps.canGetLocation();

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            curentpoint = new LatLng(latitude, longitude);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(curentpoint).zoom(19f).tilt(70).build();
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
            mGoogleMap.clear();

            mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                @Override
                public void onCameraChange(CameraPosition arg0) {

                    center = mGoogleMap.getCameraPosition().target;


                    mGoogleMap.clear();
                    markerLayout.setVisibility(View.VISIBLE);

                    try {

                        Toast.makeText(getApplicationContext(), String.valueOf(center.longitude), Toast.LENGTH_LONG).show();

                        /*new GetLocationAsync(center.latitude, center.longitude)
                                .execute();*/

                    } catch (Exception e) {
                    }
                }
            });

            markerLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {

                        LatLng latLng1 = new LatLng(center.latitude,
                                center.longitude);

                        Marker m = mGoogleMap.addMarker(new MarkerOptions()
                                .position(latLng1)
                                .title(" Set your Location ")
                                .snippet("")
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.map_markericon)));
                        m.setDraggable(true);

                        markerLayout.setVisibility(View.GONE);
                    } catch (Exception e) {
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.ENGLISH);
        try {
           List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Toast.makeText(getApplicationContext(), addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private void setupMap() {
        try {

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapFragment);
            mapFragment.getMapAsync(this);
            // Enabling MyLocation in Google Map
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMap.getUiSettings().setCompassEnabled(true);
            mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
            mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);

            PendingResult<Status> result = LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                            new LocationListener() {

                                @Override
                                public void onLocationChanged(Location location) {
                                    // TODO print
                                }
                            });

            Log.e("Reached", "here");

            result.setResultCallback(new ResultCallback<Status>() {

                @Override
                public void onResult(Status status) {

                    if (status.isSuccess()) {

                        Log.e("Result", "success");

                    } else if (status.hasResolution()) {
                        // Google provides a way to fix the issue
                        try {
                            status.startResolutionForResult(MainActivity.this,
                                    100);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            gps = new GpsTrackerNew(this);

            gps.canGetLocation();

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            curentpoint = new LatLng(latitude, longitude);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(curentpoint).zoom(19f).tilt(70).build();
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // return;
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
            // mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
            // Clears all the existing markers
            mGoogleMap.clear();

            mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                @Override
                public void onCameraChange(CameraPosition arg0) {
                    // TODO Auto-generated method stub
                    center = mGoogleMap.getCameraPosition().target;

                    // markerText.setText(" Set your Location ");
                    mGoogleMap.clear();
                    markerLayout.setVisibility(View.VISIBLE);

                    try {
                        getAddressFromLatLong(center.latitude, center.longitude);
                        /*new GetLocationAsync(center.latitude, center.longitude)
                                .execute();*/

                    } catch (Exception e) {
                    }
                }
            });

            markerLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    try {

                        LatLng latLng1 = new LatLng(center.latitude,
                                center.longitude);

                        Marker m = mGoogleMap.addMarker(new MarkerOptions()
                                .position(latLng1)
                                .title(" Set your Location ")
                                .snippet("")
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.marker_icon)));
                        m.setDraggable(true);

                        // markerLayout.setVisibility(View.GONE);
                    } catch (Exception e) {
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupMap();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                }
            }
        }
    }

    private void getAddressFromLatLong(double lat, double lng){
        if (lat != 0.0 && lng !=0.0) {
            LocationAddress.getAddressFromLocation(lat, lng,
                    getApplicationContext(), new GeocoderHandler());

            // LocationAddress.getAddress(lat, lng, getApplicationContext(), new GeocoderHandler());
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    String locationAddress = bundle.getString("address");
                    if (locationAddress != null){
                        Log.e("ADDRESS==",locationAddress);
                        // Toast.makeText(getApplicationContext(), locationAddress, Toast.LENGTH_LONG).show();
                    }
                    break;
                default:
                    locationAddress = null;
            }
        }
    }

    private class GetLocationAsync extends AsyncTask<String, Void, String> {
        double x, y;
        StringBuilder str;

        public GetLocationAsync(double latitude, double longitude) {
            x = latitude;
            y = longitude;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                geocoder = new Geocoder(MainActivity.this, Locale.ENGLISH);
                addresses = geocoder.getFromLocation(x, y, 1);
                str = new StringBuilder();
                if (Geocoder.isPresent()) {
                    Address returnAddress = addresses.get(0);

                    String localityString = returnAddress.getLocality();
                    String city = returnAddress.getCountryName();
                    String region_code = returnAddress.getCountryCode();
                    String zipcode = returnAddress.getPostalCode();

                    str.append(localityString + "");
                    str.append(city + "" + region_code + "");
                    str.append(zipcode + "");

                } else {
                }
            } catch (IOException e) {
                Log.e("tag", e.getMessage());
            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            try {
                /*Address.setText(addresses.get(0).getAddressLine(0)
                        + addresses.get(0).getAddressLine(1) + " ");*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }
}
