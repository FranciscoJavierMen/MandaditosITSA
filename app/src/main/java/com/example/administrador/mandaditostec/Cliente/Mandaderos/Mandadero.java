package com.example.administrador.mandaditostec.Cliente.Mandaderos;

public class Mandadero {
    private String id;
    private String nombre;
    private String estado;
    private String imagen;

    public Mandadero(String id, String nombre, String estado, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
        this.imagen = imagen;
    }

    public Mandadero(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
