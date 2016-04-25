package com.example.dieaigar.vlctour.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.dieaigar.vlctour.POI;
import com.example.dieaigar.vlctour.R;
import com.example.dieaigar.vlctour.databases.MySqliteOpenHelper;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

//TODO guardar el resultado de google places api en una lista, trabajar sobre la lista con los filtros. Siempre se importa todo, se trabaja sobre la lista,
//TODO solo se llama a la api de google al cambiar de posicion
public class NearMeFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private ImageButton enternainmentButton, hotelButton, restaurantButton, parkButton, museumButton, monumentButton, beachButton, shoppingButton, pubButton;
    private SeekBar seekBar;
    private GoogleMap map;
    private double radius;
    private Map<Integer, List<String>> filters = new HashMap<Integer, List<String>>(0);
    private MapFragment mapFragment;
    private Location userLocation;
    private LocationRequest locationRequest;
    private View view;
    private GoogleApiClient googleApiClient;
    private MySqliteOpenHelper db;
    private List<Marker> markers = new ArrayList<>(0);
    private Map<String,List<Marker>> mapMarkers = new HashMap<String, List<Marker>>(0);

    List<String> googleAPIFilters;

    private static final List<String> restaurant = new ArrayList<String>(Arrays.asList("restaurant"));
    private static final List<String> shopping = new ArrayList<String>(Arrays.asList("department_store", "shopping_mall", "clothing_store", "jewelry_store", "shoe_store"));
    private static final List<String> hotel = new ArrayList<String>(Arrays.asList("lodging"));
    private static final List<String> pub = new ArrayList<String>(Arrays.asList("night_club"));
    private static final List<String> entertainment = new ArrayList<String>(Arrays.asList("movie_theater"));
    private static final String PARK = "park";
    private static final String MUSEUM = "museum";
    private static final String MONUMENT = "monument";
    private static final String BEACH = "beach";

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
                addApi(Places.GEO_DATA_API).
                addApi(Places.PLACE_DETECTION_API).
                build();

        //Markers
        mapMarkers.put("park", new ArrayList<Marker>(0));
        mapMarkers.put("museum", new ArrayList<Marker>(0));
        mapMarkers.put("monument", new ArrayList<Marker>(0));
        mapMarkers.put("beach", new ArrayList<Marker>(0));

        //Location
        userLocation = new Location("");
        userLocation.setLatitude(39.463824);
        userLocation.setLongitude(-0.358462);

        //Creating googleAPI filters
        googleAPIFilters = new ArrayList<String>(0);

        //Obtain instance to access db
        db = MySqliteOpenHelper.getInstance(this.getActivity());

        //Creating locationRequest
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(100);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onPause() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("radius", seekBar.getProgress());
        editor.apply();
        super.onPause();
    }

    @Override
    public void onResume() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        seekBar.setProgress(preferences.getInt("radius", 1000));
        super.onResume();
    }

    @Override
    public void onStop() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("NewLocation", location.toString());
        userLocation = location;
        updateUI();
        checkRadius();
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
        radius = 0;
        filters.put(0, new ArrayList<String>(0));
        filters.put(1, new ArrayList<String>(0));
        hotelButton = (ImageButton) view.findViewById(R.id.hotel);
        enternainmentButton = (ImageButton) view.findViewById(R.id.entertainment);
        restaurantButton = (ImageButton) view.findViewById(R.id.restaurant);
        parkButton = (ImageButton) view.findViewById(R.id.park);
        parkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("filterChange1Before",filters.get(1).toString());
                Log.d("filterChange0Before",filters.toString());
                if(filters.get(0).contains("park")) {
                    filters.get(1).add("park");
                    Log.d("DEBUG","Añadido a 1");
                    filters.get(0).remove("park");
                    Log.d("DEBUG","Eliminado de 0");
                    changeMarkers("park",1);
                    changeColor("park",1);
                }
                else if(filters.get(1).contains("park")) {
                    filters.get(0).add("park");
                    Log.d("DEBUG","Añadido a 0");
                    filters.get(1).remove("park");
                    Log.d("DEBUG","Eliminado de 1");
                    changeMarkers("park",0);
                    changeColor("park",0);
                }
                Log.d("filterChange1After",filters.get(1).toString());
                Log.d("filterChange0After",filters.get(0).toString());
            }
        });
        museumButton = (ImageButton) view.findViewById(R.id.museum);
        museumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("filterChange1Before",filters.get(1).toString());
                Log.d("filterChange0Before",filters.toString());
                if(filters.get(0).contains("museum")) {
                    filters.get(1).add("museum");
                    Log.d("DEBUG","Añadido a 1");
                    filters.get(0).remove("museum");
                    Log.d("DEBUG","Eliminado de 0");
                    changeMarkers("museum",1);
                    changeColor("museum",1);
                }
                else if(filters.get(1).contains("museum")) {
                    filters.get(0).add("museum");
                    Log.d("DEBUG","Añadido a 0");
                    filters.get(1).remove("museum");
                    Log.d("DEBUG","Eliminado de 1");
                    changeMarkers("museum",0);
                    changeColor("museum",0);
                }
                Log.d("filterChange1After",filters.get(1).toString());
                Log.d("filterChange0After",filters.get(0).toString());
            }
        });
        monumentButton = (ImageButton) view.findViewById(R.id.monument);
        monumentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("filterChange1Before",filters.get(1).toString());
                Log.d("filterChange0Before",filters.toString());
                if(filters.get(0).contains("monument")) {
                    filters.get(1).add("monument");
                    Log.d("DEBUG","Añadido a 1");
                    filters.get(0).remove("monument");
                    Log.d("DEBUG","Eliminado de 0");
                    changeMarkers("monument",1);
                    changeColor("monument",1);
                }
                else if(filters.get(1).contains("monument")) {
                    filters.get(0).add("monument");
                    Log.d("DEBUG","Añadido a 0");
                    filters.get(1).remove("monument");
                    Log.d("DEBUG","Eliminado de 1");
                    changeMarkers("monument",0);
                    changeColor("monument",0);
                }
                Log.d("filterChange1After",filters.get(1).toString());
                Log.d("filterChange0After",filters.get(0).toString());
            }
        });
        beachButton = (ImageButton) view.findViewById(R.id.beach);
        beachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("filterChange1Before",filters.get(1).toString());
                Log.d("filterChange0Before",filters.toString());
                if(filters.get(0).contains("beach")) {
                    filters.get(1).add("beach");
                    Log.d("DEBUG","Añadido a 1");
                    filters.get(0).remove("beach");
                    Log.d("DEBUG","Eliminado de 0");
                    changeMarkers("beach",1);
                    changeColor("beach",1);
                }
                else if(filters.get(1).contains("beach")) {
                    filters.get(0).add("beach");
                    Log.d("DEBUG","Añadido a 0");
                    filters.get(1).remove("beach");
                    Log.d("DEBUG","Eliminado de 1");
                    changeMarkers("beach",0);
                    changeColor("beach",0);
                }
                Log.d("filterChange1After",filters.get(1).toString());
                Log.d("filterChange0After",filters.get(0).toString());
            }
        });
        shoppingButton = (ImageButton) view.findViewById(R.id.shopping);
        pubButton = (ImageButton) view.findViewById(R.id.pub);



        //Calling the map
        mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.nearMeMap);
        mapFragment.getMapAsync(NearMeFragment.this);

        //All filters selected initially
        filters.get(1).add(getResources().getResourceEntryName(R.id.pub));
        filters.get(1).add(getResources().getResourceEntryName(R.id.shopping));
        filters.get(1).add(getResources().getResourceEntryName(R.id.hotel));
        filters.get(1).add(getResources().getResourceEntryName(R.id.restaurant));
        filters.get(1).add(getResources().getResourceEntryName(R.id.entertainment));
        filters.get(1).add(getResources().getResourceEntryName(R.id.museum));
        filters.get(1).add(getResources().getResourceEntryName(R.id.park));
        filters.get(1).add(getResources().getResourceEntryName(R.id.monument));
        filters.get(1).add(getResources().getResourceEntryName(R.id.beach));
        Log.d("filtersContent1",filters.get(1).toString());
        Log.d("filtersContent0",filters.get(0).toString());

        //Radius
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            Toast toast = null;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius = (progress / 2.0) * 1000;
                Log.d("radius change",""+radius);
                checkRadius();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(getActivity(), "Radius: " + radius + "m.", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        return view;
    }

    public void checkRadius() {
        for(Map.Entry<String, List<Marker>> entry : mapMarkers.entrySet()) {
            for(Marker marker : entry.getValue()) {
                Location markerLocation = new Location("");
                markerLocation.setLatitude(marker.getPosition().latitude);
                markerLocation.setLongitude(marker.getPosition().longitude);
                if(userLocation.distanceTo(markerLocation) > radius) {
                    marker.setVisible(false);
                }
                else {
                    marker.setVisible(true);
                }
            }
        }
    }

    public void changeMarkers(String name, int number) {
        for(Marker marker : mapMarkers.get(name)) {
            if(number == 1) {
                marker.setVisible(true);
            }
            else { marker.setVisible(false); }
        }
    }

    public void changeColor(String name, int number) {
        switch(name) {
            case "pub" :
                if(1 == number)  {
                    pubButton.setBackground(getResources().getDrawable(R.drawable.ic_pub_selected)); }
                else { pubButton.setBackground(getResources().getDrawable(R.drawable.ic_pub)); }
                break;
            case "shopping" :
                if(1 == number)  {
                    shoppingButton.setBackground(getResources().getDrawable(R.drawable.ic_shopping_selected)); }
                else { shoppingButton.setBackground(getResources().getDrawable(R.drawable.ic_shopping)); }
                break;
            case "hotel" :
                if(1 == number)  {
                    hotelButton.setBackground(getResources().getDrawable(R.drawable.ic_hotel_selected)); }
                else { hotelButton.setBackground(getResources().getDrawable(R.drawable.ic_hotel)); }
                break;
            case "restaurant" :
                if(1 == number)  {
                    restaurantButton.setBackground(getResources().getDrawable(R.drawable.ic_restaurant_selected)); }
                else { restaurantButton.setBackground(getResources().getDrawable(R.drawable.ic_restaurant)); }
                break;
            case "entertainment" :
                if(1 == number)  {
                    enternainmentButton.setBackground(getResources().getDrawable(R.drawable.ic_entertainment_selected)); }
                else { enternainmentButton.setBackground(getResources().getDrawable(R.drawable.ic_entertainment)); }
                break;
            case "museum" :
                if(1 == number)  {
                    museumButton.setBackground(getResources().getDrawable(R.drawable.ic_museum_selected)); }
                else { museumButton.setBackground(getResources().getDrawable(R.drawable.ic_museum)); }
                break;
            case "park" :
                if(1 == number)  {
                    parkButton.setBackground(getResources().getDrawable(R.drawable.ic_park_selected)); }
                else { parkButton.setBackground(getResources().getDrawable(R.drawable.ic_park)); }
                break;
            case "monument" :
                if(1 == number)  {
                    monumentButton.setBackground(getResources().getDrawable(R.drawable.ic_monument_selected)); }
                else { monumentButton.setBackground(getResources().getDrawable(R.drawable.ic_monument)); }
                break;
            case "beach" :
                if(1 == number)  {
                    beachButton.setBackground(getResources().getDrawable(R.drawable.ic_beach_selected)); }
                else { beachButton.setBackground(getResources().getDrawable(R.drawable.ic_beach)); }
                break;
        }
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Map<Integer, List<String>> getFilters() {
        return filters;
    }

    public void setFilters(Map<Integer, List<String>> filters) {
        this.filters = filters;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(userLocation.getLatitude(), userLocation.getLongitude())).zoom(13).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        map.setBuildingsEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);

        currentLocation();

        new databaseAsyncTask().execute();
        new googleAPIAsyncTask().execute();
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

    private class googleAPIAsyncTask extends AsyncTask<List<String>, Void, Void> {

        String types = "";

        @Override
        protected void onPreExecute() {

            types = buildTypes();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(List<String>... params) {
            Log.d("googleAsyncLocationNull", ""+(userLocation == null));
            Log.d("googleAsyncLocationPre", userLocation.toString());
            String uri = String.format("https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                    "location="+userLocation.getLatitude()+","+userLocation.getLongitude()+
                    "&radius="+radius+"&types=" +  types +
                    "&key=AIzaSyB22Jq8Betpdl92CmKV5OqahRgsI7EnCvI");

            try {
                URL url = new URL(uri);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                if (connection.getResponseCode() < 300) {

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    Log.d("response", response.toString());
                }
            } catch (MalformedURLException e) {
                Log.e("googleAPIAsyncTaskERROR","URL errónea");
                e.printStackTrace();
            } catch (ProtocolException e) {
                Log.e("googleAPIAsyncTaskERROR","Error de protocolo");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("googleAPIAsyncTaskERROR","Excepcion I/O");
                e.printStackTrace();
            }
            return null;
        }

        public void setGoogleAPIFilters() {

            for(String type : filters.get(1)) {
                switch (type) {
                    case "restaurant" :
                        googleAPIFilters.addAll(restaurant);
                        break;
                    case "shopping" :
                        googleAPIFilters.addAll(shopping);
                        break;
                    case "hotel" :
                        googleAPIFilters.addAll(hotel);
                        break;
                    case "pub" :
                        googleAPIFilters.addAll(pub);
                        break;
                    case "entertainment" :
                        googleAPIFilters.addAll(entertainment);
                        break;
                }
            }
            Log.d("googleAPIFilters",googleAPIFilters.toString());
        }

        public String buildTypes() {

            setGoogleAPIFilters();

            StringBuilder sb = new StringBuilder();
            String prefix = "";
            for(String string : googleAPIFilters) {
                sb.append(prefix);
                prefix = "|";
                sb.append(string);
            }
            Log.d("googleAPI call list", sb.toString());
            return sb.length() == 0 ? "" : sb.toString();
        }
    }

    private class databaseAsyncTask extends AsyncTask<List<String> ,POI, Void> {

        @Override
        protected void onPreExecute() {
            mapMarkers.get("museum").clear();
            mapMarkers.get("park").clear();
            mapMarkers.get("monument").clear();
            mapMarkers.get("beach").clear();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(List<String>... params) {
            ArrayList<POI> pois = db.getPOIs();
            for(POI poi : pois) {
                Location poiLocation = new Location("");
                poiLocation.setLatitude(poi.getLatitud());
                poiLocation.setLongitude(poi.getLongitud());
                publishProgress(poi);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(POI... values) {
            addMarker(values[0]);
            super.onProgressUpdate(values);
        }

        public void addMarker(POI poi) {
            MarkerOptions options = new MarkerOptions();
            options.position(new LatLng(poi.getLatitud(), poi.getLongitud()));
            options.title(poi.getNombre());
            switch (poi.getTipo()) {
                case "museum" :
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_museum));
                    mapMarkers.get("museum").add(map.addMarker(options));
                    break;
                case "park" :
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_park));
                    mapMarkers.get("park").add(map.addMarker(options));
                    break;
                case "monument" :
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_monument));
                    mapMarkers.get("monument").add(map.addMarker(options));
                    break;
                case "beach" :
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_beach));
                    mapMarkers.get("beach").add(map.addMarker(options));
                    break;
            }
        }
    }
}