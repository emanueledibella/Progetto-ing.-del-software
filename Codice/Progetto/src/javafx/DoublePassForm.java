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
import java.util.Properties;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

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
            String tipologiaUtente;

            if(userControl.dbAziendaManager.addettoAziendaHasEmail(email)) {
                tipologiaUtente = "AddettoAzienda";
                userControl.dbAziendaManager.updatePassword(email, newPassword, tipologiaUtente);
            } else if(userControl.dbAziendaManager.corriereHasEmail(email)) {
                tipologiaUtente = "Corriere";
                userControl.dbAziendaManager.updatePassword(email, newPassword, tipologiaUtente);
            } else {    // Farmacista
                userControl.dbFarmaciaManager.updatePassword(email, newPassword);
            }

            // Inviare email
            String mittente="aziendafarmaceutica@azienda.it";
            String host="smtp.freesmtpservers.com";
            String subject = "Azienda Farmaceutica: notifica di avvenuto cambio password";
            String text = "Gentile utente,\n\nla password nel tuo account sul Sistema aziendale è stata modificata correttamente. \n\nSe non sei stato tu a cambiare la password, contatta l'amministrazione.";
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
