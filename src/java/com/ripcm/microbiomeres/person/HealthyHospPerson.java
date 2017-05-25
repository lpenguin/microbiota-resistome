package com.ripcm.microbiomeres.person;

import com.ripcm.microbiomeres.Simulation;
import com.ripcm.microbiomeres.Utils;

/**
 * Created by anna on 05.12.16.
 */
public class HealthyHospPerson extends Person {
    //constructor
    public HealthyHospPerson (double micResistance, int trCountdown){
        super(micResistance);
        this.trCountdown = trCountdown;
    }
    public int trCountdown;
    public boolean infected = false;
    public boolean pRes;
    public void tick(Simulation myComp, double pInfHosp, double decreaseCoef, double pResistant){
        if(micResistance !=0){
            micResistance = micResistance - decreaseCoef;
            if(micResistance < 0) {micResistance = 0;}
        }
        if(trCountdown >0) trCountdown = trCountdown-1;
        else {
            infected = Utils.bernoulli(pInfHosp);
            if(infected) pRes = Utils.bernoulli(pResistant);
        }
    }
}
