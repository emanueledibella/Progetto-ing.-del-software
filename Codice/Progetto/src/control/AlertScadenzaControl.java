package control;

import java.time.Duration;
import java.time.LocalDate;
import java.util.LinkedList;
import jdbc.DBAziendaManager;
import model.Medicine;
import model.Order;

public class AlertScadenzaControl {
    private Order order;
    private LinkedList<Medicine> meds; 
    public DBAziendaManager dbAziendaManager = new DBAziendaManager();

    public AlertScadenzaControl(Order order) {
        this.order = order;
        this.meds = dbAziendaManager.getMedicinesByDeadline();
    }

    public LinkedList<Medicine> calcNearExpirationDateList() {
        LinkedList<Medicine> nearExpirationDateList = new LinkedList<>();

        // Calcolare la data due mesi dopo la data di prenotazione
        LocalDate now = LocalDate.now();
        LocalDate twoMonthsFromNow = now.plusMonths(2);

        // Ottenere la lista dei farmaci la cui data di scadenza è inferiore a due mesi dalla data di prenotazione
        for(int i=0; i<order.getMedicines().size(); i++) {
            for(int j=0; j<meds.size(); j++) {
                if(meds.get(j).getNome().equals(order.getMedicines().get(i).getNome()) && meds.get(j).getDisponibilita() > 0) {
                    LocalDate dataScadenza = meds.get(j).getDataScadenza().toLocalDate();
                    Duration diff = Duration.between(twoMonthsFromNow.atStartOfDay(), dataScadenza.atStartOfDay());

                    if(diff.toDays() <= 62) {
                        nearExpirationDateList.add(meds.get(j));
                        break;
                    }
                }
            }
        }

        return nearExpirationDateList;
    }

    public LocalDate calcNewDate() {
        LocalDate newDataConsegna = order.getDataConsegna();   // Oggetto che verrà aggiornato con la nuova data di consegna
        LocalDate now = LocalDate.now();
        LocalDate twoMonthsFromNow = now.plusMonths(2); 

        for(int i=0; i<order.getMedicines().size(); i++) {
            Medicine medicine = order.getMedicines().get(i);     // Farmaco che si vuole prenotare
            int qty = medicine.getDisponibilita();
            for(int j=0; j<meds.size(); j++) {
                Medicine med = meds.get(j);                      // Farmaco dal magazzino aziendale

                if(med.getNome().equals(medicine.getNome()) && med.getDisponibilita() != 0) {
                    Duration diff = Duration.between(twoMonthsFromNow.atStartOfDay(), med.getDataScadenza().toLocalDate().atStartOfDay());

                    if(diff.toDays() <= 62) {
                        continue;
                    } else {
                        if(qty <= med.getDisponibilita()) {
                            qty = 0;
                            break;
                        } else {
                            qty -= med.getDisponibilita();
                        }
                    }
                }
            }

            if(qty != 0) {
                Duration diff = Duration.between(order.getDataConsegna().atStartOfDay(), newDataConsegna.atStartOfDay());
                int settimane = (int)(diff.toDays()/7);   // Considerando che la ricarica periodica avviene ogni settimana, esprimo la differenza in settimane

                if((qty - settimane*500) > 0) {     // Considerando che la ricarica periodica consiste di 500 unità ogni settimana, calcolo il numero di settimane che ci vorrebbero per raggiungere la quantità di farmaci richiesta e ancora mancante
                    int settimaneAttesaStimate = (qty - settimane*500)/500 + 1;     // Ottenere le settimane di attesa necessarie
                    newDataConsegna = newDataConsegna.plusDays(settimaneAttesaStimate*7); // Aggiornare la nuova data di consegna
                }
            }
        }
        
        order.setAlertScadenza(true);
        return newDataConsegna;
    }
}
