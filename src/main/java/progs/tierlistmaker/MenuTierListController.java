package progs.tierlistmaker;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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
    
    @FXML
    private Button btnInsertar;
    @FXML
    private Button btnCrear;
    @FXML
    private Button btnEliminarTier;
    @FXML
    private Button btnModificar;
    @FXML
    private Button btnSubir;
    @FXML
    private Button btnBajar;
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private TextField txtCambioNombre;
    @FXML
    private FlowPane flowPane;    
    @FXML
    private VBox contendor;   
    @FXML
    private ScrollPane spTier;
    @FXML
    private ColorPicker cpColor;
    
    private final String[] nombresDefault = {"S","A","b","C","D","E","F"};
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
            System.out.println("Ruta de la imagen seleccionada: " + file.getAbsolutePath());
            Image image = new Image(file.toURI().toString());           
            ImageView imageView = new ImageView(image);           
            imageView.setFitWidth(70);
            imageView.setPreserveRatio(true);           
            arrastrarImagenesOrigen(imageView);
            flowPane.getChildren().add(imageView);
        } else {
            System.out.println("No se seleccionó ninguna imagen.");
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
        }else{
            System.out.print("Por favor, seleccione una fila primero");
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
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(App.class.getResource("login.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) btnCerrarSesion.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar la pantalla de Tier List.");
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
        //nuevo
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
        if(btnEliminarTier!=null){
            btnEliminarTier.setDisable(!activo);
        }
        if(btnModificar!=null){
            btnModificar.setDisable(!activo);
        }
        if(btnSubir!=null){
            btnSubir.setDisable(!activo);
        }
        if(btnBajar!=null){
            btnBajar.setDisable(!activo);
        }
        if(btnCrear!=null){
            btnCrear.setDisable(activo);
        }
    }
    
    private void arrastrarImagenesOrigen(ImageView imagen){
        imagen.setOnDragDetected(e ->{
            Dragboard db = imagen.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putImage(imagen.getImage());
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
            if(e.getGestureSource() != pane && e.getDragboard().hasImage()){
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });
        pane.setOnDragDropped(e ->{
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasImage()){
                ImageView nuevaImagen = new ImageView(db.getImage());
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
    
}