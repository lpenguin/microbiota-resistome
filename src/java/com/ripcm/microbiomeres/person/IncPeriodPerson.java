package com.ripcm.microbiomeres.person;

import com.ripcm.microbiomeres.Simulation;

/**
 * Created by anna on 20.04.16.
 */
public class IncPeriodPerson extends InfectedPerson{

    //Constructor
    public IncPeriodPerson (double micResistance,boolean isResistant, int incubPeriod){
        super(micResistance,isResistant,incubPeriod);
    }


    public void tick(Simulation simulation, double p, double decreaseCoef, double coefficient){
        incubPeriod -= 1;
        changePathResistance(micResistance, coefficient);
        if(micResistance !=0) {
            micResistance = micResistance - decreaseCoef;
            if (micResistance < 0) {
                micResistance = 0;
            }
        }
    }
}