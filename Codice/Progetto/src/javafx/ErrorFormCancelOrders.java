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

public class ErrorFormCancelOrders {

    @FXML
    private Button tornaIndietroButton;

    private UserControl userControl;

    public ErrorFormCancelOrders(UserControl userControl) {
        this.userControl = userControl;
    }

    @FXML
    void tornaIndietro(MouseEvent event) {
        if(event.getSource() == tornaIndietroButton) {
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

}