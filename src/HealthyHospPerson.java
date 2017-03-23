/**
 * Created by anna on 05.12.16.
 */
public class HealthyHospPerson extends Person {
    //constructor
    public HealthyHospPerson (double micResistance, int trCountdown){
        super(micResistance);
        this.trCountdown = trCountdown;
    }
    public int trCountdown;
    public boolean infected = false;
    public boolean pRes;
    public void tick(MyComponent myComp, double pInfHosp, double decreaseCoef, double pResistant){
        if(micResistance !=0){
            micResistance = micResistance - decreaseCoef;
            if(micResistance < 0) {micResistance = 0;}
        }
        if(trCountdown >0) trCountdown = trCountdown-1;
        else {
            infected = bernoulli(pInfHosp);
            if(infected) pRes = bernoulli(pResistant);
        }
    }
}
