package javafx;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import control.MissConfirmControl;
import control.OrderControl;
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
import model.Corriere;
import model.Farmacista;
import model.Order;
import javafx.scene.Node;

public class LoginForm implements Initializable {

    @FXML
    private TextField emailTextField;

    @FXML
    private Label errorLabel;

    @FXML
    private PasswordField passwordPasswordField;

    @FXML
    private Label recoverPasswordLabel;

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
                        // Invocare Ricarica periodica
                        PeriodicUpdateControl periodicUpdateControl = new PeriodicUpdateControl();
                        periodicUpdateControl.initPeriodicUpdate();

                        // Invocare Mostra ordini non confermati
                        OrderControl orderControl = new OrderControl();
                        LinkedList<Order> notConfirmedOrders = orderControl.dbAziendaManager.getNotConfirmedOrders();
                        if(notConfirmedOrders.isEmpty()) {
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
                            try {
                                Parent root = FXMLLoader.load(getClass().getResource("../javafx/NotConfirmedOrdersForm.fxml"));
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
                        int idCorriere = userControl.dbAziendaManager.getIdCorriere(emailTextField.getText());
                        Corriere corriere = new Corriere(idCorriere);
                        userControl.user = corriere;

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
                else {
                    errorLabel.setText("ATTENZIONE: Le credenziali inserite sono errate!");
                }
            }
            else {
                if(userControl.dbFarmaciaManager.login(emailTextField.getText(), passwordPasswordField.getText())) {
                    // Creare la sessione del Farmacista
                    int refFarmacia = userControl.dbFarmaciaManager.getRefFarmacia(emailTextField.getText());
                    Farmacista farmacista = new Farmacista(refFarmacia);
                    userControl.user = farmacista;
                    userControl.user.setSession(tipologiaUtente);

                    // Invocare Prenotazione automatica e Prenotazione periodica
                    OrderControl orderControl = new OrderControl();
                    orderControl.getOrder().setRefFarmacia(farmacista.getRefFarmacia());
                    orderControl.startPeriodicOrder();
                    orderControl.startAutomaticOrder();

                    // Lanciare Alert mancata conferma
                    MissConfirmControl missConfirmControl = new MissConfirmControl(userControl);
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime eightPMToday = LocalDateTime.now()
                                                                .withHour(20)
                                                                .withMinute(0)
                                                                .withSecond(0)
                                                                .withNano(0);
                    ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
                    ses.schedule(() -> missConfirmControl.startConfirmAlert(),
                                now.until(eightPMToday, ChronoUnit.MILLIS),
                                TimeUnit.MILLISECONDS);

                    // Mostrare homepage
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
    void recoverPassword(MouseEvent event) {
        if(event.getSource() == recoverPasswordLabel) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("../javafx/RecoverPassForm.fxml"));
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
