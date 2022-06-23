package javafx;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.scene.Node;
import java.sql.*;
import control.UpdateQtyControl;
import model.Medicine;

public class UpdateQtyForm implements Initializable {

    @FXML
    private Button confermaButton;

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

    private UpdateQtyControl updateQtyControl = new UpdateQtyControl();

    @FXML
    void confirm(MouseEvent event) {
        if(event.getSource() == confermaButton) {
            LinkedList<Medicine> meds = new LinkedList<>();
            
            for(int i=0; i<tableView.getItems().size(); i++) {
                int idFarmaco = idFarmacoTableColumn.getCellData(i);
                String nome = nomeTableColumn.getCellData(i); 
                String principioAttivo = principioAttivoTableColumn.getCellData(i);
                Date dataScadenza = dataScadenzaTableColumn.getCellData(i);
                int disponibilita = disponibilitaTableColumn.getCellData(i);

                Medicine med = new Medicine(idFarmaco, nome, principioAttivo, dataScadenza, disponibilita);
                meds.add(med);
            }

            updateQtyControl.dbAziendaManager.updateInventory(meds);

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

        LinkedList<Medicine> meds = updateQtyControl.dbAziendaManager.getMedicines();
        tableView.getItems().addAll(meds);

        tableView.getSelectionModel().cellSelectionEnabledProperty().set(true);
        disponibilitaTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        disponibilitaTableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Medicine,Integer>>() {

            @Override
            public void handle(CellEditEvent<Medicine, Integer> arg0) {
                Medicine medicine = arg0.getRowValue();
                medicine.setDisponibilita(arg0.getNewValue());
            }
            
        });
    }    
}
