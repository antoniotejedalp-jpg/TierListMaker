package progs.tierlistmaker;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController implements Initializable {

    @FXML private Button btnIngresar;
    @FXML private Button btnRegistrarse;
    
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    
    
    @FXML
    private void handleIngresar(ActionEvent event) {
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Por favor, ingresa tu usuario y contraseña.");
            return;
        }

        try (Connection conn = ConexionDB.conectar()) {
            String sql = "SELECT * FROM usuarios WHERE usuario = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, usuario);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                App.idUsuarioActual = rs.getInt("id");
                String rol = rs.getString("rol");
                System.out.println("¡Login exitoso! Rol detectado: " + rol);
                
                String pantallaDestino = "";
                if (rol.equals("admin")) {
                    pantallaDestino = "panelAdmin.fxml";
                } else {
                    pantallaDestino = "menuTierList.fxml"; 
                }
                
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(App.class.getResource(pantallaDestino));
                javafx.scene.Parent root = loader.load();
                javafx.stage.Stage stage = (javafx.stage.Stage) btnIngresar.getScene().getWindow();
                stage.setScene(new javafx.scene.Scene(root));
                stage.centerOnScreen();
                stage.show();
            } else {
                mostrarAlerta("Error de Login", "Usuario o contraseña incorrectos.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error de Conexión", "No se pudo conectar a la base de datos.");
        }
    }
    
    @FXML
    private void handleRegistrarse(ActionEvent event) {
        String nuevoUsuario = txtUsuario.getText().trim();
        String nuevaPassword = txtPassword.getText().trim();

        if (nuevoUsuario.isEmpty() || nuevaPassword.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Para registrarte, inventa un usuario y contraseña y escríbelos en las cajas.");
            return;
        }

        try (Connection conn = ConexionDB.conectar()) {
            String sqlCheck = "SELECT * FROM usuarios WHERE usuario = ?";
            PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck);
            pstmtCheck.setString(1, nuevoUsuario);
            ResultSet rs = pstmtCheck.executeQuery();
            
            if (rs.next()) {
                mostrarAlerta("Usuario no disponible", "Ese nombre de usuario ya está ocupado. Por favor, elige otro.");
                return;
            }

            String sqlInsert = "INSERT INTO usuarios (usuario, password) VALUES (?, ?)";
            PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert);
            pstmtInsert.setString(1, nuevoUsuario);
            pstmtInsert.setString(2, nuevaPassword);

            int filasAfectadas = pstmtInsert.executeUpdate();

            if (filasAfectadas > 0) {
                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.setTitle("Registro Exitoso");
                alerta.setHeaderText(null);
                alerta.setContentText("¡Bienvenido! El usuario '" + nuevoUsuario + "' ha sido creado. Ahora dale clic a Ingresar para entrar a tu Tier List.");
                alerta.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error en BD", "Hubo un problema al registrar: " + e.getMessage());
        }
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        if(titulo.equals("Campos vacíos")) {
            alerta.setAlertType(Alert.AlertType.WARNING);
        }
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}