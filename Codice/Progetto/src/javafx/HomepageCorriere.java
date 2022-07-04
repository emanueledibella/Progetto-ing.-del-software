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

public class HomepageCorriere {

    @FXML
    private Button logoutButton;

    private UserControl userControl;

    public HomepageCorriere(UserControl userControl) {
        this.userControl = userControl;
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
    void viewDelivery(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/DeliveryList.fxml"));
            DeliveryList deliveryList = new DeliveryList(userControl);
            loader.setController(deliveryList);
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
