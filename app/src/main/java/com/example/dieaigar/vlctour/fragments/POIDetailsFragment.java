package com.example.dieaigar.vlctour.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dieaigar.vlctour.POI;
import com.example.dieaigar.vlctour.POIAdapter;
import com.example.dieaigar.vlctour.R;
import com.example.dieaigar.vlctour.databases.MySqliteOpenHelper;

import org.apache.commons.lang3.text.WordUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class POIDetailsFragment extends Fragment {
    public static final String ARG_PLANET_NUMBER = "planet_number";
    private POI poi;
    /*
Declarar instancias globales
 */
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    public POIDetailsFragment() {
        // Empty constructor required for fragment subclasses
    }

    public static POIDetailsFragment newInstance(POI poi){

        POIDetailsFragment poifragment = new POIDetailsFragment();
        Bundle args = new Bundle();
        poifragment.setPOI(poi);
        return poifragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poi_detail, container, false);
        getActivity().setTitle(poi.getNombre());

        setImage(view);
        setName(view);
        setTipo(view);
        setInfo(view);

        return view;
    }

    public void setPOI(POI p){
        this.poi = p;

    }
    public void setImage(View v){
        ImageView image = (ImageView) v.findViewById(R.id.imagedetailpoi);
        image.setImageResource(poi.getImagen());

    }
    public void setName(View v){
        TextView tv = (TextView) v.findViewById(R.id.nombredetailpoi);
        tv.setText(poi.getNombre());

    }
    public void setTipo(View v){
        TextView tv = (TextView) v.findViewById(R.id.tipodetailpoi);
        tv.setText(poi.getTipo());
    }

    public void setInfo(View v){
        TextView tv = (TextView) v.findViewById(R.id.infodetailpoi);
        tv.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut.");
    }

}
