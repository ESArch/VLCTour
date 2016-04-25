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

    ArrayList<Integer> ruta = new ArrayList<>();
    MySqliteOpenHelper db = MySqliteOpenHelper.getInstance(this.getActivity());
    View ref;

    public SaveRouteFragment() {

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
        ref = rootView;

        return rootView;
    }

    public void save() {
        String path = "";
        EditText name = (EditText) ref.findViewById(R.id.editText);

        CheckBox monument = (CheckBox) ref.findViewById(R.id.checkBox);
        CheckBox museum = (CheckBox) ref.findViewById(R.id.checkBox2);
        CheckBox parks = (CheckBox) ref.findViewById(R.id.checkBox3);
        CheckBox beaches = (CheckBox) ref.findViewById(R.id.checkBox4);

        String tipo = "";
        if(monument.isChecked()) tipo += "monument,";
        if(museum.isChecked()) tipo += "museum,";
        if(parks.isChecked()) tipo += "parks,";
        if(beaches.isChecked()) tipo += "beaches,";

        for(int i=0; i<ruta.size(); i++) {
            path += ruta.get(i)+",";
            if(i == ruta.size()-1) path += ruta.get(i);
        }
        db.addRoute(name.getText().toString(), tipo.substring(0, tipo.length()-1), path, db.getWritableDatabase());
    }
}
