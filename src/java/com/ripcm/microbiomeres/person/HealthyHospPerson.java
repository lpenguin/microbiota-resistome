package com.ripcm.microbiomeres.person;

import com.ripcm.microbiomeres.Simulation;
import com.ripcm.microbiomeres.Utils;

/**
 * Created by anna on 05.12.16.
 */
public class HealthyHospPerson extends Person {
    //constructor
    public HealthyHospPerson (String id, double micResistance, int treatmentPeriod){
        super(id, micResistance);
        this.treatmentPeriod = treatmentPeriod;
    }
    public int treatmentPeriod;
    public boolean isInfected = false;
    public boolean isResistant; //TRUE - resistant pathogen, FALSE -non resistant pathogen
    //public boolean pRes;
    public void tick(Simulation simulation, double pBeInfectedInHospital, double decreaseCoef, double pResistant){
        if(micResistance !=0){
            micResistance = micResistance - decreaseCoef;
            if(micResistance < ModelValues.PERM_RESIST_LEVEL) {micResistance = ModelValues.PERM_RESIST_LEVEL;}
        }
        if(treatmentPeriod >0) treatmentPeriod = treatmentPeriod -1;
        else {
            isInfected = Utils.bernoulli(pBeInfectedInHospital);
            if(isInfected)  isResistant = Utils.bernoulli(pResistant);
        }
    }
}
