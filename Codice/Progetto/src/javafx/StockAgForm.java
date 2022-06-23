package javafx;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.LinkedList;
import java.util.ResourceBundle;

import control.StockAgControl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.Medicine;

public class StockAgForm implements Initializable {
    
    @FXML
    private TableColumn<Medicine, Date> dataScadenzaTableColumn;

    @FXML
    private TableColumn<Medicine, Integer> disponibilitaTableColumn;

    @FXML
    private TableColumn<Medicine, Integer> idFarmacoTableColumn;

    @FXML
    private Button indietroButton;

    @FXML
    private TableColumn<Medicine, String> nomeTableColumn;

    @FXML
    private TableColumn<Medicine, String> principioAttivoTableColumn;

    @FXML
    private TableView<Medicine> tableView;

    private StockAgControl stockAgControl = new StockAgControl();

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
            }
            catch (IOException e) {
                    e.printStackTrace();
            }
        }
    }

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        idFarmacoTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, Integer>("idFarmaco"));
        nomeTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, String>("nome"));
        principioAttivoTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, String>("principioAttivo"));
        dataScadenzaTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, Date>("dataScadenza"));
        disponibilitaTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, Integer>("disponibilita"));

        LinkedList<Medicine> meds = stockAgControl.dbAziendaManager.getMedicines();
        tableView.getItems().addAll(meds);
    }

}
