package com.example.dieaigar.vlctour.fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class RoutesFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap map;
    Polyline route = null;
    final String directions_API_KEY = "AIzaSyCqfQOGG0ToG3EYKnsrmtUKj8OsUjeqzW0";

    public RoutesFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.routes_fragment, container, false);
        getActivity().setTitle("Route Map");

        MapFragment mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content_frame, mMapFragment);
        fragmentTransaction.commit();

        mMapFragment.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(39.482463, -0.346415)).zoom(10).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        map.setBuildingsEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);

        MySqliteOpenHelper db =  MySqliteOpenHelper.getInstance(this.getActivity());
        ArrayList<POI> pois = db.getPOIs();
        for(int i=0;i<pois.size();i++) {
            MarkerOptions options = new MarkerOptions();
            options.position(new LatLng(pois.get(i).getLatitud(), pois.get(i).getLongitud()));
            options.title(pois.get(i).getNombre());
            //options.snippet(elements[1]);
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            map.addMarker(options);
        }

        new RouteAsyncTask().execute(); //pinta ruta en el mapa
    }

    /*private void addMarkers () {
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(39.482463, -0.346415));
        options.title("Lab SDM");
        options.snippet("UPV, Valencia, Spain");
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        map.addMarker(options);

        options.position(new LatLng(39.482824, -0.347934));
        options.title("Escuela de Informática UPV");
        options.snippet("UPV, Valencia, Spain");
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        map.addMarker(options);
    }*/

    private class RouteAsyncTask extends AsyncTask<Double, Void, List<LatLng>> {

        @Override
        protected List<LatLng> doInBackground(Double... params) {

            List<LatLng> pointsList = null;

            String uri = String.format("https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=39.482463,-0.346415&destination=43.361161,-8.445140&waypoints=Cádiz&mode=driving&" +
                    "key="+directions_API_KEY);

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
}
