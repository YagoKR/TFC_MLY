package com.example.tfc.bbdd.entidades;

import java.io.Serializable;

public class Inventario implements Serializable {

    private long id;
    private int idPersonaje;
    private String producto;
    private String categoria;
    private int cantidad;
    private int precio;
    private String valor;
    private String descripcion;
    private String imagenItem;

    public Inventario() {
    }

    public Inventario(long id, int idPersonaje, String producto, String categoria,
                      int cantidad, int precio, String valor, String descripcion, String imagen) {
        this.id = id;
        this.idPersonaje = idPersonaje;
        this.producto = producto;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.precio = precio;
        this.valor = valor;
        this.descripcion = descripcion;
        this.imagenItem = imagen;
    }

    public Inventario(int idPersonaje, String producto, String categoria,
                      int cantidad, int precio, String valor, String descripcion, String imagen) {
        this.idPersonaje = idPersonaje;
        this.producto = producto;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.precio = precio;
        this.valor = valor;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
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

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
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
