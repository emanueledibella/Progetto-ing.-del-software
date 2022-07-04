package jdbc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;

import model.Farmacia;
import model.Medicine;
import model.Order;

public class DBAziendaManager {
    private String dbUrl = "jdbc:mysql://localhost:3306/DatabaseAzienda?sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false";
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

            ResultSet resultSet = statement.executeQuery("SELECT AddettoAzienda.email FROM AddettoAzienda WHERE AddettoAzienda.email = '" + email + "'");
            boolean existsAddettoAzienda = resultSet.next();
            resultSet = statement.executeQuery("SELECT Corriere.email FROM Corriere WHERE Corriere.email = '" + email + "'");
            boolean existsCorriere = resultSet.next();
            exists = existsAddettoAzienda || existsCorriere;

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return exists;
    }

    public void deleteOrder(Order order) {
        LinkedList<Medicine> medicines = order.getMedicines();

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();

            // Ripristinare le quantità dei farmaci
            for(int i=0; i<medicines.size(); i++) {
                int idFarmaco = medicines.get(i).getIdFarmaco();
                int disponibilita = medicines.get(i).getDisponibilita();

                statement.executeUpdate("UPDATE Farmaco SET disponibilita=disponibilita + '" + disponibilita + "' WHERE idFarmaco='" + idFarmaco + "'");
            }

            // Cancellare tutte le entry inerenti l'ordine nella tabella Compone
            statement.executeUpdate("DELETE FROM Compone WHERE refOrdine ='" + order.getIdOrdine() + "'");

            // Cancellare entry nella tabella Ordine
            statement.executeUpdate("DELETE FROM Ordine WHERE idOrdine='" + order.getIdOrdine() + "'");

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public LinkedList<Order> getDeliveredOrdersByRefFarmacia(int refFarmacia) {
        LinkedList<Order> orders = new LinkedList<>();

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
    
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Ordine WHERE refFarmacia = '" + refFarmacia + "' AND stato = 'Consegnato' ORDER BY idOrdine DESC");
            while(resultSet.next()) {
                int idOrdine = Integer.parseInt(resultSet.getString("idOrdine"));
                String stato = resultSet.getString("stato");
                LocalDate dataConsegna = LocalDate.parse(resultSet.getString("dataConsegna"));
                int refCorriere = Integer.parseInt(resultSet.getString("refCorriere"));
                LinkedList<Medicine> medicines = this.getMedicinesByOrderId(idOrdine);

                Order order = new Order(idOrdine, stato, dataConsegna, refCorriere, refFarmacia, medicines, false);
                orders.add(order);
            }

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return orders;
    }

    public Farmacia getFarmaciaByRefFarmacia(int refFarmacia) {
        Farmacia farmacia = null;

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
    
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Farmacia WHERE idFarmacia = '" + refFarmacia + "'");
            while(resultSet.next()) {
                int idFarmacia = Integer.parseInt(resultSet.getString("idFarmacia"));
                String nome = resultSet.getString("nome");
                String numeroTelefono = resultSet.getString("numeroTelefono");
                String indirizzo = resultSet.getString("indirizzo");

                farmacia = new Farmacia(idFarmacia, nome, numeroTelefono, indirizzo);
            }

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return farmacia;
    }

    public LinkedList<Integer> getIdCorrieri() {
        LinkedList<Integer> idCorrieri = new LinkedList<>();

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
    
            ResultSet resultSet = statement.executeQuery("SELECT idUtente FROM Corriere;");
            while(resultSet.next()) {
                int idCorriere = Integer.parseInt(resultSet.getString("idUtente"));

                idCorrieri.add(idCorriere);
            }

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return idCorrieri;
    }

    public LinkedList<Medicine> getMedicines() {
        LinkedList<Medicine> medicines = new LinkedList<>();

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
    
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Farmaco");
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

    public LinkedList<Medicine> getMedicinesByDeadline() {
        LinkedList<Medicine> medicines = new LinkedList<>();

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
    
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Farmaco ORDER BY nome, dataScadenza");
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

    public LinkedList<Medicine> getMedicinesByOrderId(int orderId) {
        LinkedList<Medicine> medicines = new LinkedList<>();

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
    
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Compone WHERE refOrdine = '" + orderId + "'");
            while(resultSet.next()) {
                int refFarmaco = Integer.parseInt(resultSet.getString("refFarmaco"));
                Medicine medicine = null;
                
                Statement statement2 = connection.createStatement();
                ResultSet resultSet2 = statement2.executeQuery("SELECT * FROM Farmaco WHERE idFarmaco = '" + refFarmaco + "'");
                while(resultSet2.next()) {
                    int idFarmaco = Integer.parseInt(resultSet2.getString("idFarmaco"));
                    String nome = resultSet2.getString("nome");
                    String principioAttivo = resultSet2.getString("principioAttivo");
                    Date dataScadenza = Date.valueOf(resultSet2.getString("dataScadenza"));
                    int disponibilita = Integer.parseInt(resultSet.getString("quantita"));
                    boolean daBanco = Boolean.parseBoolean(resultSet2.getString("daBanco"));

                    medicine = new Medicine(idFarmaco, nome, principioAttivo, dataScadenza, disponibilita, daBanco);
                }

                medicines.add(medicine);
            }

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return medicines;
    }

    public LinkedList<Medicine> getMedicinesUnique() {
        LinkedList<Medicine> medicines = new LinkedList<>();

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
    
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT nome, principioAttivo FROM Farmaco");
            while(resultSet.next()) {
                String nome = resultSet.getString("nome");
                String principioAttivo = resultSet.getString("principioAttivo");

                medicines.add(new Medicine(-1, nome, principioAttivo, null, 0, false));
            }

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return medicines;
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

    public LinkedList<Order> getNotDeliveredOrdersByRefCorriere(int refCorriere) {
        LinkedList<Order> orders = new LinkedList<>();

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
    
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Ordine WHERE refCorriere = '" + refCorriere + "' AND stato = 'Da consegnare' ORDER BY idOrdine DESC");
            while(resultSet.next()) {
                int idOrdine = Integer.parseInt(resultSet.getString("idOrdine"));
                String stato = resultSet.getString("stato");
                LocalDate dataConsegna = LocalDate.parse(resultSet.getString("dataConsegna"));
                int refFarmacia = Integer.parseInt(resultSet.getString("refFarmacia"));
                LinkedList<Medicine> medicines = this.getMedicinesByOrderId(idOrdine);

                Order order = new Order(idOrdine, stato, dataConsegna, refCorriere, refFarmacia, medicines, false);
                orders.add(order);
            }

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return orders;
    }

    public LinkedList<Order> getNotDeliveredOrdersByRefFarmacia(int refFarmacia) {
        LinkedList<Order> orders = new LinkedList<>();

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
    
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Ordine WHERE refFarmacia = '" + refFarmacia + "' AND stato = 'Da consegnare' ORDER BY idOrdine DESC");
            while(resultSet.next()) {
                int idOrdine = Integer.parseInt(resultSet.getString("idOrdine"));
                String stato = resultSet.getString("stato");
                LocalDate dataConsegna = LocalDate.parse(resultSet.getString("dataConsegna"));
                int refCorriere = Integer.parseInt(resultSet.getString("refCorriere"));
                LinkedList<Medicine> medicines = this.getMedicinesByOrderId(idOrdine);

                Order order = new Order(idOrdine, stato, dataConsegna, refCorriere, refFarmacia, medicines, false);
                orders.add(order);
            }

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return orders;
    }

    public LinkedList<Order> getNotConfirmedOrders() {
        LinkedList<Order> orders = new LinkedList<>();

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
    
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Ordine WHERE stato = 'Non confermato' ORDER BY idOrdine DESC");
            while(resultSet.next()) {
                int idOrdine = Integer.parseInt(resultSet.getString("idOrdine"));
                String stato = resultSet.getString("stato");
                LocalDate dataConsegna = LocalDate.parse(resultSet.getString("dataConsegna"));
                int refFarmacia = Integer.parseInt(resultSet.getString("refFarmacia"));
                int refCorriere = Integer.parseInt(resultSet.getString("refCorriere"));
                LinkedList<Medicine> medicines = this.getMedicinesByOrderId(idOrdine);

                Order order = new Order(idOrdine, stato, dataConsegna, refCorriere, refFarmacia, medicines, false);
                orders.add(order);
            }

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return orders;
    }

    public LinkedList<Order> getNotConfirmedOrdersByRefFarmacia(int refFarmacia) {
        LinkedList<Order> orders = new LinkedList<>();

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
    
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Ordine WHERE stato = 'Non confermato' AND refFarmacia = '" + refFarmacia + "' ORDER BY idOrdine DESC");
            while(resultSet.next()) {
                int idOrdine = Integer.parseInt(resultSet.getString("idOrdine"));
                String stato = resultSet.getString("stato");
                LocalDate dataConsegna = LocalDate.parse(resultSet.getString("dataConsegna"));
                int refCorriere = Integer.parseInt(resultSet.getString("refCorriere"));
                LinkedList<Medicine> medicines = this.getMedicinesByOrderId(idOrdine);

                Order order = new Order(idOrdine, stato, dataConsegna, refCorriere, refFarmacia, medicines, false);
                orders.add(order);
            }

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return orders;
    }

    public LinkedList<Order> getOrders() {
        LinkedList<Order> orders = new LinkedList<>();

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
    
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Ordine ORDER BY idOrdine DESC");
            while(resultSet.next()) {
                int idOrdine = Integer.parseInt(resultSet.getString("idOrdine"));
                String stato = resultSet.getString("stato");
                LocalDate dataConsegna = LocalDate.parse(resultSet.getString("dataConsegna"));
                int refCorriere = Integer.parseInt(resultSet.getString("refCorriere"));
                int refFarmacia = Integer.parseInt(resultSet.getString("refFarmacia"));
                LinkedList<Medicine> medicines = this.getMedicinesByOrderId(idOrdine);

                Order order = new Order(idOrdine, stato, dataConsegna, refCorriere, refFarmacia, medicines, false);
                orders.add(order);
            }

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return orders;
    }

    public int getIdCorriere(String email) {
        int idCorriere = -1;

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
    
            ResultSet resultSet = statement.executeQuery("SELECT idUtente FROM Corriere WHERE email = '" + email + "'");

            resultSet.next();
            idCorriere = Integer.parseInt(resultSet.getString("idUtente"));

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return idCorriere;
    }

    public boolean login(String email, String password, String tipologiaUtente) {
        int number = -1;
        String hashPassword = getMD5(password);

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
    
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) as number FROM " + tipologiaUtente + " WHERE email='" + email + "' and hashPassword='" + hashPassword + "'");

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

    public void order(Order order) {
        String stato = order.getStato();
        Date dataConsegna = java.sql.Date.valueOf(order.getDataConsegna());
        int refCorriere = order.getRefCorriere();
        int refFarmacia = order.getRefFarmacia();

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
            
            // Ottenere l'id del prossimo ordine da inserire
            int idOrdine = 1;
            ResultSet resultSet = statement.executeQuery("SELECT MAX(idOrdine) FROM Ordine");
            resultSet.next();
            if(resultSet.getString("MAX(idOrdine)") != null) {
                idOrdine = Integer.parseInt(resultSet.getString("MAX(idOrdine)")) + 1;
            }

            // Inserire il nuovo ordine
            statement.executeUpdate("INSERT INTO Ordine(idOrdine, stato, dataConsegna, refCorriere, refFarmacia) VALUES('" + String.valueOf(idOrdine) + "', '" + stato + "', '" + String.valueOf(dataConsegna) + "', '" + String.valueOf(refCorriere) + "', '" + String.valueOf(refFarmacia) + "')");
            
            for(int i=0; i<order.getMedicines().size(); i++) {
                Medicine medicine = order.getMedicines().get(i);
                int idFarmaco = medicine.getIdFarmaco();
                int disponibilita = medicine.getDisponibilita();

                // Diminuire la disponibilità del farmaco nel magazzino dell'azienda
                statement.executeUpdate("UPDATE Farmaco SET disponibilita = disponibilita - '" + String.valueOf(disponibilita) + "' WHERE idFarmaco = '" + String.valueOf(idFarmaco) + "'");

                statement.executeUpdate("INSERT INTO Compone(quantita, refFarmaco, refOrdine) VALUES('" + String.valueOf(disponibilita) +  "', '" + String.valueOf(idFarmaco) + "', '" + String.valueOf(idOrdine) + "')");
            }

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void registerUser(String email, String password, String tipologiaUtente) {
        String hashPassword = getMD5(password);

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
            
            if(tipologiaUtente == "AddettoAzienda") {
                statement.executeUpdate("INSERT INTO AddettoAzienda(email, hashPassword) VALUES('" + email + "', '" + hashPassword + "')");
            }
            else { // Corriere
               statement.executeUpdate("INSERT INTO Corriere(email, hashPassword) VALUES('" + email + "', '" + hashPassword + "')");
            }

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setAsConfirmed(Order order) {
        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
            
            statement.executeUpdate("UPDATE Ordine SET stato='Confermato' WHERE idOrdine = '" + order.getIdOrdine() + "'");

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setAsNotConfirmed(Order order) {
        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
            
            statement.executeUpdate("UPDATE Ordine SET stato='Non confermato' WHERE idOrdine = '" + order.getIdOrdine() + "'");

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setAsSigned(Order order) {
        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
            
            statement.executeUpdate("UPDATE Ordine SET stato='Consegnato' WHERE idOrdine = '" + order.getIdOrdine() + "'");

            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAllQty(int qty) {
        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();

            // Ottenere la lista (più precisamente, nome e principio attivo) dei farmaci venduti dall'azienda
            LinkedList<String> nomiFarmaci = new LinkedList<>();
            LinkedList<String> principiAttiviFarmaci = new LinkedList<>();
            LinkedList<Boolean> daBancoFarmaci = new LinkedList<>();
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT nome, principioAttivo, daBanco FROM Farmaco");
            while(resultSet.next()) {
                String nomeFarmaco = resultSet.getString("nome");
                String principioAttivoFarmaco = resultSet.getString("principioAttivo");
                Boolean daBancoFarmaco = Boolean.parseBoolean(resultSet.getString("daBanco"));

                nomiFarmaci.add(nomeFarmaco);
                principiAttiviFarmaci.add(principioAttivoFarmaco);
                daBancoFarmaci.add(daBancoFarmaco);
            }

            // Creare un nuovo lotto di "qty" unità per ogni farmaco della lista dei farmaci.
            // Si suppone arbitrariamente che la data di scadenza del nuovo lotto sia di 4 mesi
            // successiva alla data odierna
            LocalDate now = LocalDate.now();
            LocalDate dataScadenza = now.plusMonths(4);
            for(int i=0; i<nomiFarmaci.size(); i++) {
                statement.executeUpdate("INSERT INTO Farmaco(nome, principioAttivo, dataScadenza, disponibilita, daBanco) VALUES('" + nomiFarmaci.get(i) + "', '" + principiAttiviFarmaci.get(i) + "', '" + dataScadenza + "', '" + qty + "', '" + daBancoFarmaci.get(i) + "')");
            }
            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void updateInventory(LinkedList<Medicine> meds) {
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

    public void updateOrder(Order order) {
        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();

            statement.executeUpdate("UPDATE Ordine SET stato = '" + order.getStato() + "' WHERE idOrdine = '" + order.getIdOrdine() + "'");

            LinkedList<Medicine> medicines = order.getMedicines();
            for(int i=0; i<medicines.size(); i++) {
                Medicine medicine = medicines.get(i);

                statement.executeUpdate("UPDATE Compone SET quantita = '" + medicine.getDisponibilita() + "' WHERE refFarmaco = '" + medicine.getIdFarmaco() + "' AND refOrdine = '" + order.getIdOrdine() + "'");
            }
            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePassword(String email, String newPassword, String tipologiaUtente) {
        String newPasswordHash = getMD5(newPassword);

        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();

            statement.executeUpdate("UPDATE '" + tipologiaUtente + "' SET hashPassword = '" + newPasswordHash + "' WHERE email = '" + email + "'");
            
            connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
