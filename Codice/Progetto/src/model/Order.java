package model;

import java.time.LocalDate;
import java.util.LinkedList;

public class Order {
    private int idOrdine;
    private String stato;
    private LocalDate dataConsegna = null;
    private int refCorriere;
    private int refFarmacia;
    private LinkedList<Medicine> medicines = new LinkedList<>();
    private boolean alertScadenza = false;

    public Order() {}
    public Order(int idOrdine, String stato, LocalDate dataConsegna, int refCorriere, int refFarmacia, LinkedList<Medicine> medicines, boolean alertScadenza) {
        this.setIdOrdine(idOrdine);
        this.setStato(stato);
        this.setDataConsegna(dataConsegna);
        this.setRefCorriere(refCorriere);
        this.setRefFarmacia(refFarmacia);
        this.setMedicines(medicines);
        this.setAlertScadenza(alertScadenza);
    }
    public Order(String stato, LocalDate dataConsegna, int refCorriere, int refFarmacia, LinkedList<Medicine> medicines, boolean alertScadenza) {
        this.setStato(stato);
        this.setDataConsegna(dataConsegna);
        this.setRefCorriere(refCorriere);
        this.setRefFarmacia(refFarmacia);
        this.setMedicines(medicines);
        this.setAlertScadenza(alertScadenza);
    }

    public void setAlertScadenza(boolean alertScadenza) {
        this.alertScadenza = alertScadenza;
    }

    public void setDataConsegna(LocalDate dataConsegna) {
        this.dataConsegna = dataConsegna;
    }

    public void setMedicines(LinkedList<Medicine> medicines) {
        this.medicines = medicines;
    }

    public void setRefCorriere(int refCorriere) {
        this.refCorriere = refCorriere;
    }

    public void setRefFarmacia(int refFarmacia) {
        this.refFarmacia = refFarmacia;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public void setIdOrdine(int idOrdine) {
        this.idOrdine = idOrdine;
    }

    public boolean getAlertScadenza() {
        return this.alertScadenza;
    }

    public LocalDate getDataConsegna() {
        return this.dataConsegna;
    }

    public LinkedList<Medicine> getMedicines() {
        return this.medicines;
    }

    public int getRefCorriere() {
        return this.refCorriere;
    }

    public int getRefFarmacia() {
        return this.refFarmacia;
    }

    public String getStato() {
        return this.stato;
    }

    public int getIdOrdine() {
        return this.idOrdine;
    }
}
