package com.example.dieaigar.vlctour.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dieaigar.vlctour.R;

public class NearMeFragment extends Fragment {

    public NearMeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View nearMe = inflater.inflate(R.layout.fragment_near_me, container, false);
        getActivity().setTitle("Near me");
        return nearMe;
    }
}
