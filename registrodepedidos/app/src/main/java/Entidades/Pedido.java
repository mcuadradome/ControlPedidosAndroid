package Entidades;

import java.io.Serializable;
import java.util.Date;

public class Pedido implements  Serializable {

    private int id;
    private String cliente;
    private String codigoProd;
    private String descripcion;
    private int cantidad;
    private int total;
    private int porPaquete;
    private int embalaje;
    private int totalProducto, precioProducto;
    private String fecha;

    public Pedido(int id,String cliente, String codigoProd, String descripcion, int cantidad, int total, int totalProducto, int porPaquete, int embalaje, String fecha) {
        this.id=id;
        this.cliente = cliente;
        this.codigoProd = codigoProd;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.total = total;
        this.porPaquete=porPaquete;
        this.embalaje=embalaje;
        this.totalProducto = totalProducto;
        this.fecha = fecha;
    }

    public Pedido(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCliente() {   return cliente; }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getCodigoProd() {
        return codigoProd;
    }

    public void setCodigoProd(String codigoProd) {
        this.codigoProd = codigoProd;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        descripcion = descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalProducto() {
        return totalProducto;
    }

    public void setTotalProducto(int totalProducto) {
        this.totalProducto = totalProducto;
    }

    public String getFecha() {  return fecha;  }

    public void setFecha(String fecha) {  this.fecha = fecha;}

    public int getPrecioProducto() {
        return precioProducto;
    }

    public void setPrecioProducto(int precioProducto) {
        this.precioProducto = precioProducto;
    }

    public int getPorPaquete() {  return porPaquete; }

    public void setPorPaquete(int porPaquete) { this.porPaquete = porPaquete; }

    public int getEmbalaje() { return embalaje; }

    public void setEmbalaje(int embalaje) {  this.embalaje = embalaje;}
}
