package com.ripcm.microbiomeres.person;

/**
 * Created by oksana on 6/1/17.
 */
public class ModelValues {
    public final static int N_INCUB_LIMIT = 4;//incubation period, in normal it's from 2 to 5 days
    public final static int N_INCUB_LIMIT_RESIST = 4;//incubation period for pathogen that becomes resistant

    public final static double C_INFECTED_COEF = 19139/(142.8*Math.pow(10,6));
    public final static double C_INFECTED_COEF_INCUB = 0;//0.125;// coefficient in the next formula (average number of people, that an ill person infects per day
    public final static double C_INFECTED_COEF_ANT_TREATED_IN_TOWN = 0.23;//0.125;//C_INFECTED_COEF_INCUB/10;// coefficient in the next formula (average number of people, that an AntTr person in Town infects per day
    public final static double C_PATHOGEN_RESIST_CHANGE_COEF = 0.4; //coefficient for probability of pathogen to become resistant because of microbiome resistance
    public final static double P_INCUB_TO_HOSPITAL = 0.0005;//probability of being hospitalized at the first day of antibiotic treatment
    public final static double P_WRONG_TREATMENT = 0.21;//probability of wrong antibiotic treatment
    public static final double P_BE_INFECTED_IN_HOSPITAL = 0.01; //probability to be isInfected after being hospitalized for a "healthy" person
    public static final double P_TREATMENT_TO_HOSP = P_INCUB_TO_HOSPITAL*100; //I don't know i defined variables right or not

    public final static double C_GROWTH_COEF = 1./21.;//daily growth of microbiota resistance during antibiotic cource
    public final static double C_DECREASE_COEF = 1./90.;//daily decrease of microbiota resistance in absence of antibiotics

    //public static final float SOME_PROBALILITY = 0.2;
    //public static final float SOME_PROBALILITY_2 = 0.2;
}
