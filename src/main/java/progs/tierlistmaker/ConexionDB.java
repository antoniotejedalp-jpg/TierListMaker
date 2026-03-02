package progs.tierlistmaker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    
    // Datos de conexión a tu base de datos local
    private static final String URL = "jdbc:mysql://localhost:3306/tierlist_db";
    private static final String USER = "admin_tierlist"; // Casi siempre es root
    private static final String PASSWORD = "lista123"; // Pon la contraseña de tu MySQL aquí
    
    /**
     * Método estático para obtener la conexión a la base de datos.
     * @return Connection o null si hay un error.
     */
    public static Connection conectar() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("¡Conexión a MySQL (tierlist_db) establecida con éxito!");
            return conn;
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
            return null;
        }
    }
}