package com.example.dieaigar.vlctour.pojos;

/**
 * Created by Alberto on 26/04/2016.
 */
public class googlePlace {

    private String nombre;
    private Double latitud;
    private Double longitud;
    private String type;

    public googlePlace() {  }

    public googlePlace(String nombre,Double latitud, Double longitud) {
        this.longitud = longitud;
        this.latitud = latitud;
        this.nombre = nombre;
    }

    public googlePlace(String nombre,Double latitud, Double longitud, String type) {
        this.longitud = longitud;
        this.latitud = latitud;
        this.nombre = nombre;
        this.type = type;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "googlePlace{" +
                "nombre='" + nombre + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                '}';
    }
}
