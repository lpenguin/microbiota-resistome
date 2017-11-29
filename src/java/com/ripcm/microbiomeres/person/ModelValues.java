package com.ripcm.microbiomeres.person;

/**
 * Created by oksana on 6/1/17.
 */
public class ModelValues {
    public static int N_INCUB_LIMIT;//incubation period, in normal it's from 2 to 5 days
    public static final int N_INCUB_LIMIT_RESIST = N_INCUB_LIMIT;//incubation period for pathogen that becomes resistant
    /*public static int N_INFECTED_PEOPLE_PER_YEAR;
    public static double N_PEOPLE_IN_COUNTRY;*/
    public static double P_HEALTHY_HOSPITALIZE;//probability to be hospitalized with another infection
    //public final static double C_INFECTED_COEF_INCUB = 0;//0.125;// coefficient in the next formula (average number of people, that an ill person infects per day
    //public final static double C_INFECTED_COEF_ANT_TREATED_IN_TOWN = 0.23;//0.125;//C_INFECTED_COEF_INCUB/10;// coefficient in the next formula (average number of people, that an AntTr person in Town infects per day
    public static double C_INFECTED_COEF;
    public static double C_PATHOGEN_RESIST_CHANGE_COEF; //coefficient for probability of pathogen to become resistant because of microbiome resistance
    public static double P_INCUB_TO_HOSPITAL;//probability of being hospitalized at the first day of antibiotic treatment
    public static double P_WRONG_TREATMENT;//probability of wrong antibiotic treatment
    public static double P_BE_INFECTED_IN_HOSPITAL; //probability to be isInfected after being hospitalized for a "healthy" person
    public static final double P_TREATMENT_TO_HOSP = 0.01; //I don't know i defined variables right or not

    public static double C_GROWTH_COEF;//daily growth of microbiota resistance during antibiotic cource
    public static double C_DECREASE_COEF;//daily decrease of microbiota resistance in absence of antibiotics
    public static double PERM_RESIST_LEVEL;//Permanent level of agents microbiota resistance in general

    public static int N_PEOPLE_IN_TOWN;
    public static int N_HOSP_ANT_TR_PERSON;
    //public static final float SOME_PROBALILITY = 0.2;
    //public static final float SOME_PROBALILITY_2 = 0.2;
}
