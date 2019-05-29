package com.example.administrador.mandaditostec.Cliente.Pedido;

public class ModeloPedidos {
    private String id;
    private String latitudOrigen;
    private String longitudOrigen;
    private String latitudDestino;
    private String longitudDestino;
    private String pedido;
    private String hora;
    private String idMandadero;
    private String mandadero;
    private Boolean realizado;
    private String estado;

    public ModeloPedidos(String id, String latitudOrigen, String longitudOrigen,
                         String latitudDestino, String longitudDestino, String pedido,
                         String hora, String idMandadero, String mandadero, Boolean realizado, String estado) {
        this.id = id;
        this.latitudOrigen = latitudOrigen;
        this.longitudOrigen = longitudOrigen;
        this.latitudDestino = latitudDestino;
        this.longitudDestino = longitudDestino;
        this.pedido = pedido;
        this.hora = hora;
        this.idMandadero = idMandadero;
        this.mandadero = mandadero;
        this.realizado = realizado;
        this.estado = estado;
    }

    ModeloPedidos(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitudOrigen() {
        return latitudOrigen;
    }

    public void setLatitudOrigen(String latitudOrigen) {
        this.latitudOrigen = latitudOrigen;
    }

    public String getLongitudOrigen() {
        return longitudOrigen;
    }

    public void setLongitudOrigen(String longitudOrigen) {
        this.longitudOrigen = longitudOrigen;
    }

    public String getLatitudDestino() {
        return latitudDestino;
    }

    public void setLatitudDestino(String latitudDestino) {
        this.latitudDestino = latitudDestino;
    }

    public String getLongitudDestino() {
        return longitudDestino;
    }

    public void setLongitudDestino(String longitudDestino) {
        this.longitudDestino = longitudDestino;
    }

    public String getPedido() {
        return pedido;
    }

    public void setPedido(String pedido) {
        this.pedido = pedido;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getIdMandadero() {
        return idMandadero;
    }

    public void setIdMandadero(String idMandadero) {
        this.idMandadero = idMandadero;
    }

    public String getMandadero() {
        return mandadero;
    }

    public void setMandadero(String mandadero) {
        this.mandadero = mandadero;
    }

    public Boolean getRealizado() {
        return realizado;
    }

    public void setRealizado(Boolean realizado) {
        this.realizado = realizado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
