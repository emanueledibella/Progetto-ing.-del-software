package javafx;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.ResourceBundle;
import control.OrderControl;
import control.UserControl;
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
import model.Farmacista;
import model.Medicine;
import model.Order;
import javafx.scene.Node;

public class OrdersListCancelOrders implements Initializable {

    @FXML
    private TableColumn<TableViewWrapper, Void> azioneTableColumn;

    @FXML
    private TableColumn<TableViewWrapper, String> dataConsegnaTableColumn;

    @FXML
    private TableColumn<TableViewWrapper, String> farmaciTableColumn;

    @FXML
    private Button indietroButton;

    @FXML
    private TableView<TableViewWrapper> tableView;

    private UserControl userControl;
    private OrderControl orderControl = new OrderControl();

    public OrdersListCancelOrders(UserControl userControl) {
        this.userControl = userControl;
    }

    @FXML
    void indietroButtonOnMouseClicked(MouseEvent event) {
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

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        farmaciTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getFarmaciFormatter().getFarmaci()));
        dataConsegnaTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getOrder().getDataConsegna().toString()));
        
        Farmacista farmacista = (Farmacista)userControl.user;
        int refFarmacia = farmacista.getRefFarmacia();
        LinkedList<Order> orders = orderControl.dbAziendaManager.getNotDeliveredOrdersByRefFarmacia(refFarmacia);
        LinkedList<FarmaciFormatter> farmaciFormatters = new LinkedList<>();
        for(int i=0; i<orders.size(); i++) {
            FarmaciFormatter farmaciFormatter = new FarmaciFormatter(orders.get(i).getMedicines());
            farmaciFormatters.add(farmaciFormatter);
        }
        LinkedList<TableViewWrapper> tableViewWrappers = new LinkedList<>();
        for(int i=0; i<orders.size(); i++) {
            TableViewWrapper tableViewWrapper = new TableViewWrapper(farmaciFormatters.get(i), orders.get(i));
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

                    private final Button btn = new Button("Annulla");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            TableViewWrapper data = getTableView().getItems().get(getIndex());
                            orderControl.setOrder(data.getOrder());

                            // Controlla se la data di consegna dell'ordine scelto ?? prossima di due giorni o meno alla data odierna
                            LocalDate now = LocalDate.now();
                            Duration diff = Duration.between(now.atStartOfDay(), orderControl.getOrder().getDataConsegna().atStartOfDay());
                            if(diff.toDays() > 2) {
                                orderControl.dbAziendaManager.deleteOrder(orderControl.getOrder());

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
                            } else {
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/ErrorFormCancelOrders.fxml"));
                                    ErrorFormCancelOrders errorFormCancelOrders = new ErrorFormCancelOrders(userControl);
                                    loader.setController(errorFormCancelOrders);
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

        public TableViewWrapper(FarmaciFormatter farmaciFormatter, Order order) {
            this.setFarmaciFormatter(farmaciFormatter);
            this.setOrder(order);
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
    }
}