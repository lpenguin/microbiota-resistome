package com.ripcm.microbiomeresistom.person;

import com.ripcm.microbiomeresistom.Simulation;
import com.ripcm.microbiomeresistom.Utils;

/**
 * Created by anna on 22.04.16.
 */
public class AntTreatedPerson extends InfectedPerson {
    //constructor
    public AntTreatedPerson (double micResistance, boolean pathResistance, int treatment_countdown, double pGetToHosp, int incCountdown){
        super(micResistance,pathResistance, incCountdown);
        this.treatment_countdown = treatment_countdown;
        this.pGetToHosp = pGetToHosp;
    }

    public int treatment_countdown;
    public double pGetToHosp;
    public boolean hospitalize=false;
    public void tick(Simulation myComp, double p, double growthCoef, double coefficient){
        hospitalize = Utils.bernoulli(p);
        treatment_countdown = treatment_countdown -1;
        if (incCountdown !=0) {incCountdown = incCountdown-1;}
        boolean tmp = pathResistance;
        pathResistance = changePathResistance(pathResistance,micResistance, coefficient);
        if(tmp == false & pathResistance == true) {incCountdown = Simulation.getN_incLimit2();} //if pathogene becomes resistant the countdown begins
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