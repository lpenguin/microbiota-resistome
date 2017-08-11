package com.ripcm.microbiomeres.log;

import java.io.*;

/**
 * Created by oksana on 6/28/17.
 */
public class TransitionLogger {
    private LogWriter logWriter;

    public TransitionLogger(LogWriter logWriter) throws IOException {
        this.logWriter = logWriter;
        this.logWriter.writeMessage("Ticks PersonId TransFromClass TransToClass");
    }

    public void writeToTransLogFile(int ticks, String personId, String fromClass, String toClass) throws IOException {
        logWriter.writeMessage(String.format("%d id_%s %s %s", ticks, personId, fromClass, toClass));
    }
}
