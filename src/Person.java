import java.util.Random;


/**
 * Created by anna on 13.07.16.
 */
public abstract class Person {


    public Person( double micResistance) {
        this.micResistance = micResistance;
    }
    private static Random random;    // pseudo-random number generator
    private static long seed;       // pseudo-random number generator seed
    public static double micResistance;
    public abstract void tick(MyComponent myComp, double p, double coef, double changePathResCoef);

    //generating random number from bernoulli distribution
    static {
        seed = System.currentTimeMillis();
        random = new Random(seed);
    }
    public static void setSeed(long s) {
        seed   = s;
        random = new Random(seed);
    }

    public static boolean bernoulli(double p) {
        if (!(p >= 0.0 & p <= 1.0))
            throw new IllegalArgumentException("Probability must be between 0.0 and 1.0");
        double ans = random.nextDouble();
        return ans < p;
    }

}
