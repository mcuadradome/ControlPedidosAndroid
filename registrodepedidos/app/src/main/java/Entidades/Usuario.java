package Entidades;

public class Usuario {

    private Integer id;
    private String nombre;
    private String apellido;
    private String usuario;
    private String password;

    public Integer getId() {  return id; }

    public void setId(Integer id) {    this.id = id; }

    public String getNombre() {  return nombre;  }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
