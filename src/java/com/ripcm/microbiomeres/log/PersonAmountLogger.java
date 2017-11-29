package com.ripcm.microbiomeres.log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oksana on 6/28/17.
 */
public class PersonAmountLogger {
    private LogWriter logWriter;

    public PersonAmountLogger(LogWriter logWriter) throws IOException {
        this.logWriter = logWriter;
        this.logWriter.writeMessage("Ticks\tHealthyPersonsInTown\tInfectedPersonsInTown\tIncPeriodPersonsInTown\tIncPeriodPersonsInTown2\tAntibioticTreatedPersonsInTown\tAntibioticTreatedPersonsInTown2\tInfectedPersonsInHospital\tHealthyPersonsInHospital\tpGetInfectedTown\tAvMicResistance\tAvPathResistance");
    }

    public void WritePersonAmountLogFile(List<Number> stats) throws IOException {//, PrintWriter writeFile) { // old - WriteToFile
        StringBuilder builder = new StringBuilder();

        for (Number n : stats) {
            builder.append(n);
            builder.append("\t");
        }
        logWriter.writeMessage(builder.toString());
    }
}
