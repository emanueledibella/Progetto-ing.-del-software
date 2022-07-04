package javafx;

import java.io.IOException;
import java.util.LinkedList;
import control.UserControl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.Corriere;
import model.Farmacia;
import model.Medicine;
import model.Order;
import javafx.scene.Node;
import java.net.URL;
import java.util.ResourceBundle;
import control.DeliveryControl;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.scene.control.TableCell;
import javafx.util.Callback;

public class DeliveryList implements Initializable {

    @FXML
    private TableColumn<TableViewWrapper, Void> azioneTableColumn;

    @FXML
    private TableColumn<TableViewWrapper, String> dataConsegnaTableColumn;

    @FXML
    private TableColumn<TableViewWrapper, String> farmaciTableColumn;

    @FXML
    private TableColumn<?, ?> farmaciaTableColumn;

    @FXML
    private Button indietroButton;

    @FXML
    private TableColumn<TableViewWrapper, String> indirizzoTableColumn;

    @FXML
    private TableColumn<TableViewWrapper, String> nomeTableColumn;

    @FXML
    private TableColumn<TableViewWrapper, String> numeroTelefonoTableColumn;

    @FXML
    private TableColumn<?, ?> ordineTableColumn;

    @FXML
    private TableView<TableViewWrapper> tableView;

    private UserControl userControl;
    private DeliveryControl deliveryControl = new DeliveryControl();

    public DeliveryList(UserControl userControl) {
        this.userControl = userControl;
    }

    @FXML
    void indietroButtonOnMouseClicked(MouseEvent event) {
        if(event.getSource() == indietroButton) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/HomepageCorriere.fxml"));
                HomepageCorriere homepageCorriere = new HomepageCorriere(userControl);
                loader.setController(homepageCorriere);
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
        farmaciTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getFarmaciFormatter().getFarmaci()));
        dataConsegnaTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getOrder().getDataConsegna().toString()));
        nomeTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getFarmacia().getNome()));
        indirizzoTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getFarmacia().getIndirizzo()));
        numeroTelefonoTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getFarmacia().getNumeroTelefono()));

        Corriere corriere = (Corriere)userControl.user;
        int idCorriere = corriere.getIdCorriere();
        LinkedList<Order> orders = deliveryControl.dbAziendaManager.getNotDeliveredOrdersByRefCorriere(idCorriere);
        LinkedList<FarmaciFormatter> farmaciFormatters = new LinkedList<>();
        for(int i=0; i<orders.size(); i++) {
            FarmaciFormatter farmaciFormatter = new FarmaciFormatter(orders.get(i).getMedicines());
            farmaciFormatters.add(farmaciFormatter);
        }
        LinkedList<Farmacia> farmacie = new LinkedList<>();
        for(int i=0; i<orders.size(); i++) {
            int refFarmacia = orders.get(i).getRefFarmacia();
            Farmacia farmacia = deliveryControl.dbAziendaManager.getFarmaciaByRefFarmacia(refFarmacia);
            farmacie.add(farmacia);
        }
        LinkedList<TableViewWrapper> tableViewWrappers = new LinkedList<>();
        for(int i=0; i<orders.size(); i++) {
            TableViewWrapper tableViewWrapper = new TableViewWrapper(farmaciFormatters.get(i), orders.get(i), farmacie.get(i));
            tableViewWrappers.add(tableViewWrapper);
        }
        tableView.getItems().addAll(tableViewWrappers);

        addButtonToTable();
    }

    private void addButtonToTable() {
        Callback<TableColumn<TableViewWrapper, Void>, TableCell<TableViewWrapper, Void>> cellFactory = new Callback<TableColumn<TableViewWrapper, Void>, TableCell<TableViewWrapper, Void>>() {
            @Override
            public TableCell<TableViewWrapper, Void> call(final TableColumn<TableViewWrapper, Void> param) {
                final TableCell<TableViewWrapper, Void> cell = new TableCell<TableViewWrapper, Void>() {

                    private final Button btn = new Button("Firma");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            TableViewWrapper data = getTableView().getItems().get(getIndex());
                            deliveryControl.setOrder(data.getOrder());

                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/DeliveryForm.fxml"));
                                DeliveryForm deliveryForm = new DeliveryForm(userControl, deliveryControl);
                                loader.setController(deliveryForm);
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
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        azioneTableColumn.setCellFactory(cellFactory);
    }

    private class FarmaciFormatter {
        private String farmaci = "";

        public FarmaciFormatter(LinkedList<Medicine> medicines) {
            for(int i=0; i<medicines.size(); i++) {
                farmaci += medicines.get(i).getNome() + "(id: " + medicines.get(i).getIdFarmaco() + ")" + " x " + medicines.get(i).getDisponibilita() + "\n";
            }
        }

        public String getFarmaci() {
            return this.farmaci;
        }
    }

    private class TableViewWrapper {
        private FarmaciFormatter farmaciFormatter;
        private Order order;
        private Farmacia farmacia;

        public TableViewWrapper(FarmaciFormatter farmaciFormatter, Order order, Farmacia farmacia) {
            this.setFarmaciFormatter(farmaciFormatter);
            this.setOrder(order);
            this.setFarmacia(farmacia);
        }

        public void setFarmaciFormatter(FarmaciFormatter farmaciFormatter) {
            this.farmaciFormatter = farmaciFormatter;
        }
        public FarmaciFormatter getFarmaciFormatter() {
            return this.farmaciFormatter;
        }

        public void setOrder(Order order) {
            this.order = order;
        }
        public Order getOrder() {
            return this.order;
        }

        public void setFarmacia(Farmacia farmacia) {
            this.farmacia = farmacia;
        }
        public Farmacia getFarmacia() {
            return this.farmacia;
        }
    }
}