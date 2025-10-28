package com.example.tfc.bbdd.entidades;

import java.io.Serializable;

public class Inventario implements Serializable {

    private long id;
    private int idPersonaje;
    private String producto;
    private String rareza;
    private int cantidad;
    private int precio;
    private String descripcion;
    private String imagenItem;

    public Inventario() {
    }

    public Inventario(long id, int idPersonaje, String producto, String rareza,
                      int cantidad, int precio, String descripcion, String imagen) {
        this.id = id;
        this.idPersonaje = idPersonaje;
        this.producto = producto;
        this.rareza = rareza;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descripcion = descripcion;
        this.imagenItem = imagen;
    }

    public Inventario(int idPersonaje, String producto, String rareza,
                      int cantidad, int precio, String descripcion, String imagen) {
        this.idPersonaje = idPersonaje;
        this.producto = producto;
        this.rareza = rareza;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descripcion = descripcion;
        this.imagenItem = imagen;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIdPersonaje() {
        return idPersonaje;
    }

    public void setIdPersonaje(int idPersonaje) {
        this.idPersonaje = idPersonaje;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getRareza() {
        return rareza;
    }

    public void setRareza(String rareza) {
        this.rareza = rareza;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagenItem() {
        return imagenItem;
    }

    public void setImagenItem(String imagenItem) {
        this.imagenItem = imagenItem;
    }
}
