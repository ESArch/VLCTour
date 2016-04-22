package com.example.dieaigar.vlctour.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.dieaigar.vlctour.POI;
import com.example.dieaigar.vlctour.POIAdapter;
import com.example.dieaigar.vlctour.R;
import com.example.dieaigar.vlctour.databases.MySqliteOpenHelper;

import org.apache.commons.lang3.text.WordUtils;

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

    Fragment fragment = null;

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
        MySqliteOpenHelper db =  MySqliteOpenHelper.getInstance(this.getActivity());
        ArrayList<POI> pois = db.getPOIs();
        String[] elements = new String[3];

        for(int i=0;i<pois.size();i++){
            int poiimageid = getPOIImage(i);
            items.add(new POI(poiimageid, WordUtils.capitalizeFully(pois.get(i).getNombre(), '\'', ' '), WordUtils.capitalizeFully(pois.get(i).getTipo()), pois.get(i).getLongitud(), pois.get(i).getLatitud()));
        }
        /*
        items.add(new POI(R.drawable.poi_000, "Antiguo Hospital General", "Monument"));
        items.add(new POI(R.drawable.poi_001, "L'Almoina. Centro Arqueológico", "Monument"));
        items.add(new POI(R.drawable.poi_002, "Atarazanas", "Monument"));
        items.add(new POI(R.drawable.poi_003, "Museo Benlliure", "Museum"));
        items.add(new POI(R.drawable.poi_004, "Ermita de Santa Lucía", "Monument"));
        */

        // Obtener el Recycler
        recycler = (RecyclerView) rootView.findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this.getActivity());
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new POIAdapter(getActivity(),items);
        recycler.setAdapter(adapter);


        //Set click en la imagen
        /*RelativeLayout rl = (RelativeLayout) rootView.findViewById(R.id.individual_poi_layout);
        rl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fragment = new POIDetailsFragment();
                if(fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .commit();
                }
            }
        });*/

        return rootView;
    }

    //get poi image id
    private int getPOIImage(int index){
        int poiimageid = 0;

        switch(index){
            case 0: poiimageid = R.drawable.poi_000;
                break;
            case 1: poiimageid = R.drawable.poi_001;
                break;
            case 2: poiimageid = R.drawable.poi_002;
                break;
            case 3: poiimageid = R.drawable.poi_003;
                break;
            case 4: poiimageid = R.drawable.poi_004;
                break;
            default: poiimageid = R.drawable.poi_000;
        }
        return poiimageid;

    }

}
