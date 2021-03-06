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
import java.util.Properties;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class RecoverPassForm {

    @FXML
    private TextField emailTextField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button indietroButton;

    @FXML
    private Button recuperaPasswordButton;

    private UserControl userControl = new UserControl();

    @FXML
    void indietroButtonOnMouseClicked(MouseEvent event) {
        if(event.getSource() == indietroButton) {
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

    @FXML
    void recover(MouseEvent event) {
        if(event.getSource() == recuperaPasswordButton) {
            String email = emailTextField.getText();

            if(userControl.dbAziendaManager.checkEmail(email) || userControl.dbFarmaciaManager.checkEmail(email)) {
                userControl.user.setEmail(email);
                // Generare codice OTP
                String OTPCode = String.valueOf((int)(Math.random() * 1000000));
                userControl.user.setOTPCode(OTPCode);

                // Inviare email
                String mittente="aziendafarmaceutica@azienda.it";
                String host="smtp.freesmtpservers.com";
                String subject = "Azienda Farmaceutica: Recupera Password";
                String text = "Gentile utente,\n\nEcco il codice OTP per effettuare il recupero della password: " + OTPCode + "\n\nSe non sei stato tu a richiedere il recupero della password, ignora questa email.";
                Properties properties = new Properties();
                properties.put("mail.smtp.host", host);
                properties.put("port", 25);
                Session session = Session.getInstance(properties, null);              
                MimeMessage mail = new MimeMessage(session);
                try {
                    mail.setFrom(new InternetAddress(mittente));
                    mail.addRecipients(Message.RecipientType.TO, email);
                    mail.setSubject(subject);
                    mail.setText(text);

                    Transport.send(mail);
                } catch(Exception e) {
                    e.printStackTrace();
                }

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/OTPForm.fxml"));
                    OTPForm otpForm = new OTPForm(userControl);
                    loader.setController(otpForm);
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
                errorLabel.setText("ERRORE: All'interno del sistema non esiste un account associato a questa email!");
            }
        }
    }

}