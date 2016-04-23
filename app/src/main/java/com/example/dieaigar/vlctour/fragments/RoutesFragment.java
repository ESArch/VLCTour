package com.example.dieaigar.vlctour.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
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

    GoogleMap map;
    Polyline route = null;
    final String directions_API_KEY = "AIzaSyCqfQOGG0ToG3EYKnsrmtUKj8OsUjeqzW0";
    ArrayList<POI> ruta = new ArrayList<>();
    HashMap<Marker, POI> hash = new HashMap<>();
    ArrayList<POI> pois;

    public RoutesFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.routes_fragment, container, false);
        getActivity().setTitle("Route Map");

        MapFragment mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, mMapFragment);
        fragmentTransaction.commit();

        mMapFragment.getMapAsync(this);
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(39.482463, -0.346415)).zoom(10).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        map.setBuildingsEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                POI punto = hash.get(marker);
                ruta.add(punto);
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
            uriBuilder.appendQueryParameter("origin", ruta.get(0).getLatitud() + "," + ruta.get(0).getLongitud());
            uriBuilder.appendQueryParameter("destination", ruta.get(ruta.size()-1).getLatitud()+","+ruta.get(ruta.size()-1).getLongitud());
            uriBuilder.appendQueryParameter("waypoints", "");
            String uri = uriBuilder.build().toString();
            for(int i=1; i<ruta.size()-1; i++) {
                uri += ruta.get(i).getLatitud() + "," + ruta.get(i).getLongitud();
                if(i != ruta.size()-2) uri += "|";
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
//                addWaypointMarker(options, pois.get(i));
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
