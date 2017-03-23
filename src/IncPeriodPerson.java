/**
 * Created by anna on 20.04.16.
 */
public class IncPeriodPerson extends InfectedPerson{

    //Constructor
    public IncPeriodPerson (double micResistance,boolean pathResistance, int incCountdown){
        super(micResistance,pathResistance,incCountdown);
    }


    public void tick(MyComponent myComp, double p, double decreaseCoef, double coefficient){
        incCountdown -= 1;
        this.pathResistance = changePathResistance(this.pathResistance,this.micResistance, coefficient);
        if(micResistance !=0) {
            micResistance = micResistance - decreaseCoef;
            if (micResistance < 0) {
                micResistance = 0;
            }
        }
    }
}