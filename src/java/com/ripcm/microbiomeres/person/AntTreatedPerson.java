package com.ripcm.microbiomeres.person;

import com.ripcm.microbiomeres.Simulation;
import com.ripcm.microbiomeres.Utils;

/**
 * Created by anna on 22.04.16.
 */
public class AntTreatedPerson extends InfectedPerson {
    //constructor
    public AntTreatedPerson (String id, double micResistance, boolean isResistant, int treatmentPeriod, double pGetToHosp, int incubPeriod){
        super(id, micResistance,isResistant, incubPeriod);
        this.treatmentPeriod = treatmentPeriod;
        this.pGetToHosp = pGetToHosp;
    }

    public int treatmentPeriod;
    public double pGetToHosp;
    public boolean beHospitalized =false;
    public void tick(Simulation simulation, double pBeHospitalized, double growthCoef, double coefficient){
        beHospitalized = Utils.bernoulli(pBeHospitalized);
        treatmentPeriod -= 1;
        if (incubPeriod !=0) { //??? WTF: it's can't be
            incubPeriod -= 1;}
        boolean tmp = isResistant;
        changePathResistance(micResistance, coefficient);
        if(!tmp && isResistant) { //??? WTF: it's can't be, it's same pathogen, incub period mustn't be
            incubPeriod = Simulation.getN_incLimit2();} //if pathogene becomes resistant the countdown begins
        if(growthCoef > 0){ // now it isn't needed !
            if(micResistance !=1){
                micResistance = micResistance + growthCoef;
                if(micResistance > 1) {micResistance = 1;}
            }
        } else if(micResistance !=0){ //it's needed for HospAntreatedPersones so as resistance should decreases, because in hospital people are treated with another antibiotic!
            micResistance += growthCoef;
            if(micResistance <ModelValues.PERM_RESIST_LEVEL) {micResistance = ModelValues.PERM_RESIST_LEVEL;}
        }
    }
}