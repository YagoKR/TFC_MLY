package com.example.tfc.bbdd.entidades;

import java.io.Serializable;

public class Personaje implements Serializable {

    public String nombre, raza, clase, imagenPJ, idUsuario;
    public int idCampana, idPersonaje;

    public Personaje(String nombre, String raza, String clase, String imagenPJ, int idCampana, String idUsuario) {
        this.nombre = nombre;
        this.raza = raza;
        this.clase = clase;
        this.imagenPJ = imagenPJ;
        this.idCampana = idCampana;
        this.idUsuario = idUsuario;

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public String getImagenPJ() {
        return imagenPJ;
    }

    public void setImagenPJ(String imagenPJ) {
        this.imagenPJ = imagenPJ;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdCampana() {
        return idCampana;
    }

    public void setIdCampana(int idCampana) {
        this.idCampana = idCampana;
    }

    public int getIdPersonaje() {
        return idPersonaje;
    }

    public void setIdPersonaje(int idPersonaje) {
        this.idPersonaje = idPersonaje;
    }
}
