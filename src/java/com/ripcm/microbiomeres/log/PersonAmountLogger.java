package com.ripcm.microbiomeres.log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.ripcm.microbiomeres.Simulation;
import java.util.stream.IntStream;

/**
 * Created by oksana on 6/28/17.
 */
public class PersonAmountLogger {
    private LogWriter logWriter;

    public PersonAmountLogger(LogWriter logWriter, int nAnt) throws IOException {
        this.logWriter = logWriter;
        int[] arr = IntStream.rangeClosed(1, nAnt).toArray();
        String result1 = String.join("\t","AvMicResistance"+Arrays.toString(arr));
        String result2 = String.join("\t","AvPathResistance"+Arrays.toString(arr));
        this.logWriter.writeMessage("Ticks\tHealthyPersonsInTown\tInfectedPersonsInTown\tIncPeriodPersonsInTown\tIncPeriodPersonsInTown2\tAntibioticTreatedPersonsInTown\tAntibioticTreatedPersonsInTown2\tInfectedPersonsInHospital\tHealthyPersonsInHospital\tpGetInfectedTown\t"+result1+"\t"+result2);
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
