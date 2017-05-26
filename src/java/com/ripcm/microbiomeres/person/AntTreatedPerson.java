package com.ripcm.microbiomeres.person;

import com.ripcm.microbiomeres.Simulation;
import com.ripcm.microbiomeres.Utils;

/**
 * Created by anna on 22.04.16.
 */
public class AntTreatedPerson extends InfectedPerson {
    //constructor
    public AntTreatedPerson (double micResistance, boolean pathResistance, int treatmentCountdown, double pGetToHosp, int incCountdown){
        super(micResistance,pathResistance, incCountdown);
        this.treatmentCountdown = treatmentCountdown;
        this.pGetToHosp = pGetToHosp;
    }

    public int treatmentCountdown;
    public double pGetToHosp;
    public boolean hospitalize=false;
    public void tick(Simulation myComp, double p, double growthCoef, double coefficient){
        hospitalize = Utils.bernoulli(p);
        treatmentCountdown -= 1;
        if (incubPeriod !=0) {
            incubPeriod = incubPeriod -1;}
        boolean tmp = isResistant;
        isResistant = changePathResistance(isResistant,micResistance, coefficient);
        if(tmp == false & isResistant == true) {
            incubPeriod = Simulation.getN_incLimit2();} //if pathogene becomes resistant the countdown begins
        if(growthCoef > 0){
            if(micResistance !=1){
                micResistance = micResistance + growthCoef;
                if(micResistance > 1) {micResistance = 1;}
            }
        } else if(micResistance !=0){
            micResistance = micResistance + growthCoef;
            if(micResistance <0) {micResistance = 0;}
        }
    }
}