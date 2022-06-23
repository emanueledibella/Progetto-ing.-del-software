package model;

public class User {
    private String tipologiaUtente;

    public void setSession(String tipologiaUtente) {
        this.tipologiaUtente = tipologiaUtente;
    }

    public void destroySession() {}

    public String getTipologiaUtente() {
        return this.tipologiaUtente;
    }
}
