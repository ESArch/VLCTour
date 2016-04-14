package com.example.dieaigar.vlctour.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.dieaigar.vlctour.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NearMeFragment extends Fragment implements OnMapReadyCallback {

    private SeekBar seekBar;
    private GoogleMap map;
    private double radius;
    private Map<Integer, List<Integer>> filters  = new HashMap<Integer, List<Integer>>(0);

    private static final List<String> RESTAURANTS = new ArrayList<String>(Arrays.asList("restaurant"));
    private static final List<String> SHOPPING = new ArrayList<String>(Arrays.asList("department_store","shopping_mall","clothing_store","jewelry_store","shoe_store"));
    private static final List<String> HOTELS = new ArrayList<String>(Arrays.asList("lodging"));
    private static final List<String> PUB = new ArrayList<String>(Arrays.asList("night_club"));
    private static final List<String> ENTERTAINMENT = new ArrayList<String>(Arrays.asList("movie_theater"));
    private static final List<String> PARK = new ArrayList<String>(Arrays.asList("park","museum","monument","beach"));
    private static final List<String> MUSEUM = new ArrayList<String>(Arrays.asList("museum"));
    private static final List<String> MONUMENT = new ArrayList<String>(Arrays.asList("monument"));
    private static final List<String> BEACH = new ArrayList<String>(Arrays.asList("beach"));

    public NearMeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_near_me, container, false);
        getActivity().setTitle("Near Me");

        //Set variables
        seekBar = (SeekBar) view.findViewById(R.id.radius);
        radius = 0.0;
        filters.put(0, new ArrayList<Integer>(0));
        filters.put(1, new ArrayList<Integer>(0));

        //All filters selected initially
        filters.get(1).add(((ImageButton)view.findViewById(R.id.pub)).getId());
        filters.get(1).add(((ImageButton)view.findViewById(R.id.shopping)).getId());
        filters.get(1).add(((ImageButton)view.findViewById(R.id.hotels)).getId());
        filters.get(1).add(((ImageButton)view.findViewById(R.id.restaurant)).getId());
        filters.get(1).add(((ImageButton)view.findViewById(R.id.entertainment)).getId());
        filters.get(1).add(((ImageButton)view.findViewById(R.id.museum)).getId());
        filters.get(1).add(((ImageButton)view.findViewById(R.id.park)).getId());
        filters.get(1).add(((ImageButton)view.findViewById(R.id.monument)).getId());
        filters.get(1).add(((ImageButton)view.findViewById(R.id.beach)).getId());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            Toast toast = null;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius = (progress/2.0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(toast != null) { toast.cancel(); }
                toast = Toast.makeText(getActivity(),"Radius: " + radius + "Km.", Toast.LENGTH_SHORT);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //TODO getCurrent location
        //map.setLocationSource();
    }
}
