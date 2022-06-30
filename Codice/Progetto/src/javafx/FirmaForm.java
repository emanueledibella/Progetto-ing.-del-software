package javafx;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Node;

import control.DeliveryControl;;

public class FirmaForm {
    private int idOrdine;

    @FXML
    private Canvas firmaCanvas;
    
    @FXML
    private Button indietroButton;

    @FXML
    private Button proseguiButton;

    private DeliveryControl deliveryControl = new DeliveryControl();

    public FirmaForm(int idOrdine){
        this.idOrdine = idOrdine;
    }

    @FXML
    void firma(MouseEvent event){
        GraphicsContext gc = firmaCanvas.getGraphicsContext2D();
        gc.setLineWidth(7.0);
        firmaCanvas.setOnMousePressed(e -> gc.beginPath()); 
        firmaCanvas.setOnMouseDragged(e -> { 
                gc.lineTo(e.getX(), e.getY()); 
                gc.stroke(); 
        });
    }

    @FXML
    void indietroOnMouseClicked(MouseEvent event) {
        if(event.getSource() == indietroButton){
            try {
                Parent root = FXMLLoader.load(getClass().getResource("../javafx/DeliveryList.fxml"));
                Scene scene = new Scene(root);
                Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
                stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
                stage.show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void proseguiOnMouseClicked(MouseEvent event) {
        if (event.getSource() == proseguiButton) {
            deliveryControl.dbAziendaManager.setAsSigned(idOrdine);
            try {
                Parent root = FXMLLoader.load(getClass().getResource("../javafx/DeliveryList.fxml"));
                Scene scene = new Scene(root);
                Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
                stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
                stage.show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
