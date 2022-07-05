package control;

import java.io.IOException;
import java.util.LinkedList;

import javafx.HomepageFarmacista;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import jdbc.DBAziendaManager;
import model.Farmacista;
import model.Order;

public class MissConfirmControl {
    private UserControl userControl;
    public DBAziendaManager dbAziendaManager = new DBAziendaManager();

    public MissConfirmControl(UserControl userControl) {
        this.userControl = userControl;
    }

    public void startConfirmAlert() {
        Farmacista farmacista = (Farmacista)userControl.user;
        LinkedList<Order> notConfirmedOrders = dbAziendaManager.getNotConfirmedOrdersByRefFarmacia(farmacista.getRefFarmacia());

        if(notConfirmedOrders.isEmpty()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/HomepageFarmacista.fxml"));
                HomepageFarmacista homepageFarmacista = new HomepageFarmacista(userControl);
                loader.setController(homepageFarmacista);
                Parent root;
                root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage)Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../javafx/MissConfirmForm.fxml"));
                MissConfirmControl missConfirmControl = new MissConfirmControl(userControl);
                loader.setController(missConfirmControl);
                Parent root;
                root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage)Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
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