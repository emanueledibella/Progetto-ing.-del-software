package control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import jdbc.DBAziendaManager;

public class PeriodicUpdateControl {
    private DBAziendaManager dbAziendaManager = new DBAziendaManager();

    public void initPeriodicUpdate() {
        File file = new File("../periodicUpdateDate.txt");

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
                // Lettura dal file dell'ultima data in cui è stata effettuata una ricarica periodica
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                LocalDate periodicUpdateDate = LocalDate.parse(bufferedReader.readLine());
                bufferedReader.close();

                // Calcolo della differenza fra la data in cui è stata effettuata l'ultima ricarica periodica e la data attuale
                LocalDate now = LocalDate.now();
                Duration diff = Duration.between(periodicUpdateDate.atStartOfDay(), now.atStartOfDay());

                // Eventuale incremento delle disponibilità dei farmaci in magazzino
                if(diff.toDays() >= 7) {
                    dbAziendaManager.updateAllQty(500);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
