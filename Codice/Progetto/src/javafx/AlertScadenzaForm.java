package javafx;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.ResourceBundle;

import control.AlertScadenzaControl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.Medicine;

public class AlertScadenzaForm implements Initializable {

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

    @FXML
    private Button proseguiButton;

    @FXML
    private Button spostaConsegnaButton;

    private AlertScadenzaControl alertScadenzaControl;
    private OrderMedsForm orderMedsForm;


    public AlertScadenzaForm(OrderMedsForm orderMedsForm) {
        alertScadenzaControl = new AlertScadenzaControl(orderMedsForm.getOrderControl().getOrder());
        this.orderMedsForm = orderMedsForm;
    }

    @FXML
    void confirm(MouseEvent event) {
        if(event.getSource() == proseguiButton) {
            orderMedsForm.order();
        }
    }

    @FXML
    void indietroButtonOnMouseClicked(MouseEvent event) {
        if(event.getSource() == indietroButton) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/OrderMedsForm.fxml"));
                loader.setController(orderMedsForm);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LinkedList<Medicine> nearExpirationDateList = alertScadenzaControl.calcNearExpirationDateList();

        if(!nearExpirationDateList.isEmpty()) {
            nomeTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, String>("nome"));
            principioAttivoTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, String>("principioAttivo"));
            dataScadenzaTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, Date>("dataScadenza"));

            tableView.getItems().addAll(nearExpirationDateList);
        } else {
            orderMedsForm.order();
        }
    }

    @FXML
    void moveDelivery(MouseEvent event) {
        if(event.getSource() == spostaConsegnaButton) {
            LocalDate newDataConsegna = alertScadenzaControl.calcNewDate();
            orderMedsForm.getOrderControl().getOrder().setDataConsegna(newDataConsegna);
            orderMedsForm.order();
        }
    }
}
