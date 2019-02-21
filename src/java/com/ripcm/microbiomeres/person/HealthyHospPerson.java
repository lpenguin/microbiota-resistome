package com.ripcm.microbiomeres.person;

import com.ripcm.microbiomeres.Simulation;
import com.ripcm.microbiomeres.Utils;

/**
 * Created by anna on 05.12.16.
 */
public class HealthyHospPerson extends Person {
    //constructor
    public HealthyHospPerson (int id, double[] micResistance, int treatmentPeriod){
        super(id, micResistance);
        this.treatmentPeriod = treatmentPeriod;
    }
    public int treatmentPeriod;
    public boolean isInfected = false;
    public boolean[] isResistant; //TRUE - resistant pathogen, FALSE -non resistant pathogen
    //public boolean pRes;
    public void tick(Simulation simulation, double pBeInfectedInHospital, double pHospitalized){
        for (int i = 0; i < simulation.N_ANT; i++){
            if(micResistance[i] !=0){
                micResistance[i] = micResistance[i] - ModelValues.C_DECREASE_COEF[i];
                if(micResistance[i] < ModelValues.PERM_RESIST_LEVEL[i]) {micResistance[i] = ModelValues.PERM_RESIST_LEVEL[i];}
            }
        }
        if(treatmentPeriod >0) treatmentPeriod = treatmentPeriod -1;
        else {
            isInfected = Utils.bernoulli(pBeInfectedInHospital);
            if(isInfected)  for (int i = 0; i < simulation.N_ANT; i++) isResistant[i] = Utils.bernoulli(simulation.getFixAvPathResistTown()[i]);
        }
    }
}
