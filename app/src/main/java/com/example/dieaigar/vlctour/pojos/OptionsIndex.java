package com.example.dieaigar.vlctour.pojos;

import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Pablo on 26/04/2016.
 */
public class OptionsIndex {
    MarkerOptions options;
    int index;

    public OptionsIndex(MarkerOptions options, int index) {
        this.options = options;
        this.index = index;
    }

    public MarkerOptions getOptions() {
        return options;
    }

    public int getIndex() {
        return index;
    }

    public void setOptions(MarkerOptions options) {
        this.options = options;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
