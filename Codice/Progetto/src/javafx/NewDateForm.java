package javafx;


import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Node;

public class NewDateForm implements Initializable {

    @FXML
    private Button annullaButton;

    @FXML
    private Label newDateLabel;

    @FXML
    private Button proseguiButton;

    private UserControl userControl;
    private OrderControl orderControl;

    public NewDateForm(UserControl userControl, OrderControl orderControl) {
        this.userControl = userControl;
        this.orderControl = orderControl;
    }

    @FXML
    void annullaButtonOnMouseClicked(MouseEvent event) {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LocalDate dataConsegna = orderControl.getOrder().getDataConsegna();
        newDateLabel.setText("Prima data di consegna disponibile: " + dataConsegna.toString());
    }

}