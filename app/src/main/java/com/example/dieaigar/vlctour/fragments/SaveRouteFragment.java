package com.example.dieaigar.vlctour.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.dieaigar.vlctour.R;

/**
 * Created by pabsanji on 20/04/2016.
 */
public class SaveRouteFragment extends Fragment {
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
                Toast.makeText(getActivity(), "Saved route", Toast.LENGTH_SHORT).show();
            }
        };
        button.setOnClickListener(buttonListener);

        return rootView;
    }

    public void save() {

    }
}
