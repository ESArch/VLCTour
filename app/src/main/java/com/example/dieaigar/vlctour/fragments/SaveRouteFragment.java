package com.example.dieaigar.vlctour.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dieaigar.vlctour.POI;
import com.example.dieaigar.vlctour.R;
import com.example.dieaigar.vlctour.databases.MySqliteOpenHelper;

import java.util.ArrayList;

/**
 * Created by pabsanji on 20/04/2016.
 */
public class SaveRouteFragment extends Fragment {

    private String pois;
    MySqliteOpenHelper db = MySqliteOpenHelper.getInstance(this.getActivity());
    EditText name;
    CheckBox monument;
    CheckBox museum;
    CheckBox parks;
    CheckBox beaches;

    public SaveRouteFragment() {

    }

    public static SaveRouteFragment newInstance(String pois){
        SaveRouteFragment fragment = new SaveRouteFragment();
        Bundle args = new Bundle();
        args.putString("pois", pois);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            pois = getArguments().getString("pois");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.route_save, container, false);
        getActivity().setTitle("Save Route");

        Button button = (Button) rootView.findViewById(R.id.button_save);
        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                Toast.makeText(getActivity(), "Saved route", Toast.LENGTH_SHORT).show();
            }
        };
        button.setOnClickListener(buttonListener);

        name = (EditText) rootView.findViewById(R.id.editText);
        monument = (CheckBox) rootView.findViewById(R.id.checkBox);
        museum = (CheckBox) rootView.findViewById(R.id.checkBox2);
        parks = (CheckBox) rootView.findViewById(R.id.checkBox3);
        beaches = (CheckBox) rootView.findViewById(R.id.checkBox4);

        return rootView;
    }

    public void save() {
        String tipo = "";

        if(monument.isChecked()) tipo += "monument,";
        if(museum.isChecked()) tipo += "museum,";
        if(parks.isChecked()) tipo += "parks,";
        if(beaches.isChecked()) tipo += "beaches,";

        /*for(int i=0; i<ruta.size(); i++) {
            path += ruta.get(i)+",";
            if(i == ruta.size()-1) path += ruta.get(i);
        }*/

        db.addRoute(name.getText().toString(), tipo.substring(0, tipo.length()-1), pois, db.getWritableDatabase());
    }

    public String getPois() {
        return pois;
    }

    public void setPois(String pois) {
        this.pois = pois;
    }
}
