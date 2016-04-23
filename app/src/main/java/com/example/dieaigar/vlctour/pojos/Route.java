package com.example.dieaigar.vlctour.pojos;

/**
 * Created by Pablo on 23/04/2016.
 */
public class Route {
    private int id;
    private String nombre;
    private String tipo;
    private double distancia;

    public Route(int id, String nombre, String tipo, double distancia) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.distancia = distancia;
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

    public double getDistancia() {
        return distancia;
    }
}
