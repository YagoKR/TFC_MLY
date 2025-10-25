package com.example.tfc.bbdd.entidades;

public class Campana {
    public String nombreCampanha, descripcion, imagenCampanha;
    public Campana() {
    }

    public Campana(String nombreCampanha, String descripcion, String imagenCampanha) {
        this.nombreCampanha = nombreCampanha;
        this.descripcion = descripcion;
        this.imagenCampanha = imagenCampanha;
    }

    public String getNombreCampanha() {
        return nombreCampanha;
    }

    public void setNombreCampanha(String nombreCampanha) {
        this.nombreCampanha = nombreCampanha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagenCampanha() {
        return imagenCampanha;
    }

    public void setImagenCampanha(String imagenCampanha) {
        this.imagenCampanha = imagenCampanha;
    }
}
