package model;

public class Farmacista extends User {
    private int refFarmacia;

    public Farmacista(int refFarmacia) {
        super.setSession("Farmacista");
        this.refFarmacia = refFarmacia;
    }

    public int getRefFarmacia() {
        return this.refFarmacia;
    }
}
