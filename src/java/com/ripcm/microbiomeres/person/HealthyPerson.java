package com.ripcm.microbiomeres.person;

import com.ripcm.microbiomeres.Simulation;
import com.ripcm.microbiomeres.Utils;

/**
 * Created by anna on 20.04.16.
 */

//TODO: read coefficient for probability to be isInfected from initial conditions
public class HealthyPerson extends Person {

    //constructor
    public HealthyPerson (int id, double micResistance){
        super(id, micResistance);
    }
    public boolean toBeChanged = false; // probability that agent will be isInfected
    public boolean toBeHospitalized = false; // probability that agent will be hospitalized with other illness
    public boolean isResistant; // probability that pathogen will be resistant
    //public boolean getToBeChanged(){return toBeChanged;}

    // marking a person to be isInfected
    public void tick(Simulation simulation, double pInfected, double decreaseCoef, double pHospitalize){
        if(micResistance !=0){
            micResistance = micResistance - decreaseCoef;
            if(micResistance < ModelValues.PERM_RESIST_LEVEL) {micResistance =ModelValues.PERM_RESIST_LEVEL;}
        }
        toBeChanged = Utils.bernoulli(pInfected);
        if (toBeChanged){
            isResistant = Utils.bernoulli(simulation.getFixAvPathResistTown());
        } else { toBeHospitalized = Utils.bernoulli(pHospitalize);}
    }
}