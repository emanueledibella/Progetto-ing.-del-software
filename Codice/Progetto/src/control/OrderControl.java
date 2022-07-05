package control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.LinkedList;

import jdbc.DBAziendaManager;
import jdbc.DBFarmaciaManager;
import model.Medicine;
import model.Order;

public class OrderControl {
    private Order order = new Order();
    public DBAziendaManager dbAziendaManager = new DBAziendaManager();
    public DBFarmaciaManager dbFarmaciaManager = new DBFarmaciaManager();

    public void setOrder(Order order) {
        this.order = order;
    }
    public Order getOrder() {
        return this.order;
    }

    public void startAutomaticOrder() {
        File file = new File("automaticOrderDate.txt");

        if(!file.exists()) {
            try {
                // Creazione del file
                file.createNewFile();
                
                // Scrittura nel file della data corrente
                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(LocalDate.now().toString());
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                // Lettura dal file dell'ultima data in cui è stata effettuata una prenotazione automatica
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                LocalDate periodicUpdateDate = LocalDate.parse(bufferedReader.readLine());
                bufferedReader.close();

                // Calcolo della differenza fra la data in cui è stata effettuata l'ultima prenotazione automatica e la data attuale
                LocalDate now = LocalDate.now();
                Duration diff = Duration.between(periodicUpdateDate.atStartOfDay(), now.atStartOfDay());

                // Se è trascorso almeno un giorno dall'ultima prenotazione automatica
                if(diff.toDays() >= 1) {
                    LinkedList<Medicine> medicines = dbFarmaciaManager.getAllMeds(order.getRefFarmacia());

                    // Controlla se vi sono farmaci scaduti. Nell'evenienza che vi siano farmaci scaduti, li rimuove
                    for(int i=0; i<medicines.size(); i++) {
                        Medicine medicine = medicines.get(i);

                        diff = Duration.between(now.atStartOfDay(), medicine.getDataScadenza().toLocalDate().atStartOfDay());
                        if(diff.toDays() < 0) {
                            medicine.setDisponibilita(0);
                            dbFarmaciaManager.decreaseQty(medicine);
                        }
                    }

                    // Calcola la quantità di ogni farmaco
                    for(int i=0; i<medicines.size(); i++) {
                        // Evita di calcolare più volte la quantità di uno stesso farmaco. Poiché il metodo dbFarmaciaManager.getAllMeds restituisce i farmaci
                        // in ordine alfabetico, allora il controllo di seguito è sufficiente per risolvere il problema
                        if(i > 0 && medicines.get(i).getNome().equals(medicines.get(i-1).getNome()))
                            continue;
                        
                        Medicine medicine = medicines.get(i);
                        int qty = 0;
                        for(int j=0; j<medicines.size(); j++) {
                            if(medicines.get(j).getNome().equals(medicine.getNome())) {
                                qty += medicines.get(j).getDisponibilita();
                            }
                        }

                        if(qty < 15) {
                            medicine.setDisponibilita(20);
                            order.getMedicines().add(medicine);
                        }
                    }
                    order.setAlertScadenza(false);
                    order.setDataConsegna(now.plusDays(7));
                    this.order();

                    // Imposta la data odierna come la data in cui è stata effettuata l'ultima prenotazione automatica
                    file.delete();
                    FileWriter fileWriter = new FileWriter(file);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(LocalDate.now().toString());
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void startPeriodicOrder() {
        File file = new File("periodicOrderDate.txt");

        if(!file.exists()) {
            try {
                // Creazione del file
                file.createNewFile();
                
                // Scrittura nel file della data corrente
                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(LocalDate.now().toString());
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                // Lettura dal file dell'ultima data in cui è stata effettuata una prenotazione periodica
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                LocalDate periodicUpdateDate = LocalDate.parse(bufferedReader.readLine());
                bufferedReader.close();

                // Calcolo della differenza fra la data in cui è stata effettuata l'ultima prenotazione periodica e la data attuale
                LocalDate now = LocalDate.now();
                Duration diff = Duration.between(periodicUpdateDate.atStartOfDay(), now.atStartOfDay());

                // Se è trascorso almeno una settimana dall'ultima prenotazione periodica
                if(diff.toDays() >= 7) {
                    LinkedList<Medicine> medicines = dbFarmaciaManager.getNotOTCMeds(order.getRefFarmacia());

                    for(int i=0; i<medicines.size(); i++ ) {
                        Medicine medicine = medicines.get(i);
                        medicine.setDisponibilita(20);
                        order.getMedicines().add(medicine);
                    }
                    order.setAlertScadenza(false);
                    order.setDataConsegna(now.plusDays(7));
                    this.order();

                    // Imposta la data odierna come la data in cui è stata effettuata l'ultima prenotazione periodica
                    file.delete();
                    FileWriter fileWriter = new FileWriter(file);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(LocalDate.now().toString());
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void order() {
        Order orderDB = new Order();
        orderDB.setRefFarmacia(order.getRefFarmacia());

        // Assegnare l'ordine ad un Corriere random
        LinkedList<Integer> idCorrieri = dbAziendaManager.getIdCorrieri();
        int idCorriereRandom = (int)(Math.random() * idCorrieri.size()) + 1;
        orderDB.setRefCorriere(idCorriereRandom); 

        // Impostare lo stato dell'ordine in "da consegnare"
        orderDB.setStato("Da consegnare");

        
        LocalDate newDataConsegna = order.getDataConsegna();   // Oggetto che verrà aggiornato con la nuova data di consegna
        LocalDate now = LocalDate.now();
        LocalDate twoMonthsFromNow = now.plusMonths(2);
        LinkedList<Medicine> meds = dbAziendaManager.getMedicinesByDeadline();
        //boolean scorteInsufficienti = false;
        for(int i=0; i<order.getMedicines().size(); i++) {
            Medicine medicine = order.getMedicines().get(i);     // Farmaco che si vuole prenotare
            int qty = medicine.getDisponibilita();
            for(int j=0; j<meds.size(); j++) {
                Medicine med = meds.get(j);                      // Farmaco dal magazzino aziendale

                if(med.getNome().equals(medicine.getNome()) && med.getDisponibilita() != 0) {
                    Duration diff = Duration.between(twoMonthsFromNow.atStartOfDay(), med.getDataScadenza().toLocalDate().atStartOfDay());

                    if((diff.toDays() <= 62) && order.getAlertScadenza()) {
                        continue;
                    } else {
                        if(qty <= med.getDisponibilita()) {
                            med.setDisponibilita(qty);
                            orderDB.getMedicines().add(med);
                            qty = 0;
                            break;
                        } else {
                            qty -= med.getDisponibilita();
                            orderDB.getMedicines().add(med);
                        }
                    }
                }
            }

            if(qty != 0) {
                //scorteInsufficienti = true;
                Duration diff = Duration.between(order.getDataConsegna().atStartOfDay(), newDataConsegna.atStartOfDay());
                int settimane = (int)(diff.toDays()/7);   // Considerando che la ricarica periodica avviene ogni settimana, esprimo la differenza in settimane

                if((qty - settimane*500) > 0) {     // Considerando che la ricarica periodica consiste di 500 unità ogni settimana, calcolo il numero di settimane che ci vorrebbero per raggiungere la quantità di farmaci richiesta e ancora mancante
                    int settimaneAttesaStimate = (qty - settimane*500)/500 + 1;     // Ottenere le settimane di attesa necessarie
                    newDataConsegna = newDataConsegna.plusDays(settimaneAttesaStimate*7); // Aggiornare la nuova data di consegna
                }
            }
        }

        /*
        if(scorteInsufficienti && urgente == false) {   // urgente sarebbe un parametro del suddetto metodo. Infatti, nel momento in cui si richiama il suddetto metodo dal metodo startAutomaticOrder, questo controllo si dovrebbe saltare
            // TODO: Messaggio in cui si chiede al Farmacista se vuole spostare la data dell'ordine alla data NewDataConsegna
        }
        */
        orderDB.setDataConsegna(newDataConsegna);

        dbAziendaManager.order(orderDB);
    }
}
