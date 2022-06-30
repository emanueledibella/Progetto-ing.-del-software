package model;

import java.sql.Date;
import java.util.Iterator;
import java.util.LinkedList;

public class Delivery {
    private int idOrdine;
    private Date dataConsegna;
    private String nomeFarmacia;
    private String indirizzo;
    private String numeroTelefono;
    private StringBuilder meds;

    public Delivery(int idOrdine, Date dataConsegna,String nomeFarmacia, String indirizzo, String numeroTelefono, LinkedList<String> meds){
        this.idOrdine = idOrdine;
        this.dataConsegna=dataConsegna;
        this.nomeFarmacia=nomeFarmacia;
        this.indirizzo=indirizzo;
        this.numeroTelefono=numeroTelefono;
        this.meds=toStringBuilder(meds);
    }

    public int getIdOrdine(){
        return this.idOrdine;
    }
    
    public Date getDataConsegna(){
        return this.dataConsegna;
    }

    public String getNomeFarmacia(){
        return this.nomeFarmacia;
    }
    
    public String getIndirizzo(){
    return this.indirizzo;
    }

    public String getNumeroTelefono(){
        return this.numeroTelefono;
    }

    public StringBuilder getMeds(){
        return this.meds;
    }

    private StringBuilder toStringBuilder(LinkedList<String> list){
        Iterator<String> i = list.iterator();
        StringBuilder sb = new StringBuilder();
        while (i.hasNext()) {
            sb.append(i.next());
            if (i.hasNext()) {
                sb.append(", ");
            }
        }
        return sb;
    }
}