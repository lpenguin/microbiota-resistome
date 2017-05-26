package com.ripcm.microbiomeres.person;

import com.ripcm.microbiomeres.Simulation;
import com.ripcm.microbiomeres.Utils;

/**
 * Created by anna on 20.04.16.
 */

//TODO: read coefficient for probability to be infected from initial conditions
public class HealthyPerson extends Person {

    //constructor
    public HealthyPerson (double micResistance){
        super(micResistance);
    }
    public boolean toBeChanged = false; // probability that agent will be infected
    public boolean toBeHospitalized = false; // probability that agent will be hospitalized with other illness
    public boolean isResistant; // probability that pathogen will be resistant
    //public boolean getToBeChanged(){return toBeChanged;}

    // marking a person to be infected
    public void tick(Simulation simulation, double pInfected, double decreaseCoef, double pHospitalize){
        if(micResistance !=0){
            micResistance = micResistance - decreaseCoef;
            if(micResistance < 0) {micResistance =0;}
        }
        toBeChanged = Utils.bernoulli(pInfected);
        if (toBeChanged){
            isResistant = Utils.bernoulli(simulation.getFixAvPathResist());
        } else { toBeHospitalized = Utils.bernoulli(pHospitalize);}
    }
}