package com.ripcm.microbiomeres;

import com.ripcm.microbiomeres.log.*;
import com.ripcm.microbiomeres.person.*;

//import javax.jws.WebParam;
//import java.sql.Array;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.util.Random;
import java.lang.*;


//TODO: coordinates are not proper for more than 1 hospital
//TODO: make separate functions to replace healthy persons with infectious, and infectious with antibiotic treated


/**
 * Created by Oxana on 25.06.18.
 */
public class Simulation {

    //variables from ModelValues class

    //number of antibiotic
    public static final int N_ANT = ModelValues.C_PATHOGEN_RESIST_CHANGE_COEF.length;

    double pGetToHosp(boolean[] isResistant) {//I don't know i defined variables right or not
        double pGetToHospArray[] = new double[N_ANT];
        for (int i = 0; i < N_ANT; i++){
            if (isResistant[i])
                pGetToHospArray[i] = ModelValues.P_TREATMENT_TO_HOSP*ModelValues.C_PATHOGEN_RESIST_CHANGE_COEF[i] * 10;
        }
        //double max = Arrays.stream(pGetToHospArray).max().getAsDouble();
        if (Arrays.stream(pGetToHospArray).max().getAsDouble()!=0) {
            return (Arrays.stream(pGetToHospArray).max().getAsDouble());
        } else return ModelValues.P_TREATMENT_TO_HOSP;
    }


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
    //private static final int N_Hosp = 1; //number of hospitals


    //pathogene properties
    private static final int N_ANT_COURSE_TOWN_RIGHT = 5; //wright length of antibiotic course
    private static final int N_ANT_COURSE_TOWN_WRONG = 3; //wrong length of antibiotic course
    private static final int N_ANT_COURSE_HOSP = 7; //length of antibiotic course


    private double[] avPathResist = new double[N_ANT]; //percentage of resistant pathogens among all (now initially it can be only 0)
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
    private double[] avMicResist = ModelValues.PERM_RESIST_LEVEL;// initial averaged level of microbiome resistance
    private double[] fixAvPathResistTown = ModelValues.PERM_RESIST_LEVEL;//averaged level of path resistance in town resistance in previous time step
    private double[] fixAvPathResistHosp = ModelValues.PERM_RESIST_LEVEL;//averaged level of microbiome resistance in previous time step in hospital


    //time and graphics parameters
    private int ticks = 0;
    private Random rand = new Random();
    //private static final int TOWN_W = 300;
    //private static final int HOSP_W = 200;


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

    public double[] getFixAvPathResistTown() {
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
        for (int i = 0; i < N_INC_PER_TOWN; i++) { //"new boolean[N_ANT]" is false by default
            townIncPerPersons.add(new IncPeriodPerson(idIter, ModelValues.PERM_RESIST_LEVEL, new boolean[N_ANT], ModelValues.N_INCUB_LIMIT + 1));
            transLogger.writeToTransLogFile(ticks, Integer.toString(idIter), "NA", "townIncPerPersons");
            idIter -= 1;
        }
        for (int i = 0; i < N_ANT_TR_TOWN; i++) {

            townAntTrPersons.add(new AntTreatedPerson(idIter, ModelValues.PERM_RESIST_LEVEL, new boolean[N_ANT], N_ANT_COURSE_TOWN_RIGHT + 1, pGetToHosp(new boolean[N_ANT]), 0, rand.nextInt(N_ANT), true));
            transLogger.writeToTransLogFile(ticks, Integer.toString(idIter), "NA", "townAntTrPersons");
            idIter -= 1;
        }
        //TODO:!
        for (int i = 0; i < N_PERS_HOSP; i++) {
//            int w = (10 + i * 20) % HOSP_W;
//            int k = (10 + 20 * i - w) / HOSP_W;
//            int h = k * 20 + 10;
            hospAntTrPersons.add(new AntTreatedPerson(idIter, ModelValues.PERM_RESIST_LEVEL, new boolean[N_ANT], N_ANT_COURSE_HOSP + 1, 0, 0, N_ANT-1, false));
            transLogger.writeToTransLogFile(ticks, Integer.toString(idIter), "NA", "hospAntTrPersons");
            idIter -= 1;
        }
        //TODO:!
        for (int i = 0; i < N_HEALTHY_HOSP; i++) {
            healthyHospPeople.add(new HealthyHospPerson(idIter, ModelValues.PERM_RESIST_LEVEL, N_ANT_COURSE_HOSP + 1));
            transLogger.writeToTransLogFile(ticks, Integer.toString(idIter), "NA", "healthyHospPeople");
            idIter -= 1;
        }
        for (int i = 0; i < N_INC_PER_TOWN_2; i++) {
            boolean[] bufArr = new boolean[N_ANT];
            bufArr[rand.nextInt(N_ANT)] = true;
            townIncPerPersons2.add(new IncPeriodPerson(idIter, ModelValues.PERM_RESIST_LEVEL, bufArr, ModelValues.N_INCUB_LIMIT_RESIST + 1));
            transLogger.writeToTransLogFile(ticks, Integer.toString(idIter), "NA", "townIncPerPersons2");
            idIter -= 1;
        }
        for (int i = 0; i < N_ANT_TR_TOWN_2; i++) {
            townAntTrPersons2.add(new AntTreatedPerson(idIter, ModelValues.PERM_RESIST_LEVEL, new boolean[N_ANT], N_ANT_COURSE_TOWN_WRONG + 1, pGetToHosp(new boolean[N_ANT]), 0,rand.nextInt(N_ANT), true));
            transLogger.writeToTransLogFile(ticks, Integer.toString(idIter), "NA", "townAntTrPersons2");
            idIter -= 1;
        }
    }

    public int[] nResMembersA(ArrayList<AntTreatedPerson> memberList) {
        int[] n = new int[N_ANT];
        for (int k = 0; k < N_ANT; k++){
            for (int i = 0; i < memberList.size(); i++) {
                if (memberList.get(i).isResistant[k]) n[k] += 1;
            }
        }
        return n;
    }

    public int[] nResMembersI(ArrayList<IncPeriodPerson> memberList) {
        int[] n = new int[N_ANT];
        for (int k = 0; k < N_ANT; k++){
            for (int i = 0; i < memberList.size(); i++) {
                if (memberList.get(i).isResistant[k]) n[k] += 1;
            }
        }
        return n;
    }

    public double[] rateMicrResMembers(ArrayList<? extends Person> memberList) { //ne vagno 4to, glavnoe, 4tobi nasledovalos ot Person
        double[] sum = new double[N_ANT];
        for (int k = 0; k < N_ANT; k++){
            for(Person p : memberList){
                sum[k] += p.micResistance[k];
            }
        }
        return sum;
    }

    public int[] add2(int[] first, int[] second) {
        int[] result = new int[N_ANT];
        for (int i = 0; i < N_ANT; i++) {
            result[i] = first[i] + second[i];
        }
        return result;
    }

    public int[] add4(int[] first, int[] second, int[] third, int[] fourth) {
        int[] result = new int[N_ANT];
        for (int i = 0; i < N_ANT; i++) {
            result[i] = first[i] + second[i] + third[i] + fourth[i];
        }
        return result;
    }

    public double[] add7(double[] p1, double[] p2, double[] p3, double[] p4, double[] p5, double[] p6, double[] p7) {
        double[] result = new double[N_ANT];
        for (int i = 0; i < N_ANT; i++) {
            result[i] = p1[i] + p2[i] + p3[i] + p4[i] + p5[i] + p6[i] + p7[i];
        }
        return result;
    }

    public double[] divide(double[] arrI, double dividerI) {
        double[] result = new double[N_ANT];
        for (int i = 0; i < N_ANT; i++) {
            result[i] = arrI[i]/dividerI;
        }
        return result;
    }

    public boolean boolenContain(boolean[] arrB, boolean elem) {
        for (int i = 0; i < N_ANT; i++) {
            if (arrB[i]==elem) return true;
        }
        return false;
    }

    // What happend with each agent on each tick
    //action for each component in a list
    public void action() throws IOException {//(PrintWriter writeFile, int nTicks) {
        //double rMic;
        //boolean pathRes;
        //double hospNumber;

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
        for (int i = 0; i < N_ANT; i++) personAmount.add(avMicResist[i]);
        for (int i = 0; i < N_ANT; i++) personAmount.add(avPathResist[i]);
        personAmountLogger.WritePersonAmountLogFile(personAmount);//WriteToFile(personAmount, writeFile);


        // it's we see on console
        messagesWriter.writeMessage("nHealthyTown =" + townHealthyPersons.size());
        messagesWriter.writeMessage("nIncPerTown = " + townIncPerPersons.size());
        messagesWriter.writeMessage("nAntTrTown =" + townAntTrPersons.size());
        messagesWriter.writeMessage("N_PERS_HOSP =" + hospAntTrPersons.size());
        messagesWriter.writeMessage("P_INF = " + p_INF + "\n");
        messagesWriter.writeMessage("avMicrobiоtaResistance = " + String.join(",",Arrays.toString(avMicResist)));
        messagesWriter.writeMessage("avPathogeneResistance = " + String.join(",",Arrays.toString(avPathResist)));
        messagesWriter.writeMessage("N_HEALTHY_HOSP = " + healthyHospPeople.size());


        avMicResist = new double[N_ANT]; // i think this should not be if ticks!=0
        avPathResist = new double[N_ANT]; // i think this should not be if ticks!=0
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

            townHealthyPersons.get(i).tick(this, p_INF, ModelValues.P_HEALTHY_HOSPITALIZE);
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
            townIncPerPersons.get(i).tick(this, 0, 0);
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
                    hospAntTrPersons.add(new AntTreatedPerson(pers.id, pers.micResistance, pers.isResistant, N_ANT_COURSE_HOSP + 1, 0, 0, N_ANT-1, false));
                    transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "townIncPerPersons", "hospAntTrPersons");
                } else {
                    if (!Utils.bernoulli(ModelValues.P_WRONG_TREATMENT)) {
                        //TODO: check it! Why N_ANT_COURSE_TOWN_RIGHT + 1 ?
                        townAntTrPersons.add(new AntTreatedPerson(pers.id, pers.micResistance, pers.isResistant, N_ANT_COURSE_TOWN_RIGHT + 1, ModelValues.P_INCUB_TO_HOSPITAL, 0,rand.nextInt(N_ANT),true));// early was pGetToHosp(pers.isResistant)
                        transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "townIncPerPersons", "townAntTrPersons");
                    } else {
                        townAntTrPersons2.add(new AntTreatedPerson(pers.id, pers.micResistance, pers.isResistant, N_ANT_COURSE_TOWN_WRONG + 1, ModelValues.P_INCUB_TO_HOSPITAL, 0,rand.nextInt(N_ANT), true)); // early was pGetToHosp(true)
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

            townIncPerPersons2.get(i).tick(this, 0, 0);
            IncPeriodPerson pers = townIncPerPersons2.get(i);
            //avMicResist = avMicResist + pers.micResistance;
            if (pers.getIncubPeriod() == 0) {
                //rMic = pers.micResistance;
                townIncPerPersons2.remove(i);
                i--;
                hospAntTrPersons.add(new AntTreatedPerson(pers.id, pers.micResistance, pers.isResistant, N_ANT_COURSE_HOSP + 1, 0, 0,N_ANT-1, false));
                transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "townIncPerPersons2", "hospAntTrPersons");
            }
        }

        //action for AntTreated people in town
        for (int i = 0; i < townAntTrPersons.size(); i++) {
            if(chekedPersons[townAntTrPersons.get(i).id] == true) {
                continue;
            }
            AntTreatedPerson pers = townAntTrPersons.get(i);
            chekedPersons[pers.id] = true;

            pers.tick(this, pGetToHosp(pers.isResistant),0);

            for (int k = 0; k < N_ANT; k++) avMicResist[k] += pers.micResistance[k];
            if (pers.beHospitalized) {
                townAntTrPersons.remove(i);
                i--;
                hospAntTrPersons.add(new AntTreatedPerson(pers.id, pers.micResistance, pers.isResistant, N_ANT_COURSE_HOSP + 1, 0, 0, N_ANT-1, false));
                transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "townAntTrPersons", "hospAntTrPersons");

            } else if (pers.treatmentPeriod == 0) {
                townAntTrPersons.remove(i);
                i--;

                //TODO: isResistant always false - NO, it isn't so
                if (!boolenContain(pers.isResistant, true)) {
                    townHealthyPersons.add(new HealthyPerson(pers.id, pers.micResistance));
                    transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "townAntTrPersons", "townHealthyPersons");
                } else { // hospital treatment period should be for AB2 !!! OR NOT?????
                    hospAntTrPersons.add(new AntTreatedPerson(pers.id, pers.micResistance, pers.isResistant, N_ANT_COURSE_HOSP + 1, 0, 0, N_ANT-1, false));
                    transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "townAntTrPersons", "hospAntTrPersons");
                }
            }
        }


        //action for AntTreated2 people in town
        for (int i = 0; i < townAntTrPersons2.size(); i++) {
            AntTreatedPerson person = townAntTrPersons2.get(i);
            if(chekedPersons[person.id] == true) {
                continue;
            }
            chekedPersons[person.id] = true;

            person.tick(this, pGetToHosp(person.isResistant), 0);
            AntTreatedPerson pers = person;
            for (int k = 0; k < N_ANT; k++) avMicResist[k] += pers.micResistance[k];
            if (pers.beHospitalized) {
                townAntTrPersons2.remove(i);
                i--;
                hospAntTrPersons.add(new AntTreatedPerson(pers.id, pers.micResistance, pers.isResistant, N_ANT_COURSE_HOSP + 1, 0, 0, N_ANT-1, false));
                transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "townAntTrPersons2", "hospAntTrPersons");
            } else if (pers.treatmentPeriod == 0) {
                townAntTrPersons2.remove(i);
                boolean[] bufArr = new boolean[N_ANT];
                bufArr[person.antibioticType] = true;
                townIncPerPersons2.add(new IncPeriodPerson(pers.id, pers.micResistance, bufArr, ModelValues.N_INCUB_LIMIT_RESIST + 1)); //wrong treatment leads to resistance of pathogen
                transLogger.writeToTransLogFile(ticks, Integer.toString(pers.id), "townAntTrPersons2", "townIncPerPersons2");
                i--;
            }
        }


        //action for HospAntTr people (in hosp, isInfected)
        for (int i = 0; i < hospAntTrPersons.size(); i++) {
            if(chekedPersons[hospAntTrPersons.get(i).id] == true) {
                continue;
            }
            chekedPersons[hospAntTrPersons.get(i).id] = true;

            hospAntTrPersons.get(i).tick(this, 1, 0);//resistance decreases, because in hospital people are treated with another antibiotic
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

            healthyHospPeople.get(i).tick(this, ModelValues.P_BE_INFECTED_IN_HOSPITAL, 0);//resistance decreases, because in hospital people are treated with another antibiotic
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



        int[] nPathResistTown = add4(nResMembersA(townAntTrPersons), nResMembersA(townAntTrPersons2), nResMembersI(townIncPerPersons), nResMembersI(townIncPerPersons2));
        int[] nPathResistHosp = nResMembersA(hospAntTrPersons);

        double[] sumMicResOfPopul = add7(rateMicrResMembers(townHealthyPersons),rateMicrResMembers(healthyHospPeople),rateMicrResMembers(townIncPerPersons),rateMicrResMembers(townAntTrPersons),
                rateMicrResMembers(townIncPerPersons2),rateMicrResMembers(townAntTrPersons2),rateMicrResMembers(hospAntTrPersons));
        avMicResist = divide(sumMicResOfPopul, ((double) (townHealthyPersons.size() + healthyHospPeople.size() + townIncPerPersons.size() + townAntTrPersons.size() +
                townAntTrPersons2.size() + townIncPerPersons2.size() + hospAntTrPersons.size() )));
        if ((townIncPerPersons.size() + townIncPerPersons2.size() + townAntTrPersons.size() + townAntTrPersons2.size() + hospAntTrPersons.size())==0) {
            avPathResist = new double[N_ANT];
        } else {
            avPathResist = divide(Arrays.stream(add2(nPathResistTown,nPathResistHosp)).asDoubleStream().toArray(), (double) (townIncPerPersons.size() + townIncPerPersons2.size() + townAntTrPersons.size() + townAntTrPersons2.size() + hospAntTrPersons.size()));
        }
        if ((townIncPerPersons.size() + townAntTrPersons.size() + townAntTrPersons2.size() + townIncPerPersons2.size())==0) {
            fixAvPathResistTown = new double[N_ANT];
        } else {
            fixAvPathResistTown = divide( Arrays.stream(nPathResistTown).asDoubleStream().toArray(), (double) (townIncPerPersons.size() + townAntTrPersons.size() + townAntTrPersons2.size() + townIncPerPersons2.size()));
        }
        if ((hospAntTrPersons.size() + healthyHospPeople.size())==0) {
            fixAvPathResistHosp = new double[N_ANT];
        } else {
            fixAvPathResistHosp = divide(Arrays.stream(nPathResistHosp).asDoubleStream().toArray(), (double) (hospAntTrPersons.size() + healthyHospPeople.size()));
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
