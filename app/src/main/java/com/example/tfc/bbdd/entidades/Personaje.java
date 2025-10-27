package com.example.tfc.bbdd.entidades;

import java.io.Serializable;

public class Personaje implements Serializable {

    public String nombre, raza, clase, imagen, idUsuario;
    public int idCampana;

    public Personaje(String nombre, String raza, String clase, String imagen, int idCampana, String idUsuario) {
        this.nombre = nombre;
        this.raza = raza;
        this.clase = clase;
        this.imagen = imagen;
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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
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
}
