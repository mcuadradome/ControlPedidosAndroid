package Entidades;

public class Producto {

    private String codigo;
    private String Descripcion;
    private int precio;
    private int iva;
    private Integer embalaje;

    public String getCodigo() {  return codigo;  }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getIva() {
        return iva;
    }

    public void setIva(int iva) {
        this.iva = iva;
    }

    public Integer getEmbalaje() {
        return embalaje;
    }

    public void setEmbalaje(Integer embalaje) {
        this.embalaje = embalaje;
    }
}
