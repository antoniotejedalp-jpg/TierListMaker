package progs.tierlistmaker;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * FXML Controller class
 *
 * @author ianca
 */
public class MenuTierListController implements Initializable {

    @FXML private Button btnInsertar;
    @FXML private Button btnCrear;
    @FXML private Button btnEliminarTier;
    @FXML private Button btnModificar;
    @FXML private Button btnSubir;
    @FXML private Button btnBajar;
    @FXML private Button btnCerrarSesion;
    @FXML private Button btnGuardar; 
    @FXML private Button btnCargar;  
    
    @FXML private TextField txtCambioNombre;
    @FXML private FlowPane flowPane;    
    @FXML private VBox contendor;   
    @FXML private ScrollPane spTier;
    @FXML private ColorPicker cpColor;
    
    private final String[] nombresDefault = {"S","A","B","C","D","E","F"};
    private final String[] coloresDefault = {"#FF7F7F","FFBF7F","FFDF7F","FFFF7F","BFFF7F"};
    private int contadorTiers = 0;
    private HBox filaSeleccionada = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        spTier.setFitToWidth(true);    
        flowPane.setHgap(10);
        flowPane.setVgap(10);
        arrastrarImagenesDestino(flowPane);        
        if (contendor != null){
            contendor.setFillWidth(true);
            contendor.setOnMouseClicked(e ->{
                if(e.getTarget()==contendor){
                    deseleccionarTodo();
                }
            });
        }                        
        botonEstado(false);
        for(int i = 0; i < 5; i++){
            crearNuevaTier(null, null);
        }
    }

    private String toHexString(Color color){
        return String.format("#%02X%02X%02X",
                (int)(color.getRed()*255),
                (int)(color.getGreen()*255),
                (int)(color.getBlue()*255));
    }
    
    @FXML
    private void handleInsertarImagen(ActionEvent event) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Buscar Imagen para la Tier List");
        fileChooser.getExtensionFilters().addAll(
            new javafx.stage.FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        javafx.stage.Stage stage = (javafx.stage.Stage) btnInsertar.getScene().getWindow();
        java.io.File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            Image image = new Image(file.toURI().toString());            
            ImageView imageView = new ImageView(image);            
            imageView.setFitWidth(70);
            imageView.setPreserveRatio(true);            
            arrastrarImagenesOrigen(imageView);
            flowPane.getChildren().add(imageView);
        }
    }
 
    @FXML
    private void handleCrearTier(ActionEvent event){
        String nombreNuevatier =txtCambioNombre.getText().trim();
        String colorHex = toHexString(cpColor.getValue());
        crearNuevaTier(nombreNuevatier, colorHex);
        txtCambioNombre.clear();
    }
    
    @FXML
    private void handleEliminarTier(ActionEvent event){
        if(filaSeleccionada != null){
            FlowPane fpTier =(FlowPane) filaSeleccionada.getChildren().get(1);
            List<Node> imagenes =new ArrayList<>(fpTier.getChildren());
            flowPane.getChildren().addAll(imagenes);            
            contendor.getChildren().remove(filaSeleccionada);            
            filaSeleccionada =null;            
        }
    }
    
    @FXML
    private void handleCambiarNombre(ActionEvent event){
        if(filaSeleccionada != null){
            String nuevoTexto = txtCambioNombre.getText().trim();
            Label labelNombre =(Label) filaSeleccionada.getUserData();
            if(!nuevoTexto.isEmpty()&&labelNombre!=null){
                labelNombre.setText(nuevoTexto);
            }        
            StackPane cuadro =(StackPane) filaSeleccionada.getChildren().get(0);
            String nuevoColor =toHexString(cpColor.getValue());
            cuadro.setStyle("-fx-background-color: "+nuevoColor+"; -fx-border-color: black; -fx-border-width: 0.5;");
            deseleccionarTodo();
        }
    }
    
    @FXML
    private void handleSubirTier(ActionEvent event){
        if(filaSeleccionada != null){
            int indiceActual=contendor.getChildren().indexOf(filaSeleccionada);     
            if(indiceActual >0){
                contendor.getChildren().remove(filaSeleccionada);
                contendor.getChildren().add(indiceActual-1,filaSeleccionada);      
            }
        }
    }
    
    @FXML
    private void handleBajarTier(ActionEvent event){
        if(filaSeleccionada != null){
            int indiceActual=contendor.getChildren().indexOf(filaSeleccionada);
            if(indiceActual<contendor.getChildren().size()-1){
                contendor.getChildren().remove(filaSeleccionada);
                contendor.getChildren().add(indiceActual+1,filaSeleccionada);                
            }
        }
    }
    
    @FXML
    private void handleCerrarSesion(ActionEvent event){
        try {
            App.idUsuarioActual = -1; // Limpiar sesión
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(App.class.getResource("login.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) btnCerrarSesion.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    
    private void crearNuevaTier(String nombreTier, String colorPersonalizado){
        String nombre;
        if(nombreTier !=null&&!nombreTier.isEmpty()){
            nombre=nombreTier;
        }else{
            nombre=(contadorTiers < nombresDefault.length)? nombresDefault[contadorTiers]: "Tier "+(contadorTiers + 1);
        }
        String colorFinal;
        if(colorPersonalizado!=null){
            colorFinal=colorPersonalizado;
        }else{
            colorFinal=coloresDefault[contadorTiers % coloresDefault.length];
        }
        contadorTiers++;     
        HBox filaTier = new HBox();
        filaTier.setMinHeight(100);
        filaTier.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-cursor: hand;");
        HBox.setHgrow(filaTier, Priority.ALWAYS);
        
        filaTier.setOnMouseClicked(e ->{
            if(filaSeleccionada==filaTier){
                deseleccionarTodo();
            }else{
                if(filaSeleccionada != null){
                    filaSeleccionada.setStyle("-fx-background-color: black; -fx-border-width: 1; -fx-cursor: hand;"); 
                }
                filaSeleccionada=filaTier;
                filaTier.setStyle("-fx-border-color: #0078D7; -fx-border-width: 3; -fx-cursor: hand;");
                
                Label lb = (Label) filaTier.getUserData();
                StackPane sp=(StackPane) filaTier.getChildren().get(0);
                if (lb!=null){
                    txtCambioNombre.setText(lb.getText());
                }
                if(sp.getBackground()!=null){
                    Color c = (Color) sp.getBackground().getFills().get(0).getFill();
                    cpColor.setValue(c);
                }
                botonEstado(true);
            }
            e.consume();
        });
        StackPane cuadroNombre = new StackPane();
        cuadroNombre.setPrefWidth(80);
        cuadroNombre.setMinWidth(80);
        cuadroNombre.setStyle("-fx-background-color: " + colorFinal + "; -fx-border-color: black; -fx-border-width: 0;");
        Label labelNombre = new Label(nombre);
        labelNombre.setFont(Font.font("System", FontWeight.BOLD, 16));
        cuadroNombre.getChildren().add(labelNombre);    
        filaTier.setUserData(labelNombre); 
        FlowPane nuevoTier = new FlowPane();
        nuevoTier.setOrientation(Orientation.HORIZONTAL);
        nuevoTier.setHgap(10);
        nuevoTier.setVgap(10);
        nuevoTier.setPadding(new javafx.geometry.Insets(10));
        nuevoTier.setStyle("-fx-background-color: #1a1a1a;");        
        HBox.setHgrow(nuevoTier, Priority.ALWAYS);
        nuevoTier.setMaxWidth(Double.MAX_VALUE);       
        arrastrarImagenesDestino(nuevoTier);
        filaTier.getChildren().addAll(cuadroNombre, nuevoTier);
        contendor.getChildren().add(filaTier);
    }
    
    private void deseleccionarTodo(){
        if(filaSeleccionada!=null){
            filaSeleccionada.setStyle("-fx-border-color: black; -fx-border-width: 1;");
            filaSeleccionada = null;
        }
        if(txtCambioNombre!=null){
           txtCambioNombre.clear();
        }
        botonEstado(false);
    }
    
    private void botonEstado(boolean activo){
        if(btnEliminarTier!=null) btnEliminarTier.setDisable(!activo);
        if(btnModificar!=null) btnModificar.setDisable(!activo);
        if(btnSubir!=null) btnSubir.setDisable(!activo);
        if(btnBajar!=null) btnBajar.setDisable(!activo);
        if(btnCrear!=null) btnCrear.setDisable(activo);
    }
    
    private void arrastrarImagenesOrigen(ImageView imagen){
        imagen.setOnDragDetected(e ->{
            Dragboard db = imagen.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putImage(imagen.getImage());
            
            // Guardamos la ruta en el portapapeles para no perderla
            if (imagen.getImage().getUrl() != null) {
                content.putString(imagen.getImage().getUrl());
            }
            
            db.setContent(content);
            imagen.setUserData("dragging");
            e.consume();
        });
        imagen.setOnDragDone(e ->{
            if (e.getTransferMode() == TransferMode.MOVE){
                if(imagen.getParent() instanceof FlowPane){
                    ((FlowPane) imagen.getParent()).getChildren().remove(imagen);
                }
            }
            e.consume();
        });
    }
    
    private void arrastrarImagenesDestino(FlowPane pane){
        pane.setOnDragOver(e ->{
            if(e.getGestureSource() != pane && (e.getDragboard().hasImage() || e.getDragboard().hasString())){
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });
        pane.setOnDragDropped(e ->{
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasImage() || db.hasString()){
                Image imgRecuperada;
                
                // Si la imagen trae su ruta textual, la reconstruimos desde ahí
                if (db.hasString() && db.getString().startsWith("file:")) {
                    imgRecuperada = new Image(db.getString());
                } else {
                    imgRecuperada = db.getImage();
                }
                
                ImageView nuevaImagen = new ImageView(imgRecuperada);
                nuevaImagen.setFitWidth(70);
                nuevaImagen.setPreserveRatio(true);
                arrastrarImagenesOrigen(nuevaImagen);
                pane.getChildren().add(nuevaImagen);
                success = true;
            }
            e.setDropCompleted(success);
            e.consume();
        });
    }

    @FXML
    private void handleGuardarTierList(ActionEvent event) {
        if (App.idUsuarioActual == -1) {
            mostrarAlerta("Error", "No hay un usuario logueado.", Alert.AlertType.ERROR);
            return;
        }

        TextInputDialog dialog = new TextInputDialog("Mi Tier List");
        dialog.setTitle("Guardar Tier List");
        dialog.setHeaderText("Guardando progreso...");
        dialog.setContentText("Por favor, dale un nombre a esta Tier List:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String nombreTL = result.get().trim();
            if(!nombreTL.isEmpty()){
                guardarEnBaseDeDatos(nombreTL);
            }
        }
    }

    private void guardarEnBaseDeDatos(String nombreTL) {
        String sqlTierList = "INSERT INTO tierlists (id_usuario, nombre_tierlist) VALUES (?, ?)";
        String sqlCategoria = "INSERT INTO categorias (id_tierlist, nombre_categoria, color_hex) VALUES (?, ?, ?)";
        String sqlElemento = "INSERT INTO elementos (id_categoria, ruta_imagen) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = ConexionDB.conectar();
            if (conn == null) return;
            conn.setAutoCommit(false);

            PreparedStatement pstTL = conn.prepareStatement(sqlTierList, Statement.RETURN_GENERATED_KEYS);
            pstTL.setInt(1, App.idUsuarioActual);
            pstTL.setString(2, nombreTL);
            pstTL.executeUpdate();
            
            ResultSet rsTL = pstTL.getGeneratedKeys();
            int idTierList = -1;
            if (rsTL.next()) idTierList = rsTL.getInt(1);

            PreparedStatement pstCat = conn.prepareStatement(sqlCategoria, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement pstElem = conn.prepareStatement(sqlElemento);

            for (Node nodoFila : contendor.getChildren()) {
                if (nodoFila instanceof HBox) {
                    HBox fila = (HBox) nodoFila;
                    StackPane cuadroNombre = (StackPane) fila.getChildren().get(0);
                    FlowPane fpImagenes = (FlowPane) fila.getChildren().get(1);

                    Label labelNombre = (Label) fila.getUserData();
                    String nombreFila = labelNombre.getText();
                    Color colorFila = (Color) cuadroNombre.getBackground().getFills().get(0).getFill();
                    String hexColor = toHexString(colorFila);

                    pstCat.setInt(1, idTierList);
                    pstCat.setString(2, nombreFila);
                    pstCat.setString(3, hexColor);
                    pstCat.executeUpdate();

                    ResultSet rsCat = pstCat.getGeneratedKeys();
                    int idCategoria = -1;
                    if (rsCat.next()) idCategoria = rsCat.getInt(1);

                    for (Node nodoImg : fpImagenes.getChildren()) {
                        if (nodoImg instanceof ImageView) {
                            ImageView imgView = (ImageView) nodoImg;
                            String rutaImagen = imgView.getImage().getUrl(); 

                            // ESCUDO: Solo se hace el INSERT si la ruta NO es nula
                            if (rutaImagen != null) {
                                pstElem.setInt(1, idCategoria);
                                pstElem.setString(2, rutaImagen);
                                pstElem.addBatch(); 
                            } else {
                                System.out.println("Se ignoró una imagen porque perdió su ruta original.");
                            }
                        }
                    }
                    pstElem.executeBatch(); 
                }
            }
            conn.commit();
            mostrarAlerta("Éxito", "¡Tu Tier List '" + nombreTL + "' se ha guardado correctamente!", Alert.AlertType.INFORMATION);

        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            mostrarAlerta("Error al guardar", e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @FXML
    private void handleCargarTierList(ActionEvent event) {
        if (App.idUsuarioActual == -1) {
            mostrarAlerta("Error", "No hay un usuario logueado.", Alert.AlertType.ERROR);
            return;
        }

        List<ModeloTierList> misTierLists = obtenerTierListsDelUsuario();
        
        if (misTierLists.isEmpty()) {
            mostrarAlerta("Aviso", "Aún no tienes ninguna Tier List guardada.", Alert.AlertType.INFORMATION);
            return;
        }

        ChoiceDialog<ModeloTierList> dialog = new ChoiceDialog<>(misTierLists.get(0), misTierLists);
        dialog.setTitle("Cargar Tier List");
        dialog.setHeaderText("Tus Tier Lists guardadas");
        dialog.setContentText("Selecciona la Tier List que deseas cargar:");

        Optional<ModeloTierList> result = dialog.showAndWait();
        if (result.isPresent()) {
            cargarDesdeBaseDeDatos(result.get().getId());
        }
    }

    private List<ModeloTierList> obtenerTierListsDelUsuario() {
        List<ModeloTierList> lista = new ArrayList<>();
        String sql = "SELECT id_tierlist, nombre_tierlist FROM tierlists WHERE id_usuario = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, App.idUsuarioActual);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(new ModeloTierList(rs.getInt("id_tierlist"), rs.getString("nombre_tierlist")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private void cargarDesdeBaseDeDatos(int idTierList) {
        contendor.getChildren().clear();
        flowPane.getChildren().clear(); 
        filaSeleccionada = null;
        contadorTiers = 0; 

        String sqlCategorias = "SELECT id_categoria, nombre_categoria, color_hex FROM categorias WHERE id_tierlist = ? ORDER BY id_categoria ASC";
        String sqlElementos = "SELECT ruta_imagen FROM elementos WHERE id_categoria = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstCat = conn.prepareStatement(sqlCategorias);
             PreparedStatement pstElem = conn.prepareStatement(sqlElementos)) {

            pstCat.setInt(1, idTierList);
            ResultSet rsCat = pstCat.executeQuery();

            while (rsCat.next()) {
                int idCategoria = rsCat.getInt("id_categoria");
                String nombreFila = rsCat.getString("nombre_categoria");
                String colorHex = rsCat.getString("color_hex");

                crearNuevaTier(nombreFila, colorHex);
                
                HBox filaRecienCreada = (HBox) contendor.getChildren().get(contendor.getChildren().size() - 1);
                FlowPane fpImagenes = (FlowPane) filaRecienCreada.getChildren().get(1);

                pstElem.setInt(1, idCategoria);
                ResultSet rsElem = pstElem.executeQuery();

                while (rsElem.next()) {
                    String rutaImagen = rsElem.getString("ruta_imagen");
                    try {
                        Image image = new Image(rutaImagen);
                        ImageView imageView = new ImageView(image);
                        imageView.setFitWidth(70);
                        imageView.setPreserveRatio(true);
                        arrastrarImagenesOrigen(imageView); 
                        fpImagenes.getChildren().add(imageView);
                    } catch (IllegalArgumentException e) {
                        System.out.println("No se encontró la imagen en: " + rutaImagen);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la Tier List: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}