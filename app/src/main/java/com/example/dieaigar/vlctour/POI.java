package com.example.dieaigar.vlctour;


public class POI {
    private int imagen;
    private String nombre;
    private String tipo;
    private Double latitud;
    private Double longitud;

    public POI(int imagen, String nombre, String tipo, Double latitud, Double longitud) {
        this.imagen = imagen;
        this.nombre = nombre;
        this.tipo = tipo;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return tipo;
    }

    public int getImagen() {
        return imagen;
    }

    public Double getLatitud() {return latitud;}

    public Double getLongitud() {return  longitud;}
}
