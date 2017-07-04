package com.ripcm.microbiomeres;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by oksana on 6/28/17.
 */
public class TransitionLogger {
    private PrintWriter writeFile = null;

    public TransitionLogger(String fileN) throws FileNotFoundException {
        if (fileN!=null) {
            writeFile = openTransLogFile(fileN);
        }
    }

    private PrintWriter openTransLogFile(String fName) throws FileNotFoundException {
        PrintWriter writeFile = new PrintWriter(fName);
        String st = "Ticks PersonId TransFromClass TransToClass";
        writeFile.println(st);
        return writeFile;
    }

    public void writeToTransLogFile(String st) {
        if (writeFile!=null){
            writeFile.println(st.trim());
            writeFile.flush();
        }

    }


}
