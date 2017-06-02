package com.ripcm.microbiomeres;

import com.ripcm.microbiomeres.person.*;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.io.PrintWriter;

//TODO: coordinates are not proper for more than 1 hospital
//TODO: make separate functions to replace healthy persons with infectious, and infectious with antibiotic treated


/**
 * Created by anna on 20.07.16.
 */
public class Simulation {

    public Graphics2D graphics;
    //variables from ModelValues class
    double C_INF_COEF = ModelValues.C_INFECTED_COEF;
    double C_INF_COEF_1 =  ModelValues.C_INFECTED_COEF_INCUB;
    double C_INF_COEF_2 =  ModelValues.C_INFECTED_COEF_ANT_TREATED_IN_TOWN;
    double C_CHANGE_PATH_RES_COEF = ModelValues.C_PATHOGEN_RESIST_CHANGE_COEF; //coefficient for probability of pathogen to become resistant because of microbiome resistance
    double P_INC_HOSP = ModelValues.P_INCUB_TO_HOSPITAL;//probability of being hospitalized at the first day of antibiotic treatment
    double P_WRONG_TREATM = ModelValues.P_WRONG_TREATMENT;//probability of wrong antibiotic treatment

    double P_TREATMENT_TO_HOSP = P_INC_HOSP*100; //I don't know i defined variables right or not
    double pGetToHosp(boolean isResistant) {//I don't know i defined variables right or not
        if(isResistant){
            return P_TREATMENT_TO_HOSP*(C_CHANGE_PATH_RES_COEF*10);
        } else return P_TREATMENT_TO_HOSP;
    }; //probability to get to a hospital during antibiotic course

    double P_HOSP_INF = ModelValues.P_BE_INFECTED_IN_HOSPITAL; //probability to be isInfected after being hospitalized for a "healthy" person

    //initial number of people
    private static final int N_HEALTHY_TOWN = 10000; //HealthyPerson, townHealthyPersons | state 1 on scheme in "Препринт"
    private static final int N_INC_PER_TOWN = 50;//IncPeriodPerson, townIncPerPersons | state 2 on scheme in "Препринт"
    private static final int N_INC_PER_TOWN_2 = 0; //IncPeriodPerson, townIncPerPersons2 | incubation period after wrong treatment (state 5)
    private static final int N_ANT_TR_TOWN = 0; //AntTreatedPerson, townAntTrPersons | state 3 on scheme in "Препринт"
    private static final int N_ANT_TR_TOWN_2 = 0;//AntTreatedPerson, townAntTrPersons2 | wrong antibiotic treatment (state 4)
    private static final int N_INFECTED_TOWN = N_ANT_TR_TOWN + N_INC_PER_TOWN + N_ANT_TR_TOWN_2 + N_INC_PER_TOWN_2;
    private static final int N_PERS_HOSP = 0; //AntTreatedPerson, hospAntTrPersons | isInfected persons in hospital (state 6 - isInfected)
    private static final int N_HEALTHY_HOSP = 0;//number of hospitalized persons without pathogen
    //number of hospitals (not working yet, only 1 hospital now)
    private final static int N_Hosp = 1; //number of hospitals

    private static final double P_HEALTHY_HOSPITALIZE = 0.002;//probability to be hospitalized with another infection
    //pathogene properties
    private final static int N_INC_LIMIT = 2;//incubation period
    private final static int N_INC_LIMIT_2 = 2;//incubation period for pathogen that becomes resistant
    private final static int N_ANT_COURSE_TOWN_RIGHT = 5; //wright length of antibiotic course
    private final static int N_ANT_COURSE_TOWN_WRONG = 2; //wrong length of antibiotic course
    private final static int N_ANT_COURSE_HOSP = 7; //length of antibiotic course


    private static double avPathResist = 0; //percentage of resistant pathogens among all (now initially it can be only 0)



/*
    private final static double P_HOSP_NONRES = 0.2;//0.005;//probability of being hospitalized for an AntTreatedPerson with nonresistant pathogen
    private final static double P_HOSP_RES = 0.1;// 0.01;//probability of being hospitalized for an AntTreatedPerson with resistant pathogen
    private static double pGetToHosp(boolean isResistant) {
        if(isResistant){
            return P_HOSP_RES;
        } else return P_HOSP_NONRES;
    }  ; //probability to get to a hospital during antibiotic course
    private final static int C_wrongTr =10000000;// 5;// each C_wrongTr infected person is treated wrong (N_ANT_COURSE_TOWN_WRONG)
*/


    //microbiota properties
    private double avMicResist = 0;// initial averaged level of microbiome resistance
    private final static double C_GROWTH_COEF = 1./21.;//daily growth of microbiota resistance during antibiotic cource
    private final static double C_DECREASE_COEF = 1./90.;//daily decrease of microbiota resistance in absence of antibiotics
    private double fixAvPathResistTown =  avPathResist ;//averaged level of path resistance in town resistance in previous time step
    private double fixAvPathResistHosp =  avPathResist ;//averaged level of microbiome resistance in previous time step in hospital


    //time and graphics parameters
    private int ticks = 0;
    private final static int TOWN_W = 300;
    private final static int HOSP_W = 200;


    //lists of persons
    // and places
    private static ArrayList<AntTreatedPerson> hospAntTrPersons = new ArrayList<>(N_PERS_HOSP);// isInfected persons in hospital (state 6 - isInfected)
    private static ArrayList<HealthyPerson> townHealthyPersons = new ArrayList<>(N_HEALTHY_TOWN); //state 1 on scheme in "Препринт"
    private static ArrayList<IncPeriodPerson> townIncPerPersons = new ArrayList<>(N_INC_PER_TOWN);//state 2 on scheme in "Препринт"
    private static ArrayList<IncPeriodPerson> townIncPerPersons2 = new ArrayList<>(N_INC_PER_TOWN_2);//incubation period after wrong treatment (state 5)
    private static ArrayList<AntTreatedPerson> townAntTrPersons = new ArrayList<>(N_ANT_TR_TOWN);//state 3 on scheme in "Препринт"
    private static ArrayList<AntTreatedPerson> townAntTrPersons2 = new ArrayList<>(N_ANT_TR_TOWN_2);//wrong antibiotic treatment (state 4)
    private static ArrayList<HealthyHospPerson> healthyHospPeople = new ArrayList<>(N_HEALTHY_HOSP);  //"healthy" persons in hospitals (with another pathogen or doctors) (state 6 - not isInfected)


    public void printBlock(String inpStr) {
        System.out.print("=="+ new String(new char[inpStr.length()]).replace("\0", "=") +"== \n");
        System.out.print("= "+inpStr+"= \n");
        System.out.print("=="+ new String(new char[inpStr.length()]).replace("\0", "=") +"== \n");
    };

    //Constructor
    public Simulation(int iterationNum, String fileName) throws FileNotFoundException {

        printBlock("Start working!");
        initPersones();
        PrintWriter writeFile = openLogFile(fileName);
        for (ticks=0; ticks< iterationNum; ticks++){
            action(writeFile);
        }

        writeFile.close();

        printBlock("Working finished!");
    }

    public double getFixAvPathResistTown() {return fixAvPathResistTown;}

    public static int getN_incLimit2() {return N_INC_LIMIT;}


    private PrintWriter openLogFile(String fName) throws FileNotFoundException {
        PrintWriter writeFile = new PrintWriter(fName);
        //not right seq of column names!!!! change
        String st = "Ticks HealthyPersonsInTown InfectedPersonsInTown IncPeriodPersonsInTown IncPeriodPersonsInTown2 AntibioticTreatedPersonsInTown AntibioticTreatedPersonsInTown2 InfectedPersonsInHospital HealthyPersonsInHospital pGetInfectedTown AvMicResistance AvPathResistance ";
        writeFile.println(st);
        return writeFile;
    }

    private void initPersones(){
        //initialise lists of personsrPath
        for (int i = 0; i < N_HEALTHY_TOWN + N_INFECTED_TOWN; i++) {
            int w = (10 + i * 20) % TOWN_W;
            int k = (10 + 20 * i - w) / TOWN_W;
            int h = k * 20 + 10;
            if (i < N_INC_PER_TOWN) {
                townIncPerPersons.add(new IncPeriodPerson(0,false, N_INC_LIMIT +1));
            } else {
                if (i < N_ANT_TR_TOWN) {
                    townAntTrPersons.add(new AntTreatedPerson(0,false, N_ANT_COURSE_TOWN_RIGHT +1, pGetToHosp(false),0));
                } else townHealthyPersons.add(new HealthyPerson(0));
            }
        }

        for (int i = 0; i < N_PERS_HOSP; i++) {
            int w = (10 + i * 20) % HOSP_W;
            int k = (10 + 20 * i - w) / HOSP_W;
            int h = k * 20 + 10;
            hospAntTrPersons.add(new AntTreatedPerson(0,false, N_ANT_COURSE_HOSP +1, 0,0));
        }
        for (int i = 0; i< N_HEALTHY_HOSP; i++) {
            healthyHospPeople.add(new HealthyHospPerson(0, N_ANT_COURSE_HOSP +1));
        }
        for (int i = 0; i< N_INC_PER_TOWN_2; i++) {
            townIncPerPersons2.add(new IncPeriodPerson(0,false, N_INC_LIMIT_2 +1));
        }
        for (int i = 0; i< N_ANT_TR_TOWN_2; i++) {
            townAntTrPersons2.add(new AntTreatedPerson(0,false, N_ANT_COURSE_TOWN_WRONG +1,pGetToHosp(false),0));
        }
    }


    public void WriteToFile(List<Number> l, PrintWriter writeFile) {
        String st = "";
        for (Number n : l) {
            st += n + " ";
        }
        writeFile.println(st.trim());
        writeFile.flush();
    }

    public int nResMembersA(ArrayList<AntTreatedPerson> memberList) {
        int n = 0;
        for (int i = 0; i < memberList.size(); i++) {
            if (memberList.get(i).isResistant) n += 1;
        }
        return n;
    }
    public int nResMembersI(ArrayList<IncPeriodPerson> memberList) {
        int n = 0;
        for (int i = 0; i < memberList.size(); i++) {
            if (memberList.get(i).isResistant) n += 1;
        }
        return n;
    }

    // What happend with each agent on each tick
    //action for each component in a list
    public void action(PrintWriter writeFile) {
        double rMic;
        boolean pathRes;

        double hospNumber;
        int numInfectedTown = townIncPerPersons.size() +
                              townIncPerPersons2.size() +
                              townAntTrPersons2.size() +
                              townAntTrPersons.size();

        double p_INF = (C_INF_COEF * ((double) townIncPerPersons.size() + (double) townIncPerPersons2.size() + (double) N_ANT_TR_TOWN)) /
                ((double) townHealthyPersons.size() + (double) numInfectedTown);

//        double p_INF = (C_INF_COEF_1 * ((double) townIncPerPersons.size() + (double) townIncPerPersons2.size()) + C_INF_COEF_2 * ((double) N_ANT_TR_TOWN)) /
//                (((double) townHealthyPersons.size()) + ((double) numInfectedTown));

        //output
        System.out.print(townHealthyPersons.size() + " " + townIncPerPersons.size() + " " + townAntTrPersons.size() + " " +
                hospAntTrPersons.size() + " " + townAntTrPersons2.size() + " " + townIncPerPersons2.size() +"\n");
        List<Number> personAmount = new ArrayList<Number>();
        personAmount.add(ticks);
        personAmount.add(townHealthyPersons.size());
        personAmount.add(numInfectedTown);
        personAmount.add(townIncPerPersons.size());
        personAmount.add(townIncPerPersons2.size());
        personAmount.add(townAntTrPersons.size());
        personAmount.add(townAntTrPersons2.size());
        personAmount.add(hospAntTrPersons.size());
        personAmount.add(healthyHospPeople.size());
        personAmount.add(p_INF);
        personAmount.add(avMicResist);
        personAmount.add(avPathResist);


        // it's we see on console
        System.out.print("nHealthyTown =" + townHealthyPersons.size() + "\n");
        System.out.print("nIncPerTown = " + townIncPerPersons.size() + "\n");
        System.out.print("nAntTrTown =" + townAntTrPersons.size() + "\n");
        System.out.print("N_PERS_HOSP =" + hospAntTrPersons.size() + "\n");
        System.out.print("P_INF = " + p_INF + "\n");
        System.out.print("avMicrobiоtaResistance = " + avMicResist + "\n");
        System.out.print("avPathogeneResistance = " + avPathResist + "\n");
        System.out.print("N_HEALTHY_HOSP = " + healthyHospPeople.size() + "\n");
        WriteToFile(personAmount, writeFile);

        avMicResist = 0.; // i think this should not be if ticks!=0
        avPathResist = 0.; // i think this should not be if ticks!=0
        int nHospResistant =0;
        double townAvPathRes = 0;
        double wrongTrFlag = 1;

        //it should be done for removing step of "i--" in a loop
        //        ArrayList <Integer> removeIndexes = new ArrayList<Integer>(0);
//        Iterator<HealthyPerson> it = townHealthyPersons.iterator();
//        while (it.hasNext()){
//            HealthyPerson pers = it.next();
//            if(pers.getToBeChanged()){
//                it.remove();
//            }
//        }

        //action for healthy people in town
        for (int i = 0; i < townHealthyPersons.size(); i++) {
            townHealthyPersons.get(i).tick(this, p_INF, C_DECREASE_COEF, P_HEALTHY_HOSPITALIZE);
            HealthyPerson pers = townHealthyPersons.get(i);
            avMicResist = avMicResist + pers.micResistance;
            if (pers.toBeChanged){// & nHealthyTown != 0) {
                townHealthyPersons.remove(i);
                i--;
                townIncPerPersons.add(new IncPeriodPerson(pers.micResistance, pers.isResistant, N_INC_LIMIT + 1));
            } else {
                if (pers.toBeHospitalized) {
                    townHealthyPersons.remove(i);
                    i--;
                    healthyHospPeople.add(new HealthyHospPerson(pers.micResistance, N_ANT_COURSE_HOSP +1)); // ??? WHY here N_ANT_COURSE_HOSP, if it 's heath pers in hospital?
                }
            }
        }
        //action for IncPeriod people in town
        for (int i = 0; i < townIncPerPersons.size(); i++) {
            //System.out.print("i = " + i+ ", nIncPerTown = " + nIncPerTown + "\n");
            townIncPerPersons.get(i).tick(this, 0, C_DECREASE_COEF, C_CHANGE_PATH_RES_COEF);
            IncPeriodPerson pers = townIncPerPersons.get(i);
            avMicResist = avMicResist +pers.micResistance;
            if(pers.isResistant){
                avPathResist += 1;
                townAvPathRes +=1;
            }
            if (pers.getIncubPeriod() == 0) {

                boolean getToHospital = Utils.bernoulli(P_INC_HOSP);
                townIncPerPersons.remove(i);
                //removeIndexes.add(i);
                i--;
                if (getToHospital) {
                    hospAntTrPersons.add(new AntTreatedPerson(pers.micResistance, pers.isResistant, N_ANT_COURSE_HOSP + 1,0,0));
                } else {
                    if (!Utils.bernoulli(P_WRONG_TREATM)){
                        townAntTrPersons.add(new AntTreatedPerson(pers.micResistance, pers.isResistant, N_ANT_COURSE_TOWN_RIGHT + 1,pGetToHosp(pers.isResistant),0));
                    } else {
                        townAntTrPersons2.add(new AntTreatedPerson(pers.micResistance, pers.isResistant, N_ANT_COURSE_TOWN_WRONG +1, pGetToHosp(true),0));
                    }
                }
            }
        }

        //action for IncPeriod2 people in town
        //incubation period after wrong treatment (state 5)
        for (int i = 0; i < townIncPerPersons2.size(); i++) {
            townIncPerPersons2.get(i).tick(this, 0, C_DECREASE_COEF, C_CHANGE_PATH_RES_COEF);
            IncPeriodPerson pers = townIncPerPersons2.get(i);
            avMicResist = avMicResist +pers.micResistance;
            if (pers.getIncubPeriod() == 0) {
                //rMic = pers.micResistance;
                townIncPerPersons2.remove(i);
                i--;
                hospAntTrPersons.add(new AntTreatedPerson(pers.micResistance, pers.isResistant, N_ANT_COURSE_HOSP + 1,0,0));
            }
        }

        //action for AntTreated people in town
        for (int i = 0; i < townAntTrPersons.size(); i++) {
            townAntTrPersons.get(i).tick(this, pGetToHosp(townAntTrPersons.get(i).isResistant),
                                          C_GROWTH_COEF, C_CHANGE_PATH_RES_COEF);
            AntTreatedPerson pers = townAntTrPersons.get(i);
            avMicResist += pers.micResistance;
            if (pers.beHospitalized) {
                townAntTrPersons.remove(i);
                i--;
                hospAntTrPersons.add(new AntTreatedPerson(pers.micResistance,pers.isResistant, N_ANT_COURSE_HOSP + 1,0,0));

            } else if(pers.treatmentPeriod == 0) {
                townAntTrPersons.remove(i);
                i--;

                //TODO: isResistant always false
                if (!pers.isResistant) {
                    townHealthyPersons.add(new HealthyPerson(pers.micResistance));
                } else { // hospital treatment period should be for AB2 !!! OR NOT?????
                    hospAntTrPersons.add(new AntTreatedPerson(pers.micResistance, pers.isResistant, N_ANT_COURSE_HOSP + 1, 0,0));
                }
            }
        }


        //action for AntTreated2 people in town
        for (int i = 0; i< townAntTrPersons2.size(); i++) {
            townAntTrPersons2.get(i).tick(this, pGetToHosp(townAntTrPersons2.get(i).isResistant), C_GROWTH_COEF, C_CHANGE_PATH_RES_COEF);
            AntTreatedPerson pers = townAntTrPersons2.get(i);
            avMicResist += pers.micResistance;
            if (pers.beHospitalized) {
                townAntTrPersons2.remove(i);
                i--;
                hospAntTrPersons.add(new AntTreatedPerson(pers.micResistance, pers.isResistant, N_ANT_COURSE_HOSP + 1, 0, 0));
            } else if (pers.treatmentPeriod == 0) {
                townAntTrPersons2.remove(i);
                i--;
                townIncPerPersons2.add(new IncPeriodPerson(pers.micResistance, true, N_INC_LIMIT_2 +1)); //wrong treatment leads to resistance of pathogen
            }
        }




        //action for HospAntTr people (in hosp, isInfected)
        for (int i = 0; i < hospAntTrPersons.size(); i++) {
            hospAntTrPersons.get(i).tick(this, 1, -C_DECREASE_COEF, C_CHANGE_PATH_RES_COEF);//resistance decreases, because in hospital people are treated with another antibiotic
            AntTreatedPerson pers = hospAntTrPersons.get(i);
            if (pers.isResistant) nHospResistant = nHospResistant + 1; // it's never used
            avMicResist = avMicResist + pers.micResistance;
            // TODO: Сделать переход в состояние два (townIncPerPersons2)
            if (pers.treatmentPeriod == 0) {
                hospAntTrPersons.remove(i);
                i--;
                townHealthyPersons.add(new HealthyPerson(pers.micResistance));
            }
        }


        //action for healthy people in hosp
        for (int i = 0; i < healthyHospPeople.size(); i++) {
            healthyHospPeople.get(i).tick(this, P_HOSP_INF, C_DECREASE_COEF, fixAvPathResistHosp);//resistance decreases, because in hospital people are treated with another antibiotic
            HealthyHospPerson pers = healthyHospPeople.get(i);
            avMicResist = avMicResist +pers.micResistance;
            if(pers.treatmentPeriod == 0) {
                if(!pers.isInfected){
                    townHealthyPersons.add(new HealthyPerson(pers.micResistance));
                } else {
                    townIncPerPersons.add(new IncPeriodPerson(pers.micResistance, pers.isResistant, N_INC_LIMIT +1));
                }
                healthyHospPeople.remove(i);
                i--;
            }
        }

        int nPathResist =nResMembersA(townAntTrPersons) +nResMembersA(townAntTrPersons2) +
                nResMembersI(townIncPerPersons) + nResMembersI(townIncPerPersons2) + nResMembersA(hospAntTrPersons);
        int nPathResistTown = nResMembersA(townAntTrPersons) +nResMembersA(townAntTrPersons2) +
                nResMembersI(townIncPerPersons) + nResMembersI(townIncPerPersons2);
        int nPathResistHosp = nResMembersA(hospAntTrPersons);


        avMicResist = avMicResist/((double) (townHealthyPersons.size() + townIncPerPersons.size()+ townAntTrPersons.size()+
                townAntTrPersons2.size() + townIncPerPersons2.size()+ hospAntTrPersons.size()+ healthyHospPeople.size()));
        avPathResist = nPathResist/((double) (townIncPerPersons.size() + townIncPerPersons2.size()+ townAntTrPersons.size()+ townAntTrPersons2.size() + hospAntTrPersons.size() ));
        fixAvPathResistTown = nPathResistTown/((double) (townIncPerPersons.size()+ townAntTrPersons.size() + townAntTrPersons2.size() + townIncPerPersons2.size()));
        fixAvPathResistHosp = nPathResistHosp/((double) (hospAntTrPersons.size() + healthyHospPeople.size()));
    }
}


//if Person has coordinates
/*     public void action(ArrayList<HealthyPerson> TownHealthyPer, ArrayList<IncPeriodPerson> TownInfPer,
                       ArrayList<AntibioticTreatedPerson> TownAntTr) {
        nIncPerTown = TownInfPer.size();
        nAntTrTown = TownAntTr.size();
        nHealthyTown = TownHealthyPer.size();
        N_INFECTED_TOWN = nIncPerTown + nAntTrTown;
        double p = ((double) N_INFECTED_TOWN) / (((double) nHealthyTown) + ((double) N_INFECTED_TOWN));
        System.out.print("p = " + p + "\n");
        for (int i = 0; i < TownHealthyPer.size(); i++) {
            HealthyPerson pers = TownHealthyPer.get(i);
            pers.tick(this, p);
            if (pers.getToBeChanged() & nHealthyTown != 0) {
                int missx = pers.getMissX();
                int missy = pers.getMissY();
                TownHealthyPer.remove(i);
                TownInfPer.add(new IncPeriodPerson(missx, missy, N_INC_LIMIT + 1));
            }
        }
        for (int i = 0; i < TownInfPer.size(); i++) {
            IncPeriodPerson pers = TownInfPer.get(i);
            pers.tick(this, p);
            if (pers.incubation_countdown == 0) {
                int missx = pers.getMissX();
                int missy = pers.getMissY();
                TownInfPer.remove(i);
                TownAntTr.add(new AntibioticTreatedPerson(missx, missy, 0));
            }
        }
    }*/
