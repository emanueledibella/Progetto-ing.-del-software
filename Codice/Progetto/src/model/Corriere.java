package model;

public class Corriere extends User {
    private int idCorriere;

    public Corriere(int idCorriere) {
        super.setSession("Farmacista");
        this.idCorriere = idCorriere;
    }

    public int getIdCorriere() {
        return this.idCorriere;
    }
}
