package javafx;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Node;

public class ViewOrdersBoundary {

    @FXML
    private TableColumn<?, ?> dataConsegnaTableColumn;

    @FXML
    private TableColumn<?, ?> farmaciTableColumn;

    @FXML
    private TableColumn<?, ?> farmaciaTableColumn;

    @FXML
    private Button indietroButton;

    @FXML
    private TableColumn<?, ?> indirizzoTableColumn;

    @FXML
    private TableColumn<?, ?> nomeTableColumn;

    @FXML
    private TableColumn<?, ?> numeroTelefonoTableColumn;

    @FXML
    private TableColumn<?, ?> ordineTableColumn;

    @FXML
    private TableColumn<?, ?> principioAttivoTableColumn1;

    @FXML
    private TableColumn<?, ?> principioAttivoTableColumn11;

    @FXML
    private TableColumn<?, ?> quantitaTableColumn;

    @FXML
    private TableColumn<?, ?> statoTableColumn;

    @FXML
    private TableView<?> tableView;

    @FXML
    void indietroButtonOnMouseClicked(MouseEvent event) {
        if(event.getSource() == indietroButton) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("../javafx/HomepageAddettoAzienda.fxml"));
                Scene scene = new Scene(root);
                Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
                stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //TODO
}