package progs.tierlistmaker;

public class Usuario {
    private int id;
    private String usuario;
    private String rol;

    // Constructor
    public Usuario(int id, String usuario, String rol) {
        this.id = id;
        this.usuario = usuario;
        this.rol = rol;
    }

    // Getters (Súper importantes para que la tabla pueda leer los datos)
    public int getId() { return id; }
    public String getUsuario() { return usuario; }
    public String getRol() { return rol; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public void setRol(String rol) { this.rol = rol; }
}