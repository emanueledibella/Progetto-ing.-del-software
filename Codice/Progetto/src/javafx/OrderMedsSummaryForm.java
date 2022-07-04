package javafx;

import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Node;
import model.Medicine;

public class OrderMedsSummaryForm implements Initializable {

    @FXML
    private Button annullaButton;

    @FXML
    private Button confermaButton;

    @FXML
    private Label dataConsegnaLabel;

    @FXML
    private TableColumn<Medicine, Integer> quantitaTableColumn;

    @FXML
    private TableColumn<Medicine, Integer> idFarmacoTableColumn;

    @FXML
    private TableColumn<Medicine, String> nomeTableColumn;

    @FXML
    private TableColumn<Medicine, String> principioAttivoTableColumn;

    @FXML
    private TableView<Medicine> tableView;

    private UserControl userControl;
    //private LinkedList<Medicine> medicines;
    //private LocalDate dataConsegna;
    //private OrderMedsForm orderMedsForm;
    private OrderControl orderControl;


    public OrderMedsSummaryForm(UserControl userControl, OrderControl orderControl/*, LinkedList<Medicine> medicines, LocalDate dataConsegna, OrderMedsForm orderMedsForm*/) {
        this.userControl = userControl;
        //this.medicines = medicines;
        //this.dataConsegna = dataConsegna;
        //this.orderMedsForm = orderMedsForm;
        this.orderControl = orderControl;
    }

    @FXML
    void cancel(MouseEvent event) {
        if(event.getSource() == annullaButton) {
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
        }
    }

    @FXML
    void confirm(MouseEvent event) {
        if(event.getSource() == confermaButton) {
            //Farmacista farmacista = (Farmacista)userControl.user;
            //orderMedsForm.getOrderControl().getOrder().setRefFarmacia(farmacista.getRefFarmacia());
            //orderMedsForm.getOrderControl().getOrder().setMedicines(medicines);
            //orderMedsForm.getOrderControl().getOrder().setDataConsegna(dataConsegna);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/AlertScadenzaForm.fxml"));
                AlertScadenzaForm alertScadenzaForm = new AlertScadenzaForm(userControl, orderControl);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idFarmacoTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, Integer>("idFarmaco"));
        nomeTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, String>("nome"));
        principioAttivoTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, String>("principioAttivo"));
        quantitaTableColumn.setCellValueFactory(new PropertyValueFactory<Medicine, Integer>("disponibilita"));
        tableView.getItems().addAll(orderControl.getOrder().getMedicines());

        dataConsegnaLabel.setText("Data di consegna: " + orderControl.getOrder().getDataConsegna().toString());
    }
}
