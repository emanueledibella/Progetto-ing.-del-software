package javafx;

import control.UserControl;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
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
import javafx.scene.Node;
import java.sql.*;

public class RegisterForm implements Initializable {

    @FXML
    private TextField emailTextField;

    @FXML
    private Label farmaciaLabel;

    @FXML
    private ComboBox<String> farmacieComboBox;

    @FXML
    private PasswordField passwordPasswordField;

    @FXML
    private Button registratiButton;

    @FXML
    private ComboBox<String> tipologiaUtenteComboBox;

    @FXML
    private Label errorLabel;

    @FXML
    private Button indietroButton;

    private UserControl userControl;

    public RegisterForm() {
        this.userControl = new UserControl();
    }


    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        String tipologieUtenti[] = { "AddettoAzienda", "Farmacista", "Corriere" };
        tipologiaUtenteComboBox.getItems().addAll(tipologieUtenti);
        tipologiaUtenteComboBox.getSelectionModel().select(1);

        try {
            String dbUrl = "jdbc:mysql://localhost:3306/DatabaseFarmacia";
            String username = "root";
            String password = "QPkFSey6wEwTM9Dz";
            
            Connection connection = DriverManager.getConnection(dbUrl, username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT idFarmacia, nome FROM Farmacia");
            
            while(resultSet.next()) {
                farmacieComboBox.getItems().add(resultSet.getString("nome") + ":" + resultSet.getString("idFarmacia"));
            }
            farmacieComboBox.setPromptText(farmacieComboBox.getItems().get(0));
            farmacieComboBox.getSelectionModel().select(0);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

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
            }
            catch (IOException e) {
                    e.printStackTrace();
            }
        }
    }

    @FXML
    void registratiButtonOnMouseClicked(MouseEvent event) {
        if(event.getSource() == registratiButton) {
            if(!userControl.checkFields(emailTextField.getText(), passwordPasswordField.getText())) {
                errorLabel.setText("ATTENZIONE: Compilare tutti i campi!");
            }
            else {
                String selectedItem = tipologiaUtenteComboBox.getSelectionModel().getSelectedItem().toString();

                if(selectedItem == "AddettoAzienda" || selectedItem == "Corriere") {
                    if(userControl.dbAziendaManager.checkEmail(emailTextField.getText())) {
                        errorLabel.setText("ATTENZIONE: Esiste già un utente con la stessa email!");
                    }
                    else {
                        userControl.dbAziendaManager.registerUser(emailTextField.getText(), passwordPasswordField.getText(), selectedItem);

                        userControl.user.setSession(selectedItem);

                        if(selectedItem == "AddettoAzienda") {
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
                }
                else {
                    if(userControl.dbFarmaciaManager.checkEmail(emailTextField.getText())) {
                        errorLabel.setText("ATTENZIONE: Esiste già un utente con la stessa email!");
                    }
                    else {
                        String itemsFarmacia[] = farmacieComboBox.getSelectionModel().getSelectedItem().toString().split(":");
                        String refFarmaciaString = itemsFarmacia[itemsFarmacia.length-1];
                        userControl.dbFarmaciaManager.registerUser(emailTextField.getText(), passwordPasswordField.getText(), refFarmaciaString);

                        int refFarmacia = Integer.parseInt(refFarmaciaString);
                        Farmacista farmacista = new Farmacista(refFarmacia);
                        userControl.user = farmacista;

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
                }
            }
        }
    }

    @FXML
    void resizeWindow(ActionEvent event) {
        if(event.getSource() == tipologiaUtenteComboBox) {
            errorLabel.setText("");
            Stage mainWindow = (Stage)tipologiaUtenteComboBox.getScene().getWindow();
            switch(tipologiaUtenteComboBox.getSelectionModel().getSelectedItem()) {
                case "AddettoAzienda":
                case "Corriere":
                    farmaciaLabel.setLayoutY(326);
                    farmacieComboBox.setLayoutY(342);
                    errorLabel.setLayoutY(202);
                    registratiButton.setLayoutY(250);
                    mainWindow.setHeight(327);
                break;
                case "Farmacista":
                    farmaciaLabel.setLayoutY(216);
                    farmacieComboBox.setLayoutY(232);
                    errorLabel.setLayoutY(262);
                    registratiButton.setLayoutY(310);
                    mainWindow.setHeight(388);
            }
        }
    }

}