package com.ripcm.microbiomeres.person;

import com.ripcm.microbiomeres.Simulation;
import com.ripcm.microbiomeres.Utils;

/**
 * Created by anna on 22.04.16.
 */
public class AntTreatedPerson extends InfectedPerson {
    //constructor
    public AntTreatedPerson (int id, double[] micResistance, boolean[] isResistant, int treatmentPeriod, double pGetToHosp, int incubPeriod, int antibioticType, boolean incrResist){
        super(id, micResistance,isResistant, incubPeriod);
        this.treatmentPeriod = treatmentPeriod;
        this.pGetToHosp = pGetToHosp;
        this.antibioticType=antibioticType;
    }
    public int antibioticType;
    public int treatmentPeriod;
    public double pGetToHosp;
    public boolean beHospitalized =false;
    public boolean incrResist;
    public void tick(Simulation simulation,double pInfect, double pBeHospitalized){
        beHospitalized = Utils.bernoulli(pBeHospitalized);
        treatmentPeriod -= 1;
        //TODO: we dont use incubPeriod in hospTreatment
        if (incubPeriod !=0) { //??? WTF: it's can't be
            incubPeriod -= 1;}
        boolean tmp = isResistant[antibioticType];
        changePathResistance(micResistance, ModelValues.C_PATHOGEN_RESIST_CHANGE_COEF);
        if(!tmp && isResistant[antibioticType]) { //??? WTF: it's can't be, it's same pathogen, incub period mustn't be
            incubPeriod = Simulation.getN_incLimit2();} //if pathogene becomes resistant the countdown begins
        if(incrResist){ // now it isn't needed !
            if(micResistance[antibioticType] !=1){
                micResistance[antibioticType] = micResistance[antibioticType] + ModelValues.C_GROWTH_COEF[antibioticType];
                if(micResistance[antibioticType] > 1) {micResistance[antibioticType] = 1;}
            }
        } else
            for (int i = 0; i < simulation.N_ANT; i++){
                if(micResistance[i] !=0){ //it's needed for HospAntreatedPersones so as resistance should decreases, because in hospital people are treated with another antibiotic!
                    micResistance[i] -= ModelValues.C_DECREASE_COEF[i];
                    if(micResistance[i] <ModelValues.PERM_RESIST_LEVEL[i]) {micResistance[i] = ModelValues.PERM_RESIST_LEVEL[i];}
                }
            }
    }
}