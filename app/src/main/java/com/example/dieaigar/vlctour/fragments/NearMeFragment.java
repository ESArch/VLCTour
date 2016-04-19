package com.example.dieaigar.vlctour.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.dieaigar.vlctour.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NearMeFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private SeekBar seekBar;
    private GoogleMap map;
    private double radius;
    private Map<Integer, List<Integer>> filters = new HashMap<Integer, List<Integer>>(0);
    private MapFragment mapFragment;
    private Location userLocation;
    private LocationRequest locationRequest;
    private View view;
    private GoogleApiClient googleApiClient;

    private static final List<String> RESTAURANTS = new ArrayList<String>(Arrays.asList("restaurant"));
    private static final List<String> SHOPPING = new ArrayList<String>(Arrays.asList("department_store", "shopping_mall", "clothing_store", "jewelry_store", "shoe_store"));
    private static final List<String> HOTELS = new ArrayList<String>(Arrays.asList("lodging"));
    private static final List<String> PUB = new ArrayList<String>(Arrays.asList("night_club"));
    private static final List<String> ENTERTAINMENT = new ArrayList<String>(Arrays.asList("movie_theater"));
    private static final List<String> PARK = new ArrayList<String>(Arrays.asList("park", "museum", "monument", "beach"));
    private static final List<String> MUSEUM = new ArrayList<String>(Arrays.asList("museum"));
    private static final List<String> MONUMENT = new ArrayList<String>(Arrays.asList("monument"));
    private static final List<String> BEACH = new ArrayList<String>(Arrays.asList("beach"));

    public NearMeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Creating the google api client
        googleApiClient = new GoogleApiClient.
                Builder(getActivity()).
                addOnConnectionFailedListener(this).
                addConnectionCallbacks(this).
                addApi(LocationServices.API).
                build();

        //Creating locationRequest
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        userLocation = location;
        updateUI();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Service unavaliable. Try later.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        userLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getActivity(), "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_near_me, container, false);
        getActivity().setTitle("Near Me");

        //Set variables
        seekBar = (SeekBar) view.findViewById(R.id.radius);
        radius = 0.0;
        filters.put(0, new ArrayList<Integer>(0));
        filters.put(1, new ArrayList<Integer>(0));

        //Calling the map
        mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.nearMeMap);
        mapFragment.getMapAsync(NearMeFragment.this);

        //All filters selected initially
        filters.get(1).add(((ImageButton) view.findViewById(R.id.pub)).getId());
        filters.get(1).add(((ImageButton) view.findViewById(R.id.shopping)).getId());
        filters.get(1).add(((ImageButton) view.findViewById(R.id.hotels)).getId());
        filters.get(1).add(((ImageButton) view.findViewById(R.id.restaurant)).getId());
        filters.get(1).add(((ImageButton) view.findViewById(R.id.entertainment)).getId());
        filters.get(1).add(((ImageButton) view.findViewById(R.id.museum)).getId());
        filters.get(1).add(((ImageButton) view.findViewById(R.id.park)).getId());
        filters.get(1).add(((ImageButton) view.findViewById(R.id.monument)).getId());
        filters.get(1).add(((ImageButton) view.findViewById(R.id.beach)).getId());

        //Radius
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            Toast toast = null;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius = (progress / 2.0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(getActivity(), "Radius: " + radius + "Km.", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        return view;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Map<Integer, List<Integer>> getFilters() {
        return filters;
    }

    public void setFilters(Map<Integer, List<Integer>> filters) {
        this.filters = filters;
    }

    public void updateMap() {

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(39.469684, -0.376326)).zoom(12).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        map.setBuildingsEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);

        currentLocation();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void currentLocation() {
        int hasLocationPermissions = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        if(hasLocationPermissions != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        //updateUI();
    }

    public void updateUI() {

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(userLocation.getLatitude(), userLocation.getLongitude())).zoom(12).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS :
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                    map.getUiSettings().setMyLocationButtonEnabled(true);
                }
                else {
                    Toast.makeText(getActivity(), "Location access denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default :
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
