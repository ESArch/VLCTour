package com.example.dieaigar.vlctour;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;

public class POI {
    private int imagen;
    private String nombre;
    private String tipo;
    private Double latitud;
    private Double longitud;
    private int id;
    private Context context;


    public POI(String nombre, String tipo, Double longitud, Double latitud) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public POI(String nombre, String tipo, Double longitud, Double latitud, int id) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.id = id;
    }

    public POI(int imagen, int id, String nombre, String tipo, Double longitud, Double latitud) {
        this.imagen = imagen;
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getNombre() {return nombre;}

    public String getTipo() {
        return tipo;
    }

    public int getImagen() {
        return imagen;
    }

    public Double getLatitud() {return latitud;}

    public Double getLongitud() {return  longitud;}

    public int getId() {return id;}

    public String toString(){
        return "POI: " + nombre + " latitud: " + latitud + " longitud: " + longitud;
    }

}
