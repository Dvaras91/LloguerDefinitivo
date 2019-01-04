package com.example.davidvarassolano.lloguermaterialgires;

public class Comanda {
    String id;
    String name,usuari,data;
    Boolean entrega;

    public Comanda(String name) {
        this.name = name;
        this.usuari = "paco";
        this.data = "01/12/2019";
    }

    public Comanda(String id, String name, String usuari, String data) {
        this.id = id;
        this.name = name;
        this.usuari = usuari;
        this.data = data;
    }

    public Comanda(String name, String usuari, String data) {
        this.name = name;
        this.usuari = usuari;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsuari() {
        return usuari;
    }

    public void setUsuari(String usuari) {
        this.usuari = usuari;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Boolean getEntrega() {
        return entrega;
    }

    public void setEntrega(Boolean entrega) {
        this.entrega = entrega;
    }
}
