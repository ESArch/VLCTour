package com.example.dieaigar.vlctour.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.dieaigar.vlctour.R;

import java.util.Locale;

public class RoutesFragment extends Fragment {
    public static final String ARG_PLANET_NUMBER = "planet_number";

    public RoutesFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.routes_fragment, container, false);
        getActivity().setTitle("Routes");
        return rootView;
    }
}
