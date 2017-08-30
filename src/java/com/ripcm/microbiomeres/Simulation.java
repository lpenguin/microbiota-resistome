package com.ripcm.microbiomeres;

import com.ripcm.microbiomeres.log.*;
import com.ripcm.microbiomeres.person.*;

import java.util.List;
import java.util.ArrayList;
import java.io.*;

//TODO: coordinates are not proper for more than 1 hospital
//TODO: make separate functions to replace healthy persons with infectious, and infectious with antibiotic treated


/**
 * Created by anna on 20.07.16.
 */
public class Simulation {

    //variables from ModelValues class

    double pGetToHosp(boolean isResistant) {//I don't know i defined variables right or not
        if (isResistant) {
            return ModelValues.P_TREATMENT_TO_HOSP * (ModelValues.C_PATHOGEN_RESIST_CHANGE_COEF * 10);
        } else return ModelValues.P_TREATMENT_TO_HOSP;
    }

    ; //probability to get to a hospital during antibiotic course

    //initial number of people
    private static final int N_HEALTHY_TOWN = 10000; //HealthyPerson, townHealthyPersons | state 1 on scheme in "Препринт"
    private static final int N_INC_PER_TOWN = 50;//IncPeriodPerson, townIncPerPersons | state 2 on scheme in "Препринт"
    private static final int N_INC_PER_TOWN_2 = 0; //IncPeriodPerson, townIncPerPersons2 | incubation period after wrong treatment (state 5)
    private static final int N_ANT_TR_TOWN = 0; //AntTreatedPerson, townAntTrPersons | state 3 on scheme in "Препринт"
    private static final int N_ANT_TR_TOWN_2 = 0;//AntTreatedPerson, townAntTrPersons2 | wrong antibiotic treatment (state 4)
    private static final int N_INFECTED_TOWN = N_ANT_TR_TOWN + N_INC_PER_TOWN + N_ANT_TR_TOWN_2 + N_INC_PER_TOWN_2 + 0;
    private static final int N_PERS_HOSP = 0; //AntTreatedPerson, hospAntTrPersons | isInfected persons in hospital (state 6 - isInfected)
    private static final int N_HEALTHY_HOSP = 0;//number of hospitalized persons without pathogen
    //number of hospitals (not working yet, only 1 hospital now)
    private static final int N_Hosp = 1; //number of hospitals


    //pathogene properties
    private static final int N_ANT_COURSE_TOWN_RIGHT = 5; //wright length of antibiotic course
    private static final int N_ANT_COURSE_TOWN_WRONG = 3; //wrong length of antibiotic course
    private static final int N_ANT_COURSE_HOSP = 7; //length of antibiotic course


    private double avPathResist = 0; //percentage of resistant pathogens among all (now initially it can be only 0)
    /*private static final double C_INFECTED_COEF =
            ((double) ModelValues.N_INFECTED_PEOPLE_PER_YEAR*Math.pow(10,3))/
            ( ModelValues.N_PEOPLE_IN_COUNTRY*Math.pow(10,6));*/



/*
    private static final double P_HOSP_NONRES = 0.2;//0.005;//probability of being hospitalized for an AntTreatedPerson with nonresistant pathogen
    private static final double P_HOSP_RES = 0.1;// 0.01;//probability of being hospitalized for an AntTreatedPerson with resistant pathogen
    private static double pGetToHosp(boolean isResistant) {
        if(isResistant){
            return P_HOSP_RES;
        } else return P_HOSP_NONRES;
    }  ; //probability to get to a hospital during antibiotic course
    private static final int C_wrongTr =10000000;// 5;// each C_wrongTr infected person is treated wrong (N_ANT_COURSE_TOWN_WRONG)
*/


    //microbiota properties
    private double avMicResist = 0;// initial averaged level of microbiome resistance
    private double fixAvPathResistTown = avPathResist;//averaged level of path resistance in town resistance in previous time step
    private double fixAvPathResistHosp = avPathResist;//averaged level of microbiome resistance in previous time step in hospital


    //time and graphics parameters
    private int ticks = 0;
    private static final int TOWN_W = 300;
    private static final int HOSP_W = 200;


    //lists of persons
    // and places
    private ArrayList<AntTreatedPerson> hospAntTrPersons = new ArrayList<>(N_PERS_HOSP);// isInfected persons in hospital (state 6 - isInfected)
    private ArrayList<HealthyPerson> townHealthyPersons = new ArrayList<>(N_HEALTHY_TOWN); //state 1 on scheme in "Препринт"
    private ArrayList<IncPeriodPerson> townIncPerPersons = new ArrayList<>(N_INC_PER_TOWN);//state 2 on scheme in "Препринт"
    private ArrayList<IncPeriodPerson> townIncPerPersons2 = new ArrayList<>(N_INC_PER_TOWN_2);//incubation period after wrong treatment (state 5)
    private ArrayList<AntTreatedPerson> townAntTrPersons = new ArrayList<>(N_ANT_TR_TOWN);//state 3 on scheme in "Препринт"
    private ArrayList<AntTreatedPerson> townAntTrPersons2 = new ArrayList<>(N_ANT_TR_TOWN_2);//wrong antibiotic treatment (state 4)
    private ArrayList<HealthyHospPerson> healthyHospPeople = new ArrayList<>(N_HEALTHY_HOSP);  //"healthy" persons in hospitals (with another pathogen or doctors) (state 6 - not isInfected)

    private TransitionLogger transLogger;
    private LogWriter messagesWriter;
    private PersonAmountLogger personAmountLogger;


    //Constructor
    public Simulation(int iterationNum, PersonAmountLogger personAmountLogger, TransitionLogger transLogger, LogWriter messagesWriter) throws IOException {
        this.personAmountLogger = personAmountLogger;
        this.transLogger = transLogger;
        this.messagesWriter = messagesWriter;

        printBlock("A calculus starting!");

        //TODO: join loggers (logTransFileName and PersonAmountLogger)

        initPersones();
        for (ticks = 0; ticks < iterationNum; ticks++) {
            action();//(personAmountLogger, ticks);
        }

        printBlock("The successful completion of a task!");
    }

    public void printBlock(String inpStr) throws IOException {
        messagesWriter.writeMessage("==" + new String(new char[inpStr.length()]).replace("\0", "=") + "==");
        messagesWriter.writeMessage("= " + inpStr + "= ");
        messagesWriter.writeMessage("==" + new String(new char[inpStr.length()]).replace("\0", "=") + "== ");
    }

    public double getFixAvPathResistTown() {
        return fixAvPathResistTown;
    }

    public static int getN_incLimit2() {
        return ModelValues.N_INCUB_LIMIT;
    }

    private void initPersones() throws IOException {
        //initialise lists of personsrPath
        int idIter = N_HEALTHY_TOWN + N_INFECTED_TOWN + N_PERS_HOSP + N_HEALTHY_HOSP;
        for (int i = 0; i < N_HEALTHY_TOWN + N_INFECTED_TOWN; i++) {
//            int w = (10 + i * 20) % TOWN_W;
//            int k = (10 + 20 * i - w) / TOWN_W;
//            int h = k * 20 + 10;
            if (i < N_INC_PER_TOWN) {
                townIncPerPersons.add(new IncPeriodPerson(Integer.toString(idIter), 0, false, ModelValues.N_INCUB_LIMIT + 1));
                transLogger.writeToTransLogFile(ticks, Integer.toString(idIter), "NA", "townIncPerPersons");
                idIter -= 1;
            } else {
                if (i < N_ANT_TR_TOWN) {
                    townAntTrPersons.add(new AntTreatedPerson(Integer.toString(idIter), 0, false, N_ANT_COURSE_TOWN_RIGHT + 1, pGetToHosp(false), 0));
                    transLogger.writeToTransLogFile(ticks, Integer.toString(idIter), "NA", "townAntTrPersons");
                    idIter -= 1;
                } else {
                    townHealthyPersons.add(new HealthyPerson(Integer.toString(idIter), 0));
                    transLogger.writeToTransLogFile(ticks, Integer.toString(idIter), "NA", "townHealthyPersons");
                    idIter -= 1;
                }
            }
        }

        for (int i = 0; i < N_PERS_HOSP; i++) {
//            int w = (10 + i * 20) % HOSP_W;
//            int k = (10 + 20 * i - w) / HOSP_W;
//            int h = k * 20 + 10;
            hospAntTrPersons.add(new AntTreatedPerson(Integer.toString(idIter), 0, false, N_ANT_COURSE_HOSP + 1, 0, 0));
            transLogger.writeToTransLogFile(ticks, Integer.toString(idIter), "NA", "hospAntTrPersons");
            idIter -= 1;
        }
        for (int i = 0; i < N_HEALTHY_HOSP; i++) {
            healthyHospPeople.add(new HealthyHospPerson(Integer.toString(idIter), 0, N_ANT_COURSE_HOSP + 1));
            transLogger.writeToTransLogFile(ticks, Integer.toString(idIter), "NA", "healthyHospPeople");
            idIter -= 1;
        }
        for (int i = 0; i < N_INC_PER_TOWN_2; i++) {
            townIncPerPersons2.add(new IncPeriodPerson(Integer.toString(idIter), 0, false, ModelValues.N_INCUB_LIMIT_RESIST + 1));
            transLogger.writeToTransLogFile(ticks, Integer.toString(idIter), "NA", "townIncPerPersons2");
            idIter -= 1;
        }
        for (int i = 0; i < N_ANT_TR_TOWN_2; i++) {
            townAntTrPersons2.add(new AntTreatedPerson(Integer.toString(idIter), 0, false, N_ANT_COURSE_TOWN_WRONG + 1, pGetToHosp(false), 0));
            transLogger.writeToTransLogFile(ticks, Integer.toString(idIter), "NA", "townAntTrPersons2");
            idIter -= 1;
        }
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
    public void action() throws IOException {//(PrintWriter writeFile, int nTicks) {
        double rMic;
        boolean pathRes;

        double hospNumber;
        int numInfectedTown = townIncPerPersons.size() +
                townIncPerPersons2.size() +
                townAntTrPersons2.size() +
                townAntTrPersons.size();

        double p_INF = (ModelValues.C_INFECTED_COEF * (1.2*((double) townIncPerPersons.size() + (double) townIncPerPersons2.size()) + (double) N_ANT_TR_TOWN + (double) N_ANT_TR_TOWN_2)) /
                ((double) townHealthyPersons.size() + (double) numInfectedTown);
/*
        System.out.print("p_INF = (C_INFECTED_COEF * ((double) townIncPerPersons.size() + (double) townIncPerPersons2.size() + (double) N_ANT_TR_TOWN)) /\n" +
                "                ((double) townHealthyPersons.size() + (double) numInfectedTown) = ");
        System.out.print(C_INFECTED_COEF+" * ("+ townIncPerPersons.size()+ " + " + townIncPerPersons2.size()+ " + " + N_ANT_TR_TOWN+") / ("+
                townHealthyPersons.size()+" + "+numInfectedTown + ")"+"\n");
*/

//        double p_INF = (C_INFECTED_COEF_INCUB * ((double) townIncPerPersons.size() + (double) townIncPerPersons2.size()) + C_INFECTED_COEF_ANT_TREATED_IN_TOWN * ((double) N_ANT_TR_TOWN)) /
//                (((double) townHealthyPersons.size()) + ((double) numInfectedTown));

        //output
        messagesWriter.writeMessage(String.format("%d %d %d %d %d %d", townHealthyPersons.size(), townIncPerPersons.size(), townAntTrPersons.size(),
                hospAntTrPersons.size(), townAntTrPersons2.size(), townIncPerPersons2.size()));
        List<Number> personAmount = new ArrayList<Number>(12);
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
        personAmountLogger.WritePersonAmountLogFile(personAmount);//WriteToFile(personAmount, writeFile);


        // it's we see on console
        messagesWriter.writeMessage("nHealthyTown =" + townHealthyPersons.size());
        messagesWriter.writeMessage("nIncPerTown = " + townIncPerPersons.size());
        messagesWriter.writeMessage("nAntTrTown =" + townAntTrPersons.size());
        messagesWriter.writeMessage("N_PERS_HOSP =" + hospAntTrPersons.size());
        messagesWriter.writeMessage("P_INF = " + p_INF + "\n");
        messagesWriter.writeMessage("avMicrobiоtaResistance = " + avMicResist);
        messagesWriter.writeMessage("avPathogeneResistance = " + avPathResist);
        messagesWriter.writeMessage("N_HEALTHY_HOSP = " + healthyHospPeople.size());


        avMicResist = 0.; // i think this should not be if ticks!=0
        avPathResist = 0.; // i think this should not be if ticks!=0
        int nHospResistant = 0;
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
            townHealthyPersons.get(i).tick(this, p_INF, ModelValues.C_DECREASE_COEF, ModelValues.P_HEALTHY_HOSPITALIZE);
            HealthyPerson pers = townHealthyPersons.get(i);
            avMicResist = avMicResist + pers.micResistance;
            if (pers.toBeChanged) {// & nHealthyTown != 0) { //marking a person to be isInfected
                townHealthyPersons.remove(i);
                i--;
                townIncPerPersons.add(new IncPeriodPerson(pers.id, pers.micResistance, pers.isResistant, ModelValues.N_INCUB_LIMIT + 1));
                transLogger.writeToTransLogFile(ticks, pers.id, "townHealthyPersons", "townIncPerPersons");

            } else {
                if (pers.toBeHospitalized) {
                    townHealthyPersons.remove(i);
                    i--;
                    healthyHospPeople.add(new HealthyHospPerson(pers.id, pers.micResistance, N_ANT_COURSE_HOSP + 1)); // ??? WHY here N_ANT_COURSE_HOSP, if it 's heath pers in hospital?
                    transLogger.writeToTransLogFile(ticks, pers.id, "townHealthyPersons", "healthyHospPeople");
                }
            }
        }
        //action for IncPeriod people in town
        for (int i = 0; i < townIncPerPersons.size(); i++) {
            //System.out.print("i = " + i+ ", nIncPerTown = " + nIncPerTown + "\n");
            townIncPerPersons.get(i).tick(this, 0, ModelValues.C_DECREASE_COEF, ModelValues.C_PATHOGEN_RESIST_CHANGE_COEF);
            IncPeriodPerson pers = townIncPerPersons.get(i);
            avMicResist = avMicResist + pers.micResistance;
            if (pers.isResistant) {
                avPathResist += 1;
                townAvPathRes += 1;
            }
            if (pers.getIncubPeriod() == 0) {

                boolean getToHospital = Utils.bernoulli(ModelValues.P_INCUB_TO_HOSPITAL);
                townIncPerPersons.remove(i);
                //removeIndexes.add(i);
                i--;
                if (getToHospital) {
                    hospAntTrPersons.add(new AntTreatedPerson(pers.id, pers.micResistance, pers.isResistant, N_ANT_COURSE_HOSP + 1, 0, 0));
                    transLogger.writeToTransLogFile(ticks, pers.id, "townIncPerPersons", "hospAntTrPersons");
                } else {
                    if (!Utils.bernoulli(ModelValues.P_WRONG_TREATMENT)) {
                        townAntTrPersons.add(new AntTreatedPerson(pers.id, pers.micResistance, pers.isResistant, N_ANT_COURSE_TOWN_RIGHT + 1, pGetToHosp(pers.isResistant), 0));
                        transLogger.writeToTransLogFile(ticks, pers.id, "townIncPerPersons", "townAntTrPersons");
                    } else {
                        townAntTrPersons2.add(new AntTreatedPerson(pers.id, pers.micResistance, pers.isResistant, N_ANT_COURSE_TOWN_WRONG + 1, pGetToHosp(true), 0));
                        transLogger.writeToTransLogFile(ticks, pers.id, "townIncPerPersons", "townAntTrPersons2");
                    }
                }
            }
        }

        //action for IncPeriod2 people in town
        //incubation period after wrong treatment (state 5)
        for (int i = 0; i < townIncPerPersons2.size(); i++) {
            townIncPerPersons2.get(i).tick(this, 0, ModelValues.C_DECREASE_COEF, ModelValues.C_PATHOGEN_RESIST_CHANGE_COEF);
            IncPeriodPerson pers = townIncPerPersons2.get(i);
            avMicResist = avMicResist + pers.micResistance;
            if (pers.getIncubPeriod() == 0) {
                //rMic = pers.micResistance;
                townIncPerPersons2.remove(i);
                i--;
                hospAntTrPersons.add(new AntTreatedPerson(pers.id, pers.micResistance, pers.isResistant, N_ANT_COURSE_HOSP + 1, 0, 0));
                transLogger.writeToTransLogFile(ticks, pers.id, "townIncPerPersons2", "hospAntTrPersons");
            }
        }

        //action for AntTreated people in town
        for (int i = 0; i < townAntTrPersons.size(); i++) {
            townAntTrPersons.get(i).tick(this, pGetToHosp(townAntTrPersons.get(i).isResistant),
                    ModelValues.C_GROWTH_COEF, ModelValues.C_PATHOGEN_RESIST_CHANGE_COEF);
            AntTreatedPerson pers = townAntTrPersons.get(i);
            avMicResist += pers.micResistance;
            if (pers.beHospitalized) {
                townAntTrPersons.remove(i);
                i--;
                hospAntTrPersons.add(new AntTreatedPerson(pers.id, pers.micResistance, pers.isResistant, N_ANT_COURSE_HOSP + 1, 0, 0));
                transLogger.writeToTransLogFile(ticks, pers.id, "townAntTrPersons", "hospAntTrPersons");

            } else if (pers.treatmentPeriod == 0) {
                townAntTrPersons.remove(i);
                i--;

                //TODO: isResistant always false
                if (!pers.isResistant) {
                    townHealthyPersons.add(new HealthyPerson(pers.id, pers.micResistance));
                    transLogger.writeToTransLogFile(ticks, pers.id, "townAntTrPersons", "townHealthyPersons");
                } else { // hospital treatment period should be for AB2 !!! OR NOT?????
                    hospAntTrPersons.add(new AntTreatedPerson(pers.id, pers.micResistance, pers.isResistant, N_ANT_COURSE_HOSP + 1, 0, 0));
                    transLogger.writeToTransLogFile(ticks, pers.id, "townAntTrPersons", "hospAntTrPersons");
                }
            }
        }


        //action for AntTreated2 people in town
        for (int i = 0; i < townAntTrPersons2.size(); i++) {
            townAntTrPersons2.get(i).tick(this, pGetToHosp(townAntTrPersons2.get(i).isResistant), ModelValues.C_GROWTH_COEF, ModelValues.C_PATHOGEN_RESIST_CHANGE_COEF);
            AntTreatedPerson pers = townAntTrPersons2.get(i);
            avMicResist += pers.micResistance;
            if (pers.beHospitalized) {
                townAntTrPersons2.remove(i);
                i--;
                hospAntTrPersons.add(new AntTreatedPerson(pers.id, pers.micResistance, pers.isResistant, N_ANT_COURSE_HOSP + 1, 0, 0));
                transLogger.writeToTransLogFile(ticks, pers.id, "townAntTrPersons2", "hospAntTrPersons");
            } else if (pers.treatmentPeriod == 0) {
                townAntTrPersons2.remove(i);
                i--;

                townIncPerPersons2.add(new IncPeriodPerson(pers.id, pers.micResistance, true, ModelValues.N_INCUB_LIMIT_RESIST + 1)); //wrong treatment leads to resistance of pathogen
                transLogger.writeToTransLogFile(ticks, pers.id, "townAntTrPersons2", "townIncPerPersons2");
            }
        }


        //action for HospAntTr people (in hosp, isInfected)
        for (int i = 0; i < hospAntTrPersons.size(); i++) {
            hospAntTrPersons.get(i).tick(this, 1, -ModelValues.C_DECREASE_COEF, ModelValues.C_PATHOGEN_RESIST_CHANGE_COEF);//resistance decreases, because in hospital people are treated with another antibiotic
            AntTreatedPerson pers = hospAntTrPersons.get(i);
            if (pers.isResistant) nHospResistant = nHospResistant + 1; // it's never used
            avMicResist = avMicResist + pers.micResistance;
            // TODO: Сделать переход в состояние два (townIncPerPersons2)
            if (pers.treatmentPeriod == 0) {
                hospAntTrPersons.remove(i);
                i--;
                townHealthyPersons.add(new HealthyPerson(pers.id, pers.micResistance));
                transLogger.writeToTransLogFile(ticks, pers.id, "hospAntTrPersons", "townHealthyPersons");
            }
        }


        //action for healthy people in hosp
        for (int i = 0; i < healthyHospPeople.size(); i++) {
            healthyHospPeople.get(i).tick(this, ModelValues.P_BE_INFECTED_IN_HOSPITAL, ModelValues.C_DECREASE_COEF, fixAvPathResistHosp);//resistance decreases, because in hospital people are treated with another antibiotic
            HealthyHospPerson pers = healthyHospPeople.get(i);
            avMicResist = avMicResist + pers.micResistance;
            if (pers.treatmentPeriod == 0) {
                if (!pers.isInfected) {
                    townHealthyPersons.add(new HealthyPerson(pers.id, pers.micResistance));
                    transLogger.writeToTransLogFile(ticks, pers.id, "healthyHospPeople", "townHealthyPersons");
                } else {
                    townIncPerPersons.add(new IncPeriodPerson(pers.id, pers.micResistance, pers.isResistant, ModelValues.N_INCUB_LIMIT + 1));
                    transLogger.writeToTransLogFile(ticks, pers.id, "healthyHospPeople", "townIncPerPersons");
                }
                healthyHospPeople.remove(i);
                i--;
            }
        }


        int nPathResistTown = nResMembersA(townAntTrPersons) + nResMembersA(townAntTrPersons2) +
                nResMembersI(townIncPerPersons) + nResMembersI(townIncPerPersons2);
        int nPathResistHosp = nResMembersA(hospAntTrPersons);


        avMicResist = avMicResist / ((double) (townHealthyPersons.size() + townIncPerPersons.size() + townAntTrPersons.size() +
                townAntTrPersons2.size() + townIncPerPersons2.size() + hospAntTrPersons.size() + healthyHospPeople.size()));
        avPathResist = (nPathResistTown+nPathResistHosp) / ((double) (townIncPerPersons.size() + townIncPerPersons2.size() + townAntTrPersons.size() + townAntTrPersons2.size() + hospAntTrPersons.size()));
        fixAvPathResistTown = nPathResistTown / ((double) (townIncPerPersons.size() + townAntTrPersons.size() + townAntTrPersons2.size() + townIncPerPersons2.size()));
        fixAvPathResistHosp = nPathResistHosp / ((double) (hospAntTrPersons.size() + healthyHospPeople.size()));
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
