package javafx;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import control.PeriodicUpdateControl;
import control.UserControl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.Farmacista;
import javafx.scene.Node;

public class LoginForm implements Initializable {

    @FXML
    private TextField emailTextField;

    @FXML
    private Label errorLabel;

    @FXML
    private PasswordField passwordPasswordField;

    @FXML
    private Label registratiLabel;

    @FXML
    private ComboBox<String> tipologiaUtenteComboBox;

    @FXML
    private Button accediButton;

    private UserControl userControl;

    public LoginForm() {
        this.userControl = new UserControl();
    }

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        String tipologieUtenti[] = { "AddettoAzienda", "Farmacista", "Corriere" };
        tipologiaUtenteComboBox.getItems().addAll(tipologieUtenti);
        tipologiaUtenteComboBox.getSelectionModel().select(1);
    }

    @FXML
    void login(MouseEvent event) {
        if(event.getSource() == accediButton) {
            String tipologiaUtente = tipologiaUtenteComboBox.getSelectionModel().getSelectedItem().toString();

            if(tipologiaUtente == "AddettoAzienda" || tipologiaUtente == "Corriere") {
                if(userControl.dbAziendaManager.login(emailTextField.getText(), passwordPasswordField.getText(), tipologiaUtente)) {
                    userControl.user.setSession(tipologiaUtente);
                    if(tipologiaUtente == "AddettoAzienda") {
                        PeriodicUpdateControl periodicUpdateControl = new PeriodicUpdateControl();
                        periodicUpdateControl.initPeriodicUpdate();

                        // TODO: Realizzare collegamento con il caso d'uso Mostra ordini non confermati  
                        // ---------------------
                    }
                    else {
                        Parent root;
                        try {
                            root = FXMLLoader.load(getClass().getResource("../javafx/HomepageCorriere.fxml"));
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
                else {
                    errorLabel.setText("ATTENZIONE: Le credenziali inserite sono errate!");
                }
            }
            else {
                if(userControl.dbFarmaciaManager.login(emailTextField.getText(), passwordPasswordField.getText())) {
                    int refFarmacia = userControl.dbFarmaciaManager.getRefFarmacia(emailTextField.getText());
                    Farmacista farmacista = new Farmacista(refFarmacia);
                    userControl.user = farmacista;

                    userControl.user.setSession(tipologiaUtente);

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
                else {
                    errorLabel.setText("ATTENZIONE: Le credenziali inserite sono errate!");
                }
            }
        }
    }

    @FXML
    void register(MouseEvent event) {       
        if(event.getSource() == registratiLabel) {
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getResource("../javafx/RegisterForm.fxml"));
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
