package javafx;

import java.io.IOException;

import control.UserControl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Node;

public class HomepageFarmacista {

    @FXML
    private Button annullaOrdineButton;

    @FXML
    private Button confermaOrdineButton;

    @FXML
    private Button gestisciScaricoButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button modificaOrdineButton;

    @FXML
    private Button prenotaFarmaciButton;

    @FXML
    private Button visualizzaScorteButton;

    private UserControl userControl;

    public HomepageFarmacista(UserControl userControl) {
        this.userControl = userControl;
    }

    @FXML
    void cancelOrders(MouseEvent event) {
        if(event.getSource() == annullaOrdineButton) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/OrdersListCancelOrders.fxml"));
                OrdersListCancelOrders ordersListCancelOrders = new OrdersListCancelOrders(userControl);
                loader.setController(ordersListCancelOrders);
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
    void confirmOrders(MouseEvent event) {
        if(event.getSource() == confermaOrdineButton) {
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
    void editOrders(MouseEvent event) {
        if(event.getSource() == modificaOrdineButton) {
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

    @FXML
    void logout(MouseEvent event) {
        if(event.getSource() == logoutButton) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/LogoutForm.fxml"));
                LogoutForm logoutForm = new LogoutForm(userControl);
                loader.setController(logoutForm);
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
    void orderMedicines(MouseEvent event) {
        if(event.getSource() == prenotaFarmaciButton) {
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
    void unloadWarehouse(MouseEvent event) {
        if(event.getSource() == gestisciScaricoButton) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/UnloadWareForm.fxml"));
                UnloadWareForm unloadWareForm = new UnloadWareForm(userControl);
                loader.setController(unloadWareForm);
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
    void viewStocks(MouseEvent event) {
        if(event.getSource() == visualizzaScorteButton) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/StockFaForm.fxml"));
                StockFaForm stockFaForm = new StockFaForm(userControl);
                loader.setController(stockFaForm);
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
