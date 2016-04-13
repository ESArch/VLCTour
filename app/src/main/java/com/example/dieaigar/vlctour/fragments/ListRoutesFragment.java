package com.example.dieaigar.vlctour.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dieaigar.vlctour.R;

import java.util.ArrayList;

public class ListRoutesFragment extends Fragment {
    public ListRoutesFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.route_list, container, false);
        getActivity().setTitle("Route List");

        FloatingActionButton myFab = (FloatingActionButton)  rootView.findViewById(R.id.add_route);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //add_route();
                Toast.makeText(rootView.getContext(), "Button pressed", Toast.LENGTH_SHORT).show();
            }
        });

        ArrayList<String> list = new ArrayList<String>();
        list.add("Route 1");
        list.add("Route 2");
        list.add("Route 3");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, list);
        ListView lv = (ListView)  rootView.findViewById(R.id.list);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        return rootView;
    }
}
