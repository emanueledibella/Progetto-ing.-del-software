package control;

import jdbc.DBAziendaManager;
import jdbc.DBFarmaciaManager;
import model.User;

public class UserControl {
    public DBFarmaciaManager dbFarmaciaManager = new DBFarmaciaManager();
    public DBAziendaManager dbAziendaManager = new DBAziendaManager();
    public User user = new User();

    public boolean checkFields(String email, String password) {
        if(email.isEmpty() || password.isEmpty())
            return false;
        return true;
    }
}