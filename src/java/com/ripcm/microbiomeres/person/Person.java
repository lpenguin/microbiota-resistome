package com.ripcm.microbiomeres.person;

import com.ripcm.microbiomeres.Simulation;


/**
 * Created by anna on 13.07.16.
 */
public abstract class Person {


    public Person( double micResistance) {
        this.micResistance = micResistance;
    }

    public double micResistance;
    public abstract void tick(Simulation simulation, double p, double coef, double changePathResCoef);
}
