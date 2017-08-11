package com.ripcm.microbiomeres.log;

import java.io.IOException;

public interface LogWriter {
    void writeMessage(String message) throws IOException;

    void close() throws IOException;
}
