package com.ripcm.microbiomeres.log;

import java.io.IOException;

public class StdoutLogWriter implements LogWriter{
    @Override
    public void writeMessage(String message) throws IOException {
        System.out.println(message);
    }

    @Override
    public void close() throws IOException {

    }
}
