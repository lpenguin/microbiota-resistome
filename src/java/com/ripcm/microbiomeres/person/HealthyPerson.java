package com.ripcm.microbiomeres.person;

import com.ripcm.microbiomeres.Simulation;
import com.ripcm.microbiomeres.Utils;

/**
 * Created by anna on 20.04.16.
 */

//TODO: read coefficient for probability to be isInfected from initial conditions
public class HealthyPerson extends Person {

    //constructor
    public HealthyPerson (int id, double[] micResistance){

        super(id, micResistance);
        isResistant = new boolean[micResistance.length];
    }
    public boolean toBeChanged = false; // probability that agent will be isInfected
    public boolean toBeHospitalized = false; // probability that agent will be hospitalized with other illness
    public boolean[] isResistant; // probability that pathogen will be resistant
    //public boolean getToBeChanged(){return toBeChanged;}

    // marking a person to be isInfected
    public void tick(Simulation simulation, double pInfected, double pHospitalize){
        for (int i = 0; i < simulation.N_ANT; i++) {
            if(micResistance[i] !=0){
                micResistance[i] = micResistance[i] - ModelValues.C_DECREASE_COEF[i];
                if(micResistance[i] < ModelValues.PERM_RESIST_LEVEL[i]) {micResistance[i] =ModelValues.PERM_RESIST_LEVEL[i];}
            }
        }

        toBeChanged = Utils.bernoulli(pInfected);
        if (toBeChanged){
            for (int i = 0; i < simulation.N_ANT; i++) {
                isResistant[i] = Utils.bernoulli(simulation.getFixAvPathResistTown()[i]);
            }
        } else { toBeHospitalized = Utils.bernoulli(pHospitalize);}
    }
}