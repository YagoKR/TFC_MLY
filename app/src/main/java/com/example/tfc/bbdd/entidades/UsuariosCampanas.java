package com.example.tfc.bbdd.entidades;

public class UsuariosCampanas {
    public String idUsuario;
    public int idCampana;

    public UsuariosCampanas (String idUsuario, int idCampana) {
        this.idUsuario = idUsuario;
        this.idCampana = idCampana;
    }

    public UsuariosCampanas ()  {

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
