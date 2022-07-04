package javafx;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.ResourceBundle;

import control.AlertScadenzaControl;
import control.OrderControl;
import control.UserControl;
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
import javafx.stage.Window;
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
    private UserControl userControl;
    private OrderControl orderControl;
    private LinkedList<Medicine> medicines;
    private LocalDate dataConsegna;
    private boolean invokedByPrenotaFarmaci;

    public AlertScadenzaForm(UserControl userControl, OrderControl orderControl) {
        alertScadenzaControl = new AlertScadenzaControl(orderControl.getOrder());
        this.userControl = userControl;
        this.orderControl = orderControl;
        this.invokedByPrenotaFarmaci = true;    // Alert scadenza è invocato da Prenota farmaci
    }
    public AlertScadenzaForm(UserControl userControl, OrderControl orderControl, LinkedList<Medicine> medicines, LocalDate dataConsegna) {
        alertScadenzaControl = new AlertScadenzaControl(orderControl.getOrder());
        this.userControl = userControl;
        this.orderControl = orderControl;
        this.medicines = medicines;
        this.dataConsegna = dataConsegna;
        this.invokedByPrenotaFarmaci = false;   // Alert scadenza è invocato da Modifica ordine
    }

    @FXML
    void confirm(MouseEvent event) {
        if(event.getSource() == proseguiButton) {
            if(!invokedByPrenotaFarmaci) {
                orderControl.dbAziendaManager.deleteOrder(orderControl.getOrder());
                orderControl.getOrder().setMedicines(medicines);
                orderControl.getOrder().setDataConsegna(dataConsegna);
            }

            orderControl.order();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/HomepageFarmacista.fxml"));
                HomepageFarmacista homepageFarmacista = new HomepageFarmacista(userControl);
                loader.setController(homepageFarmacista);
                Parent root;
                root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage)Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
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
            if(invokedByPrenotaFarmaci) { // Alert scadenza è stato invocato da Prenota Farmaci
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/OrderMedsForm.fxml"));
                    OrderMedsForm orderMedsForm = new OrderMedsForm(userControl);
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
            } else { // Alert scadenza è stato invocato da Modifica ordine
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/OrdersListEditOrders.fxml"));
                    OrdersListEditOrders ordersListEditOrders = new OrdersListEditOrders(userControl);
                    loader.setController(ordersListEditOrders);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LinkedList<Medicine> nearExpirationDateList = alertScadenzaControl.calcNearExpirationDateList();

        if(!nearExpirationDateList.isEmpty()) {
            nomeTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, String>("nome"));
            principioAttivoTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, String>("principioAttivo"));
            dataScadenzaTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, Date>("dataScadenza"));

            tableView.getItems().addAll(nearExpirationDateList);
        } else {
            if(!invokedByPrenotaFarmaci) {
                orderControl.dbAziendaManager.deleteOrder(orderControl.getOrder());
                orderControl.getOrder().setMedicines(medicines);
                orderControl.getOrder().setDataConsegna(dataConsegna);
            }

            orderControl.order();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/HomepageFarmacista.fxml"));
                HomepageFarmacista homepageFarmacista = new HomepageFarmacista(userControl);
                loader.setController(homepageFarmacista);
                Parent root;
                root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage)Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
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
    void moveDelivery(MouseEvent event) {
        if(event.getSource() == spostaConsegnaButton) {
            if(!invokedByPrenotaFarmaci) { // Alert scadenza è stato invocato da Modifica ordine
                // Da un punto di vista implementativo, piuttosto che modificare l'ordine
                // in realtà si annulla l'ordine preesistente e
                // si crea un nuovo ordine sulla base dei dati dell'ordine già esistente
                orderControl.dbAziendaManager.deleteOrder(orderControl.getOrder());
                //Correggere le quantità dei farmaci dell'ordine con le quantità modificate del Farmacista
                orderControl.getOrder().setMedicines(medicines);
                // Correggere la data di consegna dell'ordine con la data di consegna modificata del Farmacista
                orderControl.getOrder().setDataConsegna(dataConsegna);
            }
            
            LocalDate newDataConsegna = alertScadenzaControl.calcNewDate();
            orderControl.getOrder().setDataConsegna(newDataConsegna);

            orderControl.order();
            
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
}
