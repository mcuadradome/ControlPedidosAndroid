package Entidades;

import java.io.Serializable;

public class Cliente implements Serializable {

    private String nombre;
    private String zona;
    private int embalaje;

    public Cliente(String nombre, String zona, int embalaje) {
        this.nombre = nombre;
        this.zona = zona;
        this.embalaje=embalaje;
    }

    public Cliente(){

    }

    public int getEmblaje() {
        return embalaje;
    }

    public void setEmblaje(int embalaje) {
        this.embalaje = embalaje;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
