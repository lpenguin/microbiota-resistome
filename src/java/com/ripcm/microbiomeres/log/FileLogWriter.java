package com.ripcm.microbiomeres.log;

import java.io.*;

public class FileLogWriter implements LogWriter {
    private Writer writer;

    public FileLogWriter(String fileName) throws IOException {
        writer = openLogFile(fileName);
    }

    private Writer openLogFile(String fName) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fName), "utf-8"));
    }

    @Override
    public void writeMessage(String message) throws IOException {
        writer.write(message.trim());
        writer.write("\n");
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
