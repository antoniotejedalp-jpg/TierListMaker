package progs.tierlistmaker;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;

public class PanelAdminController implements Initializable {

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colUsuario;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private Button btnCerrarSesion;

    private ObservableList<Usuario> listaUsuarios;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        listaUsuarios = FXCollections.observableArrayList();
        cargarDatosTabla();
    }

    private void cargarDatosTabla() {
        listaUsuarios.clear();
        try (Connection conn = ConexionDB.conectar()) {
            String sql = "SELECT id, usuario, rol FROM usuarios";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                listaUsuarios.add(new Usuario(rs.getInt("id"), rs.getString("usuario"), rs.getString("rol")));
            }
            tablaUsuarios.setItems(listaUsuarios);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEliminarUsuario(ActionEvent event) {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selección requerida", "Selecciona un usuario para eliminar.");
            return;
        }
        if (seleccionado.getUsuario().equals("admin")) {
            mostrarAlerta("Acción no permitida", "No puedes eliminar al admin principal.");
            return;
        }

        try (Connection conn = ConexionDB.conectar()) {
            String sql = "{CALL EliminarUsuario(?)}"; 
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, seleccionado.getId());
            pstmt.executeUpdate();
            listaUsuarios.remove(seleccionado);
            mostrarAlerta("Éxito", "Usuario eliminado.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHacerAdmin(ActionEvent event) {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        try (Connection conn = ConexionDB.conectar()) {
            String sql = "UPDATE usuarios SET rol = 'admin' WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, seleccionado.getId());
            pstmt.executeUpdate();
            seleccionado.setRol("admin");
            tablaUsuarios.refresh();
            mostrarAlerta("Éxito", seleccionado.getUsuario() + " ahora es Admin.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleModificarUsuario(ActionEvent event) {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        TextInputDialog dialog = new TextInputDialog(seleccionado.getUsuario());
        dialog.setTitle("Modificar Usuario");
        dialog.setHeaderText("Cambiando nombre a: " + seleccionado.getUsuario());
        dialog.setContentText("Nuevo nombre:");

        dialog.showAndWait().ifPresent(nuevoNombre -> {
            try (Connection conn = ConexionDB.conectar()) {
                String sql = "UPDATE usuarios SET usuario = ? WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, nuevoNombre);
                pstmt.setInt(2, seleccionado.getId());
                pstmt.executeUpdate();

                seleccionado.setUsuario(nuevoNombre);
                tablaUsuarios.refresh();
                mostrarAlerta("Éxito", "Usuario actualizado.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleCerrarSesion(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(App.class.getResource("login.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) btnCerrarSesion.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}