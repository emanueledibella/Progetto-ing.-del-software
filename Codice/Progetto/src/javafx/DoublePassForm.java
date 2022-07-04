package javafx;

import java.io.IOException;

import control.UserControl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Node;

public class DoublePassForm {

    @FXML
    private Button aggiornaPassword;

    @FXML
    private TextField newPasswordTextField;

    private UserControl userControl;

    public DoublePassForm(UserControl userControl) {
        this.userControl = userControl;
    }

    @FXML
    void updatePassword(MouseEvent event) {
        if(event.getSource() == aggiornaPassword) {
            String email = userControl.user.getEmail();
            String newPassword = newPasswordTextField.getText();

            if(userControl.user.getTipologiaUtente().equals("AddettoAzienda") || userControl.user.getTipologiaUtente().equals("Corriere")) {
                String tipologiaUtente = userControl.user.getTipologiaUtente();
                userControl.dbAziendaManager.updatePassword(email, newPassword, tipologiaUtente);
            } else {
                userControl.dbFarmaciaManager.updatePassword(email, newPassword);
            }

            // TODO: Inviare email
            //
            //

            try {
                Parent root = FXMLLoader.load(getClass().getResource("../javafx/LoginForm.fxml"));
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
