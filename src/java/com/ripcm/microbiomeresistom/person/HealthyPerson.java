package com.ripcm.microbiomeresistom.person;

import com.ripcm.microbiomeresistom.Simulation;
import com.ripcm.microbiomeresistom.Utils;

/**
 * Created by anna on 20.04.16.
 */

//TODO: read coefficient for probability to be infected from initial conditions
public class HealthyPerson extends Person {

    //constructor
    public HealthyPerson (double micResistance){
        super(micResistance);
    }
    private boolean toBeChanged = false;
    public boolean toBeHospitalized = false;
    public boolean pathRes;
    public boolean getToBeChanged(){return this.toBeChanged;}

    // marking a person to be infected
    public void tick(Simulation myComp, double pInfected, double decreaseCoef, double pHospitalize){
        if(micResistance !=0){
            micResistance = micResistance - decreaseCoef;
            if(micResistance < 0) {micResistance =0;}
        }
        toBeChanged = Utils.bernoulli(pInfected);
        if (toBeChanged){
            pathRes = Utils.bernoulli(myComp.getFixAvPathResist());
        } else { toBeHospitalized = Utils.bernoulli(pHospitalize);}
    }
}