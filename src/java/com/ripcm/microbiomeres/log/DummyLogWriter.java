package com.ripcm.microbiomeres.log;

import java.io.IOException;

public class DummyLogWriter implements LogWriter {
    @Override
    public void writeMessage(String message) throws IOException {
        // noop
    }

    @Override
    public void close() throws IOException {
        // noop
    }
}
