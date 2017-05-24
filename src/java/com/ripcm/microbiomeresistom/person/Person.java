package com.ripcm.microbiomeresistom.person;

import com.ripcm.microbiomeresistom.Simulation;
import com.ripcm.microbiomeresistom.Utils;

import java.util.Random;


/**
 * Created by anna on 13.07.16.
 */
public abstract class Person {


    public Person( double micResistance) {
        this.micResistance = micResistance;
    }

    public double micResistance;
    public abstract void tick(Simulation myComp, double p, double coef, double changePathResCoef);
}
