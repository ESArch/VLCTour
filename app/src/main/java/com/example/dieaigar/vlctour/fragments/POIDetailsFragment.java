package com.example.dieaigar.vlctour.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dieaigar.vlctour.POI;
import com.example.dieaigar.vlctour.POIAdapter;
import com.example.dieaigar.vlctour.R;
import com.example.dieaigar.vlctour.databases.MySqliteOpenHelper;

import org.apache.commons.lang3.text.WordUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class POIDetailsFragment extends Fragment {
    private POI poi;
    /*
Declarar instancias globales
 */
    private Context context;


    public POIDetailsFragment() {
        // Empty constructor required for fragment subclasses
    }

    public static POIDetailsFragment newInstance(POI poi, Context context){

        POIDetailsFragment poifragment = new POIDetailsFragment();
        Bundle args = new Bundle();
        poifragment.setPOI(poi);
        poifragment.setContext(context);
        return poifragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poi_detail, container, false);
        getActivity().setTitle(poi.getNombre());

        setImage(view);
        setName(view);
        setTipo(view);
        setInfo(view);

        return view;
    }

    public void setPOI(POI p){
        this.poi = p;

    }
    public void setImage(View v){
        ImageView image = (ImageView) v.findViewById(R.id.imagedetailpoi);
        image.setImageDrawable(ResizeImage(poi.getImagen()));

    }
    public void setName(View v){
        TextView tv = (TextView) v.findViewById(R.id.nombredetailpoi);
        tv.setText(poi.getNombre());

    }
    public void setTipo(View v){
        TextView tv = (TextView) v.findViewById(R.id.tipodetailpoi);
        tv.setText(poi.getTipo());
    }

    public void setInfo(View v){
        TextView tv = (TextView) v.findViewById(R.id.infodetailpoi);
        tv.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat.\n");
    }

    public void setContext(Context c){
        this.context = c;
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
