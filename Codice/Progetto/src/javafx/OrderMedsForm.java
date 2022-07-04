package javafx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import model.Farmacista;
import model.Medicine;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn.CellEditEvent;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.scene.Node;
import control.OrderControl;
import control.UserControl;



public class OrderMedsForm implements Initializable {

    @FXML
    private Button confermaButton;

    @FXML
    private DatePicker dataConsegnaDatePicker;

    @FXML
    private TableColumn<Medicine, Integer> quantitaTableColumn;

    @FXML
    private Button indietroButton;

    @FXML
    private TableColumn<Medicine, String> nomeTableColumn;

    @FXML
    private TableColumn<Medicine, String> principioAttivoTableColumn;

    @FXML
    private TableView<Medicine> tableView;

    private UserControl userControl;
    private OrderControl orderControl = new OrderControl();

    public OrderMedsForm(UserControl userControl) {
        this.userControl = userControl;
    }

    @FXML
    void indietroButtonOnMouseClicked(MouseEvent event) {
        if(event.getSource() == indietroButton) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/HomepageFarmacista.fxml"));
                HomepageFarmacista homepageFarmacista = new HomepageFarmacista(userControl);
                loader.setController(homepageFarmacista);
                Parent root;
                root = loader.load();
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

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        // Inizializzare la tabella dei farmaci
        nomeTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, String>("nome"));
        principioAttivoTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, String>("principioAttivo"));
        quantitaTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, Integer>("disponibilita"));
        LinkedList<Medicine> medicines = orderControl.dbAziendaManager.getMedicinesUnique();
        tableView.getItems().addAll(medicines);

        // Consentire al Farmacista di specificare la quantità di farmaci che intende ordinare
        tableView.getSelectionModel().cellSelectionEnabledProperty().set(true);
        quantitaTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantitaTableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Medicine,Integer>>() {

            @Override
            public void handle(CellEditEvent<Medicine, Integer> arg0) {
                Medicine medicine = arg0.getRowValue();
                medicine.setDisponibilita(arg0.getNewValue());
            }
            
        });

        // Impedire al Farmacista di selezionare una data di consegna antecedente alla data odierna
        LocalDate minDate = LocalDate.now();
        dataConsegnaDatePicker.setValue(minDate);
        dataConsegnaDatePicker.setDayCellFactory(d ->
            new DateCell() {
                @Override public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(item.isBefore(minDate));
            }});
    }

    @FXML
    void order(MouseEvent event) {
        if(event.getSource() == confermaButton) {
            // Ottenere lista dei farmaci che il Farmacista vuole prenotare
            LinkedList<Medicine> medicines = new LinkedList<>();
            for(int i=0; i<tableView.getItems().size(); i++) {
                int quantita = quantitaTableColumn.getCellData(i);

                if(quantita != 0) {
                    String nome = nomeTableColumn.getCellData(i); 
                    String principioAttivo = principioAttivoTableColumn.getCellData(i);

                    Medicine medicine = new Medicine(-1, nome, principioAttivo, null, quantita, false);
                    medicines.add(medicine);
                }
            }

            // Ottenere data di consegna selezionata dal Farmacista
            LocalDate dataConsegna = dataConsegnaDatePicker.getValue();

            Farmacista farmacista = (Farmacista)userControl.user;
            orderControl.getOrder().setRefFarmacia(farmacista.getRefFarmacia());
            orderControl.getOrder().setMedicines(medicines);
            orderControl.getOrder().setDataConsegna(dataConsegna);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/OrderMedsSummaryForm.fxml"));
                OrderMedsSummaryForm orderMedsSummaryForm = new OrderMedsSummaryForm(userControl, orderControl/*medicines, dataConsegna, this*/);
                loader.setController(orderMedsSummaryForm);
                Parent root;
                root = loader.load();
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
}
