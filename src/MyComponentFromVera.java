/*
import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.IOException;

//TODO: coordinates are not proper for more than 1 hospital
//TODO: make separate functions to replace healthy persons with infectious, and infectious with antibiotic treated


*/
/**
 * Created by anna on 20.07.16.
 *//*

public class MyComponentFromVera extends JPanel {

    //Constructor
    public MyComponentFromVera(int width, int height) {
        setBackground(Color.black);

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
        add(panel,BorderLayout.PAGE_START);
    }

    public Graphics2D graphics;
    //initial number of people
    private static int nHealthyTown = 10000;
    private static int nIncPerTown = 50;
    private static int nIncPerTown2 = 0;
    private static int nAntTrTown = 0;
    private static int nAntTrTown2 = 0;
    private static int nInfectedTown = nAntTrTown + nIncPerTown + nAntTrTown2 +nIncPerTown2;
    private static int nPersHosp = 0;
    private static int nHealthyHosp = 0;//number of hospitalized persons without pathogen
    //number of hospitals (not working yet, only 1 hospital now)
    private final static int nHosp = 1; //number of hospitals
    private static double pHealthyHospitalize = 0.002;//probability to be hospitalized with another infection
    private static double pHospInf = 0.01; //probability to be infected after being hospitalized for a "healthy" person
    //pathogene poperties
    private final static int incLimit = 2;//incubation period
    private final static int incLimit2 = 2;//incubation period for pathogen that becomes resistant
    private final static int antCourseTown = 5; //wright length of antibiotic course
    private final static int antCourseTown2 = 2; //wrong length of antibiotic course
    private final static int antCourseHosp = 7; //length of antibiotic course

    private final static double infCoef1 = 0;//0.125;// coefficient in the next formula (average number of people, that an ill person infects per day

    private final static double infCoef2 = 0.23;//0.125;//infCoef1/10;// coefficient in the next formula (average number of people, that an AntTr person in Town infects per day
    private static double pInf = (infCoef1*((double) nIncPerTown + (double) nIncPerTown2) + infCoef2 * ((double) nAntTrTown) + (double) nAntTrTown2)/
            (((double) nHealthyTown) + ((double) nInfectedTown));
    private static double avPathResist =0; //percentage of resistant pathogens among all (now initially it can be only 0)
    private final static double changePathResCoef = 0.02; //coefficient for probability of pathogen to become resistant because of microbiome resistance
    private final static double pIncHosp = 0.0005;//probability of being hospitalized at the first day of antibiotic treatment
    private final static double pHospNonres = 0.2;//0.005;//probability of being hospitalized for an AntTreatedPerson with nonresistant pathogen
    private final static double pHospRes = 0.1;// 0.01;//probability of being hospitalized for an AntTreatedPerson with resistant pathogen
    private static double pGetToHosp(boolean rPath) {
        if(rPath){
            return pHospRes;
        } else return pHospNonres;
    }  ; //probability to get to a hospital during antibiotic course
    private final int wrongTr =10000000;// 5;// each wrongTr infected person is treated wrong (antCourseTown2)


    //microbiota properties
    private static double avMicResist = 0;// initial averaged level of microbiome resistance
    private final static double growthCoef = 1./21.;//daily growth of microbiota resistance during antibiotic cource
    private final static double decreaseCoef = 1./90.;//daily decrease of microbiota resistance in absence of antibiotics
    private double fixAvPathResist =  avPathResist ;//averaged level of path resistance in town resistance in previous time step
    private double fixAvPathResistH =  avPathResist ;//averaged level of microbiome resistance in previous time step


    //time and graphics parameters
    private static int ticks = 0;
    private final static int tickslimit = 4000;
    private static int l = 8;
    private Timer timer = null;
    private final static int town_h = 600;
    private final static int town_w = 300;
    private final static int hosp_h = 150;
    private final static int hosp_w = 200;
    private static int actualHosp = 0;
    private static final int D_HEIGHT = 1000;
    private static final int D_WIDTH = 1500;


    //Variables for writing output in a file
    private static final String logFile_name = "out/log.txt";
  //  private static final String logFile_name = "/home/vera/agent_resistome/microbiota-resistome/out/log.txt";
    private File logFile = new File(logFile_name);
    private PrintWriter writeFile = null;
    String ID = null;

    //lists of persons and places
    private static ArrayList<HealthyPerson> TownHealthyPersons = new ArrayList<HealthyPerson>(nHealthyTown); //state 1 on scheme in "Препринт"
    private static ArrayList<IncPeriodPerson> TownIncPerPersons = new ArrayList<IncPeriodPerson>(nIncPerTown);//state 2 on scheme in "Препринт"
    private static ArrayList<IncPeriodPerson> TownIncPerPersons2 = new ArrayList<IncPeriodPerson>(nIncPerTown2);//incubation period after wrong treatment (state 5)
    private static ArrayList<AntTreatedPerson> TownAntTrPersons = new ArrayList<AntTreatedPerson>(nAntTrTown);//state 3 on scheme in "Препринт"
    private static ArrayList<AntTreatedPerson> TownAntTtPersons2 = new ArrayList<AntTreatedPerson>(nAntTrTown2);//wrong antibiotic treatment (state 4)
    private static ArrayList<Location> Hospitals = new ArrayList<Location>(nHosp);// for visualization (to be deleted)
    private static ArrayList<AntTreatedPerson> HospPers = new ArrayList<AntTreatedPerson>(nPersHosp);// infected persons in hospital (state 6 - infected)
    private static ArrayList<HealthyHospPerson> HealthyHospPersons = new ArrayList<HealthyHospPerson>(nHealthyHosp);  //"healthy" persons in hospitals (with another pathogen or doctors) (state 6 - not infected)


    //list for output information
    private List<Number> PersonAmount = new ArrayList<Number>();

    public double getFixAvPathResist() {return fixAvPathResist;}
    public double getFixAvPathResistH() {return fixAvPathResistH;}

    public static int getIncLimit2() {return incLimit;}

    public int changeActualHosp (int numberOfHospitals, int lastHospital) {//needed for several hospitals, not used now
        int ans = lastHospital + 1;
        if (ans == numberOfHospitals) { ans = 0;}
        return ans;
    }



    @Override
    public void paintComponent(Graphics g) {
        //town
        Location town = new Location(60, 40, town_w, town_h, Color.blue);
        town.setnHealthy(nHealthyTown);
        town.setnIncPer(nIncPerTown);
        town.setnAntTreated(nAntTrTown);
        town.drawLocation(g);
        repaint();

        //hospitals
        for (int i = 0; i < nHosp; i++) {
            Location hosp = new Location(80 + town_w, 40 + (hosp_h + 30) * i, hosp_w, hosp_h, Color.red);
            Hospitals.add(hosp);
            Hospitals.get(i).setnHealthy(0);
            Hospitals.get(i).setnIncPer(0);
            if (i == 0) {
                Hospitals.get(i).setnAntTreated(nPersHosp);
            }
            Hospitals.get(i).drawLocation(g);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(D_WIDTH, D_HEIGHT);
    }

    private void actionCore() {
        if (ticks == 0) {
            //create out file
            try {
                writeFile = new PrintWriter(logFile);
                String st = "Ticks HealthyPersonsInTown InfectedPersonsInTown IncPeriodPersonsInTown IncPeriodPersonsInTown2 AntibioticTreatedPersonsInTown AntibioticTreatedPersonsInTown2 InfectedPersonsInHospital HealthyPersonsInHospital pGetInfectedTown AvMicResistance AvPathResistance ";
                writeFile.println(st);
            } catch (IOException i) {
                i.printStackTrace();
            }
            //initialise lists of persons
            for (int i = 0; i < nHealthyTown + nInfectedTown; i++) {
                int w = (10 + i * 20) % town_w;
                int k = (10 + 20 * i - w) / town_w;
                int h = k * 20 + 10;
                if (i < nIncPerTown) {
                    TownIncPerPersons.add(new IncPeriodPerson(0,false,incLimit+1));
                } else {
                    if (i < nAntTrTown) {
                        TownAntTrPersons.add(new AntTreatedPerson(0,false, antCourseTown +1, pGetToHosp(false),0));
                    } else TownHealthyPersons.add(new HealthyPerson(0));
                }
            }

            for (int i = 0; i < nPersHosp; i++) {
                int w = (10 + i * 20) % hosp_w;
                int k = (10 + 20 * i - w) / hosp_w;
                int h = k * 20 + 10;
                HospPers.add(new AntTreatedPerson(0,false,antCourseHosp+1, 0,0));
            }
            for (int i = 0; i<nHealthyHosp; i++) {
                HealthyHospPersons.add(new HealthyHospPerson(0,antCourseHosp+1));
            }
            for (int i=0; i<nIncPerTown2; i++) {
                TownIncPerPersons2.add(new IncPeriodPerson(0,false,incLimit2+1));
            }
            for (int i=0; i<nAntTrTown2; i++) {
                TownAntTtPersons2.add(new AntTreatedPerson(0,false,antCourseTown2+1,pGetToHosp(false),0));
            }
        }
        ticks += 1;



        Action(TownHealthyPersons, TownIncPerPersons, TownAntTrPersons, HospPers,
                TownAntTtPersons2, TownIncPerPersons2, HealthyHospPersons);

        if (ticks == tickslimit) {
            writeFile.close();
            System.exit(0);
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

    //action for each component in a list
    public void Action(ArrayList<HealthyPerson> TownHealthyPer, ArrayList<IncPeriodPerson> TownIncPer,
                       ArrayList<AntTreatedPerson> TownAntTr, ArrayList<AntTreatedPerson> HospAntTr,
                       ArrayList<AntTreatedPerson> TownAntTr2, ArrayList<IncPeriodPerson> TownIncPer2,
                       ArrayList<HealthyHospPerson> HealthyHospPer) {
        double rMic;
        boolean rPath;//resistance of current pathogen
        boolean pathRes;
        boolean getToHospital;
        double hospNumber;
        nIncPerTown = TownIncPer.size();
        nIncPerTown2 = TownIncPer2.size();
        nAntTrTown = TownAntTr.size();
        nAntTrTown2 = TownAntTr2.size();
        nHealthyTown = TownHealthyPer.size();
        nHealthyHosp = HealthyHospPer.size();
        nInfectedTown = nIncPerTown + nAntTrTown + nIncPerTown2 + nAntTrTown2;
        nPersHosp = HospAntTr.size(); //number of infected in hospital
        pInf = (infCoef1*((double) nIncPerTown + (double) nIncPerTown2) + infCoef2 * ((double) nAntTrTown))/
                (((double) nHealthyTown) + ((double) nInfectedTown));
        //pInf = 1- java.lang.Math.pow((1-k1/(nHealthyTown+nInfectedTown -k1 +1)),(nIncPerTown +nIncPerTown2)) *
        //        java.lang.Math.pow((1-k2/(nHealthyTown+nInfectedTown -k2 +1)),(nAntTrTown+nAntTrTown2));


        //output
        System.out.print(nHealthyTown + " " + nIncPerTown + " " + nAntTrTown + " " +
                nPersHosp+ " " + nAntTrTown2 + " " + nIncPerTown2 +"\n");
        PersonAmount = new ArrayList<Number>();
        PersonAmount.add(ticks);
        PersonAmount.add(nHealthyTown);
        PersonAmount.add(nInfectedTown);
        PersonAmount.add(nIncPerTown);
        PersonAmount.add(nIncPerTown2);
        PersonAmount.add(nAntTrTown);
        PersonAmount.add(nAntTrTown2);
        PersonAmount.add(nPersHosp);
        PersonAmount.add(nHealthyHosp);
        PersonAmount.add(pInf);
        PersonAmount.add(avMicResist);
        PersonAmount.add(avPathResist);

        System.out.print("nHealthyTown =" + nHealthyTown + "\n");
        System.out.print("nIncPerTown = " + nIncPerTown + "\n");
        System.out.print("nAntTrTown =" + nAntTrTown + "\n");
        System.out.print("nPersHosp =" + nPersHosp + "\n");
        System.out.print("pInf = " + pInf + "\n");
        System.out.print("avMicrobiоtaResistance = " + avMicResist + "\n");
        System.out.print("avPathogeneResistance = " + avPathResist + "\n");
        System.out.print("nHealthyHosp = " + nHealthyHosp + "\n");
        WriteToFile(PersonAmount, writeFile);

        avMicResist = 0.;
        avPathResist = 0.;
        int nHospResistant =0;
        double townAvPathRes = 0;
        double wrongTrFlag = 1;
        ArrayList <Integer> removeIndexes = new ArrayList<Integer>(0);


        //action for healthy people in town
        for (int i=0; i < nHealthyTown; i++) {
            TownHealthyPer.get(i).tick(this, pInf, decreaseCoef,pHealthyHospitalize);
            HealthyPerson pers = TownHealthyPer.get(i);
            avMicResist = avMicResist +pers.micResistance;
            rMic = pers.micResistance;
            if (pers.getToBeChanged()){// & nHealthyTown != 0) {
                rPath = pers.pathRes;
                TownHealthyPer.remove(i);
                nHealthyTown--;
                i--;
                TownIncPer.add(new IncPeriodPerson(rMic,rPath, incLimit + 1));
            } else {
                if (pers.toBeHospitalized) {
                    TownHealthyPer.remove(i);
                    nHealthyTown--;
                    i--;
                    HealthyHospPer.add(new HealthyHospPerson(rMic, antCourseHosp +1));
                }
            }
        }
        //action for IncPeriod people in town
        for (int i = 0; i < nIncPerTown; i++) {
            //System.out.print("i = " + i+ ", nIncPerTown = " + nIncPerTown + "\n");
            TownIncPer.get(i).tick(this, 0, decreaseCoef, changePathResCoef);
            IncPeriodPerson pers = TownIncPer.get(i);
            avMicResist = avMicResist +pers.micResistance;
            if(pers.pathResistance){
                avPathResist += 1;
                townAvPathRes +=1;
            }
            if (pers.getIncCountdown() == 0) {
                rMic = pers.micResistance;
                rPath = pers.pathResistance;
                getToHospital = pers.bernoulli(pIncHosp);
                TownIncPer.remove(i);
                removeIndexes.add(i);
                nIncPerTown--;
                i--;
                if (getToHospital) {
                    HospAntTr.add(new AntTreatedPerson(rMic,rPath,antCourseHosp  + 1,0,0));
                } else {
                    if (wrongTrFlag < wrongTr){
                        TownAntTr.add(new AntTreatedPerson(rMic,rPath,antCourseTown  + 1,pGetToHosp(rPath),0));
                        wrongTrFlag = wrongTrFlag +1;
                    } else {
                        TownAntTr2.add(new AntTreatedPerson(rMic, rPath, antCourseTown2 +1, pGetToHosp(true),0));
                        wrongTrFlag = 1;
                    }
                }
            }
        }

        //action for IncPeriod2 people in town
        for (int i = 0; i < nIncPerTown2; i++) {
            TownIncPer2.get(i).tick(this, 0, decreaseCoef, changePathResCoef);
            IncPeriodPerson pers = TownIncPer2.get(i);
            avMicResist = avMicResist +pers.micResistance;
            if (pers.getIncCountdown() == 0) {
                rMic = pers.micResistance;
                rPath = pers.pathResistance;
                TownIncPer2.remove(i);
                nIncPerTown2--;
                i--;
                HospAntTr.add(new AntTreatedPerson(rMic,rPath,antCourseHosp  + 1,0,0));
            }
        }

        //action for AntTreated people in town
        for (int i = 0; i < nAntTrTown; i++) {
            TownAntTr.get(i).tick(this,pGetToHosp(TownAntTr.get(i).pathResistance), growthCoef, changePathResCoef);
            AntTreatedPerson pers = TownAntTr.get(i);
            avMicResist = avMicResist +pers.micResistance;
            rMic = pers.micResistance;
            rPath = pers.pathResistance;
            if (pers.hospitalize) {
                TownAntTr.remove(i);
                nAntTrTown--;
                i--;
                HospAntTr.add(new AntTreatedPerson(rMic,rPath,antCourseHosp + 1,0,0));

            } else if(pers.treatment_countdown == 0) {
                TownAntTr.remove(i);
                nAntTrTown--;
                i--;
                if (!rPath) {
                    TownHealthyPer.add(new HealthyPerson(rMic));
                } else {
                    HospAntTr.add(new AntTreatedPerson(rMic, rPath, antCourseHosp + 1, 0,0));
                }
            }
        }


        //action for AntTreated2 people in town
        if(nAntTrTown2>0){
            for (int i=0; i< nAntTrTown2; i++) {
                TownAntTr2.get(i).tick(this, pGetToHosp(TownAntTr2.get(i).pathResistance), growthCoef, changePathResCoef);
                AntTreatedPerson pers = TownAntTr2.get(i);
                avMicResist = avMicResist + pers.micResistance;
                rMic = pers.micResistance;
                rPath = pers.pathResistance;
                if (pers.hospitalize) {
                    TownAntTr2.remove(i);
                    nAntTrTown2--;
                    i--;
                    HospAntTr.add(new AntTreatedPerson(rMic, rPath, antCourseHosp + 1, 0, 0));
                } else if (pers.treatment_countdown == 0) {
                    TownAntTr2.remove(i);
                    nAntTrTown2--;
                    i--;
                    TownIncPer2.add(new IncPeriodPerson(rMic, true, incLimit2+1)); //wrong treatment leads to resistance of pathogen
                }
            }
        }




        //action for HospAntTr people (in hosp, infected)
        if(nPersHosp>0) {
            for (int i = 0; i < nPersHosp; i++) {
                HospAntTr.get(i).tick(this, 1, -decreaseCoef, changePathResCoef);//resistance decreases, because in hospital people are treated with another antibiotic
                AntTreatedPerson pers = HospAntTr.get(i);
                if (pers.pathResistance) nHospResistant = nHospResistant + 1;
                avMicResist = avMicResist + pers.micResistance;
                if (pers.treatment_countdown == 0) {
                    rMic = pers.micResistance;
                    HospAntTr.remove(i);
                    nPersHosp--;
                    i--;
                    TownHealthyPer.add(new HealthyPerson(rMic));
                }
            }
        }


        //action for healthy people in hosp
        if(nHealthyHosp >0) {
            for (int i = 0; i < nHealthyHosp; i++) {
                HealthyHospPer.get(i).tick(this, pHospInf ,decreaseCoef, fixAvPathResistH);//resistance decreases, because in hospital people are treated with another antibiotic
                HealthyHospPerson pers = HealthyHospPer.get(i);
                avMicResist = avMicResist +pers.micResistance;
                if(pers.trCountdown == 0) {
                    rMic = pers.micResistance;
                    if(!pers.infected){
                        TownHealthyPer.add(new HealthyPerson(rMic));
                    } else {
                        TownIncPer.add(new IncPeriodPerson(rMic, pers.infected, incLimit+1));
                    }
                    nHealthyHosp--;
                    HealthyHospPer.remove(i);
                    i--;
                }
            }
        }

        int nPathResist =nResMembersA(TownAntTr) +nResMembersA(TownAntTr2) +
                nResMembersI(TownIncPer) + nResMembersI(TownIncPer2) + nResMembersA(HospAntTr);
        int nPathResistTown = nResMembersA(TownAntTr) +nResMembersA(TownAntTr2) +
                nResMembersI(TownIncPer) + nResMembersI(TownIncPer2);
        int nPathResistHosp = nResMembersA(HospAntTr);


        //avMicResist = avMicResist/(nAntTrTown+nAntTrTown2+nIncPerTown2+nIncPerTown+nHealthyTown+nHealthyHosp+nPersHosp);
        avMicResist = avMicResist/((double) (TownHealthyPer.size() + TownIncPer.size()+ TownAntTr.size()+
                TownAntTr2.size() + TownIncPer2.size()+ HospAntTr.size()+HealthyHospPer.size()));
        avPathResist = nPathResist/((double) (TownIncPer.size() + TownIncPer2.size()+TownAntTr.size()+ TownAntTr2.size() +HospAntTr.size() ));
        fixAvPathResist = nPathResistTown/((double) (TownIncPer.size()+TownAntTr.size() + TownAntTr2.size() + TownIncPer2.size()));
        fixAvPathResistH = nPathResistHosp/((double) (nPersHosp + nHealthyHosp));
    }
}


//if Person has coordinates
*/
/*     public void Action(ArrayList<HealthyPerson> TownHealthyPer, ArrayList<IncPeriodPerson> TownInfPer,
                       ArrayList<AntibioticTreatedPerson> TownAntTr) {
        nIncPerTown = TownInfPer.size();
        nAntTrTown = TownAntTr.size();
        nHealthyTown = TownHealthyPer.size();
        nInfectedTown = nIncPerTown + nAntTrTown;
        double p = ((double) nInfectedTown) / (((double) nHealthyTown) + ((double) nInfectedTown));
        System.out.print("p = " + p + "\n");
        for (int i = 0; i < TownHealthyPer.size(); i++) {
            HealthyPerson pers = TownHealthyPer.get(i);
            pers.tick(this, p);
            if (pers.getToBeChanged() & nHealthyTown != 0) {
                int missx = pers.getMissX();
                int missy = pers.getMissY();
                TownHealthyPer.remove(i);
                TownInfPer.add(new IncPeriodPerson(missx, missy, incLimit + 1));
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

