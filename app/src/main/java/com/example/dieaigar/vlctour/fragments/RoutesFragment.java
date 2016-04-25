package com.example.dieaigar.vlctour.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dieaigar.vlctour.MainActivity;
import com.example.dieaigar.vlctour.POI;
import com.example.dieaigar.vlctour.R;
import com.example.dieaigar.vlctour.databases.MySqliteOpenHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class RoutesFragment extends Fragment implements OnMapReadyCallback {

    MapFragment mMapFragment;
    GoogleMap map;
    Polyline route = null;
    final String directions_API_KEY = "AIzaSyCqfQOGG0ToG3EYKnsrmtUKj8OsUjeqzW0";
    ArrayList<POI> poisRuta = new ArrayList<>();
    ArrayList<Integer> ruta = new ArrayList<>();
    HashMap<Marker, POI> hash = new HashMap<>();
    ArrayList<POI> pois;
    Fragment fragment = null;


    public RoutesFragment() {

    }

    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.routes_fragment, container, false);
        getActivity().setTitle("Route Map");
        final LayoutInflater i = inflater;

        mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.map_container, mMapFragment);
        fragmentTransaction.commit();
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.save_route);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fragment = new SaveRouteFragment();
                if(fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .commit();
                }
            }
        });


        //mMapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);

        mMapFragment.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(39.463824, -0.358462)).zoom(13).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        map.setBuildingsEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                POI punto = hash.get(marker);
                pois.add(punto);
                marker.hideInfoWindow();
                Toast.makeText(getActivity(), "Marker added to route", Toast.LENGTH_SHORT).show();
                System.out.println("(" + punto.getId() + ") " + marker.getTitle() + ": " + marker.getPosition().latitude + ", " + marker.getPosition().longitude);
            }
        });

        if(ruta.size() == 0) new MarkerAsyncTask().execute(this);
        else new RouteAsyncTask().execute();
    }

    private class RouteAsyncTask extends AsyncTask<Double, Void, List<LatLng>> {

        @Override
        protected List<LatLng> doInBackground(Double... params) {

            List<LatLng> pointsList = null;

            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("https");
            uriBuilder.authority("maps.googleapis.com");
            uriBuilder.appendPath("maps");
            uriBuilder.appendPath("api");
            uriBuilder.appendPath("directions");
            uriBuilder.appendPath("json");
            uriBuilder.appendQueryParameter("origin", poisRuta.get(0).getLatitud() + "," + poisRuta.get(0).getLongitud());
            uriBuilder.appendQueryParameter("destination", poisRuta.get(poisRuta.size()-1).getLatitud()+","+poisRuta.get(poisRuta.size()-1).getLongitud());
            uriBuilder.appendQueryParameter("waypoints", "");
            String uri = uriBuilder.build().toString();
            for(int i=1; i<poisRuta.size()-1; i++) {
                uri += poisRuta.get(i).getLatitud() + "," + poisRuta.get(i).getLongitud();
                if(i != poisRuta.size()-2) uri += "|";
            }

            uri += "&key="+directions_API_KEY;

            System.out.println(uri);

            try {
                URL url = new URL(uri);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                Log.d("DEBUG", "BACKGROUND");

                if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {

                    Log.d("DEBUG", "HTTP_OK");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    JSONObject object = new JSONObject(response.toString());
                    JSONArray routesArray = object.getJSONArray("routes");
                    JSONObject route = routesArray.getJSONObject(0);
                    JSONObject polyline = route.getJSONObject("overview_polyline");
                    pointsList = PolyUtil.decode(polyline.getString("points"));
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return pointsList;
        }

        @Override
        protected void onPostExecute(List<LatLng> result) {
            Log.d("DEBUG", "POST EXECUTE OUT");
            if (result != null) {
                if (route != null) {
                    route.remove();
                }
                Log.d("DEBUG", "POST EXECUTE IN");

                route = map.addPolyline(new PolylineOptions()
                        .addAll(result)
                        .color(Color.parseColor("#008000"))
                        .width(10)
                        .geodesic(true));
            }
        }
    }

    private class MarkerAsyncTask extends AsyncTask<RoutesFragment, MarkerOptions, Void> {

        HashMap<MarkerOptions, POI> hashMap;

        @Override
        protected Void doInBackground(RoutesFragment... params) {
            MySqliteOpenHelper db = MySqliteOpenHelper.getInstance(params[0].getActivity());
            pois = db.getPOIs();
            hashMap = new HashMap<>();
            for(int i=0;i<pois.size();i++) {
                POI poi = pois.get(i);
                Log.d("Routes debug", pois.get(i).toString());
                MarkerOptions options = new MarkerOptions();
                options.position(new LatLng(poi.getLatitud(), poi.getLongitud()));
                options.title(poi.getNombre());
                hashMap.put(options, poi);
                publishProgress(options);
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(MarkerOptions... values) {
            super.onProgressUpdate(values);
            addWaypointMarker(values[0], hashMap.get(values[0]));
        }
    }

    private void addWaypointMarker(MarkerOptions options, POI poi) {
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        Marker marker = map.addMarker(options);
        hash.put(marker, poi);
    }

    private void addOriginMarker(MarkerOptions options, POI poi) {
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        Marker marker = map.addMarker(options);
        hash.put(marker, poi);
    }

    private void addDestinationMarker(MarkerOptions options, POI poi) {
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        Marker marker = map.addMarker(options);
        hash.put(marker, poi);
    }
}
