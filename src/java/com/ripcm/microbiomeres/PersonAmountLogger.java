package com.ripcm.microbiomeres;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by oksana on 6/28/17.
 */
public class PersonAmountLogger {
    private PrintWriter writeFile = null;

    public PersonAmountLogger(String fileN) throws FileNotFoundException {
        if (fileN!=null) {
            writeFile = openPersonAmountLogFile(fileN);
        }
    }

    private PrintWriter openPersonAmountLogFile(String fileN) throws FileNotFoundException { // old - openResultsFile
        PrintWriter writeFile = new PrintWriter(fileN);
        String st = "Ticks HealthyPersonsInTown InfectedPersonsInTown IncPeriodPersonsInTown IncPeriodPersonsInTown2 AntibioticTreatedPersonsInTown AntibioticTreatedPersonsInTown2 InfectedPersonsInHospital HealthyPersonsInHospital pGetInfectedTown AvMicResistance AvPathResistance ";
        writeFile.println(st);
        return writeFile;
    }

    public void WritePersonAmountLogFile(List<Number> l){//, PrintWriter writeFile) { // old - WriteToFile
        String st = "";
        for (Number n : l) {
            st += n + " ";
        }
        writeFile.println(st.trim());
        writeFile.flush();
    }
}
