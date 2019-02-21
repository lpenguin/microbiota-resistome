package com.ripcm.microbiomeres.person;

import com.ripcm.microbiomeres.Simulation;

/**
 * Created by anna on 20.04.16.
 */
public class IncPeriodPerson extends InfectedPerson{

    //Constructor
    public IncPeriodPerson (int id, double[] micResistance,boolean[] isResistant, int incubPeriod){
        super(id, micResistance,isResistant,incubPeriod);
    }


    public void tick(Simulation simulation, double pInfected, double pHospit){
        incubPeriod -= 1;
        changePathResistance(micResistance, ModelValues.C_PATHOGEN_RESIST_CHANGE_COEF);
        for (int i = 0; i < simulation.N_ANT; i++) {
            if(micResistance[i] !=0){
                micResistance[i] = micResistance[i] - ModelValues.C_DECREASE_COEF[i];
                if(micResistance[i] < ModelValues.PERM_RESIST_LEVEL[i]) {micResistance[i] =ModelValues.PERM_RESIST_LEVEL[i];}
            }
        }
    }
}