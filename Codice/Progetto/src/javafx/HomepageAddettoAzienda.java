package javafx;

import java.io.IOException;
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

public class HomepageAddettoAzienda {

    @FXML
    private Button aggiornamentoManualeButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button visualizzaOrdiniButton;

    @FXML
    private Button visualizzaScorteButton;

    @FXML
    void logout(MouseEvent event) {
        if(event.getSource() == logoutButton) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/LogoutForm.fxml"));
                LogoutForm logoutForm = new LogoutForm(null);
                loader.setController(logoutForm);
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
    void startUpdate(MouseEvent event) {
        if(event.getSource() == aggiornamentoManualeButton) {
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getResource("../javafx/UpdateQtyForm.fxml"));
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
    void viewOrders(MouseEvent event) {
        if(event.getSource() == visualizzaOrdiniButton) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("../javafx/ViewOrdersBoundary.fxml"));
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
    void viewStocks(MouseEvent event) {
        if(event.getSource() == visualizzaScorteButton) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("../javafx/StockAgForm.fxml"));
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
