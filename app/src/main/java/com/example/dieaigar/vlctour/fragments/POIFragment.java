package com.example.dieaigar.vlctour.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dieaigar.vlctour.POI;
import com.example.dieaigar.vlctour.POIAdapter;
import com.example.dieaigar.vlctour.R;

import java.util.ArrayList;
import java.util.List;

public class POIFragment extends Fragment {
    public static final String ARG_PLANET_NUMBER = "planet_number";

    /*
Declarar instancias globales
 */
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    public POIFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_poi, container, false);
        getActivity().setTitle("Points Of Interest");


        // Inicializar POIs
        List<POI> items = new ArrayList<>();

        items.add(new POI(R.drawable.poi_000, "Antiguo Hospital General", "Monument"));
        items.add(new POI(R.drawable.poi_001, "L'Almoina. Centro Arqueológico", "Monument"));
        items.add(new POI(R.drawable.poi_002, "Atarazanas", "Monument"));
        items.add(new POI(R.drawable.poi_003, "Museo Benlliure", "Museum"));
        items.add(new POI(R.drawable.poi_004, "Ermita de Santa Lucía", "Monument"));

        // Obtener el Recycler
        recycler = (RecyclerView) rootView.findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this.getActivity());
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new POIAdapter(items);
        recycler.setAdapter(adapter);
        return rootView;
    }
}
