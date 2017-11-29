package com.ripcm.microbiomeres;

import com.ripcm.microbiomeres.log.*;
import com.ripcm.microbiomeres.person.*;

import javax.jws.WebParam;
import java.sql.Array;
import java.util.Arrays;
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
            return (ModelValues.P_TREATMENT_TO_HOSP*ModelValues.C_PATHOGEN_RESIST_CHANGE_COEF * 10);
        } else return ModelValues.P_TREATMENT_TO_HOSP;
    }

    ; //probability to get to a hospital during antibiotic course

    //initial number of people
    private static final int N_PERS_HOSP = ModelValues.N_HOSP_ANT_TR_PERSON; //AntTreatedPerson, hospAntTrPersons | isInfected persons in hospital (state 6 - isInfected)
    //TODO: check it, right or not

    private static final int N_INC_PER_TOWN = (int) Math.round(N_PERS_HOSP/(ModelValues.P_INCUB_TO_HOSPITAL*50));//IncPeriodPerson, townIncPerPersons | state 2 on scheme in "Препринт"
    private static final int N_ANT_TR_TOWN = (int) Math.round(N_INC_PER_TOWN*(1-ModelValues.P_INCUB_TO_HOSPITAL)*(1-ModelValues.P_WRONG_TREATMENT)); //AntTreatedPerson, townAntTrPersons | state 3 on scheme in "Препринт"
    private static final int N_ANT_TR_TOWN_2 = (int) Math.round(ModelValues.P_WRONG_TREATMENT*N_INC_PER_TOWN*(1-ModelValues.P_INCUB_TO_HOSPITAL));//AntTreatedPerson, townAntTrPersons2 | wrong antibiotic treatment (state 4)
    private static final int N_INC_PER_TOWN_2 = Math.round(N_ANT_TR_TOWN_2/4); //IncPeriodPerson, townIncPerPersons2 | incubation period after wrong treatment (state 5)
    private static final int N_INFECTED_TOWN = N_ANT_TR_TOWN + N_INC_PER_TOWN + N_ANT_TR_TOWN_2 + N_INC_PER_TOWN_2;
    private static final int N_HEALTHY_HOSP = (int) Math.round(ModelValues.P_HEALTHY_HOSPITALIZE*(ModelValues.N_PEOPLE_IN_TOWN - N_INFECTED_TOWN - N_PERS_HOSP));//number of hospitalized persons without pathogen
    private static final int N_HEALTHY_TOWN = ModelValues.N_PEOPLE_IN_TOWN - N_INFECTED_TOWN - N_HEALTHY_HOSP - N_PERS_HOSP; //HealthyPerson, townHealthyPersons | state 1 on scheme in "Препринт"


    //number of hospitals (not working yet, only 1 hospital now)
    private static final int N_Hosp = 1; //number of hospitals


    //pathogene properties
    private static final int N_ANT_COURSE_TOWN_RIGHT = 5; //wright length of antibiotic course
    private static final int N_ANT_COURSE_TOWN_WRONG = 3; //wrong length of antibiotic course
    private static final int N_ANT_COURSE_HOSP = 7; //length of antibiotic course


    private double avPathResist = 0; //percentage of resistant pathogens among all (now initially it can be only 0)
    //TODO: check of c_infected_coef limit, if it's more than 1? coef should be equal 1
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
    private double avMicResist = ModelValues.PERM_RESIST_LEVEL;// initial averaged level of microbiome resistance
    private double fixAvPathResistTown = ModelValues.PERM_RESIST_LEVEL;//averaged level of path resistance in town resistance in previous time step
    private double fixAvPathResistHosp = ModelValues.PERM_RESIST_LEVEL;//averaged level of microbiome resistance in previous time step in hospital


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

    boolean[] chekedPersons = new boolean[ModelValues.N_PEOPLE_IN_TOWN+1];
    //ArrayList<Boolean> chekedPersons = new ArrayList<Boolean>(ModelValues.N_PEOPLE_IN_TOWN);

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


        for (int i = 0; i < N_HEALTHY_TOWN; i++) {
            townHealthyPersons.add(new HealthyPerson(idIter, ModelValues.PERM_RESIST_LEVEL));
            transLogger.writeToTransLogFile(ticks, Integer.toString(idIter), "NA", "townHealthyPersons");
            idIter -= 1;
        }
        for (int i = 0; i < N_INC_PER_TOWN; i++) {
            townIncPerPersons.add(new IncPeriodPerson(idIter, ModelValues.PERM_RESIST_LEVEL, false, ModelValues.N_INCUB_LIMIT + 1));
            transLogger.writeToTransLogFile(ticks, Integer.toString(idIter), "NA", "townIncPerPersons");
            idIter -= 1;
        }
        for (int i = 0; i < N_ANT_TR_TOWN; i++) {
            townAntTrPersons.add(new AntTreatedPerson(idIter, ModelValues.PERM_RESIST_LEVEL, false, N_ANT_COURSE_TOWN_RIGHT + 1, pGetToHosp(false), 0));
            transLogger.writeToTransLogFile(ticks, Integer.toString(idIter), "NA", "townAntTrPersons");
            idIter -= 1;
        }
        for (int i = 0; i < N_PERS_HOSP; i++) {
//            int w = (10 + i * 20) % HOSP_W;
//            int k = (10 + 20 * i - w) / HOSP_W;
//            int h = k * 20 + 10;
            hospAntTrPersons.add(new AntTreatedPerson(idIter, ModelValues.PERM_RESIST_LEVEL, false, N_ANT_COURSE_HOSP + 1, 0, 0));
            transLogger.writeToTransLogFile(ticks, Integer.toString(idIter), "NA", "hospAntTrPersons");
            idIter -= 1;
        }
        for (int i = 0; i < N_HEALTHY_HOSP; i++) {
            healthyHospPeople.add(new HealthyHospPerson(idIter, ModelValues.PERM_RESIST_LEVEL, N_ANT_COURSE_HOSP + 1));
            transLogger.writeToTransLogFile(ticks, Integer.toString(idIter), "NA", "healthyHospPeople");
            idIter -= 1;
        }
        for (int i = 0; i < N_INC_PER_TOWN_2; i++) {
            townIncPerPersons2.add(new IncPeriodPerson(idIter, ModelValues.PERM_RESIST_LEVEL, true, ModelValues.N_INCUB_LIMIT_RESIST + 1));
            transLogger.writeToTransLogFile(ticks, Integer.toString(idIter), "NA", "townIncPerPersons2");
            idIter -= 1;
        }
        for (int i = 0; i < N_ANT_TR_TOWN_2; i++) {
            townAntTrPersons2.add(new AntTreatedPerson(idIter, ModelValues.PERM_RESIST_LEVEL, false, N_ANT_COURSE_TOWN_WRONG + 1, pGetToHosp(false), 0));
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

    public double rateMicrResMembers(ArrayList<? extends Person> memberList) { //ne vagno 4to, glavnoe, 4tobi nasledovalos ot Person
        double sum = 0;
        for(Person p : memberList){
            sum += p.micResistance;
        }
        return sum;
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
        double p_INF = (ModelValues.C_INFECTED_COEF * ((double) townIncPerPersons.size() + (double) townIncPerPersons2.size() + 0.5*((double) townAntTrPersons.size() + (double) townAntTrPersons2.size()))) / ((double) townHealthyPersons.size() + (double) numInfectedTown);

        //System.out.println("FixAvPathResistTown() "+fixAvPathResistTown);
/*        double p_INF = (ModelValues.C_INFECTED_COEF * ((double) townIncPerPersons.size() + (double) townIncPerPersons2.size() + ModelValues.C_INF_OF_TREAT_PERS *((double) N_ANT_TR_TOWN + (double) N_ANT_TR_TOWN_2))) /
                ((double) townHealthyPersons.size() + (double) numInfectedTown);*/
/*
        System.out.print("p_INF = (C_INFECTED_COEF * ((double) townIncPerPersons.size() + (double) townIncPerPersons2.size() + (double) N_ANT_TR_TOWN)) /\n" +
                "                ((double) townHealthyPersons.size() + (double) numInfectedTown) = ");
        System.out.print(C_INFECTED_COEF+" * ("+ townIncPerPersons.size()+ " + " + townIncPerPersons2.size()+ " + " + N_ANT_TR_TOWN+") / ("+
                townHealthyPersons.size()+" + "+numInfectedTown + ")"+"\n");
*/

//        double p_INF = (C_INFECTED_COEF_INCUB * ((double) townIncPerPersons.size() + (double) townIncPerPersons2.size()) + C_INFECTED_COEF_ANT_TREATED_IN_TOWN * ((double) N_ANT_TR_TOWN)) /
//                (((double) townHealthyPersons.size()) + ((double) numInfectedTown));

        //output
        messagesWriter.writeMessage(String.format("%d\t%d\t%d\t%d\t%d\t%d", townHealthyPersons.size(), townIncPerPersons.size(), townAntTrPersons.size(),
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
        Arrays.fill(chekedPersons, false);

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

            chekedPersons[townHealthyPersons.get(i).id] = true;

            townHealthyPersons.get(i).tick(this, p_INF, ModelValues.C_DECREASE_COEF, ModelValues.P_HEALTHY_HOSPITALIZE);
            HealthyPerson pers = townHealthyPersons.get(i);
            //avMicResist = avMicResist + pers.micResistance;
            if (pers.toBeChanged) {// & nHealthyTown != 0) { //marking a person to be isInfected
                townHealthyPersons.remove(i);
                i--;
                townIncPerPersons.add(new IncPeriodPerson(pers.id, pers.micResistance, pers.isResistant, ModelValues.N_INCUB_LIMIT + 1));
                transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "townHealthyPersons", "townIncPerPersons");

            } else {
                if (pers.toBeHospitalized) {
                    townHealthyPersons.remove(i);
                    i--;
                    healthyHospPeople.add(new HealthyHospPerson(pers.id, pers.micResistance, N_ANT_COURSE_HOSP + 1)); // ??? WHY here N_ANT_COURSE_HOSP, if it 's heath pers in hospital?
                    transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "townHealthyPersons", "healthyHospPeople");
                }
            }
        }
        //action for IncPeriod people in town
        for (int i = 0; i < townIncPerPersons.size(); i++) {
            if(chekedPersons[townIncPerPersons.get(i).id] == true) {
                continue;
            }
            chekedPersons[townIncPerPersons.get(i).id] = true;

            //System.out.print("i = " + i+ ", nIncPerTown = " + nIncPerTown + "\n");
            townIncPerPersons.get(i).tick(this, 0, ModelValues.C_DECREASE_COEF, ModelValues.C_PATHOGEN_RESIST_CHANGE_COEF);
            IncPeriodPerson pers = townIncPerPersons.get(i);
            //avMicResist = avMicResist + pers.micResistance;
            /*if (pers.isResistant) {
                avPathResist += 1;
            }*/
            if (pers.getIncubPeriod() == 0) {

                boolean getToHospital = Utils.bernoulli(ModelValues.P_INCUB_TO_HOSPITAL);
                townIncPerPersons.remove(i);
                //removeIndexes.add(i);
                i--;
                if (getToHospital) {
                    hospAntTrPersons.add(new AntTreatedPerson(pers.id, pers.micResistance, pers.isResistant, N_ANT_COURSE_HOSP + 1, 0, 0));
                    transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "townIncPerPersons", "hospAntTrPersons");
                } else {
                    if (!Utils.bernoulli(ModelValues.P_WRONG_TREATMENT)) {
                        //TODO: check it! Why N_ANT_COURSE_TOWN_RIGHT + 1 ?
                        townAntTrPersons.add(new AntTreatedPerson(pers.id, pers.micResistance, pers.isResistant, N_ANT_COURSE_TOWN_RIGHT + 1, ModelValues.P_INCUB_TO_HOSPITAL, 0));// early was pGetToHosp(pers.isResistant)
                        transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "townIncPerPersons", "townAntTrPersons");
                    } else {
                        townAntTrPersons2.add(new AntTreatedPerson(pers.id, pers.micResistance, pers.isResistant, N_ANT_COURSE_TOWN_WRONG + 1, ModelValues.P_INCUB_TO_HOSPITAL, 0)); // early was pGetToHosp(true)
                        transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "townIncPerPersons", "townAntTrPersons2");
                    }
                }
            }

        }

        //action for IncPeriod2 people in town
        //incubation period after wrong treatment (state 5)
        for (int i = 0; i < townIncPerPersons2.size(); i++) {
            if(chekedPersons[townIncPerPersons2.get(i).id] == true) {
                continue;
            }
            chekedPersons[townIncPerPersons2.get(i).id] = true;

            townIncPerPersons2.get(i).tick(this, 0, ModelValues.C_DECREASE_COEF, ModelValues.C_PATHOGEN_RESIST_CHANGE_COEF);
            IncPeriodPerson pers = townIncPerPersons2.get(i);
            //avMicResist = avMicResist + pers.micResistance;
            if (pers.getIncubPeriod() == 0) {
                //rMic = pers.micResistance;
                townIncPerPersons2.remove(i);
                i--;
                hospAntTrPersons.add(new AntTreatedPerson(pers.id, pers.micResistance, pers.isResistant, N_ANT_COURSE_HOSP + 1, 0, 0));
                transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "townIncPerPersons2", "hospAntTrPersons");
            }
        }

        //action for AntTreated people in town
        for (int i = 0; i < townAntTrPersons.size(); i++) {
            if(chekedPersons[townAntTrPersons.get(i).id] == true) {
                continue;
            }
            chekedPersons[townAntTrPersons.get(i).id] = true;

            townAntTrPersons.get(i).tick(this, pGetToHosp(townAntTrPersons.get(i).isResistant),
                    ModelValues.C_GROWTH_COEF, ModelValues.C_PATHOGEN_RESIST_CHANGE_COEF);
            AntTreatedPerson pers = townAntTrPersons.get(i);
            avMicResist += pers.micResistance;
            if (pers.beHospitalized) {
                townAntTrPersons.remove(i);
                i--;
                hospAntTrPersons.add(new AntTreatedPerson(pers.id, pers.micResistance, pers.isResistant, N_ANT_COURSE_HOSP + 1, 0, 0));
                transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "townAntTrPersons", "hospAntTrPersons");

            } else if (pers.treatmentPeriod == 0) {
                townAntTrPersons.remove(i);
                i--;

                //TODO: isResistant always false
                if (!pers.isResistant) {
                    townHealthyPersons.add(new HealthyPerson(pers.id, pers.micResistance));
                    transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "townAntTrPersons", "townHealthyPersons");
                } else { // hospital treatment period should be for AB2 !!! OR NOT?????
                    hospAntTrPersons.add(new AntTreatedPerson(pers.id, pers.micResistance, pers.isResistant, N_ANT_COURSE_HOSP + 1, 0, 0));
                    transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "townAntTrPersons", "hospAntTrPersons");
                }
            }
        }


        //action for AntTreated2 people in town
        for (int i = 0; i < townAntTrPersons2.size(); i++) {
            if(chekedPersons[townAntTrPersons2.get(i).id] == true) {
                continue;
            }
            chekedPersons[townAntTrPersons2.get(i).id] = true;

            townAntTrPersons2.get(i).tick(this, pGetToHosp(townAntTrPersons2.get(i).isResistant), ModelValues.C_GROWTH_COEF, ModelValues.C_PATHOGEN_RESIST_CHANGE_COEF);
            AntTreatedPerson pers = townAntTrPersons2.get(i);
            avMicResist += pers.micResistance;
            if (pers.beHospitalized) {
                townAntTrPersons2.remove(i);
                i--;
                hospAntTrPersons.add(new AntTreatedPerson(pers.id, pers.micResistance, pers.isResistant, N_ANT_COURSE_HOSP + 1, 0, 0));
                transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "townAntTrPersons2", "hospAntTrPersons");
            } else if (pers.treatmentPeriod == 0) {
                townAntTrPersons2.remove(i);
                i--;

                townIncPerPersons2.add(new IncPeriodPerson(pers.id, pers.micResistance, true, ModelValues.N_INCUB_LIMIT_RESIST + 1)); //wrong treatment leads to resistance of pathogen
                transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "townAntTrPersons2", "townIncPerPersons2");
            }
        }


        //action for HospAntTr people (in hosp, isInfected)
        for (int i = 0; i < hospAntTrPersons.size(); i++) {
            if(chekedPersons[hospAntTrPersons.get(i).id] == true) {
                continue;
            }
            chekedPersons[hospAntTrPersons.get(i).id] = true;

            hospAntTrPersons.get(i).tick(this, 1, -ModelValues.C_DECREASE_COEF, ModelValues.C_PATHOGEN_RESIST_CHANGE_COEF);//resistance decreases, because in hospital people are treated with another antibiotic
            AntTreatedPerson pers = hospAntTrPersons.get(i);
            // if (pers.isResistant) nHospResistant = nHospResistant + 1; // it's never used
            //avMicResist = avMicResist + pers.micResistance;
            // TODO: Сделать переход в состояние два (townIncPerPersons2)
            if (pers.treatmentPeriod == 0) {
                hospAntTrPersons.remove(i);
                i--;
                townHealthyPersons.add(new HealthyPerson(pers.id, pers.micResistance));
                transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "hospAntTrPersons", "townHealthyPersons");
            }
        }


        //action for healthy people in hosp
        for (int i = 0; i < healthyHospPeople.size(); i++) {
            if(chekedPersons[healthyHospPeople.get(i).id] == true) {
                continue;
            }
            chekedPersons[healthyHospPeople.get(i).id] = true;

            healthyHospPeople.get(i).tick(this, ModelValues.P_BE_INFECTED_IN_HOSPITAL, ModelValues.C_DECREASE_COEF, fixAvPathResistHosp);//resistance decreases, because in hospital people are treated with another antibiotic
            HealthyHospPerson pers = healthyHospPeople.get(i);
            //avMicResist = avMicResist + pers.micResistance;
            if (pers.treatmentPeriod == 0) {
                if (!pers.isInfected) {
                    townHealthyPersons.add(new HealthyPerson(pers.id, pers.micResistance));
                    transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "healthyHospPeople", "townHealthyPersons");
                } else {
                    townIncPerPersons.add(new IncPeriodPerson(pers.id, pers.micResistance, pers.isResistant, ModelValues.N_INCUB_LIMIT + 1));
                    transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "healthyHospPeople", "townIncPerPersons");
                }
                healthyHospPeople.remove(i);
                i--;
            }
        }


        int nPathResistTown = nResMembersA(townAntTrPersons) + nResMembersA(townAntTrPersons2) +
                nResMembersI(townIncPerPersons) + nResMembersI(townIncPerPersons2);
        int nPathResistHosp = nResMembersA(hospAntTrPersons);

        double sumMicResOfPopul = rateMicrResMembers(townHealthyPersons)+rateMicrResMembers(healthyHospPeople)+rateMicrResMembers(townIncPerPersons)+rateMicrResMembers(townAntTrPersons)+
                rateMicrResMembers(townIncPerPersons2)+rateMicrResMembers(townAntTrPersons2)+rateMicrResMembers(hospAntTrPersons);
        avMicResist = sumMicResOfPopul / ((double) (townHealthyPersons.size() + healthyHospPeople.size() + townIncPerPersons.size() + townAntTrPersons.size() +
                townAntTrPersons2.size() + townIncPerPersons2.size() + hospAntTrPersons.size() ));
        if ((townIncPerPersons.size() + townIncPerPersons2.size() + townAntTrPersons.size() + townAntTrPersons2.size() + hospAntTrPersons.size())==0) {
            avPathResist = 0;
        } else {
            avPathResist = (nPathResistTown+nPathResistHosp) / ((double) (townIncPerPersons.size() + townIncPerPersons2.size() + townAntTrPersons.size() + townAntTrPersons2.size() + hospAntTrPersons.size()));
        }
        if ((townIncPerPersons.size() + townAntTrPersons.size() + townAntTrPersons2.size() + townIncPerPersons2.size())==0) {
            fixAvPathResistTown = 0;
        } else {
            fixAvPathResistTown = nPathResistTown / ((double) (townIncPerPersons.size() + townAntTrPersons.size() + townAntTrPersons2.size() + townIncPerPersons2.size()));
        }
        if ((hospAntTrPersons.size() + healthyHospPeople.size())==0) {
            fixAvPathResistHosp = 0;
        } else {
            fixAvPathResistHosp = nPathResistHosp / ((double) (hospAntTrPersons.size() + healthyHospPeople.size()));
        }

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
