package jdbc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

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
}
