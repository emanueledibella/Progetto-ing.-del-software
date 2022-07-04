package javafx;

import control.UserControl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

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
        }
    }

}
