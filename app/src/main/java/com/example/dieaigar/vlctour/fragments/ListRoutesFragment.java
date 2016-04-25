package com.example.dieaigar.vlctour.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dieaigar.vlctour.MainActivity;
import com.example.dieaigar.vlctour.POI;
import com.example.dieaigar.vlctour.R;
import com.example.dieaigar.vlctour.databases.MySqliteOpenHelper;
import com.example.dieaigar.vlctour.pojos.Route;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class ListRoutesFragment extends Fragment {

    Fragment fragment = null;

    public ListRoutesFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.route_list, container, false);
        getActivity().setTitle("Route List");

        FloatingActionButton myFab = (FloatingActionButton)  rootView.findViewById(R.id.add_route);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fragment = new RoutesFragment();
                if(fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .commit();
                }
            }
        });



        ArrayList<Route> list = new ArrayList<>();
        MySqliteOpenHelper db = MySqliteOpenHelper.getInstance(this.getActivity());


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
