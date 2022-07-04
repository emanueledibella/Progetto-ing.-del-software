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

public class LogoutForm {

    @FXML
    private Button annullaButton;

    @FXML
    private Button confermaButton;

    private UserControl userControl;

    public LogoutForm(UserControl userControl) {
        this.userControl = userControl;
    }

    @FXML
    void cancel(MouseEvent event) {
        if(userControl == null) {
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
        } else {
            String tipologiaUtente = userControl.user.getTipologiaUtente();

            if(tipologiaUtente.equals("Farmacista")) {
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
            } else if(tipologiaUtente.equals("Corriere")) {
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
    }

    @FXML
    void confirm(MouseEvent event) {
        if(userControl != null)
            userControl.user.destroySession();

        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("../javafx/LoginForm.fxml"));
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