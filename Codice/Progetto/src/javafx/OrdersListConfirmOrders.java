package javafx;

import control.UserControl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import control.OrderControl;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Farmacista;
import model.Medicine;
import model.Order;
import javafx.scene.Node;

public class OrdersListConfirmOrders implements Initializable {

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

    public OrdersListConfirmOrders(UserControl userControl) {
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
        LinkedList<Order> orders = orderControl.dbAziendaManager.getDeliveredOrdersByRefFarmacia(refFarmacia);
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

                    private final Button btn = new Button("Conferma");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            TableViewWrapper data = getTableView().getItems().get(getIndex());
                            orderControl.setOrder(data.getOrder());

                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/OrderDetailFormConfirmOrders.fxml"));
                                OrderDetailFormConfirmOrders orderDetailFormConfirmOrders = new OrderDetailFormConfirmOrders(userControl, orderControl);
                                loader.setController(orderDetailFormConfirmOrders);
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