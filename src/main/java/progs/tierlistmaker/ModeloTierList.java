package progs.tierlistmaker;

public class ModeloTierList {
    private int id;
    private String nombre;

    public ModeloTierList(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    // Este método es clave para que el cuadro de diálogo muestre el texto correctamente
    @Override
    public String toString() {
        return nombre; 
    }
}
