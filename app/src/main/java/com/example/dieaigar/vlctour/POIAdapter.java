package com.example.dieaigar.vlctour;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

        viewHolder.imagen.setImageDrawable(ResizeImage(items.get(i).getImagen()));
        viewHolder.nombre.setText(items.get(i).getNombre());
        viewHolder.descripcion.setText(items.get(i).getTipo());


        viewHolder.imagen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                fragment = new POIDetailsFragment().newInstance(items.get(i), context);
                if(fragment != null) {
                    FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });



    }

    public Drawable ResizeImage(int imageID) {
        // Get device dimensions
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        double deviceWidth = display.getWidth();

        BitmapDrawable bd = (BitmapDrawable) ((Activity) context).getResources().getDrawable(
                imageID);
        double imageHeight = bd.getBitmap().getHeight();
        double imageWidth = bd.getBitmap().getWidth();

        double ratio = deviceWidth / imageWidth;
        int newImageHeight = (int) (imageHeight * ratio);

        Bitmap bMap = BitmapFactory.decodeResource(((Activity) context).getResources(), imageID);
        Drawable drawable = new BitmapDrawable(((Activity) context).getResources(),
                getResizedBitmap(bMap, newImageHeight, (int) deviceWidth));

        return drawable;
    }

    /************************ Resize Bitmap *********************************/
    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();

        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;
    }

}
