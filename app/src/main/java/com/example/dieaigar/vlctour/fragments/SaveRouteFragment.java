package com.example.dieaigar.vlctour.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        return rootView;
    }
}
