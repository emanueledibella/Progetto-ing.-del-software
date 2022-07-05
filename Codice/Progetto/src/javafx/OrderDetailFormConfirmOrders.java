package javafx;

import java.io.IOException;
import control.OrderControl;
import control.UserControl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import model.Medicine;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.net.URL;
import java.sql.Date;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.util.converter.IntegerStringConverter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn.CellEditEvent;

public class OrderDetailFormConfirmOrders implements Initializable {

    @FXML
    private Button confermaButton;

    @FXML
    private Button indietroButton;

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
    private LinkedList<Medicine> medicinesOrderCopy = new LinkedList<>();
    private boolean confirmed = true;

    public OrderDetailFormConfirmOrders(UserControl userControl, OrderControl orderControl) {
        this.userControl = userControl;
        this.orderControl = orderControl;
    }

    @FXML
    void confirm(MouseEvent event) {
        if(event.getSource() == confermaButton) {
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


            LOOP:
            for(int i=0; i<medicinesOrderCopy.size(); i++) {
                for(int j=0; j<medicines.size(); j++) {
                    if(medicinesOrderCopy.get(i).getNome().equals(medicines.get(j).getNome())) {
                        if(medicinesOrderCopy.get(i).getDisponibilita() != medicines.get(j).getDisponibilita()) {
                            confirmed = false;
                            break LOOP;
                        }
                    }
                }
            }

            if(confirmed) {
                orderControl.dbAziendaManager.setAsConfirmed(orderControl.getOrder());
                orderControl.dbFarmaciaManager.updateInventoryFromRefFarmacia(orderControl.getOrder().getMedicines(), orderControl.getOrder().getRefFarmacia());
            } else {
                orderControl.dbAziendaManager.setAsNotConfirmed(orderControl.getOrder());
            }

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
    void indietroButtonOnMouseClicked(MouseEvent event) {
        if(event.getSource() == indietroButton) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/OrdersListConfirmOrders.fxml"));
                OrdersListConfirmOrders ordersListConfirmOrders = new OrdersListConfirmOrders(userControl);
                loader.setController(ordersListConfirmOrders);
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
        LinkedList<Medicine> medicinesOrders = orderControl.getOrder().getMedicines();
        for(int i=0; i<medicinesOrders.size(); i++) {
            for(int j=0; j<medicines.size(); j++) {
                if(medicinesOrders.get(i).getNome().equals(medicines.get(j).getNome())) {
                    medicines.get(j).setDisponibilita(medicines.get(j).getDisponibilita() + medicinesOrders.get(i).getDisponibilita());
                }
            }
        }
        tableView.getItems().addAll(medicines);

        // Consentire al Farmacista di specificare la quantità di farmaci effettivamente consegnati
        tableView.getSelectionModel().cellSelectionEnabledProperty().set(true);
        quantitaTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantitaTableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Medicine,Integer>>() {

            @Override
            public void handle(CellEditEvent<Medicine, Integer> arg0) {
                Medicine medicine = arg0.getRowValue();
                medicine.setDisponibilita(arg0.getNewValue());
            }
            
        });

        // Creare una copia dei farmaci dell'ordine
        for(int i=0; i<medicines.size(); i++) {
            int idFarmaco = medicines.get(i).getIdFarmaco();
            String nome = medicines.get(i).getNome();
            String principioAttivo = medicines.get(i).getPrincipioAttivo();
            Date dataScadenza = medicines.get(i).getDataScadenza();
            int disponibilita = medicines.get(i).getDisponibilita();
            boolean daBanco = medicines.get(i).getDaBanco();

            Medicine medicine = new Medicine(idFarmaco, nome, principioAttivo, dataScadenza, disponibilita, daBanco);
            medicinesOrderCopy.add(medicine);
        }
    }
}