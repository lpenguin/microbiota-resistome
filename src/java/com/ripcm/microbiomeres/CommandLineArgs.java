package com.ripcm.microbiomeres;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class CommandLineArgs {
    @Argument(index = 0, required = true)
    public int iterationsCount;

    @Argument(index = 1, required = true)
    public String personAmountLogFile;

    @Argument(index = 2, required = true)
    public String propertiesFile;

    @Argument(index = 3)
    public String transitionLogFile;

    @Option(name = "-quiet")
    public boolean quiet = false;
}
