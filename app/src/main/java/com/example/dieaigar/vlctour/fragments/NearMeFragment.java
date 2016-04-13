package com.example.dieaigar.vlctour.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.dieaigar.vlctour.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NearMeFragment extends Fragment {

    private double radius;
    private Map<Integer, List<Integer>> filters  = new HashMap<Integer, List<Integer>>(0);

    private static final List<String> RESTAURANTS = new ArrayList<String>(Arrays.asList("restaurant"));
    private static final List<String> SHOPPING = new ArrayList<String>(Arrays.asList("department_store","shopping_mall","clothing_store","jewelry_store","shoe_store"));
    private static final List<String> HOTELS = new ArrayList<String>(Arrays.asList("lodging"));
    private static final List<String> PUB = new ArrayList<String>(Arrays.asList("night_club"));
    private static final List<String> ENTERTAINMENT = new ArrayList<String>(Arrays.asList("movie_theater"));
    private static final List<String> POI = new ArrayList<String>(Arrays.asList("park","museum","monument","beach"));

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
        radius = 0.0;
        filters.put(0, new ArrayList<Integer>(0));
        filters.put(1, new ArrayList<Integer>(0));

        //All filters selected initially
        filters.get(1).add(((ImageButton)view.findViewById(R.id.pub)).getId());
        filters.get(1).add(((ImageButton)view.findViewById(R.id.shopping)).getId());
        filters.get(1).add(((ImageButton)view.findViewById(R.id.hotels)).getId());
        filters.get(1).add(((ImageButton)view.findViewById(R.id.restaurant)).getId());
        filters.get(1).add(((ImageButton)view.findViewById(R.id.entertainment)).getId());
        filters.get(1).add(((ImageButton)view.findViewById(R.id.poi)).getId());

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

    public void filterChange(View v) {

    }

    //TODO hacerlo con listeners
}
