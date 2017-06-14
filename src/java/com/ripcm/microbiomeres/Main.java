package com.ripcm.microbiomeres;

import com.ripcm.microbiomeres.person.ModelValues;

import java.io.FileNotFoundException;
import java.util.Properties;
import java.io.*;
/**
 * main program class, which run simulation
 */

public class Main {


    public static void main(String[] args) {

        for (int i = 0; i < args.length; i++) {
            System.out.println("Argument "+(i+1)+" = "+args[i]);
        }

        int iterationNum = Integer.parseInt(args[0]);
        //String outDir = "out/simulations/";
        String fileName = args[1];

        FileInputStream fis;
        Properties property = new Properties();

        try {
            fis = new FileInputStream(args[2]);
            property.load(fis);

            ModelValues.N_INCUB_LIMIT = Integer.valueOf(property.getProperty("N_INCUB_LIMIT"));
            ModelValues.N_INCUB_LIMIT_RESIST = Integer.valueOf(property.getProperty("N_INCUB_LIMIT_RESIST"));
            ModelValues.N_INFECTED_PEOPLE_PER_YEAR = Integer.valueOf(property.getProperty("N_INFECTED_PEOPLE_PER_YEAR"));
            ModelValues.N_PEOPLE_IN_COUNTRY = Double.valueOf(property.getProperty("N_PEOPLE_IN_COUNTRY"));
            ModelValues.C_PATHOGEN_RESIST_CHANGE_COEF = Double.valueOf(property.getProperty("C_PATHOGEN_RESIST_CHANGE_COEF"));
            ModelValues.P_INCUB_TO_HOSPITAL = Double.valueOf(property.getProperty("P_INCUB_TO_HOSPITAL"));
            ModelValues.P_WRONG_TREATMENT = Double.valueOf(property.getProperty("P_WRONG_TREATMENT"));
            ModelValues.P_BE_INFECTED_IN_HOSPITAL = Double.valueOf(property.getProperty("P_BE_INFECTED_IN_HOSPITAL"));
            ModelValues.C_GROWTH_COEF = Double.valueOf(property.getProperty("C_GROWTH_COEF"));
            ModelValues.C_DECREASE_COEF = Double.valueOf(property.getProperty("C_DECREASE_COEF"));

/*          System.out.print("C_INFECTED_COEF = "+Integer.valueOf(property.getProperty("N_INFECTED_PEOPLE_PER_YEAR"))+" * "+Math.pow(10,2)+" / "+
                    Double.valueOf(property.getProperty("N_PEOPLE_IN_COUNTRY"))+" * "+Math.pow(10,6)+"\n");
            System.out.print("C_INFECTED_COEF = "+Integer.valueOf(property.getProperty("N_INFECTED_PEOPLE_PER_YEAR"))*Math.pow(10,2)+" / "+
                    Double.valueOf(property.getProperty("N_PEOPLE_IN_COUNTRY"))*Math.pow(10,6)+"\n");
            System.out.print("C_INFECTED_COEF = "+Integer.valueOf(property.getProperty("N_INFECTED_PEOPLE_PER_YEAR"))*Math.pow(10,2)/
                    (Double.valueOf(property.getProperty("N_PEOPLE_IN_COUNTRY"))*Math.pow(10,6))+"\n");*/



        } catch (IOException e) {
            System.err.println("File not found "+args[2]);
        }

        if (args.length > 3){
            String logTransFileName = args[3];
            try{
                new Simulation(iterationNum, fileName, logTransFileName);
            } catch (FileNotFoundException e){
                System.out.println("File not found "+fileName+" or "+logTransFileName);
            }
        } else{
            try {
                new Simulation(iterationNum, fileName, "");
            } catch (FileNotFoundException e) {
                System.out.println("File not found "+fileName);
                //e.printStackTrace();
            }
        }
//  //      SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//  //              JFrame frame = new JFrame();
//                frame.add(new MyComponentFromVera());
//  //              frame.add(new Rectangle()); //mooving rectangle
//                frame.pack();
//  //              frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                frame.setLocationRelativeTo(null); //?
//                frame.setVisible(true);   //?
//            }
//        });
    }
}