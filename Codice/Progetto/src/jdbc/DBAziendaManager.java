package jdbc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;

import model.Medicine;
import model.Order;
import model.Corriere;
import model.Delivery;

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

                medicines.add(new Medicine(idFarmaco, nome, principioAttivo, dataScadenza, disponibilita));
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

                medicines.add(new Medicine(idFarmaco, nome, principioAttivo, dataScadenza, disponibilita));
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
    
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT nome, principioAttivo FROM Farmaco;");
            while(resultSet.next()) {
                String nome = resultSet.getString("nome");
                String principioAttivo = resultSet.getString("principioAttivo");

                medicines.add(new Medicine(-1, nome, principioAttivo, null, 0));
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

    public void updateAllQty(int qty) {
        try {      
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();

            // Ottenere la lista (più precisamente, nome e principio attivo) dei farmaci venduti dall'azienda
            LinkedList<String> nomiFarmaci = new LinkedList<>();
            LinkedList<String> principiAttiviFarmaci = new LinkedList<>();
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT nome, principioAttivo FROM Farmaco");
            while(resultSet.next()) {
                String nomeFarmaco = resultSet.getString("nome");
                String principioAttivoFarmaco = resultSet.getString("principioAttivo");

                nomiFarmaci.add(nomeFarmaco);
                principiAttiviFarmaci.add(principioAttivoFarmaco);
            }

            // Creare un nuovo lotto di "qty" unità per ogni farmaco della lista dei farmaci.
            // Si suppone arbitrariamente che la data di scadenza del nuovo lotto sia di 4 mesi
            // successiva alla data odierna
            LocalDate now = LocalDate.now();
            LocalDate dataScadenza = now.plusMonths(4);
            for(int i=0; i<nomiFarmaci.size(); i++) {
                statement.executeUpdate("INSERT INTO Farmaco(nome, principioAttivo, dataScadenza, disponibilita) VALUES('" + nomiFarmaci.get(i) + "', '" + principiAttiviFarmaci.get(i) + "', '" + dataScadenza + "', '" + qty + "')");
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

    public LinkedList<Delivery> getDeliveries(Corriere corriere){
        try {
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT a.idOrdine,a.dataConsegna,b.nome,b.indirizzo, b.numeroTelefono,d.nome FROM ordine a,farmacia b,compone c,farmaco d WHERE stato='da consegnare' AND refCorriere=' "+ corriere.getRefCorriere() + "' AND a.refFarmacia=b.idFarmacia AND c.refOrdine=a.idOrdine AND c.refFarmaco=d.idFarmaco ORDER BY a.idOrdine ASC;");
            LinkedList<Delivery> del = new LinkedList<Delivery>();
            resultSet.next();
            while(true){
                LinkedList <String> meds = new LinkedList<>();
                int idOrdine = Integer.parseInt(resultSet.getString(1));
                Date dataConsegna = resultSet.getDate(2);
                String nomeFarmacia = resultSet.getString(3);
                String indirizzo = resultSet.getString(4);
                String numeroTelefono = resultSet.getString(5);
                meds.add(resultSet.getString(6));
                
                while (true) {
                    if (resultSet.next()) {
                        int nuovoIdOrdine = Integer.parseInt(resultSet.getString(1));
                        if (idOrdine == nuovoIdOrdine) {
                            meds.add(resultSet.getString(6));
                        } else {
                            del.add(new Delivery(idOrdine, dataConsegna, nomeFarmacia, indirizzo, numeroTelefono, meds));
                            break;
                        }    
                    } else {
                        del.add(new Delivery(idOrdine, dataConsegna, nomeFarmacia, indirizzo, numeroTelefono, meds));
                        connection.close();
                        return del;
                    }   
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setAsSigned(int idOrdine){
        try {
            Connection connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
            Statement statement = connection.createStatement();

            statement.executeUpdate("UPDATE `ordine` SET `stato`='consegnato' WHERE idOrdine= '" + idOrdine + "';");
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();;
        }
    }
}
