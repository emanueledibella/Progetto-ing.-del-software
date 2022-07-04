package javafx;

import java.io.IOException;

import control.UserControl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Node;

public class OTPForm {

    @FXML
    private Label errorLabel;
    
    @FXML
    private TextField OTPCodeTextField;

    @FXML
    private Button verificaButton;

    private UserControl userControl;

    public OTPForm(UserControl userControl) {
        this.userControl = userControl;
    }

    @FXML
    void verify(MouseEvent event) {
        if(event.getSource() == verificaButton) {
            String OTPCode = OTPCodeTextField.getText();

            if(userControl.checkOTP(OTPCode)) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/DoublePassForm.fxml"));
                    DoublePassForm doublePassForm = new DoublePassForm(userControl);
                    loader.setController(doublePassForm);
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
                errorLabel.setText("ERRORE: Il codice OTP è errato!");
            }
        }
    }

}