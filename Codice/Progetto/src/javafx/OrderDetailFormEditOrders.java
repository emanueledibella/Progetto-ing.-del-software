package javafx;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import control.OrderControl;
import control.UserControl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import model.Medicine;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.DateCell;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn.CellEditEvent;
import java.time.LocalDate;


public class OrderDetailFormEditOrders implements Initializable {

    @FXML
    private DatePicker dataConsegnaDatePicker;

    @FXML
    private Button indietroButton;

    @FXML
    private Button modificaButton;

    @FXML
    private TableColumn<Medicine, String> nomeTableColumn;

    @FXML
    private TableColumn<Medicine, String> principioAttivoTableColumn;

    @FXML
    private TableColumn<Medicine, Integer> quantitaTableColumn;

    @FXML
    private TableView<Medicine> tableView;

    private UserControl userControl;
    private OrderControl orderControl;

    public OrderDetailFormEditOrders(UserControl userControl, OrderControl orderControl) {
        this.userControl = userControl;
        this.orderControl = orderControl;
    }

    @FXML
    void indietroButtonOnMouseClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/OrdersListEditOrders.fxml"));
            OrdersListEditOrders ordersList = new OrdersListEditOrders(userControl);
            loader.setController(ordersList);
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

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        // Inizializzare la tabella dei farmaci
        nomeTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, String>("nome"));
        principioAttivoTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, String>("principioAttivo"));
        quantitaTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, Integer>("disponibilita"));
        LinkedList<Medicine> medicines = orderControl.dbAziendaManager.getMedicinesUnique();
        LinkedList<Medicine> medicinesOrders = orderControl.getOrder().getMedicines();
        for(int i=0; i<medicinesOrders.size(); i++) {
            for(int j=0; j<medicines.size(); j++) {
                if(medicinesOrders.get(i).getNome().equals(medicines.get(j).getNome())) {
                    medicines.get(j).setDisponibilita(medicines.get(j).getDisponibilita() + medicinesOrders.get(i).getDisponibilita());
                }
            }
        }
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
        // Inizializzare il datePicker con la data di consegna dell'ordine
        dataConsegnaDatePicker.setValue(orderControl.getOrder().getDataConsegna());
    }

    @FXML
    void save(MouseEvent event) {
        if(event.getSource() == modificaButton) {
            // Ottenere la lista dei farmaci
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
            // Ottenere la data di consegna selezionata dal Farmacista
            LocalDate dataConsegna = dataConsegnaDatePicker.getValue();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/AlertScadenzaForm.fxml"));
                AlertScadenzaForm alertScadenzaForm = new AlertScadenzaForm(userControl, orderControl, medicines, dataConsegna);
                loader.setController(alertScadenzaForm);
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