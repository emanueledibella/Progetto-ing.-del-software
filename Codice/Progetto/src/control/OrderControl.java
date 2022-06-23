package control;

import java.time.Duration;
import java.time.LocalDate;
import java.util.LinkedList;

import jdbc.DBAziendaManager;
import model.Medicine;
import model.Order;

public class OrderControl {
    private Order order = new Order();
    public DBAziendaManager dbAziendaManager = new DBAziendaManager();

    public Order getOrder() {
        return this.order;
    }

    public void order() {
        Order orderDB = new Order();
        orderDB.setRefFarmacia(order.getRefFarmacia());

        // Assegnare l'ordine ad un Corriere random
        LinkedList<Integer> idCorrieri = dbAziendaManager.getIdCorrieri();
        int idCorriereRandom = (int)(Math.random() * idCorrieri.size()) + 1;
        orderDB.setRefCorriere(idCorriereRandom); 

        // Impostare lo stato dell'ordine in "da consegnare"
        orderDB.setStato("da consegnare");

        
        LocalDate newDataConsegna = order.getDataConsegna();   // Oggetto che verrà aggiornato con la nuova data di consegna
        LocalDate now = LocalDate.now();
        LocalDate twoMonthsFromNow = now.plusMonths(2);
        LinkedList<Medicine> meds = dbAziendaManager.getMedicinesByDeadline();
        boolean scorteInsufficienti = false;
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
                scorteInsufficienti = true;
                Duration diff = Duration.between(order.getDataConsegna().atStartOfDay(), newDataConsegna.atStartOfDay());
                int settimane = (int)(diff.toDays()/7);   // Considerando che la ricarica periodica avviene ogni settimana, esprimo la differenza in settimane

                if((qty - settimane*500) > 0) {     // Considerando che la ricarica periodica consiste di 500 unità ogni settimana, calcolo il numero di settimane che ci vorrebbero per raggiungere la quantità di farmaci richiesta e ancora mancante
                    int settimaneAttesaStimate = (qty - settimane*500)/500 + 1;     // Ottenere le settimane di attesa necessarie
                    newDataConsegna = newDataConsegna.plusDays(settimaneAttesaStimate*7); // Aggiornare la nuova data di consegna
                }
            }
        }

        if(scorteInsufficienti) {
            // TODO: Messaggio in cui si chiede al Farmacista se vuole spostare la data dell'ordine alla data NewDataConsegna
        }
        orderDB.setDataConsegna(newDataConsegna);

        dbAziendaManager.order(orderDB);
    }
}
