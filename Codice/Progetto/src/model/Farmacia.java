package model;

public class Farmacia {
    private int idFarmacia;
    private String nome;
    private String numeroTelefono;
    private String indirizzo;

    public Farmacia(int idFarmacia, String nome, String numeroTelefono, String indirizzo) {
        setIdFarmacia(idFarmacia);
        setNome(nome);
        setNumeroTelefono(numeroTelefono);
        setIndirizzo(indirizzo);
    }
    public Farmacia(String nome, String numeroTelefono, String indirizzo) {
        setNome(nome);
        setNumeroTelefono(numeroTelefono);
        setIndirizzo(indirizzo);
    }

    public void setIdFarmacia(int idFarmacia) {
        this.idFarmacia = idFarmacia;
    }
    public int getIdFarmacia() {
        return this.idFarmacia;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getNome() {
        return this.nome;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }
    public String getNumeroTelefono() {
        return this.numeroTelefono;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }
    public String getIndirizzo() {
        return this.indirizzo;
    }
}
