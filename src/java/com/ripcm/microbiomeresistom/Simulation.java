package com.ripcm.microbiomeresistom;

import com.ripcm.microbiomeresistom.person.AntTreatedPerson;
import com.ripcm.microbiomeresistom.person.HealthyHospPerson;
import com.ripcm.microbiomeresistom.person.HealthyPerson;
import com.ripcm.microbiomeresistom.person.IncPeriodPerson;

import java.awt.*;
import java.util.Iterator;
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
    //initial number of people
    private static final int N_HEALTHY_TOWN = 10000; //HealthyPerson, townHealthyPersons | state 1 on scheme in "Препринт"
    private static final int N_INC_PER_TOWN = 50;//IncPeriodPerson, townIncPerPersons | state 2 on scheme in "Препринт"
    private static final int N_INC_PER_TOWN_2 = 0; //IncPeriodPerson, townIncPerPersons2 | incubation period after wrong treatment (state 5)
    private static final int N_ANT_TR_TOWN = 0; //AntTreatedPerson, townAntTrPersons | state 3 on scheme in "Препринт"
    private static final int N_ANT_TR_TOWN_2 = 0;//AntTreatedPerson, townAntTrPersons2 | wrong antibiotic treatment (state 4)
    private static final int N_INFECTED_TOWN = N_ANT_TR_TOWN + N_INC_PER_TOWN + N_ANT_TR_TOWN_2 + N_INC_PER_TOWN_2;
    private static final int N_PERS_HOSP = 0; //AntTreatedPerson, hospPers | infected persons in hospital (state 6 - infected)
    private static final int N_HEALTHY_HOSP = 0;//number of hospitalized persons without pathogen
    //number of hospitals (not working yet, only 1 hospital now)
    private final static int N_Hosp = 1; //number of hospitals
    private static final double P_HEALTHY_HOSPITALIZE = 0.002;//probability to be hospitalized with another infection
    private static final double P_HOSP_INF = 0.01; //probability to be infected after being hospitalized for a "healthy" person
    //pathogene properties
    private final static int N_INC_LIMIT = 2;//incubation period
    private final static int N_INC_LIMIT_2 = 2;//incubation period for pathogen that becomes resistant
    private final static int N_ANT_COURSE_TOWN_RIGHT = 5; //wright length of antibiotic course
    private final static int N_ANT_COURSE_TOWN_WRONG = 2; //wrong length of antibiotic course
    private final static int N_ANT_COURSE_HOSP = 7; //length of antibiotic course

    private final static double C_INF_COEF_1 = 0;//0.125;// coefficient in the next formula (average number of people, that an ill person infects per day

    private final static double C_INF_COEF_2 = 0.23;//0.125;//C_INF_COEF_1/10;// coefficient in the next formula (average number of people, that an AntTr person in Town infects per day
    private static double avPathResist = 0; //percentage of resistant pathogens among all (now initially it can be only 0)
    private final static double C_CHANGE_PATH_RES_COEF = 0.02; //coefficient for probability of pathogen to become resistant because of microbiome resistance
    private final static double P_INC_HOSP = 0.0005;//probability of being hospitalized at the first day of antibiotic treatment
    private final static double P_HOSP_NONRES = 0.2;//0.005;//probability of being hospitalized for an AntTreatedPerson with nonresistant pathogen
    private final static double P_HOSP_RES = 0.1;// 0.01;//probability of being hospitalized for an AntTreatedPerson with resistant pathogen
    private static double pGetToHosp(boolean rPath) {
        if(rPath){
            return P_HOSP_RES;
        } else return P_HOSP_NONRES;
    }  ; //probability to get to a hospital during antibiotic course
    private final static int C_wrongTr =10000000;// 5;// each C_wrongTr infected person is treated wrong (N_ANT_COURSE_TOWN_WRONG)


    //microbiota properties
    private double avMicResist = 0;// initial averaged level of microbiome resistance
    private final static double C_GROWTH_COEF = 1./21.;//daily growth of microbiota resistance during antibiotic cource
    private final static double C_DECREASE_COEF = 1./90.;//daily decrease of microbiota resistance in absence of antibiotics
    private double fixAvPathResist =  avPathResist ;//averaged level of path resistance in town resistance in previous time step
    private double fixAvPathResistH =  avPathResist ;//averaged level of microbiome resistance in previous time step


    //time and graphics parameters
    private int ticks = 0;
    //private static int l = 8;
    //private Timer timer = null;
    //private final static int town_h = 600;
    private final static int TOWN_W = 300;
    //private final static int hosp_h = 150;
    private final static int HOSP_W = 200;
    //private static int actualHosp = 0;
    //private static final int D_HEIGHT = 1000;
    //private static final int D_WIDTH = 1500;


    //Variables for writing output in a file
//    private static final String logFile_name = "out/log.txt";
//  //  private static final String logFile_name = "/home/vera/agent_resistome/microbiota-resistome/out/log.txt";
//    private File logFile = new File(logFile_name);
//    private PrintWriter writeFile = null;
    //String ID = null;

    //lists of persons and places
    private static ArrayList<HealthyPerson> townHealthyPersons = new ArrayList<>(N_HEALTHY_TOWN); //state 1 on scheme in "Препринт"
    private static ArrayList<IncPeriodPerson> townIncPerPersons = new ArrayList<>(N_INC_PER_TOWN);//state 2 on scheme in "Препринт"
    private static ArrayList<IncPeriodPerson> townIncPerPersons2 = new ArrayList<>(N_INC_PER_TOWN_2);//incubation period after wrong treatment (state 5)
    private static ArrayList<AntTreatedPerson> townAntTrPersons = new ArrayList<>(N_ANT_TR_TOWN);//state 3 on scheme in "Препринт"
    private static ArrayList<AntTreatedPerson> townAntTrPersons2 = new ArrayList<>(N_ANT_TR_TOWN_2);//wrong antibiotic treatment (state 4)
    //private static ArrayList<Location> Hospitals = new ArrayList<Location>(N_Hosp);// for visualization (to be deleted)
    private static ArrayList<AntTreatedPerson> hospPers = new ArrayList<>(N_PERS_HOSP);// infected persons in hospital (state 6 - infected)
    private static ArrayList<HealthyHospPerson> healthyHospPeople = new ArrayList<>(N_HEALTHY_HOSP);  //"healthy" persons in hospitals (with another pathogen or doctors) (state 6 - not infected)


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
            /*action(writeFile, townHealthyPersons, townIncPerPersons, townAntTrPersons, hospPers,
                    townAntTrPersons2, townIncPerPersons2, healthyHospPeople);*/
            action(writeFile);
        }

        writeFile.close();
        //System.exit(0);

        printBlock("Working finished!");

/*      setBackground(Color.black);

        timer = new Timer(30, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.print(ticks + "\n");
                actionCore();
                l += 1;
            }
        });
        JButton start = new JButton("Start");
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timer.start();
            } // <= eto vstavit v vipolnenie dlya commandi so sostroki
        });

        JPanel panel = new JPanel();
        panel.add(start);
        setLayout(new BorderLayout());
        add(panel,BorderLayout.PAGE_START);*/

    }


    public double getFixAvPathResist() {return fixAvPathResist;}
    public double getFixAvPathResistH() {return fixAvPathResistH;}

    public static int getN_incLimit2() {return N_INC_LIMIT;}

    public int changeActualHosp (int numberOfHospitals, int lastHospital) {//needed for several hospitals, not used now
        int ans = lastHospital + 1;
        if (ans == numberOfHospitals) { ans = 0;}
        return ans;
    }


/*

    @Override
    public void paintComponent(Graphics g) {
        //town
        Location town = new Location(60, 40, TOWN_W, town_h, Color.blue);
        town.setnHealthy(nHealthyTown);
        town.setnIncPer(nIncPerTown);
        town.setnAntTreated(nAntTrTown);
        town.drawLocation(g);
        repaint();

        //hospitals
        for (int i = 0; i < N_Hosp; i++) {
            Location hosp = new Location(80 + TOWN_W, 40 + (hosp_h + 30) * i, HOSP_W, hosp_h, Color.red);
            Hospitals.add(hosp);
            Hospitals.get(i).setnHealthy(0);
            Hospitals.get(i).setnIncPer(0);
            if (i == 0) {
                Hospitals.get(i).setnAntTreated(N_PERS_HOSP);
            }
            Hospitals.get(i).drawLocation(g);
        }
    }
*/

    /*@Override
    public Dimension getPreferredSize() {
        return new Dimension(D_WIDTH, D_HEIGHT);
    }*/

    private PrintWriter openLogFile(String fName) throws FileNotFoundException {
        PrintWriter writeFile = new PrintWriter(fName);
        //not right seq of column names!!!! change
        String st = "Ticks HealthyPersonsInTown InfectedPersonsInTown IncPeriodPersonsInTown IncPeriodPersonsInTown2 AntibioticTreatedPersonsInTown AntibioticTreatedPersonsInTown2 InfectedPersonsInHospital HealthyPersonsInHospital pGetInfectedTown AvMicResistance AvPathResistance ";
        writeFile.println(st);
        return writeFile;
    }

    private void initPersones(){
        //initialise lists of persons
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
            hospPers.add(new AntTreatedPerson(0,false, N_ANT_COURSE_HOSP +1, 0,0));
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
            if (memberList.get(i).pathResistance) n += 1;
        }
        return n;
    }
    public int nResMembersI(ArrayList<IncPeriodPerson> memberList) {
        int n = 0;
        for (int i = 0; i < memberList.size(); i++) {
            if (memberList.get(i).pathResistance) n += 1;
        }
        return n;
    }

    // What happend with each agent on each tick
    //action for each component in a list
    /*public void action(PrintWriter writeFile,ArrayList<HealthyPerson> TownHealthyPer, ArrayList<IncPeriodPerson> TownIncPer,
                       ArrayList<AntTreatedPerson> TownAntTr, ArrayList<AntTreatedPerson> HospAntTr,
                       ArrayList<AntTreatedPerson> TownAntTr2, ArrayList<IncPeriodPerson> TownIncPer2,
                       ArrayList<HealthyHospPerson> HealthyHospPer) {*/
    public void action(PrintWriter writeFile) {
        double rMic;
        boolean rPath;//resistance of current pathogen
        boolean pathRes;
        boolean getToHospital;
        double hospNumber;
      /*N_INC_PER_TOWN = townIncPerPersons.size();
        N_INC_PER_TOWN_2 = townIncPerPersons2.size();
        N_ANT_TR_TOWN = townAntTrPersons.size();
        N_ANT_TR_TOWN_2 = townAntTrPersons2.size();
        N_HEALTHY_TOWN = townHealthyPersons.size();
        N_HEALTHY_HOSP = healthyHospPeople.size();

        N_PERS_HOSP = hospPers.size(); //number of infected in hospital*/
        int numInfectedTown = townIncPerPersons.size() +
                              townIncPerPersons2.size() +
                              townAntTrPersons2.size() +
                              townAntTrPersons.size();


        double p_INF = (C_INF_COEF_1 * ((double) townIncPerPersons.size() + (double) townIncPerPersons2.size()) + C_INF_COEF_2 * ((double) N_ANT_TR_TOWN)) /
                (((double) townHealthyPersons.size()) + ((double) numInfectedTown));
        //P_INF = 1- java.lang.Math.pow((1-k1/(nHealthyTown+N_INFECTED_TOWN -k1 +1)),(nIncPerTown +N_INC_PER_TOWN_2)) *
        //        java.lang.Math.pow((1-k2/(nHealthyTown+N_INFECTED_TOWN -k2 +1)),(nAntTrTown+nAntTrTown2));


        //output
        System.out.print(townHealthyPersons.size() + " " + townIncPerPersons.size() + " " + townAntTrPersons.size() + " " +
                hospPers.size() + " " + townAntTrPersons2.size() + " " + townIncPerPersons2.size() +"\n");
        List<Number> personAmount = new ArrayList<Number>();
        personAmount.add(ticks);
        personAmount.add(townHealthyPersons.size());
        personAmount.add(numInfectedTown);
        personAmount.add(townIncPerPersons.size());
        personAmount.add(townIncPerPersons2.size());
        personAmount.add(townAntTrPersons.size());
        personAmount.add(townAntTrPersons2.size());
        personAmount.add(hospPers.size());
        personAmount.add(healthyHospPeople.size());
        personAmount.add(p_INF);
        personAmount.add(avMicResist);
        personAmount.add(avPathResist);


        // it's we see on console
        System.out.print("nHealthyTown =" + townHealthyPersons.size() + "\n");
        System.out.print("nIncPerTown = " + townIncPerPersons.size() + "\n");
        System.out.print("nAntTrTown =" + townAntTrPersons.size() + "\n");
        System.out.print("N_PERS_HOSP =" + hospPers.size() + "\n");
        System.out.print("P_INF = " + p_INF + "\n");
        System.out.print("avMicrobiоtaResistance = " + avMicResist + "\n");
        System.out.print("avPathogeneResistance = " + avPathResist + "\n");
        System.out.print("N_HEALTHY_HOSP = " + healthyHospPeople.size() + "\n");
        WriteToFile(personAmount, writeFile);

        avMicResist = 0.;
        avPathResist = 0.;
        int nHospResistant =0;
        double townAvPathRes = 0;
        double wrongTrFlag = 1;
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
            avMicResist = avMicResist +pers.micResistance;
            rMic = pers.micResistance;
            if (pers.getToBeChanged()){// & nHealthyTown != 0) {
                rPath = pers.pathRes;
                townHealthyPersons.remove(i);
                //N_HEALTHY_TOWN--;
                i--;
                townIncPerPersons.add(new IncPeriodPerson(rMic,rPath, N_INC_LIMIT + 1));
            } else {
                if (pers.toBeHospitalized) {
                    townHealthyPersons.remove(i);
                    //N_HEALTHY_TOWN--;
                    i--;
                    healthyHospPeople.add(new HealthyHospPerson(rMic, N_ANT_COURSE_HOSP +1));
                }
            }
        }
        //action for IncPeriod people in town
        for (int i = 0; i < townIncPerPersons.size(); i++) {
            //System.out.print("i = " + i+ ", nIncPerTown = " + nIncPerTown + "\n");
            townIncPerPersons.get(i).tick(this, 0, C_DECREASE_COEF, C_CHANGE_PATH_RES_COEF);
            IncPeriodPerson pers = townIncPerPersons.get(i);
            avMicResist = avMicResist +pers.micResistance;
            if(pers.pathResistance){
                avPathResist += 1;
                townAvPathRes +=1;
            }
            if (pers.getIncCountdown() == 0) {
                rMic = pers.micResistance;
                rPath = pers.pathResistance;
                getToHospital = Utils.bernoulli(P_INC_HOSP);
                townIncPerPersons.remove(i);
                //removeIndexes.add(i);
                //N_INC_PER_TOWN--;
                i--;
                if (getToHospital) {
                    hospPers.add(new AntTreatedPerson(rMic,rPath, N_ANT_COURSE_HOSP + 1,0,0));
                } else {
                    if (wrongTrFlag < C_wrongTr){
                        townAntTrPersons.add(new AntTreatedPerson(rMic,rPath, N_ANT_COURSE_TOWN_RIGHT + 1,pGetToHosp(rPath),0));
                        wrongTrFlag = wrongTrFlag +1;
                    } else {
                        townAntTrPersons2.add(new AntTreatedPerson(rMic, rPath, N_ANT_COURSE_TOWN_WRONG +1, pGetToHosp(true),0));
                        wrongTrFlag = 1;
                    }
                }
            }
        }

        //action for IncPeriod2 people in town
        for (int i = 0; i < townIncPerPersons2.size(); i++) {
            townIncPerPersons2.get(i).tick(this, 0, C_DECREASE_COEF, C_CHANGE_PATH_RES_COEF);
            IncPeriodPerson pers = townIncPerPersons2.get(i);
            avMicResist = avMicResist +pers.micResistance;
            if (pers.getIncCountdown() == 0) {
                rMic = pers.micResistance;
                rPath = pers.pathResistance;
                townIncPerPersons2.remove(i);
                //N_INC_PER_TOWN_2--;
                i--;
                hospPers.add(new AntTreatedPerson(rMic,rPath, N_ANT_COURSE_HOSP + 1,0,0));
            }
        }

        //action for AntTreated people in town
        for (int i = 0; i < townAntTrPersons.size(); i++) {
            townAntTrPersons.get(i).tick(this,pGetToHosp(townAntTrPersons.get(i).pathResistance), C_GROWTH_COEF, C_CHANGE_PATH_RES_COEF);
            AntTreatedPerson pers = townAntTrPersons.get(i);
            avMicResist = avMicResist +pers.micResistance;
            rMic = pers.micResistance;
            rPath = pers.pathResistance;
            if (pers.hospitalize) {
                townAntTrPersons.remove(i);
                //N_ANT_TR_TOWN--;
                i--;
                hospPers.add(new AntTreatedPerson(rMic,rPath, N_ANT_COURSE_HOSP + 1,0,0));

            } else if(pers.treatment_countdown == 0) {
                townAntTrPersons.remove(i);
                //N_ANT_TR_TOWN--;
                i--;
                if (!rPath) {
                    townHealthyPersons.add(new HealthyPerson(rMic));
                } else {
                    hospPers.add(new AntTreatedPerson(rMic, rPath, N_ANT_COURSE_HOSP + 1, 0,0));
                }
            }
        }


        //action for AntTreated2 people in town
        for (int i = 0; i< townAntTrPersons2.size(); i++) {
            townAntTrPersons2.get(i).tick(this, pGetToHosp(townAntTrPersons2.get(i).pathResistance), C_GROWTH_COEF, C_CHANGE_PATH_RES_COEF);
            AntTreatedPerson pers = townAntTrPersons2.get(i);
            avMicResist = avMicResist + pers.micResistance;
            rMic = pers.micResistance;
            rPath = pers.pathResistance;
            if (pers.hospitalize) {
                townAntTrPersons2.remove(i);
                //N_ANT_TR_TOWN_2--;
                i--;
                hospPers.add(new AntTreatedPerson(rMic, rPath, N_ANT_COURSE_HOSP + 1, 0, 0));
            } else if (pers.treatment_countdown == 0) {
                townAntTrPersons2.remove(i);
                //N_ANT_TR_TOWN_2--;
                i--;
                townIncPerPersons2.add(new IncPeriodPerson(rMic, true, N_INC_LIMIT_2 +1)); //wrong treatment leads to resistance of pathogen
            }
        }




        //action for HospAntTr people (in hosp, infected)
        for (int i = 0; i < hospPers.size(); i++) {
            hospPers.get(i).tick(this, 1, -C_DECREASE_COEF, C_CHANGE_PATH_RES_COEF);//resistance decreases, because in hospital people are treated with another antibiotic
            AntTreatedPerson pers = hospPers.get(i);
            if (pers.pathResistance) nHospResistant = nHospResistant + 1;
            avMicResist = avMicResist + pers.micResistance;

            // TODO: Сделать переход в состояние два (townIncPerPersons2)
            if (pers.treatment_countdown == 0) {
                rMic = pers.micResistance;
                hospPers.remove(i);
                //N_PERS_HOSP--;
                i--;
                townHealthyPersons.add(new HealthyPerson(rMic));
            }
        }


        //action for healthy people in hosp
        for (int i = 0; i < healthyHospPeople.size(); i++) {
            healthyHospPeople.get(i).tick(this, P_HOSP_INF, C_DECREASE_COEF, fixAvPathResistH);//resistance decreases, because in hospital people are treated with another antibiotic
            HealthyHospPerson pers = healthyHospPeople.get(i);
            avMicResist = avMicResist +pers.micResistance;
            if(pers.trCountdown == 0) {
                rMic = pers.micResistance;
                if(!pers.infected){
                    townHealthyPersons.add(new HealthyPerson(rMic));
                } else {
                    townIncPerPersons.add(new IncPeriodPerson(rMic, pers.infected, N_INC_LIMIT +1));
                }
                //N_HEALTHY_HOSP--;
                healthyHospPeople.remove(i);
                i--;
            }
        }

        int nPathResist =nResMembersA(townAntTrPersons) +nResMembersA(townAntTrPersons2) +
                nResMembersI(townIncPerPersons) + nResMembersI(townIncPerPersons2) + nResMembersA(hospPers);
        int nPathResistTown = nResMembersA(townAntTrPersons) +nResMembersA(townAntTrPersons2) +
                nResMembersI(townIncPerPersons) + nResMembersI(townIncPerPersons2);
        int nPathResistHosp = nResMembersA(hospPers);


        //avMicResist = avMicResist/(nAntTrTown+nAntTrTown2+N_INC_PER_TOWN_2+nIncPerTown+nHealthyTown+N_HEALTHY_HOSP+N_PERS_HOSP);
        avMicResist = avMicResist/((double) (townHealthyPersons.size() + townIncPerPersons.size()+ townAntTrPersons.size()+
                townAntTrPersons2.size() + townIncPerPersons2.size()+ hospPers.size()+ healthyHospPeople.size()));
        avPathResist = nPathResist/((double) (townIncPerPersons.size() + townIncPerPersons2.size()+ townAntTrPersons.size()+ townAntTrPersons2.size() + hospPers.size() ));
        fixAvPathResist = nPathResistTown/((double) (townIncPerPersons.size()+ townAntTrPersons.size() + townAntTrPersons2.size() + townIncPerPersons2.size()));
        fixAvPathResistH = nPathResistHosp/((double) (hospPers.size() + healthyHospPeople.size()));
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
