package com.ripcm.microbiomeres.person;

import com.ripcm.microbiomeres.Simulation;
import com.ripcm.microbiomeres.Utils;

/**
 describes class of infected peaple
 */

public abstract class InfectedPerson extends Person {

    public InfectedPerson(double micResistance, boolean isResistant, int incubPeriod) {
        super(micResistance);
        this.isResistant = isResistant;
        this.incubPeriod = incubPeriod;
    }
    public int incubPeriod;
    public boolean isResistant; //TRUE - resistant pathogen, FALSE -non resistant pathogen
    public int getIncubPeriod() {return this.incubPeriod;}

    public boolean changePathResistance(boolean isRes, double micRes,double coefficient) { //isRes - local variable for isResistant
        if (isRes == false){
            isRes = Utils.bernoulli(micRes*coefficient); //(boolean)
        }
        return isRes;
    }
    public abstract void tick(Simulation simulation, double p, double coef, double changePathResCoef); //Is it needed???
}

