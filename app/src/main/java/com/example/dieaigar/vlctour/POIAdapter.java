package com.example.dieaigar.vlctour;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dieaigar.vlctour.fragments.POIDetailsFragment;

import java.util.List;

public class POIAdapter extends RecyclerView.Adapter<POIAdapter.POIViewHolder>{
    private List<POI> items;
    Fragment fragment;
    private Context context;


    public static class POIViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public ImageView imagen;
        public TextView nombre;
        public TextView descripcion;
        Fragment fragment;
        private Context context;

        public POIViewHolder(View v) {
            super(v);
            imagen = (ImageView) v.findViewById(R.id.imagen);
            nombre = (TextView) v.findViewById(R.id.nombre);
            descripcion = (TextView) v.findViewById(R.id.descripcion);

        }

    }

    public POIAdapter(Activity a, List<POI> items) {
        this.items = items;
        this.context = a;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public POIViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.poi_card, viewGroup, false);

        return new POIViewHolder(v);
    }

    @Override
    public void onBindViewHolder(POIViewHolder viewHolder, final int i) {

        viewHolder.imagen.setImageResource(items.get(i).getImagen());
        viewHolder.nombre.setText(items.get(i).getNombre());
        viewHolder.descripcion.setText(items.get(i).getTipo());


        viewHolder.imagen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                fragment = new POIDetailsFragment().newInstance(items.get(i));
                if(fragment != null) {
                    FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .commit();
                }
            }
        });



    }

}
