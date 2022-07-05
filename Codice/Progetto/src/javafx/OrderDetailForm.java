package javafx;

import javafx.event.EventHandler;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import control.OrderControl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import model.Medicine;
import javafx.scene.Node;

public class OrderDetailForm implements Initializable {

    @FXML
    private TableColumn<Medicine, String> farmacoTableColumn;

    @FXML
    private TableColumn<Medicine, Integer> quantitaTableColumn;

    @FXML
    private Button indietroButton;

    @FXML
    private Button salvaButton;

    @FXML
    private ChoiceBox<String> statoChoiceBox;

    @FXML
    private TableView<Medicine> tableView;

    private OrderControl orderControl;

    public OrderDetailForm(OrderControl orderControl) {
        this.orderControl = orderControl;
    }

    @FXML
    void indietroButtonOnMouseClicked(MouseEvent event) {
        if(event.getSource() == indietroButton) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("../javafx/ViewOrdersBoundary.fxml"));
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
        farmacoTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, String>("nome"));
        quantitaTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, Integer>("disponibilita"));
        LinkedList<Medicine> medicines = orderControl.getOrder().getMedicines();
        tableView.getItems().addAll(medicines);

        // Consentire all'AddettoAzienda di modificare la quantità dei farmaci
        tableView.getSelectionModel().cellSelectionEnabledProperty().set(true);
        quantitaTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantitaTableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Medicine,Integer>>() {

            @Override
            public void handle(CellEditEvent<Medicine, Integer> arg0) {
                Medicine medicine = arg0.getRowValue();
                medicine.setDisponibilita(arg0.getNewValue());
            }
            
        });

        // Inizializzare la ChoiceBox per eventualmente modificare lo stato dell'ordine
        String statiOrdine[] = { "Da consegnare", "Consegnato", "Non confermato", "Confermato" };
        statoChoiceBox.getItems().addAll(statiOrdine);
        switch(orderControl.getOrder().getStato()) {
            case "Da consegnare":
                statoChoiceBox.getSelectionModel().select(0);
                break;
            case "Consegnato":
                statoChoiceBox.getSelectionModel().select(1);
                break;
            case "Non confermato":
                statoChoiceBox.getSelectionModel().select(2);
                break;
            case "Confermato":
                statoChoiceBox.getSelectionModel().select(3);
                break;
        }
    }

    @FXML
    void save(MouseEvent event) {
        if(event.getSource() == salvaButton) {
            String stato = statoChoiceBox.getSelectionModel().getSelectedItem().toString();
            orderControl.getOrder().setStato(stato);

            for(int i=0; i<tableView.getItems().size(); i++) {
                int quantita = quantitaTableColumn.getCellData(i);
                orderControl.getOrder().getMedicines().get(i).setDisponibilita(quantita);
            }

            orderControl.dbAziendaManager.updateOrder(orderControl.getOrder());

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

}
