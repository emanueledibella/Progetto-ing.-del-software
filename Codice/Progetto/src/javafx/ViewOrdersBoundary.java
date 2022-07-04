package javafx;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import control.OrderControl;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Farmacia;
import model.Medicine;
import model.Order;
import javafx.scene.Node;

public class ViewOrdersBoundary implements Initializable {

    @FXML
    private TableColumn<TableViewWrapper, Void> azioneColumnTable;

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
    private TableColumn<TableViewWrapper, String> refCorriereTableColumn;

    @FXML
    private TableColumn<TableViewWrapper, String> statoTableColumn;

    @FXML
    private TableView<TableViewWrapper> tableView;

    private OrderControl orderControl = new OrderControl();

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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        farmaciTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getFarmaciFormatter().getFarmaci()));
        dataConsegnaTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getOrder().getDataConsegna().toString()));
        refCorriereTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(String.valueOf(cellData.getValue().getOrder().getRefCorriere())));
        statoTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getOrder().getStato()));
        nomeTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getFarmacia().getNome()));
        indirizzoTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getFarmacia().getIndirizzo()));
        numeroTelefonoTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getFarmacia().getNumeroTelefono()));

        LinkedList<Order> orders = orderControl.dbAziendaManager.getOrders();
        LinkedList<Farmacia> farmacie = new LinkedList<>();
        for(int i=0; i<orders.size(); i++) {
            Farmacia farmacia = orderControl.dbAziendaManager.getFarmaciaByRefFarmacia(orders.get(i).getRefFarmacia());
            farmacie.add(farmacia);
        }
        LinkedList<FarmaciFormatter> farmaciFormatters = new LinkedList<>();
        for(int i=0; i<orders.size(); i++) {
            FarmaciFormatter farmaciFormatter = new FarmaciFormatter(orders.get(i).getMedicines());
            farmaciFormatters.add(farmaciFormatter);
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

                    private final Button btn = new Button("Gestisci");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            TableViewWrapper data = getTableView().getItems().get(getIndex());
                            orderControl.setOrder(data.getOrder());

                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/OrderDetailForm.fxml"));
                                OrderDetailForm orderDetailForm = new OrderDetailForm(orderControl);
                                loader.setController(orderDetailForm);
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

        azioneColumnTable.setCellFactory(cellFactory);
    }

    private class FarmaciFormatter {
        private String farmaci = "";

        public FarmaciFormatter(LinkedList<Medicine> medicines) {
            for(int i=0; i<medicines.size(); i++) {
                farmaci += medicines.get(i).getNome() + " x " + medicines.get(i).getDisponibilita() + "\n";
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