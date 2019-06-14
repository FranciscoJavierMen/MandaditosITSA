package com.example.administrador.mandaditostec.Cliente.Pedido;

public class Mensajes {

    private String message;
    private long time;
    private String type;
    private String from;

    public Mensajes(String message, long time, String type, String from) {
        this.message = message;
        this.time = time;
        this.type = type;
        this.from = from;
    }

    public Mensajes(){}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
