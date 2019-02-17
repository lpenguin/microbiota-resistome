package com.ripcm.microbiomeres.person;

import com.ripcm.microbiomeres.Simulation;

import java.util.Arrays;


/**
 * Created by anna on 13.07.16.
 */
public abstract class Person {


    public Person(int id, double[] micResistance) {
        this.id = id;
        this.micResistance = Arrays.copyOf(micResistance, micResistance.length);
    }

    public double[] micResistance;
    public int id;

    public abstract void tick(Simulation simulation, double p, double pResistant);
    //TODO: check it! it was so ->  public abstract void tick(Simulation simulation, double p, double coef, double changePathResCoef);
}
