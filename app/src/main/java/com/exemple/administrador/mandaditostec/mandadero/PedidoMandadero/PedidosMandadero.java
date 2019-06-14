package com.exemple.administrador.mandaditostec.mandadero.PedidoMandadero;

public class PedidosMandadero {

    private String id;
    private String direccionOrigen;
    private String direccionDestino;
    private String pedido;
    private String hora;
    private String mandadero;
    private Boolean realizado;

    public PedidosMandadero(String id, String direccionOrigen, String direccionDestino, String pedido,
                         String hora, String mandadero, Boolean realizado) {
        this.id = id;
        this.direccionOrigen = direccionOrigen;
        this.direccionDestino = direccionDestino;
        this.pedido = pedido;
        this.hora = hora;
        this.mandadero = mandadero;
        this.realizado = realizado;
    }

    PedidosMandadero(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDireccionOrigen() {
        return direccionOrigen;
    }

    public void setDireccionOrigen(String direccionOrigen) {
        this.direccionOrigen = direccionOrigen;
    }

    public String getDireccionDestino() {
        return direccionDestino;
    }

    public void setDireccionDestino(String direccionDestino) {
        this.direccionDestino = direccionDestino;
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
}
