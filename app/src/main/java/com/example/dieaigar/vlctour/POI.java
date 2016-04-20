package com.example.dieaigar.vlctour;


public class POI {
    private int imagen;
    private String nombre;
    private String tipo;
    private Double latitud;
    private Double longitud;

    public POI(String nombre, String tipo, Double longitud, Double latitud) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public POI(int imagen, String nombre, String tipo, Double longitud, Double latitud) {
        this.imagen = imagen;
        this.nombre = nombre;
        this.tipo = tipo;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public int getImagen() {
        return imagen;
    }

    public Double getLatitud() {return latitud;}

    public Double getLongitud() {return  longitud;}
}
