package javafx;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.LinkedList;
import java.util.ResourceBundle;

import control.DeliveryControl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart.Data;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Corriere;
import model.Delivery;
import javafx.scene.Node;

public class DeliveryList implements Initializable {

    @FXML
    private TableColumn<Data, Void> firmaTableColumn;

    @FXML
    private Button indietroButton;

    @FXML
    private TableColumn<Delivery, String> indirizzoTableColumn;

    @FXML
    private TableColumn<Delivery, String> nomeTableColumn;

    @FXML
    private TableColumn<Delivery, Date> dataConsegnaTableColumn;

    @FXML
    private TableColumn<Delivery, StringBuilder> farmaciTableColumn;

    @FXML
    private TableView<Delivery> tableview;

    @FXML
    private TableColumn<Delivery, String> telefonoTableColumn;

    DeliveryControl deliveryControl = new DeliveryControl();

    public void initialize(URL url, ResourceBundle rb){
        dataConsegnaTableColumn.setCellValueFactory(new PropertyValueFactory<Delivery,Date>("dataConsegna"));
        indirizzoTableColumn.setCellValueFactory(new PropertyValueFactory<Delivery,String>("indirizzo"));
        nomeTableColumn.setCellValueFactory(new PropertyValueFactory<Delivery, String>("nomeFarmacia"));
        telefonoTableColumn.setCellValueFactory(new PropertyValueFactory<Delivery, String>("numeroTelefono"));
        farmaciTableColumn.setCellValueFactory(new PropertyValueFactory<Delivery, StringBuilder>("meds"));
        addButtonToTable();

        LinkedList<Delivery> del = deliveryControl.dbAziendaManager.getDeliveries(new Corriere());
        tableview.getItems().addAll(del);
    }

    @FXML
    void indietroOnMouseClicked(MouseEvent event) {
        if(event.getSource() == indietroButton) {
            try {
                    Parent root = FXMLLoader.load(getClass().getResource("../javafx/HomepageCorriere.fxml"));
                    Scene scene = new Scene(root);
                    Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                    stage.setScene(scene);
                    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                    stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
                    stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
                    stage.show();
            }
            catch (IOException e) {
                    e.printStackTrace();
            }
        }
    }

    private void addButtonToTable() {
        Callback<TableColumn<Data, Void>, TableCell<Data, Void>> cellFactory = new Callback<TableColumn<Data, Void>, TableCell<Data, Void>>() {
            @Override
            public TableCell<Data, Void> call(final TableColumn<Data, Void> param) {
                final TableCell<Data, Void> cell = new TableCell<Data, Void>() {
                    private final Button btn = new Button("Firma");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            int idOrdine = tableview.getItems().get(getIndex()).getIdOrdine();
                            try
                            {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/FirmaForm.fxml"));
                                FirmaForm firmaForm = new FirmaForm(idOrdine);
                                loader.setController(firmaForm);
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
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        };
        firmaTableColumn.setCellFactory(cellFactory);
    }

    
}
