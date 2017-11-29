package com.ripcm.microbiomeres.log;

import java.io.*;

/**
 * Created by oksana on 6/28/17.
 */
public class TransitionLogger {
    private LogWriter logWriter;

    public TransitionLogger(LogWriter logWriter) throws IOException {
        this.logWriter = logWriter;
        this.logWriter.writeMessage("Ticks\tPersonId\tTransFromClass\tTransToClass");
    }

    public void writeToTransLogFile(int ticks, String personId, String fromClass, String toClass) throws IOException {
        logWriter.writeMessage(String.format("%d\tid_%s\t%s\t%s", ticks, personId, fromClass, toClass));
    }
}
