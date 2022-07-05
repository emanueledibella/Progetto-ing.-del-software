package jdbc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.LinkedList;

import model.Medicine;

public class DBFarmaciaManager {
    private String dbUrl = "jdbc:mysql://localhost:3306/DatabaseFarmacia?sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false";
    private String username = "root";
    private String password = "QPkFSey6wEwTM9Dz";

    // N.B.: Si consideri il sequence diagram Registrazione.
    // Il metodo di seguito rappresenta il messaggio 6: checkUsername().
    // Poiché effettivamente non si fa il check dell'username ma si fa il check dell'email,
    // allora ecco perché è chiamato checkEmail() e non checkUsername()
    public boolean checkEmail(String email) {
        boolean exists = false;

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT email FROM Farmacista WHERE email = '" + email + "'");

            exists = resultSet.next();
            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return exists;
    }

    public void decreaseQty(Medicine medicine) {
        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
    
            statement.executeUpdate("UPDATE Farmaco SET disponibilita = 0 WHERE idFarmaco = '" + medicine.getIdFarmaco() + "'");

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String getMD5(String stringa) {
        String hashString = null;

        try 
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(stringa.getBytes());
            byte[] bytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            hashString = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hashString;
    }

    public LinkedList<Medicine> getAllMeds(int refFarmacia) {
        LinkedList<Medicine> medicines = new LinkedList<>();

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
    
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Farmaco WHERE refFarmacia = '" + refFarmacia + "' ORDER BY nome");
            while(resultSet.next()) {
                int idFarmaco = Integer.parseInt(resultSet.getString("idFarmaco"));
                String nome = resultSet.getString("nome");
                String principioAttivo = resultSet.getString("principioAttivo");
                Date dataScadenza = Date.valueOf(resultSet.getString("dataScadenza"));
                int disponibilita = Integer.parseInt(resultSet.getString("disponibilita"));
                Boolean daBanco = Boolean.parseBoolean(resultSet.getString("daBanco"));

                medicines.add(new Medicine(idFarmaco, nome, principioAttivo, dataScadenza, disponibilita, daBanco));
            }
            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return medicines;
    }

    public LinkedList<Medicine> getMedsByRefFarmacia(int refFarmacia) {
        LinkedList<Medicine> medicines = new LinkedList<>();

        try { 
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
    
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Farmaco WHERE refFarmacia = '" + refFarmacia + "'");
            while(resultSet.next()) {
                int idFarmaco = Integer.parseInt(resultSet.getString("idFarmaco"));
                String nome = resultSet.getString("nome");
                String principioAttivo = resultSet.getString("principioAttivo");
                Date dataScadenza = Date.valueOf(resultSet.getString("dataScadenza"));
                int disponibilita = Integer.parseInt(resultSet.getString("disponibilita"));
                boolean daBanco = Boolean.parseBoolean(resultSet.getString("daBanco"));

                medicines.add(new Medicine(idFarmaco, nome, principioAttivo, dataScadenza, disponibilita, daBanco));
            }

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return medicines;
    }
    
    public LinkedList<Medicine> getNotOTCMeds(int refFarmacia) {
        LinkedList<Medicine> medicines = new LinkedList<>();

        try { 
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
    
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT nome FROM Farmaco WHERE daBanco = '0' AND refFarmacia = '" + refFarmacia + "'");
            while(resultSet.next()) {
                String nome = resultSet.getString("nome");

                medicines.add(new Medicine(-1, nome, "", null, 0, false));
            }

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return medicines;
    }

    public int getRefFarmacia(String email) {
        int refFarmacia = -1;

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
    
            ResultSet resultSet = statement.executeQuery("SELECT refFarmacia FROM Farmacista WHERE email = '" + email + "'");

            resultSet.next();
            refFarmacia = Integer.parseInt(resultSet.getString("refFarmacia"));

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return refFarmacia;
    }

    public void registerUser(String email, String password, String refFarmacia) {
        String hashPassword = getMD5(password);

        try {
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
            
            statement.executeUpdate("INSERT INTO Farmacista(email, hashPassword, refFarmacia) VALUES('" + email + "', '" + hashPassword + "', '" + refFarmacia + "')");

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean login(String email, String password) {
        int number = -1;
        String hashPassword = getMD5(password);

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
    
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) as number FROM Farmacista WHERE email='" + email + "' and hashPassword='" + hashPassword + "'");

            resultSet.next();
            number = Integer.parseInt(resultSet.getString("number"));

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        if(number != 0)
            return true;
        return false;
    }

    public void updateAvailability(LinkedList<Medicine> meds) {
        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();

            for(int i=0; i<meds.size(); i++) {
                Medicine med = meds.remove();

                statement.executeUpdate("UPDATE Farmaco SET disponibilita=" + String.valueOf(med.getDisponibilita()) + " WHERE idFarmaco=" + String.valueOf(med.getIdFarmaco()));
            }
            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void updateInventoryFromRefFarmacia(LinkedList<Medicine> meds,int refFarmacia) {
        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();

            for(int i=0; i<meds.size(); i++) {
                Medicine med = meds.get(i);
                String nome = med.getNome();
                String principioAttivo = med.getPrincipioAttivo();
                Date dataScadenza = med.getDataScadenza();
                int disponibilita = med.getDisponibilita();
                Boolean daBanco = med.getDaBanco();

                statement.executeUpdate("INSERT INTO Farmaco(nome, principioAttivo, dataScadenza, disponibilita, daBanco, refFarmacia) VALUES('" + nome + "', '" + principioAttivo + "', '" + dataScadenza + "', '" + disponibilita + "', '" + daBanco + "', '" + refFarmacia + "')");
            }
            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePassword(String email, String newPassword) {
        String newPasswordHash = getMD5(newPassword);

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();

            statement.executeUpdate("UPDATE Farmacista SET hashPassword = '" + newPasswordHash + "' WHERE email = '" + email + "'");
            
            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
