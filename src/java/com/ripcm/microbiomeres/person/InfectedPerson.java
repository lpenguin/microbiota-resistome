package com.ripcm.microbiomeres.person;

import com.ripcm.microbiomeres.Simulation;
import com.ripcm.microbiomeres.Utils;

/**
 describes class of isInfected peaple
 */

public abstract class InfectedPerson extends Person {

    public InfectedPerson(int id, double[] micResistance, boolean[] isResistant, int incubPeriod) {
        super(id, micResistance);
        this.isResistant = isResistant;
        this.incubPeriod = incubPeriod;
    }
    public int incubPeriod;
    public boolean[] isResistant; //TRUE - resistant pathogen to i-antibiotic, FALSE -non resistant pathogen
    public int getIncubPeriod() {return this.incubPeriod;}

    public void changePathResistance(double[] micRes,double[] coefficient) { //isRes - local variable for isResistant
        for (int i = 0; i < Simulation.N_ANT; i++) {
            if (!isResistant[i]) {
                isResistant[i] = Utils.bernoulli(micRes[i]*coefficient[i]); //(boolean)
            }
        }
    }
    public abstract void tick(Simulation simulation, double pInfected, double pHospit); //Is it needed???
}

