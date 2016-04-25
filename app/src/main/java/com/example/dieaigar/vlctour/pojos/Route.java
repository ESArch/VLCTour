package com.example.dieaigar.vlctour.pojos;

import com.example.dieaigar.vlctour.POI;

import java.util.ArrayList;

/**
 * Created by Pablo on 23/04/2016.
 */
public class Route {
    private int id;
    private String nombre;
    private String tipo;
    private String ruta;

    public Route(int id, String nombre, String tipo,  String ruta) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.ruta = ruta;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public String getRuta() { return ruta; }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
}
