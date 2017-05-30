package com.ripcm.microbiomeres;

import java.util.Random;

/**
 * Created by nikita on 24.05.17.
 */
public class Utils {
    //generating random number from bernoulli distribution

    private static Random random;    // pseudo-random number generator
    private static long seed;       // pseudo-random number generator seed

    static {
        //TODO: !!! set to System.currentTimeMillis();
        //seed = 123;//
        seed = System.currentTimeMillis();
        random = new Random(Utils.seed);
    }

    public static void setSeed(long s) {
        seed   = s;
        random = new Random(seed);
    }

    public static boolean bernoulli(double p) { //?????????????????
        if (!(p >= 0.0 & p <= 1.0))
            throw new IllegalArgumentException("Probability must be between 0.0 and 1.0");
        double ans = random.nextDouble();
        return ans < p;
    }
}
