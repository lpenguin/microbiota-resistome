package com.ripcm.microbiomeres;

import com.ripcm.microbiomeres.log.*;
import com.ripcm.microbiomeres.person.ModelValues;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.util.Properties;
import java.io.*;
/**
 * main program class, which run simulation
 */

public class Main {
    public static void main(String[] args) throws IOException {
        CommandLineArgs cliArgs = getCommandLineArgs(args);
        if (cliArgs == null) return;

        readProperties(cliArgs.propertiesFile);
        runSimulation(cliArgs);

    }

    private static CommandLineArgs getCommandLineArgs(String[] args) {
//        for (int i = 0; i < args.length; i++) {
//            System.out.println("Argument "+(i+1)+" = "+args[i]);
//        }

        CommandLineArgs cliArgs = new CommandLineArgs();
        CmdLineParser parser = new CmdLineParser(cliArgs);

        try{
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            return null;
        }
        return cliArgs;
    }

    private static void readProperties(String propertiesFile) {
        FileInputStream fis;
        Properties property = new Properties();

        try {
            fis = new FileInputStream(propertiesFile);
            property.load(fis);

            ModelValues.N_INCUB_LIMIT = Integer.valueOf(property.getProperty("N_INCUB_LIMIT"));
            //ModelValues.N_INCUB_LIMIT_RESIST = Integer.valueOf(property.getProperty("N_INCUB_LIMIT_RESIST"));
            /*ModelValues.N_INFECTED_PEOPLE_PER_YEAR = Integer.valueOf(property.getProperty("N_INFECTED_PEOPLE_PER_YEAR"));
            ModelValues.N_PEOPLE_IN_COUNTRY = Double.valueOf(property.getProperty("N_PEOPLE_IN_COUNTRY"));*/
            ModelValues.C_INFECTED_COEF = Double.valueOf(property.getProperty("C_INFECTED_COEF"));
            ModelValues.C_PATHOGEN_RESIST_CHANGE_COEF = Double.valueOf(property.getProperty("C_PATHOGEN_RESIST_CHANGE_COEF"));
            ModelValues.P_INCUB_TO_HOSPITAL = Double.valueOf(property.getProperty("P_INCUB_TO_HOSPITAL"));
            ModelValues.P_WRONG_TREATMENT = Double.valueOf(property.getProperty("P_WRONG_TREATMENT"));
            ModelValues.P_BE_INFECTED_IN_HOSPITAL = Double.valueOf(property.getProperty("P_BE_INFECTED_IN_HOSPITAL"));
            ModelValues.C_GROWTH_COEF = Double.valueOf(property.getProperty("C_GROWTH_COEF"));
            ModelValues.C_DECREASE_COEF = Double.valueOf(property.getProperty("C_DECREASE_COEF"));
            ModelValues.P_HEALTHY_HOSPITALIZE = Double.valueOf(property.getProperty("P_HEALTHY_HOSPITALIZE"));

        } catch (IOException e) {
            System.err.println("File not found "+ propertiesFile);
            e.printStackTrace();
        }
    }

    private static void runSimulation(CommandLineArgs cliArgs) throws IOException {
        LogWriter transLogWriter = cliArgs.transitionLogFile != null ? new FileLogWriter(cliArgs.transitionLogFile) : new DummyLogWriter();
        LogWriter personAmountWriter = cliArgs.personAmountLogFile != null ? new FileLogWriter(cliArgs.personAmountLogFile) : new DummyLogWriter();
        LogWriter messagesWriter = cliArgs.quiet ? new DummyLogWriter() : new StdoutLogWriter();

        new Simulation(
                cliArgs.iterationsCount,
                new PersonAmountLogger(personAmountWriter),
                new TransitionLogger(transLogWriter),
                messagesWriter
        );

        transLogWriter.close();
        personAmountWriter.close();
        messagesWriter.close();
    }
}