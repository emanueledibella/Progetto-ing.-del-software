package model;

import java.sql.Date;

public class Medicine {
    private int idFarmaco;
    private String nome;
    private String principioAttivo;
    private Date dataScadenza;
    private int disponibilita;
    private boolean daBanco;

    public Medicine() {}
    public Medicine(int idFarmaco, String nome, String principioAttivo, Date dataScadenza, int disponibilita, boolean daBanco) {
        this.setIdFarmaco(idFarmaco);
        this.setNome(nome);
        this.setPrincipioAttivo(principioAttivo);
        this.setDataScadenza(dataScadenza);
        this.setDisponibilita(disponibilita);
        this.setDaBanco(daBanco);
    }

    public void setIdFarmaco(int idFarmaco) {
        this.idFarmaco = idFarmaco;
    }

    public int getIdFarmaco() {
        return this.idFarmaco;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return this.nome;
    }

    public void setPrincipioAttivo(String principioAttivo) {
        this.principioAttivo = principioAttivo;
    }

    public String getPrincipioAttivo() {
        return this.principioAttivo;
    }

    public void setDataScadenza(Date dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public Date getDataScadenza() {
        return this.dataScadenza;
    }

    public void setDisponibilita(int disponibilita) {
        this.disponibilita = disponibilita;
    }

    public int getDisponibilita() {
        return this.disponibilita;
    }

    public void setDaBanco(boolean daBanco) {
        this.daBanco = daBanco;
    }

    public boolean getDaBanco() {
        return this.daBanco;
    }
}
